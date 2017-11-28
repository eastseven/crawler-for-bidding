package com.har.sjfxpt.crawler.ggzy.ggzyPageProcessotTests;

import com.har.sjfxpt.crawler.ggzyprovincial.ggzysc.ggzySCPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzysc.ggzySCPipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Spider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2017/11/27.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ggzySCPageProcessorTests {

    @Autowired
    ggzySCPageProcessor ggzyScPageProcessor;

    @Autowired
    ggzySCPipeline ggzySCPipeline;

    @Test
    public void testSCPageProcessor() {

        String urls[] = {
                "http://www.scztb.gov.cn/Info/GetInfoList?keywords=&times=1&timesStart=&timesEnd=&province=&area=&businessType=project&informationType=&page=1&parm=1511832393578",
                "http://www.scztb.gov.cn/Info/GetInfoList?keywords=&times=1&timesStart=&timesEnd=&province=&area=&businessType=purchase&informationType=&page=1&parm=1511832537059"
        };
        Spider.create(ggzyScPageProcessor)
                .addPipeline(ggzySCPipeline)
                .addUrl(urls)
                .thread(4)
                .run();
    }

    @Test
    public void testPageUtiles() {
        String content ="";
        Element element = new Element(content);
        log.debug("content=={}", content.replace("&nbsp;", ""));
    }

    @Test
    public void testUrlEcoder() throws UnsupportedEncodingException {
        String href = "/Info/ProjectDetailPurchase/1_珙委采谈[2017]23号.html\\";

        String encode = URLEncoder.encode(StringUtils.substringBefore(StringUtils.substringAfterLast(href, "/"), ".html"), "utf-8");

        String urlEncoder = "http://www.scztb.gov.cn" + StringUtils.substringBeforeLast(href, "/") + "/" + encode + ".html";

        log.debug("urlEncoder=={}", urlEncoder);
    }


}
