package com.har.sjfxpt.crawler.core.scheduler;

import com.har.sjfxpt.crawler.SpiderNewLauncher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author dongqi
 */
@Slf4j
@Component
//@Profile({"test", "prod"})
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

}
