package com.har.sjfxpt.crawler.ggzy;

import com.har.sjfxpt.crawler.ggzy.downloader.PageDownloader;
import com.har.sjfxpt.crawler.ggzy.model.DataItem;
import com.har.sjfxpt.crawler.ggzy.processor.GongGongZiYuanPageProcessor;
import com.har.sjfxpt.crawler.ggzy.repository.DataItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;
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
public class SpiderApplicationTests {

    @Autowired
    GongGongZiYuanSpiderLauncher launcher;

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
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

}