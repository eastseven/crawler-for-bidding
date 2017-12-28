package com.har.sjfxpt.crawler.core;

import com.har.sjfxpt.crawler.zgly.ZGLvYePageProcessor;
import com.har.sjfxpt.crawler.zgly.ZGLvYePipeline;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.THREAD_NUM;
import static com.har.sjfxpt.crawler.zgly.ZGLvYeSpiderLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/12/21.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ZGLvYePageProcessorTests {

    @Autowired
    ZGLvYePageProcessor zgLvYePageProcessor;

    @Autowired
    ZGLvYePipeline zgLvYePipeline;

    String[] urls = {
            "http://ec.chalieco.com/b2b/web/two/indexinfoAction.do?actionType=showMoreCgxx&xxposition=cgxx",
            "http://ec.chalieco.com/b2b/web/two/indexinfoAction.do?actionType=showMoreZbs&xxposition=zbgg",
            "http://ec.chalieco.com/b2b/web/two/indexinfoAction.do?actionType=showMoreClarifypub&xxposition=cqgg",
            "http://ec.chalieco.com/b2b/web/two/indexinfoAction.do?actionType=showMorePub&xxposition=zhongbgg"
    };

    @Test
    public void testZGLvYePageProcessors() {
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i], DateTime.now().toString("yyyy-MM-dd"), DateTime.now().toString("yyyy-MM-dd"));
        }
        Spider.create(zgLvYePageProcessor)
                .addRequest(requests)
                .addPipeline(zgLvYePipeline)
                .thread(THREAD_NUM)
                .run();
    }


}
