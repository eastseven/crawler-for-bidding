package com.har.sjfxpt.crawler.core.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

/**
 * @author dongqi
 *
 * http://blog.csdn.net/je_ge/article/details/53434321
 */
@Slf4j
@Component
public class DynamicScheduledTask implements SchedulingConfigurer {

    private static final String DEFAULT_CRON = "0/5 * * * * ?";
    private String cron = DEFAULT_CRON;

    public void setCron(String cron) {
        this.cron = cron;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        
    }
}
