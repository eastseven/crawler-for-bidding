package com.har.sjfxpt.crawler.core.tools;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by Administrator on 2018/1/12.
 */
@Slf4j
public class ScheduledTests {

    @Value("${app.fetch.test}")
    boolean flags;

    @Test
    public void testShcheduled() {
        log.debug("begin");
    }

    @Scheduled(initialDelay = 10000, fixedRate = 60 * 1000)
    public void test() {
        if (flags) {
            for (int i = 0; i < 100; i++) {
                log.debug("i={}", i);
            }
        }
    }


}
