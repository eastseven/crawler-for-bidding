package com.har.sjfxpt.crawler.ggzyprovincial.ggzyhn;

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
 * Created by Administrator on 2017/11/29.
 */
@Slf4j
@Component
public class ggzyHNPageProcessor implements BasePageProcessor {

    final static String PAGE_PARAMS = "pageParams";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int currentPage = Integer.parseInt(StringUtils.substringBetween(page.getUrl().toString(), "/index_", ".jhtml"));
        if (currentPage == 1) {
            Elements elements = page.getHtml().getDocument().body().select("body > div.containerNobg > div:nth-child(3) > div.w740 > table > tbody > tr:nth-child(11) > td > div > div");
            int size = Integer.parseInt(StringUtils.substringBetween(elements.text(), "/", "页"));
            for (int i = 2; i <= 10; i++) {
                String url = StringUtils.substringBefore(page.getUrl().toString(), "/index_") + "/index_" + i + ".jhtml";
                Request request = new Request(url);
                request.putExtra(PAGE_PARAMS, pageParams);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get(PAGE_PARAMS);
        Elements elements = page.getHtml().getDocument().body().select("body > div.containerNobg > div:nth-child(3) > div.w740 > table > tbody >tr");
        if (elements.isEmpty()) {
            log.error("fetch error, elements is empty");
            return;
        }
        List<ggzyHNDataItem> dataItems = parseContent(elements);
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
        List<ggzyHNDataItem> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("td > a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                String title = element.select("td > a").attr("title");
                String date = element.select(" td:nth-child(4)").text();
                if (PageProcessorUtil.timeCompare(date)) {
                    log.debug("{} is not on the same day", href);
                } else {
                    ggzyHNDataItem ggzyHNDataItem = new ggzyHNDataItem(href);
                    ggzyHNDataItem.setUrl(href);
                    ggzyHNDataItem.setTitle(title);
                    if (date.length() == 10) {
                        date = PageProcessorUtil.dataTxt(date);
                    }
                    ggzyHNDataItem.setDate(date);
                    Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    Elements elements = page.getHtml().getDocument().body().select("body > div.container > div > div.newsTex");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        ggzyHNDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyHNDataItem);
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
        return SiteUtil.get();
    }
}
