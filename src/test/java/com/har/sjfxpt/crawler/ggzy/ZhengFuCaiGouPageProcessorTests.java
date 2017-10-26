package com.har.sjfxpt.crawler.ggzy;

import com.har.sjfxpt.crawler.ccgp.*;
import com.har.sjfxpt.crawler.ggzy.downloader.HttpClientDownloaderExt;
import com.har.sjfxpt.crawler.ggzy.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
public class ZhengFuCaiGouPageProcessorTests extends SpiderApplicationTests {

    @Autowired
    ZhengFuCaiGouPageProcessor pageProcessor;

    @Autowired
    PageDataProcessor pageDataProcessor;

    @Autowired
    ZhengFuCaiGouPipeline pipeline;

    @Autowired
    ProxyService proxyService;

    @Autowired
    PageDataRepository pageDataRepository;

    @Test
    public void test() throws UnsupportedEncodingException {
        String url = "http://search.ccgp.gov.cn/bxsearch?searchtype=1&bidSort=&buyerName=&projectId=&pinMu=&bidType=&dbselect=bidx&kw=&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=";
        Assert.assertNotNull(url);

        String date = DateTime.now().toString("yyyy:MM:dd");
        date = URLEncoder.encode(date, "utf-8");
        String params = "&start_time=" + date + "&end_time=" + date + "&page_index=1";
        url = url + params;
        log.debug(">>> test {}", url);
        Spider.create(pageProcessor)
                .setDownloader(new HttpClientDownloaderExt())
                //.setDownloader(proxyService.getDownloader())
                .addPipeline(pipeline)
                .addRequest(new Request(url)).run();
    }

    @Test
    public void testPageData() throws Exception {
        Spider spider = Spider.create(pageDataProcessor).setDownloader(proxyService.getDownloader());
        Assert.assertNotNull(spider);
        // 7天
        DateTime df = DateTime.now().minusDays(1);
        int day = 7;
        for (int index = 0; index < day; index++) {
            String dateText = df.minusDays(index).toString("yyyy:MM:dd");
            String date = URLEncoder.encode(dateText, "utf-8");
            PageData pageData = new PageData();
            pageData.setDate(dateText);

            String url = "http://search.ccgp.gov.cn/bxsearch?searchtype=1&bidSort=&buyerName=&projectId=&pinMu=&bidType=&dbselect=bidx&kw=&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=";
            String params = "&start_time=" + date + "&end_time=" + date + "&page_index=1";
            url = url + params;
            Request request = new Request(url);
            pageData.setUrl(url);
            request.putExtra(PageData.class.getSimpleName(), pageData);

            spider.addRequest(request);
        }

        spider.thread(1).run();
    }
}
