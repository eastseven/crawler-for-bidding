package com.har.sjfxpt.crawler.ggzy.provincial;

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
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.Selectable;

import java.net.URLEncoder;
import java.util.List;

import static com.har.sjfxpt.crawler.ggzy.provincial.SCPageProcessor.*;

/**
 * Created by Administrator on 2017/11/27.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZYSC,
        sources = {
                @Source(url = GGZYSC_URL1, dayPattern = "TIMESTAMP", needPlaceholderFields = {"TIMESTAMP"}),
                @Source(url = GGZYSC_URL2, dayPattern = "TIMESTAMP", needPlaceholderFields = {"TIMESTAMP"})
        }
)
public class SCPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    final static String GGZYSC_URL1 = "http://www.scggzy.gov.cn/Info/GetInfoListNew?keywords=&times=1&timesStart=&timesEnd=&province=&area=&businessType=project&informationType=&industryType=&page=1&parm=TIMESTAMP";
    final static String GGZYSC_URL2 = "http://www.scggzy.gov.cn/Info/GetInfoListNew?keywords=&times=1&timesStart=&timesEnd=&province=&area=&businessType=purchase&informationType=&industryType=&page=1&parm=TIMESTAMP";

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        int currentPage = Integer.parseInt(StringUtils.substringBetween(page.getUrl().toString(), "page=", "&parm"));
        if (currentPage == 1) {
            JSONObject jsonObject = (JSONObject) JSONObject.parse(page.getRawText());
            int size = Integer.parseInt(JSONPath.eval(jsonObject, "$.pageCount").toString());
            log.debug("size={}", size);
            if (size >= 2) {
                for (int i = 2; i <= size; i++) {
                    String urlTarget = url.replace("page=1", "page=" + i);
                    page.addTargetRequest(urlTarget);
                }
            }
        }
    }

    @Override
    public void handleContent(Page page) throws Exception {
        List<BidNewsOriginal> dataItems = parseContent(page);
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

    public List parseContent(Page page) throws Exception {
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        Selectable data = page.getJson().jsonPath("$.data");
        List<String> stringList = data.all();
        if (stringList.size() != 0) {
            for (String target : stringList) {
                String href = StringUtils.substringBetween(target, "\"Link\":\"", "\",");
                String title = StringUtils.substringBetween(target, "\"Title\":\"", "\",");
                String date = StringUtils.substringBetween(target, "\"CreateDateAll\":\"", "\",");
                String type = StringUtils.substringBetween(target, "\"TableName\":\"", "\",");

                String encode = URLEncoder.encode(StringUtils.substringBefore(StringUtils.substringAfterLast(href, "/"), ".html"), "utf-8");
                String urlEncoder = "http://www.scggzy.gov.cn" + StringUtils.substringBeforeLast(href, "/") + "/" + encode + ".html";
                BidNewsOriginal GGZYSCDataItem = new BidNewsOriginal(urlEncoder, SourceCode.GGZYSC);
                GGZYSCDataItem.setTitle(title);
                GGZYSCDataItem.setDate(PageProcessorUtil.dataTxt(date));
                GGZYSCDataItem.setType(type);
                GGZYSCDataItem.setProvince("四川");
                try {
                    Page page1 = httpClientDownloader.download(new Request(GGZYSCDataItem.getUrl()), SiteUtil.get().setTimeOut(30000).toTask());
                    Element element = page1.getHtml().getDocument().body();
                    Elements elements = element.select("body > div > div.ContentMiddle > div > div.Middle > div.ChangeMidle > div.detailedIntroduc");
                    String formatContent = "";
                    String detailedIntroduc = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(detailedIntroduc)) {
                        formatContent = formatContent + detailedIntroduc;
                    }
                    for (Element element1 : element.select("div.Nmds")) {
                        boolean bln = StringUtils.contains(element1.attr("style"), "block");
                        if (!bln) {
                            continue;
                        }
                        String content = element1.select("input").attr("value");
                        if (content.contains("<![CDATA[")) {
                            Whitelist whitelist = Whitelist.relaxed();
                            whitelist.removeTags("iframe");
                            String html = StringUtils.substringBetween(content, "<![CDATA[", "]]");
                            formatContent = formatContent + Jsoup.clean(html, whitelist);
                        } else {
                            Whitelist whitelist = Whitelist.relaxed();
                            whitelist.removeTags("iframe");
                            formatContent = formatContent + Jsoup.clean(content, whitelist);
                        }
                    }
                    if (StringUtils.isNotBlank(formatContent)) {
                        GGZYSCDataItem.setFormatContent(formatContent);
                        dataItems.add(GGZYSCDataItem);
                    }
                } catch (Exception e) {
                    log.error("url {} is wrong page", GGZYSCDataItem.getUrl());
                    log.error("error is {}", e);
                }
            }
        } else {
            log.warn("JSON {} no data", page.getUrl().toString());
        }
        return dataItems;
    }

    @Override
    public void process(Page page) {
        handlePaging(page);
        try {
            handleContent(page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Site getSite() {
        httpClientDownloader = new HttpClientDownloader();
        return SiteUtil.get().setSleepTime(10000);
    }
}
