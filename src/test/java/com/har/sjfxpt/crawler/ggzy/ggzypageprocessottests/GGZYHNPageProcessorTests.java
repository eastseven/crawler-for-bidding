package com.har.sjfxpt.crawler.ggzy.ggzypageprocessottests;

import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhn.GGZYHNPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhn.GGZYHNPipeline;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyhn.GGZYHNSpiderLauncher.requestGenerators;

/**
 * Created by Administrator on 2017/11/29.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYHNPageProcessorTests {

    @Autowired
    GGZYHNPageProcessor GGZYHNPageProcessor;

    @Autowired
    GGZYHNPipeline GGZYHNPipeline;

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
            requests[i] = requestGenerators(urls[i]);
        }

        Spider.create(GGZYHNPageProcessor)
                .addRequest(requests)
                .addPipeline(GGZYHNPipeline)
                .thread(4)
                .run();
    }


}
