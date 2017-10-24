package com.har.sjfxpt.crawler.ggzy.scheduler;

import com.har.sjfxpt.crawler.ggzy.GongGongZiYuanSpiderLauncher;
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
    GongGongZiYuanSpiderLauncher gongGongZiYuanSpiderLauncher;

    //第一次延迟10秒后执行，之后按fixedRate的规则每20分钟执行一次
    @Scheduled(initialDelay = 10000, fixedRate = 5 * 60 * 1000)
    public void test() {
        log.info(">>> start");
        gongGongZiYuanSpiderLauncher.start();

        log.info(">>> end");
    }
}
