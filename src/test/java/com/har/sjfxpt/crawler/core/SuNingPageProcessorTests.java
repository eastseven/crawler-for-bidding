package com.har.sjfxpt.crawler.core;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.suning.SuNingPageProcessor;
import com.har.sjfxpt.crawler.suning.SuNingPipeline;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

/**
 * Created by Administrator on 2017/11/13.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SuNingPageProcessorTests {

    @Autowired
    SuNingPageProcessor suNingPageProcessor;

    @Autowired
    SuNingPipeline suNingPipeline;

    @Test
    public void testSuNing() {

        String url = "http://zb.suning.com/bid-web/searchIssue.htm";

        Request request = new Request(url);

        Map<String, Object> params = Maps.newHashMap();

        params.put("issue.msgType", "m1");
        params.put("pageNum", "1");

        request.setMethod(HttpConstant.Method.POST);

        request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));

        request.putExtra("pageParams", params);

        Spider.create(suNingPageProcessor)
                .addRequest(request)
//                .addPipeline(suNingPipeline)
                .run();
    }


    @Test
    public void testDateTime() {
        DateTime dateTime = DateTime.now().minusDays(1);
        log.debug("dateTime=={}", dateTime.toString("yyyy-MM-dd"));
    }
}
