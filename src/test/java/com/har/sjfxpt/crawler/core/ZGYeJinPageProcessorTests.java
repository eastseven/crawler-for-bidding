package com.har.sjfxpt.crawler.core;

import com.har.sjfxpt.crawler.core.service.ProxyService;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import com.har.sjfxpt.crawler.zgyj.ZGYeJinPageProcessor;
import com.har.sjfxpt.crawler.zgyj.ZGYeJinPipeline;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.io.IOException;
import java.util.Date;

import static com.har.sjfxpt.crawler.zgyj.ZGYeJinSpiderLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/10/27.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ZGYeJinPageProcessorTests {

    @Autowired
    ZGYeJinPageProcessor zgYeJinPageProcessor;

    @Autowired
    ZGYeJinPipeline zgYeJinPipeline;

    String[] urls = {
            "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMoreZbs&xxposition=zbgg",
            "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMoreCgxx&xxposition=cgxx",
            "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMoreClarifypub&xxposition=cqgg",
            "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMorePub&xxposition=zhongbgg"
    };

    //爬去当日的数据
    @Test
    public void testZGYeJinPageProcessor() {

        Request[] requests = new Request[urls.length];

        String date=DateTime.now().toString("yyyy-MM-dd");

        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i], date, date);
        }

        HttpClientDownloader test = new HttpClientDownloader();
        test.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxy()));
        Spider.create(zgYeJinPageProcessor)
                .addRequest(requests)
                .addPipeline(zgYeJinPipeline)
                .setDownloader(test)
                .thread(4)
                .run();
    }

    //爬取13年至今的历史数据
    @Test
    public void testZGYeJinHistoryPageProcessor() {
        Request[] requests = new Request[urls.length];

        String date = new DateTime(new Date()).toString("yyyy-MM-dd");

        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i], "2013-01-01", date);
        }

        Spider.create(zgYeJinPageProcessor)
                .addRequest(requests)
                .addPipeline(zgYeJinPipeline)
                .thread(8)
                .run();
    }


    @Test
    public void testPageProcessor() {
        String url = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showZbsDetail&inviteid=40494E5EDE184218AF106A0DAD7FC9BF";
        log.info(">>> download {}", url);
        try {
            Document document = Jsoup.connect(url).timeout(60000).userAgent(SiteUtil.get().getUserAgent()).get();
            String html = document.html();
            Element root = document.body().select("body > div.main-news").first();
            String formatContent = PageProcessorUtil.formatElementsByWhitelist(root);
            String textContent = PageProcessorUtil.extractTextByWhitelist(root);

            log.info("formatContent=={}", formatContent);
            log.info("textContent=={}", textContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testTime() {
        String end = DateTime.now().minusDays(1).toString("yyyy-MM-dd");
        log.debug("end=={}", end);
    }

    @Autowired
    ProxyService proxyService;

    @Test
    public void testProxyService() {
        HttpClientDownloader test = new HttpClientDownloader();
        test.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxy()));
        String html = test.download("http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMoreZbs&xxposition=zbgg").get();
        log.debug("html=={}", html);
    }


}
