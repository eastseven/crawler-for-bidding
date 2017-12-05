package com.har.sjfxpt.crawler.ggzy.ggzypageprocessottests;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyxz.GGZYXZPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyxz.GGZYXZPipeline;
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

import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyxz.GGZYXZSpiderLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/12/5.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYXZPageProcessorTests {


    @Autowired
    GGZYXZPageProcessor ggzyxzPageProcessor;

    @Autowired
    GGZYXZPipeline ggzyxzPipeline;

    @Test
    public void testGGZYXZPageProcessor() {
        String[] urls = {
                "http://www.xzggzy.gov.cn:9090/zbzsgg/index_1.jhtml",
                "http://www.xzggzy.gov.cn:9090/jyjggg/index_1.jhtml",
                "http://www.xzggzy.gov.cn:9090/zbwjcq/index_1.jhtml",
                "http://www.xzggzy.gov.cn:9090/zgysjg/index_1.jhtml",

                "http://www.xzggzy.gov.cn:9090/cggg/index_1.jhtml",
                "http://www.xzggzy.gov.cn:9090/zbgg/index_1.jhtml",
                "http://www.xzggzy.gov.cn:9090/cght/index_1.jhtml",
                "http://www.xzggzy.gov.cn:9090/gzsx/index_1.jhtml"
        };
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i]);
        }
        Spider.create(ggzyxzPageProcessor)
                .addRequest(requests)
                .addPipeline(ggzyxzPipeline)
                .thread(4)
                .run();
    }



}
