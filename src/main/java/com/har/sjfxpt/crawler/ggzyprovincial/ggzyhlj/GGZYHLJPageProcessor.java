package com.har.sjfxpt.crawler.ggzyprovincial.ggzyhlj;

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
public class GGZYHLJPageProcessor implements BasePageProcessor {

    final static String PAGE_PARAMS = "pageParams";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get("pageParams");
        String url = page.getUrl().get();
        int pageNum = Integer.parseInt(StringUtils.substringBetween(url, "pageNo=", "&type"));
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("body > div > div.content_box > div.main_wrap > div.news_inf > div > div > span");
            String elementsText = elements.text();
            int pageCount = Integer.parseInt(StringUtils.substringBetween(elementsText, "/", "页"));
            if (pageCount >= 5) {
                for (int i = 2; i <= 5; i++) {
                    String urlTarget = url.replace("pageNo=1", "pageNo=" + i);
                    Request request = new Request(urlTarget);
                    request.putExtra(PAGE_PARAMS, pageParams);
                    page.addTargetRequest(request);
                }
            } else {
                for (int i = 2; i <= pageCount; i++) {
                    String urlTarget = url.replace("pageNo=1", "pageNo=" + i);
                    Request request = new Request(urlTarget);
                    request.putExtra(PAGE_PARAMS, pageParams);
                    page.addTargetRequest(request);
                }
            }

        }
    }

    @Override
    public void handleContent(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get("pageParams");
        Elements elements = page.getHtml().getDocument().body().select("body > div > div.content_box > div.main_wrap > div.news_inf > div > ul > li");
        List<GGZYHLJDataItem> dataItems = parseContent(elements);
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
        List<GGZYHLJDataItem> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String url = element.select("a").attr("href");
            if (StringUtils.isNotBlank(url)) {
                String href = "http://hljggzyjyw.gov.cn" + url;
                String title = element.select("a").attr("title");
                String date = element.select("span.date").text();
                GGZYHLJDataItem ggzyhljDataItem = new GGZYHLJDataItem(href);
                ggzyhljDataItem.setUrl(href);
                ggzyhljDataItem.setTitle(title);
                ggzyhljDataItem.setDate(PageProcessorUtil.dataTxt(date));
                if (PageProcessorUtil.timeCompare(ggzyhljDataItem.getDate())) {
                    log.warn("{} is not the same day", ggzyhljDataItem.getUrl());
                } else {
                    Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    Elements elements = page.getHtml().getDocument().select("#contentdiv");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        ggzyhljDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyhljDataItem);
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
