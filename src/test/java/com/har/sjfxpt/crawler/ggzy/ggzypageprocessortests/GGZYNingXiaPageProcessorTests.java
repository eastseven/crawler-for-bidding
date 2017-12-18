package com.har.sjfxpt.crawler.ggzy.ggzypageprocessortests;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyningxia.GGZYNingXiaPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyningxia.GGZYNingXiaPipeline;
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

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;
import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyningxia.GGZYNingXiaSpiderLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/12/17.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYNingXiaPageProcessorTests {

    @Autowired
    GGZYNingXiaPageProcessor ggzyNingXiaPageProcessor;

    @Autowired
    GGZYNingXiaPipeline ggzyNingXiaPipeline;

    String[] urls = {
            "http://www.nxggzyjy.org/ningxiaweb/002/002001/002001001/1.html",
            "http://www.nxggzyjy.org/ningxiaweb/002/002001/002001002/1.html",
            "http://www.nxggzyjy.org/ningxiaweb/002/002001/002001003/1.html",

            "http://www.nxggzyjy.org/ningxiaweb/002/002002/002002001/1.html",
            "http://www.nxggzyjy.org/ningxiaweb/002/002002/002002002/1.html",
            "http://www.nxggzyjy.org/ningxiaweb/002/002002/002002003/1.html",
    };

    @Test
    public void testNingXiaPageProcessor() {
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i]);
        }
        Spider.create(ggzyNingXiaPageProcessor)
                .addRequest(requests)
                .addPipeline(ggzyNingXiaPipeline)
                .thread(THREAD_NUM)
                .run();
    }



    /**
     * ​​​​​​​​ss=\​​​​​​​​u200B
     */
    @Test
    public void outPut() {
        String url = "http://www.nxggzyjy.org/ningxiaweb/002/002002/002002003/1.html";
        String typeId = StringUtils.substringBetween(StringUtils.substringAfter(url, "002/"), "/", "/");
        log.info("typeId=={}", typeId);
    }


}
