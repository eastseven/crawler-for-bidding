package com.har.sjfxpt.crawler.ggzy;

import com.har.sjfxpt.crawler.ccgp.PageDataProcessor;
import com.har.sjfxpt.crawler.ccgp.PageDataRepository;
import com.har.sjfxpt.crawler.ccgp.ZhengFuCaiGouPageProcessor;
import com.har.sjfxpt.crawler.ccgp.ZhengFuCaiGouPipeline;
import com.har.sjfxpt.crawler.ggzy.downloader.HttpClientDownloaderExt;
import com.har.sjfxpt.crawler.ggzy.service.ProxyService;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;

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

    @Autowired
    ExecutorService executorService;

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
                .setDownloader(proxyService.getDownloader("120.26.162.31:3333"))
                .addPipeline(pipeline)
                .addRequest(new Request(url)).run();
    }

    @Test
    public void testPageData() throws Exception {
        // 7天


        Thread.sleep(60*1000);
    }

    @Test
    public void testDetailPage() throws Exception {
        String url = "http://www.ccgp.gov.cn/cggg/dfgg/gkzb/201710/t20171026_9052476.htm";
        Assert.assertNotNull(url);

        String summaryCssQuery = "div.vT_detail_main > div.table";
        String detailCssQuery = "div.vT_detail_main > div.vT_detail_content";
        Element element = Jsoup.connect(url).get().body();

        String summaryFormatContent = PageProcessorUtil.formatElementsByWhitelist(element.select(summaryCssQuery).first());
        String detailFormatContent  = PageProcessorUtil.formatElementsByWhitelist(element.select(detailCssQuery).first());
        String summaryTextContent   = PageProcessorUtil.extractTextByWhitelist(element.select(summaryCssQuery).first());
        String detailTextContent    = PageProcessorUtil.extractTextByWhitelist(element.select(detailCssQuery).first());

        Path path = Paths.get("target", url);
        FileUtils.write(path.toFile(), summaryFormatContent+'\n', true);
        FileUtils.write(path.toFile(), detailFormatContent+'\n', true);
        FileUtils.write(path.toFile(), summaryTextContent+'\n', true);
        FileUtils.write(path.toFile(), detailTextContent+'\n', true);
    }
}
