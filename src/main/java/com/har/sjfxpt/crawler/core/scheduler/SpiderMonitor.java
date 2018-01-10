package com.har.sjfxpt.crawler.core.scheduler;

import com.har.sjfxpt.crawler.SpiderNewLauncher;
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
//        context.getBean(SpiderNLauncher.class).info();

        log.info("\n");
    }

    @Scheduled(initialDelay = 10000, fixedRate = 60 * 1000)
    public void monitor2() {
        try {
            context.getBean(SpiderNewLauncher.class).getSpiders().forEach((uuid, spider) -> {
                log.info(">>> {}, {}, {}, {}, {}", uuid, spider.getStatus(), spider.getPageCount(), spider.getStartTime(), spider.getThreadAlive());
            });
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
