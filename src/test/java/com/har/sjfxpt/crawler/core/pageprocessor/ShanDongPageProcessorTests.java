package com.har.sjfxpt.crawler.core.pageprocessor;

import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.pipeline.MongoPipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.ggzy.provincial.ShanDongPageProcessor;
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
 * Created by Administrator on 2017/12/15.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShanDongPageProcessorTests {

    @Autowired
    ShanDongPageProcessor shanDongPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    @Autowired
    MongoPipeline mongoPipeline;

    @Test
    public void testAnnotation() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(shanDongPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(shanDongPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(mongoPipeline)
                .thread(8)
                .run();
    }

}
