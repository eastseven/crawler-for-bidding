package com.har.sjfxpt.crawler.core.province;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ccgp.provincial.HBPageProcessor;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.pipeline.MongoPipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2018/1/17.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class HBPageProcessorTests {

    @Autowired
    HBPageProcessor HBPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    @Autowired
    MongoPipeline mongoPipeline;

    @Test
    public void testHebeiPageProcessor() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(HBPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(HBPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(mongoPipeline)
                .thread(8)
                .run();
    }

    @Test
    public void test() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("citycode", "130000000");
        params.put("cityname", "省本级");

        String json = JSONObject.toJSONString(params, SerializerFeature.UseSingleQuotes);
        log.debug("json={}", json);
    }


}
