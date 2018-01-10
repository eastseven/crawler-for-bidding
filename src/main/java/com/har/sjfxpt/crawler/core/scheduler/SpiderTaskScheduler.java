package com.har.sjfxpt.crawler.core.scheduler;

import com.har.sjfxpt.crawler.SpiderNewLauncher;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
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
     * 启动后10秒执行，5分钟一次
     */
    @Scheduled(initialDelay = 10000, fixedRate = 5 * 60 * 1000)
    public void fetch() {
        if (flag) {
            context.getBean(SpiderNewLauncher.class).start();
        }
    }

    @Scheduled(initialDelay = 10001, fixedRate = 30 * 1000)
    public void monitor() {
        context.getBean(SpiderNewLauncher.class).getSpiders().forEach((uuid, spider) -> {
            String dt = new DateTime(spider.getStartTime()).toString("yyyy-MM-dd HH:mm:ss");
            log.info(">>> spider info, uuid={}, status={}, start={}, thread alive={}, page count={}", uuid, spider.getStatus(), dt, spider.getThreadAlive(), spider.getPageCount());
        });
    }
}
