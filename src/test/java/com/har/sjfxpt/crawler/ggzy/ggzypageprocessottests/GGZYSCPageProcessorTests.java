package com.har.sjfxpt.crawler.ggzy.ggzypageprocessottests;

import com.har.sjfxpt.crawler.ggzyprovincial.ggzysc.GGZYSCPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzysc.GGZYSCPipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Spider;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2017/11/27.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GGZYSCPageProcessorTests {

    @Autowired
    GGZYSCPageProcessor GGZYSCPageProcessor;

    @Autowired
    GGZYSCPipeline GGZYSCPipeline;

    //mvn test -Dtest=testSCPageProcessor -Dspring.profiles.active=prod -Dapp.fetch.current.day=false
    @Test
    public void testSCPageProcessor() {

        String urls[] = {
                "http://www.scztb.gov.cn/Info/GetInfoListNew?keywords=&times=1&timesStart=&timesEnd=&province=&area=&businessType=project&informationType=&page=1&parm=" + DateTime.now().getMillis(),
                "http://www.scztb.gov.cn/Info/GetInfoListNew?keywords=&times=1&timesStart=&timesEnd=&province=&area=&businessType=purchase&informationType=&page=1&parm=" + DateTime.now().getMillis()
        };
        Spider.create(GGZYSCPageProcessor)
                .addPipeline(GGZYSCPipeline)
                .addUrl(urls)
                .thread(4)
                .run();
    }

    @Test
    public void testPageUtiles() {
        String content = "";
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

    @Test
    public void testSiChuan() throws Exception {
        String url = "http://www.scztb.gov.cn/Info/ProjectDetail/0_H5110008009003125001.html";
        Document document = Jsoup.parse(new URL(url), 60 * 1000);
        Element body = document.body();

        for (Element element : body.select("div.Nmds")) {
            boolean bln = StringUtils.contains(element.attr("style"), "block");
            if (!bln) continue;
            String content = element.select("input").attr("value");
            if (content.contains("<![CDATA[")) {
                Whitelist whitelist = Whitelist.relaxed();
                whitelist.removeTags("iframe");
                String html = StringUtils.substringBetween(content, "<![CDATA[", "]]");
                System.out.println(Jsoup.clean(html, whitelist));
            } else {
                Whitelist whitelist = Whitelist.relaxed();
                whitelist.removeTags("iframe");
                System.out.println(Jsoup.clean(content, whitelist));
            }

        }
    }

    @Test
    public void testTime() {
        long datimeNow = DateTime.now().getMillis();
        log.info("datimeNow=={}", datimeNow);
    }


}
