package com.har.sjfxpt.crawler.core;

import com.har.sjfxpt.crawler.ccgp.*;
import com.har.sjfxpt.crawler.core.service.DataItemService;
import com.har.sjfxpt.crawler.core.service.ProxyService;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanUtil.YYYYMMDD;

@Slf4j
public class ZhengFuCaiGouPageProcessorTests extends SpiderApplicationTests {

    @Autowired
    ZhengFuCaiGouSpiderLauncher spiderLauncher;

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
        Page<PageData> pageDataList = pageDataRepository.findAll(new PageRequest(0, 10, Sort.Direction.ASC, "date"));
        Assert.assertTrue(pageDataList.hasContent());
        pageDataList.forEach(System.out::println);

        DateTime start = new DateTime("2013-01-01");
        DateTime end = DateTime.now();
        Duration duration = new Duration(start, end);

        for (int day = 0; day < duration.toStandardDays().getDays(); day++) {
            String date = start.plusDays(day).toString(YYYYMMDD).replace("-", ":");

            boolean exists = pageDataRepository.exists(date);
            if (!exists) {
                log.warn(">>> {}", date);
            }
        }
    }

    //    @Before
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

    @Test
    public void fixPageData() {
        PageRequest request = new PageRequest(0, 10);
        Page<PageData> pager = pageDataRepository.findAll(request);
        Assert.assertTrue(pager.hasContent());

        for (int page = 0; page < pager.getTotalPages(); page++) {
            Page<PageData> p = pageDataRepository.findAll(new PageRequest(page, 10));
            p.forEach(pageData -> pageData.setDateLong(Long.parseLong(pageData.getDate().replace(":", ""))));
            pageDataRepository.save(p);
            log.debug("fix {}, {}", page, p.getSize());
        }
    }


    @Autowired
    StringRedisTemplate stringRedisTemplate;

    final String names = "ccgp_history_fail_urls";

    public static final String cssQuery4List = "div.vT_z div.vT-srch-result div.vT-srch-result-list-con2 div.vT-srch-result-list ul.vT-srch-result-list-bid li";

    @Autowired
    ZhengFuCaiGouRepository repository;

    @Autowired
    DataItemService dataItemService;

    @Test
    public void getRedisUrl() {
        long total = stringRedisTemplate.boundSetOps(names).size();
        for (int i = 0; i < 3; i++) {
            String tabulationUrl = (String) stringRedisTemplate.boundSetOps(names).pop();
            log.debug("total=={},tabulationUrl=={}", total, tabulationUrl);
            Request request = new Request(tabulationUrl);
            downloader.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxies()));
            us.codecraft.webmagic.Page page = downloader.download(request, SiteUtil.get().toTask());
            Document document = page.getHtml().getDocument();
            Elements elements = document.body().select(cssQuery4List);
            List<ZhengFuCaiGouDataItem> dataItemList = pageProcessor.parseContent(elements);
            if (!dataItemList.isEmpty()) {
                repository.save(dataItemList);
            }
        }
    }


}
