package com.har.sjfxpt.crawler.ccgp.provincial;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Administrator on 2017/11/30.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.CCGPCQ,
        sources = {
                @Source(url = "https://www.cqgp.gov.cn/gwebsite/api/v1/notices/stable?pi=1&ps=20&startDate=&type=100,200,201,202,203,204,205,206,207,309,400,401,402,3091,4001&timestamp=TIMESTAMP", type = "采购公告", dayPattern = "TIMESTAMP", needPlaceholderFields = {"TIMESTAMP"}),
                @Source(url = "https://www.cqgp.gov.cn/gwebsite/api/v1/notices/stable?pi=1&ps=20&startDate=&type=301,303&timestamp=TIMESTAMP", type = "采购预公示", dayPattern = "TIMESTAMP", needPlaceholderFields = {"TIMESTAMP"}),
                @Source(url = "https://www.cqgp.gov.cn/gwebsite/api/v1/notices/stable?pi=1&ps=20&startDate=&type=300,302,304,3041,305,306,307,308&timestamp=TIMESTAMP", type = "采购结果公告", dayPattern = "TIMESTAMP", needPlaceholderFields = {"TIMESTAMP"})
        }
)
public class CQPageProcessor implements BasePageProcessor {

    final static int ARTICLE_NUM = 20;

    @Autowired
    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String type = (String) page.getRequest().getExtra("type");
        int count = Integer.parseInt(StringUtils.substringBetween(page.getUrl().toString(), "pi=", "&ps="));
        if (count == 1) {
            JSONObject jsonObject = (JSONObject) JSONObject.parse(page.getRawText());
            int announcementNum = Integer.parseInt(JSONPath.eval(jsonObject, "$.total").toString());
            int pageNum = announcementNum % ARTICLE_NUM == 0 ? announcementNum / ARTICLE_NUM : announcementNum / ARTICLE_NUM + 1;
            if (pageNum >= 2) {
                int cycleNum = pageNum >= 10 ? 10 : pageNum;
                for (int i = 2; i <= cycleNum; i++) {
                    String url = page.getUrl().toString().replace("pi=1", "pi=" + i);
                    Request request = new Request(url);
                    request.putExtra("type", type);
                    page.addTargetRequest(request);
                }
            }
        }

    }

    @Override
    public void handleContent(Page page) {
        String type = (String) page.getRequest().getExtra("type");
        List<BidNewsOriginal> dataItems = parseContent(page);
        dataItems.forEach(dataItem -> dataItem.setType(type));
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        return null;
    }

    public List parseContent(Page page) {
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        JSONObject root = (JSONObject) JSONObject.parse(page.getRawText());
        JSONArray jsonArray = (JSONArray) JSONPath.eval(root, "$.notices");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject project = (JSONObject) JSONObject.parse(jsonArray.get(i).toString());
            String id = JSONPath.eval(project, "$.id").toString();
            String title = JSONPath.eval(project, "$.title").toString();
            String purchaser = JSONPath.eval(project, "$.buyerName").toString();
            String date = JSONPath.eval(project, "$.issueTime").toString();

            String code = null;
            try {
                code = URLEncoder.encode(title.getBytes().toString(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String hrefLook = "https://www.cqgp.gov.cn/notices/detail/" + id + "?title=" + code;
            String href = "https://www.cqgp.gov.cn/gwebsite/api/v1/notices/stable/" + id;
            BidNewsOriginal ccgpcqDataItem = new BidNewsOriginal(href, SourceCode.CCGPCQ);
            ccgpcqDataItem.setTitle(title);
            ccgpcqDataItem.setPurchaser(purchaser);
            ccgpcqDataItem.setProvince("重庆");
            ccgpcqDataItem.setDate(PageProcessorUtil.dataTxt(date));
            ccgpcqDataItem.setUrl(hrefLook);
            if (PageProcessorUtil.timeCompare(ccgpcqDataItem.getDate())) {
                log.warn("{} is not the same day", ccgpcqDataItem.getUrl());
            } else {
                try {
                    Page page1 = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    JSONObject pageJson = (JSONObject) JSONObject.parse(page1.getRawText());
                    String htmlJson = (String) JSONPath.eval(pageJson, "$.notice.html");
                    Whitelist whitelist = Whitelist.relaxed();
                    whitelist.removeTags("style");
                    whitelist.removeTags("script");
                    whitelist.removeAttributes("table", "style", "width", "height");
                    whitelist.removeAttributes("td", "style", "width", "height");
                    String formatContent = Jsoup.clean(htmlJson, whitelist);
                    formatContent = StringUtils.removeAll(formatContent, "<!-{2,}.*?-{2,}>|(&nbsp;)|<o:p>|</o:p>");
                    if (StringUtils.isNotBlank(formatContent)) {
                        ccgpcqDataItem.setFormatContent(formatContent);
                        dataItems.add(ccgpcqDataItem);
                    }
                } catch (Exception e) {
                    log.warn("{}", e);
                }
            }
        }
        return dataItems;
    }

    @Override
    public void process(Page page) {
        handlePaging(page);
        handleContent(page);
    }

    @Override
    public Site getSite() {
        httpClientDownloader = new HttpClientDownloader();
        return SiteUtil.get().setTimeOut(10000);
    }
}
