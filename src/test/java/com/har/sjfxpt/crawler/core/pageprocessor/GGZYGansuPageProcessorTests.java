package com.har.sjfxpt.crawler.core.pageprocessor;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.model.BidNewsSpider;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.ggzy.provincial.GanSuPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/12/17.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYGansuPageProcessorTests {

    @Autowired
    GanSuPageProcessor ganSuPageProcessor;


    @Autowired
    HBasePipeline hBasePipeline;

    @Test
    public void test() {
        BidNewsSpider.create(ganSuPageProcessor)
                .addRequest(SourceConfigAnnotationUtils.toRequests(ganSuPageProcessor.getClass()))
                .addPipeline(hBasePipeline)
                .run();
    }

    @Test
    public void testggzyGanSuAnnotation() {
        List<SourceModel> list = SourceConfigAnnotationUtils.find(ganSuPageProcessor.getClass());
        List<Request> requestList = list.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        log.debug("request={}", requestList);
        Spider.create(ganSuPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(hBasePipeline)
                .thread(8)
                .run();
    }

    @Test
    public void testDownPage() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        String href = "http://www.gsggfw.cn/w/bid/bidDealAnnounce/7730/details.html";
        Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
        Elements elements = page.getHtml().getDocument().body().select("body > div.mod-content.clear > div.mod-cont-lft.clear > div.mod-arti-area > div.mod-arti-body");
        String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
        Elements elements1 = elements.select("iframe");
        if (!elements1.isEmpty()) {
            String iframeUrl = elements1.attr("src");
            log.debug("iframeUrl={}", iframeUrl);
            Page page1 = httpClientDownloader.download(new Request(iframeUrl), SiteUtil.get().setTimeOut(30000).toTask());
            Element element1 = page1.getHtml().getDocument().body();
            String formatContentAdd = PageProcessorUtil.formatElementsByWhitelist(element1.html());
            formatContent = formatContent + formatContentAdd;
        }
        if (StringUtils.isNotBlank(formatContent)) {
            log.debug("formatContent={}", formatContent);
        }
    }

    @Test
    public void testBlock() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        String href = "http://www.gsggfw.cn/w/bid/winResultAnno/60950/details.html";
        Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
        Elements elements = page.getHtml().getDocument().body().select("body > div.mod-content.clear > div.mod-cont-lft.clear > div.mod-arti-area > div.mod-arti-body");
        log.debug("first={}", elements.first());
        log.debug("{}", elements.first().isBlock());
    }
}
