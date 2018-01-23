package com.har.sjfxpt.crawler.core.scheduler;

import com.har.sjfxpt.crawler.SpiderNewLauncher;
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
@Profile({"test", "prod"})
public class SpiderTaskScheduler {

    @Value("${app.fetch.current.day:false}")
    private boolean flag;

    @Autowired
    private SpiderNewLauncher launcher;

    /**
     * 启动后10秒执行，30分钟一次
     */
    @Scheduled(initialDelay = 10000L, fixedRateString = "${app.fetch.fixed.rate:1800000}")
    public void fetch() {
        if (flag) {
            log.info("spider start");
            launcher.start();
        }
    }

    @Scheduled(initialDelay = 60 * 1000L, fixedRate = 60 * 1000L)
    public void log() {
        launcher.saveSpiderLogs();
    }
}
