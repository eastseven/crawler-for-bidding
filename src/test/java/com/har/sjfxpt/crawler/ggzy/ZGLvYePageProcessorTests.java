package com.har.sjfxpt.crawler.ggzy;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.zgly.ZGLvYePageProcessor;
import com.har.sjfxpt.crawler.zgly.ZGLvYePipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;

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

    public static Request requestGenerator(String url, String beginDate, String endDate) {
        Request request = new Request(url);
        String typeField = StringUtils.substringAfter(url, "xxposition=");
        Map<String, Object> pageParams = Maps.newHashMap();
        switch (typeField) {
            case "cgxx":
                pageParams.put("currpage", "1");
                pageParams.put("xxposition", "cgxx");
                pageParams.put("xxmc", "");
                pageParams.put("fbrq1", beginDate);
                pageParams.put("fbrq2", endDate);
                pageParams.put("type", "采购信息");
                break;
            case "zbgg":
                pageParams.put("currpage", "1");
                pageParams.put("xxposition", "zbgg");
                pageParams.put("zbmc", "");
                pageParams.put("sbsj1", beginDate);
                pageParams.put("sbsj2", endDate);
                pageParams.put("type", "采购公告");
                break;
            case "cqgg":
                pageParams.put("currpage", "1");
                pageParams.put("xxposition", "cqgg");
                pageParams.put("pubdesc", "");
                pageParams.put("audittime", beginDate);
                pageParams.put("audittime2", endDate);
                pageParams.put("type", "变更公告");
                break;
            case "zhongbgg":
                pageParams.put("currpage", "1");
                pageParams.put("xxposition", "zhongbgg");
                pageParams.put("pubdesc", "");
                pageParams.put("releasedate1", beginDate);
                pageParams.put("releasedate2", endDate);
                pageParams.put("type", "结果公告");
                break;
        }
        request.putExtra("pageParams", pageParams);
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
        return request;
    }
}
