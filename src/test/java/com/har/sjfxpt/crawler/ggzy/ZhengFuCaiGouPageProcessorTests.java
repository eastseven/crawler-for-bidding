package com.har.sjfxpt.crawler.ggzy;

import com.har.sjfxpt.crawler.ccgp.*;
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
        String params = "&start_time=2017%3A10%3A23&end_time=2017%3A10%3A23&page_index=1";

        Request request = new Request(url + params);

        String date = DateTime.now().toString("yyyy:MM:dd");
        log.debug("date={}, {}", date, URLEncoder.encode(date, "utf-8"));
        Spider.create(pageProcessor)
                .setDownloader(proxyService.getDownloader())
                .addPipeline(pipeline)
                .addRequest(request).thread(10).run();
    }

    @Test
    public void testPageData() throws Exception {
        Spider spider = Spider.create(pageDataProcessor).setDownloader(proxyService.getDownloader());
        Assert.assertNotNull(spider);
        // 7天
        DateTime df = DateTime.now().minusDays(1);
        int day = 30;
        for (int index = 0; index < day; index++) {
            String dateText = df.minusDays(index).toString("yyyy:MM:dd");
//            if (pageDataRepository.exists(dateText)) {
//                log.warn(">>> {} exists", dateText);
//                continue;
//            }
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
