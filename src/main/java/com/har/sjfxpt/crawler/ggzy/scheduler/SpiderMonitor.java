package com.har.sjfxpt.crawler.ggzy.scheduler;

import com.har.sjfxpt.crawler.SpiderLauncher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author dongqi
 */
@Slf4j
@Component
@Profile({"test", "prod"})
public class SpiderMonitor {

    @Autowired
    ApplicationContext context;

    @Scheduled(initialDelay = 10000, fixedRate = 60 * 1000)
    public void monitor() {
        log.info("\n");
        context.getBean(SpiderLauncher.class).info();

        log.info("\n");
    }
}
