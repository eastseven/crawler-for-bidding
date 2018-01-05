package com.har.sjfxpt.crawler.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.model.BidNewsSpider;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.suning.SuNingPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Request;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/13.
 */
@Slf4j
public class SuNingPageProcessorTests extends SpiderApplicationTests {

    @Autowired
    SuNingPageProcessor suNingPageProcessor;

    @Autowired
    HBasePipeline pipeline;

    @Test
    public void testPostParams() {
        Map<String, Object> params = Maps.newHashMap();

        params.put("issue.msgType", "m1");
        params.put("issue.updateStartDate", "");
        params.put("issue.updateEndDate", "");
        params.put("pageNum", "1");

        log.debug(">>> {}\n{}", params, JSON.toJSONString(params, SerializerFeature.UseSingleQuotes));
    }

    @Test
    public void test() {
        List<SourceModel> list = SourceConfigAnnotationUtils.find(suNingPageProcessor.getClass());
        Assert.assertTrue(CollectionUtils.isNotEmpty(list));

        Request[] requests = list.parallelStream().map(SourceModel::createRequest).toArray(Request[]::new);
        BidNewsSpider.create(suNingPageProcessor).addRequest(requests).addPipeline(pipeline).run();
    }

}
