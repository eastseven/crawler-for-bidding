package com.har.sjfxpt.crawler.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;

/**
 * Created by Administrator on 2017/12/29.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ZGTongXingPageProcessorTests {

    String[] urls = {
            "http://txzb.miit.gov.cn/DispatchAction.do?reg=denglu&pagesize=11",
            "http://txzb.miit.gov.cn/DispatchAction.do?reg=denglu&pagesize=11"
    };

    @Test
    public void testZGTongXingPageProcessor() {

    }

    public static Request requestGenerator(String url) {

        return null;
    }


}
