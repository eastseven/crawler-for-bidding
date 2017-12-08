package com.har.sjfxpt.crawler.ggzyprovincial.ggzysc;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
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

import java.net.URLEncoder;
import java.util.List;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/11/27.
 */
@Slf4j
@Component
public class GGZYSCPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        int currentPage = Integer.parseInt(StringUtils.substringBetween(page.getUrl().toString(), "page=", "&parm"));
        if (currentPage == 1) {
            GGZYSCAnnouncement data = JSONObject.parseObject(page.getRawText(), GGZYSCAnnouncement.class);
            int size = data.getPageCount();
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
        List<GGZYSCDataItem> dataItems = parseContent(page);
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
        GGZYSCAnnouncement data = JSONObject.parseObject(page.getRawText(), GGZYSCAnnouncement.class);
        List<GGZYSCDataItem> dataItems = Lists.newArrayList();
        String targets[] = StringUtils.substringsBetween(data.getData(), "{", "}");
        if (targets.length != 0) {
            for (String target : targets) {
                String href = StringUtils.substringBetween(target, "\"Link\":\"", "\",");
                String title = StringUtils.substringBetween(target, "\"Title\":\"", "\",");
                String date = StringUtils.substringBetween(target, "\"CreateDateAll\":\"", "\",");
                String type = StringUtils.substringBetween(target, "\"TableName\":\"", "\",");
                String businessType = StringUtils.substringBetween(target, "\"businessType\":\"", "\",");

                GGZYSCDataItem GGZYSCDataItem = new GGZYSCDataItem("http://www.scztb.gov.cn" + href);
                String encode = URLEncoder.encode(StringUtils.substringBefore(StringUtils.substringAfterLast(href, "/"), ".html"), "utf-8");
                String urlEncoder = "http://www.scztb.gov.cn" + StringUtils.substringBeforeLast(href, "/") + "/" + encode + ".html";
                GGZYSCDataItem.setUrl(urlEncoder);
                GGZYSCDataItem.setTitle(title);
                GGZYSCDataItem.setDate(PageProcessorUtil.dataTxt(date));
                GGZYSCDataItem.setType(type);
                GGZYSCDataItem.setBusinessType(businessType);
                try {
                    Page page1 = httpClientDownloader.download(new Request(GGZYSCDataItem.getUrl()), SiteUtil.get().setTimeOut(30000).toTask());
                    Element element = page1.getHtml().getDocument().body();
                    Elements elements = element.select("body > div.wmain > div.ContentMiddle > div > div.Middle > div.ChangeMidle > div.detailedTitle");
                    Elements elements1 = element.select("body > div.wmain > div.ContentMiddle > div > div.Middle > div.ChangeMidle > div.detailedIntroduc");
                    String formatContent = "";
                    String detailedTitle = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    String detailedIntroduc = PageProcessorUtil.formatElementsByWhitelist(elements1.first());
                    if (StringUtils.isNotBlank(detailedTitle)) {
                        formatContent = formatContent + detailedTitle;
                    }
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
