package com.har.sjfxpt.crawler.ggzy;

import com.har.sjfxpt.crawler.ccgp.*;
import com.har.sjfxpt.crawler.ggzy.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
public class ZhengFuCaiGouPageProcessorTests extends SpiderApplicationTests {

    @Autowired ZhengFuCaiGouSpiderLauncher spiderLauncher;

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

    @Autowired
    ZhengFuCaiGouDownloader downloader;

    int num = Runtime.getRuntime().availableProcessors();

    @Test
    public void testStart() {
        Assert.assertNotNull(spiderLauncher);
        spiderLauncher.getSpider().run();
    }

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
                .setDownloader(downloader)
                .addPipeline(pipeline)
                .addRequest(new Request(url)).run();
    }

    @Test
    public void testPageList() throws Exception {
        File file = Paths.get("target", "ccgp.html").toFile();
        Assert.assertNotNull(file);
        Elements elements = Jsoup.parse(file, "utf-8").body().select(ZhengFuCaiGouPageProcessor.cssQuery4List);
        List<ZhengFuCaiGouDataItem> dataItemList = pageProcessor.parseContent(elements);
        Assert.assertNotNull(dataItemList);
        Assert.assertFalse(dataItemList.isEmpty());
        dataItemList.forEach(System.out::println);
    }

    @Test
    public void testFetchPageData() {
        List<PageData> pageDataList = pageDataRepository.findAll(new Sort(Sort.Direction.ASC, "date"));
        Assert.assertFalse(pageDataList.isEmpty());

        PageData first = pageDataList.get(0);
        log.debug(">>> {}", first);

        DateTime start = new DateTime(first.getDate().replace(":", "-"));

        final String prefix = "http://search.ccgp.gov.cn/bxsearch?searchtype=1&bidSort=&buyerName=&projectId=&pinMu=&bidType=&dbselect=bidx&kw=&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=";
        int days = 14;

        Spider spider = Spider.create(pageDataProcessor).setExitWhenComplete(true);

        for (int day = 0; day < days; day++) {
            String date = null;
            String id = start.minusDays(day).toString("yyyy:MM:dd");
            try {
                date = URLEncoder.encode(id, "utf-8");
            } catch (UnsupportedEncodingException e) {
                log.error("", e);
            }
            String params = "&start_time=" + date + "&end_time=" + date + "&page_index=1";
            String url = prefix + params;

            PageData pageData = new PageData();
            pageData.setDate(id);
            pageData.setUrl(url);
            Request request = new Request(url);
            request.putExtra(PageData.class.getSimpleName(), pageData);

            spider.addRequest(request);
        }

        spider.run();
    }

    @Before
    public void init() {
        String url = "http://search.ccgp.gov.cn/bxsearch?searchtype=1&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=0&dbselect=bidx&kw=&start_time=2017%3A10%3A30&end_time=2017%3A10%3A30&timeType=0&displayZone=&zoneId=&pppStatus=0&agentName=";
        HttpClientDownloader downloader = new HttpClientDownloader();
        String html = downloader.download(url).getDocument().html();
        File file = Paths.get("src/test/data", "ccgp-list.html").toFile();

        try {
            if (!file.exists()) file.createNewFile();

            if (FileUtils.sizeOf(file) == 0L) {
                FileUtils.writeStringToFile(file, html, "utf-8");
            }

        } catch (IOException e) {
            log.error("", e);
        }
    }
}
