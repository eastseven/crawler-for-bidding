package com.har.sjfxpt.crawler.ggzy;

import com.har.sjfxpt.crawler.ccgp.*;
import com.har.sjfxpt.crawler.ggzy.service.ProxyService;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

    @Autowired
    ZhengFuCaiGouDownloader downloader;

    int num = Runtime.getRuntime().availableProcessors();

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
                //.addPipeline(pipeline)
                .addRequest(new Request(url)).thread(1).run();
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
    public void testDetailPage() throws Exception {
        String url = "http://www.ccgp.gov.cn/cggg/dfgg/gkzb/201710/t20171026_9052476.htm";
        Assert.assertNotNull(url);

        String summaryCssQuery = "div.vT_detail_main > div.table";
        String detailCssQuery = "div.vT_detail_main > div.vT_detail_content";
        Element element = Jsoup.connect(url).get().body();

        String summaryFormatContent = PageProcessorUtil.formatElementsByWhitelist(element.select(summaryCssQuery).first());
        String detailFormatContent = PageProcessorUtil.formatElementsByWhitelist(element.select(detailCssQuery).first());
        String summaryTextContent = PageProcessorUtil.extractTextByWhitelist(element.select(summaryCssQuery).first());
        String detailTextContent = PageProcessorUtil.extractTextByWhitelist(element.select(detailCssQuery).first());

        Path path = Paths.get("target", url);
        FileUtils.write(path.toFile(), summaryFormatContent + '\n', true);
        FileUtils.write(path.toFile(), detailFormatContent + '\n', true);
        FileUtils.write(path.toFile(), summaryTextContent + '\n', true);
        FileUtils.write(path.toFile(), detailTextContent + '\n', true);
    }

    @Test
    public void testFetchPageData() {
        List<PageData> pageDataList = pageDataRepository.findAll();
        Assert.assertFalse(pageDataList.isEmpty());
        final String prefix = "http://search.ccgp.gov.cn/bxsearch?searchtype=1&bidSort=&buyerName=&projectId=&pinMu=&bidType=&dbselect=bidx&kw=&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=";
        DateTime dt = DateTime.now();
        int days = 30;
        for (int day = 0; day < days; day++) {
            String date = null;
            String id = dt.minusDays(day).toString("yyyy:MM:dd");
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

            Spider.create(pageDataProcessor)
                    .addRequest(request)
                    .setExitWhenComplete(true);
        }

    }
}
