package com.har.sjfxpt.crawler.ggzy.pageprocessor;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyshanxi.GGZYShanXiPageProcessor;
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

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/12.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GGZYShanXiPageProcessorTests {

    @Autowired
    GGZYShanXiPageProcessor ggzyShanXiPageProcessor;

    @Test
    public void testShanXiPageProcessor() {
        //http://prec.sxzwfw.gov.cn/TenderProjectSx/noticeAndPublicity.do?huanJie=NOTICE
        //http://prec.sxzwfw.gov.cn/TenderProjectSx/ColTableInfoOther.do
        String url = "http://prec.sxzwfw.gov.cn/ThemeSX/TenderProjectSx/ColTableInfo";
        Request request = requestGenerator(url);
        Spider.create(ggzyShanXiPageProcessor)
                .addRequest(request)
                .thread(THREAD_NUM)
                .run();
    }

    public Request requestGenerator(String url) {
        Request request = new Request(url);
        Map<String, Object> pageParams = Maps.newHashMap();
        pageParams.put("projectName", "");
        pageParams.put("date", "1month");
        pageParams.put("begin_time", "");
        pageParams.put("end_time", "");
        pageParams.put("projectType", "zfcg");
        pageParams.put("huanJie", "PROJECT");
        pageParams.put("pageIndex", "1");
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
        request.putExtra("pageParams", pageParams);
        return request;
    }


}
