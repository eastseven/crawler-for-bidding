package com.har.sjfxpt.crawler.ggzy.ggzypageprocessortests;

import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhn.GGZYHNPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhn.GGZYHNPipeline;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyhn.GGZYHNSpiderLauncher.requestGenerators;

/**
 * Created by Administrator on 2017/11/29.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYHNPageProcessorTests {

    @Autowired
    GGZYHNPageProcessor GGZYHNPageProcessor;

    @Autowired
    GGZYHNPipeline GGZYHNPipeline;

    String[] urls = {
            "http://www.ggzy.hi.gov.cn/ggzy/ggzy/jgzbgg/index_1.jhtml",
            "http://www.ggzy.hi.gov.cn/ggzy/ggzy/jgzbgs/index_1.jhtml",
            "http://www.ggzy.hi.gov.cn/ggzy/ggzy/cggg/index_1.jhtml",
            "http://www.ggzy.hi.gov.cn/ggzy/ggzy/cgzbgg/index_1.jhtml"
    };

    @Test
    public void test() {

        Request[] requests = new Request[urls.length];

        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerators(urls[i]);
        }

        Spider.create(GGZYHNPageProcessor)
                .addRequest(requests)
                .addPipeline(GGZYHNPipeline)
                .thread(4)
                .run();
    }

    @Test
    public void testHttpdown() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Page page = httpClientDownloader.download(new Request("http://www.ggzy.hi.gov.cn/ggzy/ggzy/cgzbgg/index_1.jhtml"), SiteUtil.get().toTask());
        log.info("html=={}", page.getHtml().getDocument().body().html());
    }


}