package com.har.sjfxpt.crawler.core.scheduler;

import com.har.sjfxpt.crawler.SpiderNewLauncher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class SpiderTaskScheduler {

    @Value("${app.fetch.current.day:false}")
    boolean flag;

    @Autowired
    ApplicationContext context;

    /**
     * 启动后10秒执行，30分钟一次
     */
    @Scheduled(initialDelay = 10000, fixedRateString = "${app.fetch.fixed.rate:1800000}")
    public void fetch() {
        if (flag) {
            log.info("spider start");
            context.getBean(SpiderNewLauncher.class).start();
        }
    }

}
