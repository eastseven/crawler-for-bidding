package com.har.sjfxpt.crawler.core.pageprocessor;

import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.ggzy.provincial.XZPageProcessor;
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
 * Created by Administrator on 2017/12/5.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class XZPageProcessorTests {

    @Autowired
    XZPageProcessor XZPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    @Test
    public void testAnnotation() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(XZPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(XZPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(hBasePipeline)
                .thread(8)
                .run();
    }


}
