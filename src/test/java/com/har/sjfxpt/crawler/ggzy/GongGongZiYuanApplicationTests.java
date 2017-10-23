package com.har.sjfxpt.crawler.ggzy;

import com.har.sjfxpt.crawler.ggzy.downloader.PageDownloader;
import com.har.sjfxpt.crawler.ggzy.model.DataItem;
import com.har.sjfxpt.crawler.ggzy.processor.GongGongZiYuanPageProcessor;
import com.har.sjfxpt.crawler.ggzy.repository.DataItemRepository;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanUtil.YYYYMMDD;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GongGongZiYuanApplicationTests {

    @Autowired
    SpiderLauncher launcher;

    @Autowired
    PageDownloader pageDownloader;

    @Autowired
    DataItemRepository repository;

    @Autowired
    GongGongZiYuanPageProcessor pageProcessor;

    @Test
    public void contextLoads() {
        Assert.assertNotNull(launcher);
        launcher.start();
    }

    @Test
    public void testStartByDate() {
        Assert.assertNotNull(launcher);
        String date = DateTime.now().minusDays(RandomUtils.nextInt(1, 80)).toString(YYYYMMDD);
        launcher.start(date);
    }

    @Test
    public void testFetchHistory() {
        Assert.assertNotNull(launcher);
        launcher.fetchHistory();
    }

    @Test
    public void testDownload() {
        Assert.assertNotNull(pageDownloader);
        DataItem dataItem = repository.findTopByHtmlIsNull();
        Assert.assertNotNull(dataItem);
        Assert.assertNull(dataItem.getHtml());
        pageDownloader.download(dataItem);

        Assert.assertNotNull(dataItem.getHtml());
    }

    @Test
    public void testDownloadAll() throws InterruptedException {
        Assert.assertNotNull(pageDownloader);
        pageDownloader.download();
        Thread.sleep(5000L);
    }

    @Test
    public void testPage() throws Exception {
        String url = "http://www.ggzy.gov.cn/information/html/b/210000/0202/201710/23/0021220ee1f022d8492c84760339ecd07e50.shtml";
        Assert.assertNotNull(url);

        url = "http://www.ggzy.gov.cn/information/html/b/320000/0104/201710/23/003285dabef517a443d8ab69f8e1e71e221c.shtml";

        Document document = Jsoup.connect(url).get();
        Element content = document.body().select("#mycontent").first();
        String formatContent = PageProcessorUtil.formatElementsByWhitelist(content);
        String textContent = PageProcessorUtil.extractTextByWhitelist(content);
        Assert.assertNotNull(formatContent);
        Assert.assertNotNull(textContent);

        log.debug("\n=== format ===\n{}\n", formatContent);
        log.debug("\n=== text   ===\n{}\n", textContent);
    }
}