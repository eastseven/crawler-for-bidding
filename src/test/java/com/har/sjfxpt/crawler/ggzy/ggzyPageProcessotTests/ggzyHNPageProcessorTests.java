package com.har.sjfxpt.crawler.ggzy.ggzyPageProcessotTests;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhn.ggzyHNPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhn.ggzyHNPipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.Map;

import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyhn.ggzyHNSpiderLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/11/29.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ggzyHNPageProcessorTests {

    @Autowired
    ggzyHNPageProcessor ggzyHNPageProcessor;

    @Autowired
    ggzyHNPipeline ggzyHNPipeline;

    String[] urls = {
            "http://www.ggzy.hi.gov.cn/ggzy/jgzbgg/index_1.jhtml",
            "http://www.ggzy.hi.gov.cn/ggzy/jgzbgs/index_1.jhtml",
            "http://www.ggzy.hi.gov.cn/ggzy/cggg/index_1.jhtml",
            "http://www.ggzy.hi.gov.cn/ggzy/cgzbgg/index_1.jhtml"
    };

    @Test
    public void test() {

        Request[] requests = new Request[urls.length];

        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i]);
        }

        Spider.create(ggzyHNPageProcessor)
                .addRequest(requests)
                .addPipeline(ggzyHNPipeline)
                .thread(4)
                .run();
    }


}
