package com.har.sjfxpt.crawler.ggzy;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.sgcc.StateGridPageProcessor;
import com.har.sjfxpt.crawler.sgcc.StateGridSpiderLauncher;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static com.har.sjfxpt.crawler.sgcc.StateGridSpiderLauncher.WIN_URL;

/**
 * 国家电网
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StateGridTests {

    @Autowired
    StateGridSpiderLauncher spiderLauncher;

    @Autowired
    StateGridPageProcessor pageProcessor;

    @Test
    public void testStart() throws Exception {
        Assert.assertNotNull(spiderLauncher);
        spiderLauncher.start();
        Thread.sleep(10 * 1000L);
    }

    @Test
    public void testPageProcessor() {
        Assert.assertNotNull(pageProcessor);

        Map<String, String> types = Maps.newHashMap();
        //types.put("招标", BID_URL);
        types.put("中标", WIN_URL);

        for (String type : types.keySet()) {
            Request request = new Request(types.get(type));
            request.putExtra("type", type);
            Spider.create(pageProcessor).addRequest(request).run();
        }
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
