package com.har.sjfxpt.crawler.ccgp.provincial;

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

/**
 * Created by Administrator on 2017/11/8.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.CCGPSC,
        sources = {
                @Source(url = "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=cggg&rp=25&page=1", type = "采购公告"),
                @Source(url = "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=jggg&rp=25&page=1", type = "结果公告"),
                @Source(url = "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=gzgg&rp=25&page=1", type = "更正公告"),

                @Source(url = "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=shiji_cggg1&rp=25&page=1", type = "资格预审公告"),
                @Source(url = "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=shiji_cggg&rp=25&page=1", type = "采购公告"),
                @Source(url = "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=shiji_jggg&rp=25&page=1", type = "中标公告"),
                @Source(url = "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=shiji_cjgg&rp=25&page=1", type = "成交公告"),
                @Source(url = "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=shiji_gzgg&rp=25&page=1", type = "更正公告"),
                @Source(url = "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=shiji_fblbgg&rp=25&page=1", type = "废标流标公告"),
                @Source(url = "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=shiji_qtgg&rp=25&page=1", type = "其他公告"),
        }
)
public class SiChuanPageProcessor implements BasePageProcessor {

    private HttpClientDownloader httpClientDownloader;

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
        String type = (String) page.getRequest().getExtra("type");
        String url = page.getUrl().toString();
        int num = Integer.parseInt(StringUtils.substringAfter(url, "page="));
        if (num == 1) {
            Elements pageNum = page.getHtml().getDocument().body().select("#QuotaList_paginate>span");
            int totalPageNum = Integer.parseInt(StringUtils.substringBetween(pageNum.text(), "页次：1/", "页"));
            int cycleNum = totalPageNum >= 40 ? 40 : totalPageNum;
            for (int i = 2; i <= cycleNum; i++) {
                String targetUrl = StringUtils.replace(url, "page=1", "page=" + i);
                Request request = new Request(targetUrl);
                request.putExtra("type", type);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        String type = (String) page.getRequest().getExtra("type");
        Elements elements = page.getHtml().getDocument().body().select("body > div.main > div.colsList > ul >li");
        if (elements.isEmpty()) {
            log.error("fetch error, elements is empty");
            return;
        }
        List<BidNewsOriginal> dataItems = parseContent(elements);
        dataItems.forEach(dataItem -> dataItem.setType(type));
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        for (Element a : items) {
            Elements target = a.select("a");
            String href = target.attr("href");
            String title = target.attr("title");
            String date = a.select("span").text();
            if (!href.contains("http://")) {
                href = "http://www.sczfcg.com" + href;
            }
            BidNewsOriginal ccgpSiChuanDataItem = new BidNewsOriginal(href, SourceCode.CCGPSC);
            ccgpSiChuanDataItem.setTitle(title);
            ccgpSiChuanDataItem.setDate(date);
            ccgpSiChuanDataItem.setType(title);
            ccgpSiChuanDataItem.setProvince("四川");

            long value = stringRedisTemplate.boundSetOps(KEY_URLS).add(href);
            if (value == 0L) {
                //重复数据
                log.debug("{} is duplication", href);
                continue;
            }
            try {
                Request request = new Request(href);
                Page page = httpClientDownloader.download(request, SiteUtil.get().setTimeOut(30000).toTask());
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
                    ccgpSiChuanDataItem.setFormatContent(formatContent);
                }
                dataItems.add(ccgpSiChuanDataItem);
            } catch (Exception e) {
                log.error("", e);
                log.error("url={}", href);
            }

        }
        return dataItems;
    }

}
