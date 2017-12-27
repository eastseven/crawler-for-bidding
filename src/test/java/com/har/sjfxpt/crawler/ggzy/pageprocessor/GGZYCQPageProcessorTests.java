package com.har.sjfxpt.crawler.ggzy.pageprocessor;

import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzycq.GGZYCQPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzycq.GGZYCQPipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

/**
 * Created by Administrator on 2017/11/28.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GGZYCQPageProcessorTests {

    @Autowired
    GGZYCQPageProcessor GGZYCQPageProcessor;

    @Autowired
    GGZYCQPipeline GGZYCQPipeline;

    @Test
    public void testCQPageProcessor() {
        String[] urls = {
                "http://www.cqggzy.com/web/services/PortalsWebservice/getInfoList?response=application/json&pageIndex=1&pageSize=18&siteguid=d7878853-1c74-4913-ab15-1d72b70ff5e7&categorynum=014005001&title=&infoC=&_=1511837748941",
                "http://www.cqggzy.com/web/services/PortalsWebservice/getInfoList?response=application/json&pageIndex=1&pageSize=18&siteguid=d7878853-1c74-4913-ab15-1d72b70ff5e7&categorynum=014001001&title=&infoC=&_=1511837779151"
        };
        Spider.create(GGZYCQPageProcessor)
                .addPipeline(GGZYCQPipeline)
                .addUrl(urls)
                .thread(4)
                .run();
    }

    @Test
    public void test() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Page page = httpClientDownloader.download(new Request("http://www.cqggzy.com/xxhz/014001/014001001/014001001010/20171128/46088835-a22a-4a24-8a91-061c41607c84.html"), SiteUtil.get().toTask());
        Elements elements = page.getHtml().getDocument().body().select("body > div:nth-child(4) > div > div.detail-block");
        String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
        if (formatContent.contains("<a>相关公告</a>")) {
            String real = StringUtils.trim(StringUtils.removeAll(formatContent, "<li>(.+?)</li>"));
            String removeSpace = StringUtils.removeAll(real, "\\s");
            log.debug("removeSpace=={}", removeSpace);
        }
    }

    @Test
    public void testTime() {
        String time = DateTime.now().toString(" HH:mm");
        log.debug("time=={}", time);
    }

    @Test
    public void testGGZYCQPage() throws Exception {
        String url = "http://www.cqggzy.com/xxhz/014005/014005001/20171205/514185232283561984.html";
        Elements elements = Jsoup.connect(url).get().select("#mainContent div.wrap-post");
        String html = PageProcessorUtil.formatElementsByWhitelist(elements.first());

        Document doc = Jsoup.parse(html);
        for (Element h : doc.select("h4")) {
            if (StringUtils.containsIgnoreCase(h.text(), "预算金额")) {
                log.info("\n{}, {}\n", h, h.children().text());
                break;
            }
        }
    }


}
