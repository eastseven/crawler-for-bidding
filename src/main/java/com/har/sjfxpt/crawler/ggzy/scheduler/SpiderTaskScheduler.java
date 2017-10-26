package com.har.sjfxpt.crawler.ggzy.scheduler;

import com.har.sjfxpt.crawler.chinamobile.ChinaMobileSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.GongGongZiYuanSpiderLauncher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author dongqi
 */
@Slf4j
@Component
@Profile({"prod"})
public class SpiderTaskScheduler {

    @Value("${app.fetch.current.day:false}") boolean flag;

    @Autowired
    GongGongZiYuanSpiderLauncher gongGongZiYuanSpiderLauncher;

    @Autowired
    ChinaMobileSpiderLauncher chinaMobileSpiderLauncher;

    /**
     * 启动后10秒执行，5分钟一次
     */
    @Scheduled(initialDelay = 10000, fixedRate = 5 * 60 * 1000)
    public void fetchCurrentDay() {
        if (flag) {
            log.info(">>> start fetch ggzy");
            gongGongZiYuanSpiderLauncher.start();
        }
    }

    /**
     * 启动后20秒执行，10分钟一次
     */
    @Scheduled(initialDelay = 20000, fixedRate = 10 * 60 * 1000)
    public void fetchCurrentDay4CM() {
        if (flag) {
            log.info(">>> start fetch china mobile");
            chinaMobileSpiderLauncher.start();
        }
    }
}
