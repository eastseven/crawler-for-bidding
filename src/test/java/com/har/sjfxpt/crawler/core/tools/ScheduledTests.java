package com.har.sjfxpt.crawler.core.tools;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

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

}
