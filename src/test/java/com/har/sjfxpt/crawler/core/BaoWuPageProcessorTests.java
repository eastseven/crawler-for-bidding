package com.har.sjfxpt.crawler.core;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.baowu.BaoWuPageProcessor;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
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

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/22.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class BaoWuPageProcessorTests {

    @Autowired
    BaoWuPageProcessor baoWuPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    @Test
    public void testAnnotationPageProcessors() {
        List<SourceModel> list = SourceConfigAnnotationUtils.find(baoWuPageProcessor.getClass());
        List<Request> requests = Lists.newArrayList();
        for (SourceModel sourceModel : list) {
            Request request = sourceModel.createRequest();
            requests.add(request);
        }
        if (!requests.isEmpty()) {
            Spider.create(baoWuPageProcessor)
                    .addRequest(requests.toArray(new Request[requests.size()]))
                    .addPipeline(hBasePipeline)
                    .thread(8)
                    .run();
        } else {
            log.warn("request is empty!");
        }
    }

    @Test
    public void testDate() {
        String date = "2017-12-22 00:00:00.0";
        log.info("date={}", PageProcessorUtil.dataTxt(date));
    }


}
