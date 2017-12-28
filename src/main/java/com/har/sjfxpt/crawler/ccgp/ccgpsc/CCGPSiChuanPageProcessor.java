package com.har.sjfxpt.crawler.ccgp.ccgpsc;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/11/8.
 */
@Slf4j
@Component
public class CCGPSiChuanPageProcessor implements BasePageProcessor {

    private HttpClientDownloader httpClientDownloader;

    final static String PAGE_PARAMS = "pageParams";

    final String KEY_URLS = "ccgp_sichuan";

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public void process(Page page) {

        handlePaging(page);

        handleContent(page);

    }

    @Override
    public Site getSite() {
        httpClientDownloader = new HttpClientDownloader();
        return SiteUtil.get().setSleepTime(60 * 1000);
    }

    @Override
    public void handlePaging(Page page) {
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        String url = page.getUrl().toString();
        int num = Integer.parseInt(StringUtils.substringAfter(url, "page="));
        log.debug("pageNum=={}", num);
        if (num == 1) {
            Elements pageNum = page.getHtml().getDocument().body().select("#QuotaList_paginate>span");
            int totalPageNum = Integer.parseInt(StringUtils.substringBetween(pageNum.text(), "页次：1/", "页"));
            log.debug("totalPageNum=={}", totalPageNum);
            if (totalPageNum >= 40) {
                for (int i = 2; i <= 40; i++) {
                    String targetUrl = StringUtils.replace(url, "page=1", "page=" + i);
                    Request request = new Request(targetUrl);
                    request.putExtra(PAGE_PARAMS, pageParams);
                    page.addTargetRequest(request);
                }
            } else {
                for (int i = 2; i <= totalPageNum; i++) {
                    String targetUrl = StringUtils.replace(url, "page=1", "page=" + i);
                    Request request = new Request(targetUrl);
                    request.putExtra(PAGE_PARAMS, pageParams);
                    page.addTargetRequest(request);
                }
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        Elements elements = page.getHtml().getDocument().body().select("body > div.main > div.colsList > ul >li");
        if (elements.isEmpty()) {
            log.error("fetch error, elements is empty");
            return;
        }
        String type = (String) pageParams.get("type");
        List<CCGPSiChuanDataItem> dataItems = parseContent(elements);
        dataItems.forEach(dataItem -> dataItem.setType(type));
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<CCGPSiChuanDataItem> dataItems = Lists.newArrayList();
        for (Element a : items) {
            Elements target = a.select("a");
            String href = target.attr("href");
            String title = target.attr("title");
            String date = a.select("span").text();
            if (!href.contains("http://")) {
                href = "http://www.sczfcg.com" + href;
            }
            CCGPSiChuanDataItem ccgpSiChuanDataItem = new CCGPSiChuanDataItem(href);
            ccgpSiChuanDataItem.setTitle(title);
            ccgpSiChuanDataItem.setDate(date);
            ccgpSiChuanDataItem.setUrl(href);
            ccgpSiChuanDataItem.setType(title);

            long value = stringRedisTemplate.boundSetOps(KEY_URLS).add(href);
            if (value == 0L) {
                //重复数据
                log.debug("{} is duplication", href);
            } else {
                Request request = new Request(href);
                Page page = httpClientDownloader.download(request, SiteUtil.get().setTimeOut(30000).toTask());
                try {
                    String html = page.getHtml().getDocument().html();
                    Element element = page.getHtml().getDocument().body();
                    Elements dateDetailElement = element.select("#myPrintArea > div >span");
                    String dateDetail = StringUtils.substringAfter(dateDetailElement.text(), "系统发布时间：");
                    if (StringUtils.isNotBlank(dateDetail)) {
                        ccgpSiChuanDataItem.setDate(dateDetail);
                    }
                    Element formatContentHtml = element.select("#myPrintArea").first();
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(formatContentHtml);
                    if (StringUtils.isNotBlank(html)) {
                        ccgpSiChuanDataItem.setHtml(html);
                        ccgpSiChuanDataItem.setFormatContent(formatContent);
                    }
                    dataItems.add(ccgpSiChuanDataItem);
                } catch (Exception e) {
                    log.debug("url=={}", request.getUrl());
                    log.debug("e", e);
                }
            }

        }
        return dataItems;
    }

}
