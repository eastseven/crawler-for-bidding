package com.har.sjfxpt.crawler.ggzy;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.zgyj.ZGYeJinPageProcessor;
import com.har.sjfxpt.crawler.zgyj.ZGYeJinPipeline;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

/**
 * Created by Administrator on 2017/10/27.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ZGYeJinPageProcessorTests {

    @Autowired
    ZGYeJinPageProcessor zgYeJinPageProcessor;

    @Autowired
    ZGYeJinPipeline zgYeJinPipeline;


    @Test
    public void testZGYeJinPageProcessor() {
        String[] urls = {
                "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMoreZbs&xxposition=zbgg",
                "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMoreCgxx&xxposition=cgxx",
                "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMoreClarifypub&xxposition=cqgg",
                "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMorePub&xxposition=zhongbgg"
        };

        Request[] requests = new Request[urls.length];

        for (int i = 0; i < urls.length; i++) {
            requests[i] = this.requestGenerator(urls[i]);
        }

        Spider.create(zgYeJinPageProcessor)
                .addRequest(requests)
                .addPipeline(zgYeJinPipeline)
                .thread(8)
                .run();
    }


    //判断request
    public Request requestGenerator(String url) {
        Request request = new Request(url);
        Map<String, Object> params = Maps.newHashMap();
        if (url.contains("zbgg")) {
            params.put("actionType", "showMoreZbs");
            params.put("xxposition", "zbgg");
            params.put("currpage", 1);
            params.put("xxposition", "zbgg");
            params.put("sbsj1", "2017-10-27");
            params.put("sbsj2", "2017-10-27");
            params.put("type", "采购公告");
            request.setMethod(HttpConstant.Method.POST);
            request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
            request.putExtra("pageParams", params);
        }
        if (url.contains("cgxx")) {
            params.put("actionType", "showMoreCgxx");
            params.put("xxposition", "cgxx");
            params.put("currpage", 1);
            params.put("xxposition", "cgxx");
            params.put("fbrq1", "2017-10-27");
            params.put("fbrq2", "2017-10-27");
            params.put("type", "采购信息");
            request.setMethod(HttpConstant.Method.POST);
            request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
            request.putExtra("pageParams", params);
        }
        if (url.contains("cqgg")) {
            params.put("actionType", "showMoreClarifypub");
            params.put("xxposition", "cqgg");
            params.put("currpage", 1);
            params.put("xxposition", "cqgg");
            params.put("audittime", "2017-10-27");
            params.put("audittime2", "2017-10-27");
            params.put("type", "变更公告");
            request.setMethod(HttpConstant.Method.POST);
            request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
            request.putExtra("pageParams", params);
        }
        if (url.contains("zhongbgg")) {
            params.put("actionType", "showMorePub");
            params.put("xxposition", "zhongbgg");
            params.put("currpage", 1);
            params.put("xxposition", "cgxx");
            params.put("releasedate1", "2017-10-27");
            params.put("releasedate2", "2017-10-27");
            params.put("type", "结果公告");
            request.setMethod(HttpConstant.Method.POST);
            request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
            request.putExtra("pageParams", params);
        }

        return request;
    }

}
