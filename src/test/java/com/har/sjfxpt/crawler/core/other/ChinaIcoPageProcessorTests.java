package com.har.sjfxpt.crawler.core.other;

import com.har.sjfxpt.crawler.core.SpiderApplicationTests;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.model.BidNewsSpider;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.other.ChinaIcoPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Request;

import java.util.List;

@Slf4j
public class ChinaIcoPageProcessorTests extends SpiderApplicationTests {

    @Autowired ChinaIcoPageProcessor pageProcessor;

    @Autowired HBasePipeline pipeline;

    @Test
    public void test() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(pageProcessor.getClass());
        Assert.assertFalse(CollectionUtils.isEmpty(sourceModelList));
        sourceModelList.forEach(sourceModel -> log.debug(">>> {}", sourceModel));

        Request[] requests = sourceModelList.parallelStream().map(SourceModel::createRequest).toArray(Request[]::new);
        BidNewsSpider.create(pageProcessor).addRequest(requests).addPipeline(pipeline).thread(requests.length).run();
    }
}
