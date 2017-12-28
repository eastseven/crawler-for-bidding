package com.har.sjfxpt.crawler.core.pageprocessor;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.pipeline.DataItemDtoPipeline;
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

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/12.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GGZYShanXiPageProcessorTests {

    @Autowired
    GGZYShanXiPageProcessor ggzyShanXiPageProcessor;

    @Autowired
    DataItemDtoPipeline pipeline;

    final String url = "http://prec.sxzwfw.gov.cn/TenderProjectSx/ColTableInfoOther.do";

    @Test
    public void testShanXiPageProcessor() {

        //http://prec.sxzwfw.gov.cn/TenderProjectSx/noticeAndPublicity.do?huanJie=NOTICE
        //http://prec.sxzwfw.gov.cn/TenderProjectSx/ColTableInfoOther.do
        Request request = requestGenerator(url, "1day", "gcjs", "NOTICE");
        Spider.create(ggzyShanXiPageProcessor).addPipeline(pipeline)
                .addRequest(request)
                .thread(THREAD_NUM)
                .run();
    }

    @Test
    public void testAllShanXiPageProcessor() {
        Request[] requests = {
                requestGenerator(url, "1day", "gcjs", "NOTICE"),
                requestGenerator(url, "1day", "gcjs", "PUBLICITY"),
                requestGenerator(url, "1day", "zfcg", "NOTICE"),
                requestGenerator(url, "1day", "zfcg", "PUBLICITY"),
        };

        Spider.create(ggzyShanXiPageProcessor).addPipeline(pipeline).addRequest(requests).thread(THREAD_NUM).run();
    }

    /**
     *
     * @param url
     * @param date 1day 3day
     * @param projectType gcjs zfcg
     * @param huanJie NOTICE 交易公告 PUBLICITY 交易结果
     * @return
     */
    public static Request requestGenerator(String url, String date, String projectType, String huanJie) {
        Request request = new Request(url);
        Map<String, Object> pageParams = Maps.newHashMap();
        pageParams.put("projectName", "");
        pageParams.put("date", date);
        pageParams.put("begin_time", "");
        pageParams.put("end_time", "");
        pageParams.put("projectType", projectType);
        pageParams.put("huanJie", huanJie);
        pageParams.put("pageIndex", "1");
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
        request.putExtra("pageParams", pageParams);
        return request;
    }

}
