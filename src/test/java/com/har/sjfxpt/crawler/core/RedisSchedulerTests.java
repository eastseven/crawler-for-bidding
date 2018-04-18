package com.har.sjfxpt.crawler.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.scheduler.RedisScheduler;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisSchedulerTests {

    @Autowired
    RedisScheduler redisScheduler;

    @Test
    public void test() {
        Assert.assertNotNull(redisScheduler);
        log.debug("{}", redisScheduler);
    }
}
