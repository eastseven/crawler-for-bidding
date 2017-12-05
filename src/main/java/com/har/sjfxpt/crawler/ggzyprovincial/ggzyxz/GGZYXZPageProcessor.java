package com.har.sjfxpt.crawler.ggzyprovincial.ggzyxz;

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
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/12/5.
 */
@Slf4j
@Component
public class GGZYXZPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    final static String PAGE_PARAMS = "pageParams";

    @Override
    public void handlePaging(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get(PAGE_PARAMS);
        String url = page.getUrl().get();
        int count = Integer.parseInt(StringUtils.substringBetween(url, "/index_", ".jhtml"));
        if (count == 1) {
            Elements elements = page.getHtml().getDocument().body().select("body > div.content-old > div.jyxxcontent-old > div.article-content-old > div.pagesite > div > ul > li:nth-child(1) > a");
            int pageCount = Integer.parseInt(StringUtils.substringBetween(elements.text(), "/", "页"));
            if (pageCount >= 2) {
                if (pageCount >= 10) {
                    for (int i = 2; i <= 10; i++) {
                        String urlTarget = url.replace("index_1", "index_" + i);
                        Request request = new Request(urlTarget);
                        request.putExtra(PAGE_PARAMS, pageParams);
                        page.addTargetRequest(request);
                    }
                } else {
                    for (int i = 2; i <= pageCount; i++) {
                        String urlTarget = url.replace("index_1", "index_" + i);
                        Request request = new Request(urlTarget);
                        request.putExtra(PAGE_PARAMS, pageParams);
                        page.addTargetRequest(request);
                    }
                }

            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get(PAGE_PARAMS);
        Elements elements = page.getHtml().getDocument().body().select("body > div.content-old > div.jyxxcontent-old > div.article-content-old > ul >li");
        List<GGZYXZDataItem> dataItems = parseContent(elements);
        String type = pageParams.get("type");
        String businessType = pageParams.get("businessType");
        dataItems.forEach(dataItem -> dataItem.setType(type));
        dataItems.forEach(dataItem -> dataItem.setBusinessType(businessType));
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<GGZYXZDataItem> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String url = element.select("a").attr("href");
            String title = element.select("a").attr("title");
            String date = element.select("div").text();
            if (StringUtils.isNotBlank(url)) {
                GGZYXZDataItem ggzyxzDataItem = new GGZYXZDataItem(url);
                ggzyxzDataItem.setUrl(url);
                ggzyxzDataItem.setTitle(title);
                if (StringUtils.isNotBlank(date)) {
                    ggzyxzDataItem.setDate(PageProcessorUtil.dataTxt(date));
                }
                if (PageProcessorUtil.timeCompare(ggzyxzDataItem.getDate())) {
                    log.warn("{} is not the same day", ggzyxzDataItem.getUrl());
                } else {
                    Page page = httpClientDownloader.download(new Request(url), SiteUtil.get().setTimeOut(20000).toTask());
                    Elements elements = page.getHtml().getDocument().body().select("body > div.content > div.div-content > div.div-article2 > div");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        ggzyxzDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyxzDataItem);
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
