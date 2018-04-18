package com.har.sjfxpt.crawler.core.province;

import com.har.sjfxpt.crawler.ccgp.provincial.HuBeiPageProcessor;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
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
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2018/1/22.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class HuBeiPageProcessorTests {

    @Autowired
    HuBeiPageProcessor huBeiPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    @Test
    public void testHuBeiPageProcessorTests() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(huBeiPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest).collect(Collectors.toList());
        Spider.create(huBeiPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
//                .addPipeline(hBasePipeline)
                .thread(8)
                .run();
    }
}
