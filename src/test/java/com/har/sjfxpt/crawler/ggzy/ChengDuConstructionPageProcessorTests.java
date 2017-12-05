package com.har.sjfxpt.crawler.ggzy;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.chengduconstruction.ChengDuConstructionPageProcessor;
import com.har.sjfxpt.crawler.chengduconstruction.ChengDuConstructionPipeline;
import lombok.extern.slf4j.Slf4j;
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
 * Created by Administrator on 2017/12/5.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ChengDuConstructionPageProcessorTests {

    @Autowired
    ChengDuConstructionPageProcessor chengDuConstructionPageProcessor;

    @Autowired
    ChengDuConstructionPipeline chengDuConstructionPipeline;

    @Test
    public void testChengDuPageProcessor() {

        String url = "http://tz.xmchengdu.gov.cn/zftz/newweb/AjaxProcess/IndexPageHandler.ashx";

        Request request = new Request(url);

        Map<String, Object> param = Maps.newHashMap();

        param.put("requestType", "getXMZTBXX");
        param.put("pageIndex", 1);
        param.put("pageSize", 31);
        param.put("areaCode", "");
        param.put("type", "");
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(param, "UTF-8"));
        request.putExtra("pageParams", param);

        Spider.create(chengDuConstructionPageProcessor)
                .addRequest(request)
                .addPipeline(chengDuConstructionPipeline)
                .thread(4)
                .run();
    }
}
