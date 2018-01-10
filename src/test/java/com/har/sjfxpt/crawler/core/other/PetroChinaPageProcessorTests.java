package com.har.sjfxpt.crawler.core.other;

import com.har.sjfxpt.crawler.core.SpiderApplicationTests;
import com.har.sjfxpt.crawler.core.model.BidNewsSpider;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.other.PetroChinaPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Request;

@Slf4j
public class PetroChinaPageProcessorTests extends SpiderApplicationTests {

    @Autowired
    PetroChinaPageProcessor pageProcessor;

    @Autowired
    HBasePipeline pipeline;

    @Test
    public void test() {
        Request[] requests = SourceConfigAnnotationUtils.toRequests(pageProcessor.getClass());
        BidNewsSpider.create(pageProcessor).addPipeline(pipeline).addRequest(requests).run();
    }
}
