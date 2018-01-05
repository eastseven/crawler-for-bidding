package com.har.sjfxpt.crawler.core;

import com.har.sjfxpt.crawler.core.model.BidNewsSpider;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.sgcc.StateGridPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URL;

/**
 * 国家电网
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StateGridTests {

    @Autowired
    StateGridPageProcessor pageProcessor;

    @Autowired
    HBasePipeline pipeline;

    @Test
    public void testPageProcessor() {
        Assert.assertNotNull(pageProcessor);

        BidNewsSpider.create(pageProcessor).addPipeline(pipeline)
                .addRequest(SourceConfigAnnotationUtils.toRequests(pageProcessor.getClass()))
                .run();
    }

    @Test
    public void testFormatContent() {
        String url = "http://ecp.sgcc.com.cn/html/news/014001007/53277.html";
        String html = null;
        try {
            Elements text = Jsoup.parse(new URL(url), 60 * 1000).body().select("div.article div.bot_list");
            html = PageProcessorUtil.formatElementsByWhitelist(text.first());
            log.debug("\n{}\n", html);
        } catch (IOException e) {
            log.error("{} fetch fail", url);
            log.error("", e);
        }
        Assert.assertNotNull(html);

        try {
            Elements text1 = Jsoup.parse(new URL("http://ecp.sgcc.com.cn/html/project/014002007/9990000000010211392.html"), 60 * 2000)
                    .body().select("div.article");

            Elements table = text1.select("table");
            log.debug("\n{}\n", table);
            html = PageProcessorUtil.formatElementsByWhitelist(text1.first());
            Elements t = Jsoup.parse(html).select("table");
            log.debug("\n{}\n", t.toString());
        } catch (IOException e) {
            log.error("{} fetch fail", url);
            log.error("", e);
        }
        Assert.assertNotNull(html);
    }
}
