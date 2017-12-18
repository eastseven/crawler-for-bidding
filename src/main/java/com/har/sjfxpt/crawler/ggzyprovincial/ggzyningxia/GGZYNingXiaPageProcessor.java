package com.har.sjfxpt.crawler.ggzyprovincial.ggzyningxia;

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
 * Created by Administrator on 2017/12/17.
 */
@Slf4j
@Component
public class GGZYNingXiaPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    final static String PAGE_PARAMS = "pageParams";

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int pageNum = Integer.parseInt(StringUtils.substringBefore(StringUtils.substringAfterLast(url, "/"), ".html"));
        if (pageNum == 1) {
            String pageCountField = page.getHtml().getDocument().body().select("#index").text();
            int pageCount = Integer.parseInt(StringUtils.substringAfter(pageCountField, "/"));
            int cycleCount = pageCount >= 3 ? 3 : pageCount;
            for (int i = 2; i <= cycleCount; i++) {
                String urlTarget = url.replace("1.html", i + ".html");
                Request request = new Request(urlTarget);
                request.putExtra(PAGE_PARAMS, pageParams);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get(PAGE_PARAMS);
        String type = pageParams.get("type");
        String businessType = pageParams.get("businessType");
        Elements elements = page.getHtml().getDocument().body().select("#showList > ul > li");
        List<GGZYNingXiaDataItem> dataItems = parseContent(elements);
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
        List<GGZYNingXiaDataItem> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                if (!StringUtils.startsWith(href, "http:")) {
                    href = "http://www.nxggzyjy.org" + href;
                }
                String title = element.text();
                GGZYNingXiaDataItem ggzyNingXiaDataItem = new GGZYNingXiaDataItem(href);
                ggzyNingXiaDataItem.setUrl(href);
                ggzyNingXiaDataItem.setTitle(title);

                Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                String dateContext = page.getHtml().getDocument().body().select("body > div:nth-child(4) > div > div.ewb-main-bar").text();
                String dateReal = StringUtils.substringBetween(dateContext, "【信息时间：", "】");
                ggzyNingXiaDataItem.setDate(PageProcessorUtil.dataTxt(dateReal));
                if (PageProcessorUtil.timeCompare(ggzyNingXiaDataItem.getDate())) {
                    log.info("{} is not the same day", ggzyNingXiaDataItem.getUrl());
                } else {
                    Elements elements = page.getHtml().getDocument().body().select("#gonggaoid");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        ggzyNingXiaDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyNingXiaDataItem);
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
