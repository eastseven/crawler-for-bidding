package com.har.sjfxpt.crawler.ccgp.ccgpcq;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
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
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/11/30.
 */
@Slf4j
@Component
public class CCGPCQPageProcessor implements BasePageProcessor {

    final static String PAGE_PARAMS = "pageParams";

    final static int ARTICLE_NUM = 20;

    @Autowired
    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int count = Integer.parseInt(StringUtils.substringBetween(page.getUrl().toString(), "pi=", "&ps="));

        if (count == 1) {
            CCGPCQAnnouncement ccgpcqAnnouncement = JSONObject.parseObject(page.getRawText(), CCGPCQAnnouncement.class);
            int announcementNum = ccgpcqAnnouncement.getTotal();
            int pageNum = announcementNum % ARTICLE_NUM == 0 ? announcementNum / ARTICLE_NUM : announcementNum / ARTICLE_NUM + 1;
            if (pageNum >= 2) {
                for (int i = 2; i <= pageNum; i++) {
                    String url = page.getUrl().toString().replace("pi=1", "pi=" + i);
                    Request request = new Request(url);
                    request.putExtra(PAGE_PARAMS, pageParams);
                    page.addTargetRequest(request);
                }
            }
        }


    }

    @Override
    public void handleContent(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get(PAGE_PARAMS);
        List<CCGPCQDataItem> dataItems = parseContent(page);
        String type = pageParams.get("type");
        dataItems.forEach(dataItem -> dataItem.setType(type));
//        dataItems.forEach(dataItem -> dataItem.setForceUpdate(true));
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
        List<CCGPCQDataItem> dataItems = Lists.newArrayList();
        CCGPCQAnnouncement ccgpcqAnnouncement = JSONObject.parseObject(page.getRawText(), CCGPCQAnnouncement.class);
        List<CCGPCQAnnouncement.NoticesBean> tenderBulletin = ccgpcqAnnouncement.getNotices();
        for (CCGPCQAnnouncement.NoticesBean noticesBean : tenderBulletin) {
            String id = noticesBean.getId();
            String title = noticesBean.getTitle();
            String purchaser = noticesBean.getBuyerName();
            String industryCategory = noticesBean.getProjectDirectoryName();
            String source = noticesBean.getAgentName();
            String date = noticesBean.getIssueTime();

            String code = null;
            try {
                code = URLEncoder.encode(title.getBytes().toString(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String hrefLook = "https://www.cqgp.gov.cn/notices/detail/" + id + "?title=" + code;
            String href = "https://www.cqgp.gov.cn/gwebsite/api/v1/notices/stable/" + id;
            CCGPCQDataItem ccgpcqDataItem = new CCGPCQDataItem(href);
            ccgpcqDataItem.setTitle(title);
            ccgpcqDataItem.setPurchaser(purchaser);
            ccgpcqDataItem.setSource(source);
            ccgpcqDataItem.setIndustryCategory(industryCategory);
            ccgpcqDataItem.setDate(PageProcessorUtil.dataTxt(date));
            ccgpcqDataItem.setUrl(hrefLook);

            Page page1 = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(20000).toTask());
            CCGPCQDetailAnnouncement ccgpcqDetailAnnouncement = JSONObject.parseObject(page1.getRawText(), CCGPCQDetailAnnouncement.class);
            String html = ccgpcqDetailAnnouncement.getNotice().getHtml();
            Whitelist whitelist = Whitelist.relaxed();
            whitelist.removeTags("style");
            whitelist.removeTags("script");
            whitelist.removeAttributes("table", "style", "width", "height");
            whitelist.removeAttributes("td", "style", "width", "height");
            String formatContent = Jsoup.clean(html, whitelist);
            formatContent = StringUtils.removeAll(formatContent, "<!-{2,}.*?-{2,}>|(&nbsp;)|<o:p>|</o:p>");
            if (StringUtils.isNotBlank(formatContent)) {
                ccgpcqDataItem.setFormatContent(formatContent);
                dataItems.add(ccgpcqDataItem);
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
        return SiteUtil.get();
    }
}
