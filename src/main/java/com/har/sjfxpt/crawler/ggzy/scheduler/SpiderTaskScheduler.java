package com.har.sjfxpt.crawler.ggzy.scheduler;

import com.har.sjfxpt.crawler.ggzy.SpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.downloader.PageDownloader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author dongqi
 */
@Slf4j
@Component
@Profile({"dev", "test", "prod"})
public class SpiderTaskScheduler {

    @Autowired
    SpiderLauncher spiderLauncher;

    @Autowired
    PageDownloader pageDownloader;

    //第一次延迟10秒后执行，之后按fixedRate的规则每20分钟执行一次
    @Scheduled(initialDelay = 10000, fixedRate = 20 * 60 * 1000)
    public void test() {
        log.info(">>> start");
        spiderLauncher.start();

        log.info(">>> end");
    }

    //@Scheduled(initialDelay = 20000, fixedRate = 10 * 1000)
    public void downloadHistoryPageContent() {
        log.info(">>> download history page content start");
        pageDownloader.download(1, 10);

        log.info(">>> download history page content end");
    }
}
