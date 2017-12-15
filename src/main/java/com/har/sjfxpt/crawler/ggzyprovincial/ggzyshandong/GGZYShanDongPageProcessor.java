package com.har.sjfxpt.crawler.ggzyprovincial.ggzyshandong;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/12/15.
 */
@Slf4j
@Component
public class GGZYShanDongPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        int pageNum = Integer.parseInt(StringUtils.substringBetween(url, "inDates=", "&channelId="));
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("body > div.content > div.jyxxcontent > div > div > ul > li:nth-child(1) > a");
            int pageCount = Integer.parseInt(StringUtils.substringBetween(elements.text(), "/", "页"));
            if (pageCount >= 2) {
                for (int i = 2; i <= pageCount; i++) {
                    String urlTarget = StringUtils.replace(url, "queryContent_1", "queryContent_" + i);
                    page.addTargetRequest(urlTarget);
                }
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Elements elements = page.getHtml().getDocument().body().select("body > div.content > div.jyxxcontent > div > ul >li");
        List<GGZYShanDongDataItem> dataItems = parseContent(elements);
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<GGZYShanDongDataItem> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("div.article-list3-t > a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                String title = element.select("div.article-list3-t > a").text();
                String date = element.select("div.article-list3-t > div").text();
                String source = element.select("div.article-list3-t2 > div:nth-child(1)").text();
                String businessType = element.select("div.article-list3-t2 > div:nth-child(2)").text();
                String type = element.select("div.article-list3-t2 > div:nth-child(3)").text();

                GGZYShanDongDataItem ggzyShanDongDataItem = new GGZYShanDongDataItem(href);
                ggzyShanDongDataItem.setUrl(href);
                ggzyShanDongDataItem.setTitle(title);
                ggzyShanDongDataItem.setDate(PageProcessorUtil.dataTxt(date));
                ggzyShanDongDataItem.setSource(StringUtils.substringAfter(source, "："));
                ggzyShanDongDataItem.setBusinessType(StringUtils.substringAfter(businessType, "："));
                ggzyShanDongDataItem.setType(StringUtils.substringAfter(type, "："));
                if (PageProcessorUtil.timeCompare(ggzyShanDongDataItem.getDate())) {
                    log.warn("{} is not the same day", ggzyShanDongDataItem.getUrl());
                } else {
                    Page page = httpClientDownloader.download(new Request(ggzyShanDongDataItem.getUrl()), SiteUtil.get().setTimeOut(30000).toTask());
                    Elements elements = page.getHtml().getDocument().body().select("body > div.content > div.div-content.clearfix > div:nth-child(5) > div.div-article2");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        ggzyShanDongDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyShanDongDataItem);
                    }
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
        return SiteUtil.get().setSleepTime(10000);
    }
}
