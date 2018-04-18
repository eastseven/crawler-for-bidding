package com.har.sjfxpt.crawler.core.pageprocessor;

import com.alibaba.fastjson.JSONObject;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.ggzy.provincial.HeBeiPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2018/1/8.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class HBPageProcessorTests {

    @Autowired
    HeBeiPageProcessor heBeiPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    @Test
    public void testPostParams() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(HeBeiPageProcessor.class);
        sourceModelList.forEach(sourceModel -> log.debug(">>> {}", JSONObject.toJSONString(sourceModel, true)));
        sourceModelList.forEach(sourceModel -> log.debug(">>> {}", JSONObject.toJSONString(sourceModel.createRequest(), true)));
    }

    @Test
    public void testAnnotation() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(heBeiPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        for (Request request : requestList) {
            log.debug("request={}", request);
        }
        Spider.create(heBeiPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(hBasePipeline)
                .thread(8)
                .run();
    }

}
