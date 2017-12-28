package com.har.sjfxpt.crawler.ggzyprovincial.ggzygansu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/12/17.
 */
@Slf4j
@Component
public class GGZYGanSuPageProcessor implements BasePageProcessor {

    final static int PAGE_SIZE = 20;

    final static String PAGE_PARAMS = "pageParams";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int pageNum = Integer.parseInt(StringUtils.substringBetween(url, "pageNo=", "&pageSize="));
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("body > div.tradpage > ul > li.disabled.controls");
            int announcementCount = Integer.parseInt(StringUtils.substringBetween(elements.text(), "共 ", " 条"));
            int pageCount = announcementCount % PAGE_SIZE == 0 ? announcementCount / PAGE_SIZE : announcementCount / PAGE_SIZE + 1;
            int cycleNum = pageCount >= 6 ? 6 : pageCount;
            for (int i = 2; i <= cycleNum; i++) {
                String urlTarget = url.replace("pageNo=1", "pageNo=" + i);
                Request request = new Request(urlTarget);
                request.setMethod(HttpConstant.Method.POST);
                String jsonFiled = pageParams.get("filterparam").toString();
                Map<String, Object> filterparam = Maps.newHashMap();
                filterparam.put("filterparam", jsonFiled);
                request.setRequestBody(HttpRequestBody.form(filterparam, "UTF-8"));
                request.putExtra(PAGE_PARAMS, pageParams);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get("pageParams");
        Elements elements = page.getHtml().getDocument().body().select("body > div.trad-sear-con > ul > li");
        if (elements.isEmpty()) {
            log.warn("{} elements is empty", page.getUrl().get());
            return;
        }
        String type = (String) pageParams.get("type");
        String businessType = (String) pageParams.get("businessType");
        List<GGZYGanSuDataItem> dataItems = parseContent(elements);
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
        List<GGZYGanSuDataItem> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String hrefFiled = element.select("a").attr("onclick");
            String href = StringUtils.substringBetween(hrefFiled, "='", "'");
            if (StringUtils.isNotBlank(href)) {
                if (!StringUtils.startsWith(href, "http:")) {
                    href = "http://www.gsggfw.cn" + href;
                }
                String title = element.select("a").attr("title");
                String date = element.select("span").text();
                GGZYGanSuDataItem ggzyGanSuDataItem = new GGZYGanSuDataItem(href);
                ggzyGanSuDataItem.setUrl(href);
                ggzyGanSuDataItem.setTitle(title);
                ggzyGanSuDataItem.setDate(PageProcessorUtil.dataTxt(date));
                if (PageProcessorUtil.timeCompare(ggzyGanSuDataItem.getDate())) {
                    log.warn("{} is not the same day", ggzyGanSuDataItem.getUrl());
                } else {
                    Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    Elements elements = page.getHtml().getDocument().body().select("body > div.mod-content.clear > div.mod-cont-lft.clear > div.mod-arti-area > div.mod-arti-body");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    Elements elements1 = elements.select("iframe");
                    if (!elements1.isEmpty()) {
                        String iframeUrl = elements1.attr("src");
                        Page page1 = httpClientDownloader.download(new Request(iframeUrl), SiteUtil.get().setTimeOut(30000).toTask());
                        Element element1 = page1.getHtml().getDocument().body();
                        String formatContentAdd = PageProcessorUtil.formatElementsByWhitelist(element1);
                        formatContent = formatContent + formatContentAdd;
                    }
                    if (StringUtils.isNotBlank(formatContent)) {
                        ggzyGanSuDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyGanSuDataItem);
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
