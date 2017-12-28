package com.har.sjfxpt.crawler.ggzyprovincial.ggzyxjbt;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
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

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/12/18.
 */
@Slf4j
@Component
public class GGZYXJBTPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    final static String PAGE_PARAMS = "pageParams";

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int pageNum = Integer.parseInt(StringUtils.substringAfter(url, "Paging="));
        if (pageNum == 1) {
            String pageCountContent = page.getHtml().getDocument().body().select("body > table:nth-child(12) > tbody > tr > td:nth-child(3) > table.top10 > tbody > tr:nth-child(2) > td > div > div > div > table > tbody > tr > td:nth-child(25)").text();
            if (StringUtils.isNotBlank(pageCountContent)) {
                int pageCount = Integer.parseInt(StringUtils.substringAfter(pageCountContent, "/"));
                int cycleCount = pageCount >= 3 ? 3 : pageCount;
                for (int i = 2; i <= cycleCount; i++) {
                    String urlTarget = url.replace("Paging=1", "Paging=" + i);
                    Request request = new Request(urlTarget);
                    request.putExtra(PAGE_PARAMS, pageParams);
                    page.addTargetRequest(request);
                }
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get(PAGE_PARAMS);
        Elements elements = page.getHtml().getDocument().body().select("body > table:nth-child(12) > tbody > tr > td:nth-child(3) > table.top10 > tbody > tr:nth-child(2) > td > div > table > tbody > tr");
        List<GGZYXJBTDataItem> dataItems = parseContent(elements);
        String type = pageParams.get("type");
        String businessType = pageParams.get("businessType");
        dataItems.forEach(dataItem -> {
            dataItem.setType(type);
            dataItem.setBusinessType(businessType);
        });
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<GGZYXJBTDataItem> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("td:nth-child(2) >a ").attr("href");
            if (StringUtils.isNotBlank(href)) {
                if (!StringUtils.startsWith(href, "http://ggzy.xjbt.gov.cn")) {
                    href = "http://ggzy.xjbt.gov.cn" + href;
                }
                String title = element.select("td:nth-child(2) >a").attr("title");
                String date = element.select("td:nth-child(3)").text();
                if (date.contains("[")) {
                    date = StringUtils.substringBetween(date, "[", "]");
                }
                GGZYXJBTDataItem ggzyxjbtDataItem = new GGZYXJBTDataItem(href);
                ggzyxjbtDataItem.setUrl(href);
                ggzyxjbtDataItem.setTitle(title);
                ggzyxjbtDataItem.setDate(PageProcessorUtil.dataTxt(date));

                if (PageProcessorUtil.timeCompare(ggzyxjbtDataItem.getDate())) {
                    log.info("{} is not the same day", ggzyxjbtDataItem.getUrl());
                } else {
                    Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    Elements elements = page.getHtml().getDocument().body().select("#TDContent");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent) && StringUtils.isNotBlank(title)) {
                        ggzyxjbtDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyxjbtDataItem);
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
