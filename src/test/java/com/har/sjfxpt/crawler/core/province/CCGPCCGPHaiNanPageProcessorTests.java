package com.har.sjfxpt.crawler.core.province;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ccgp.provincial.HaiNanPageProcessor;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.List;

/**
 * Created by Administrator on 2017/11/7.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CCGPCCGPHaiNanPageProcessorTests {

    @Autowired
    HaiNanPageProcessor haiNanPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    @Test
    public void testCCGPHaiNanAnnouncation() {
        List<SourceModel> list = SourceConfigAnnotationUtils.find(haiNanPageProcessor.getClass());
        List<Request> requests = Lists.newArrayList();
        for (SourceModel sourceModel : list) {
            Request request = sourceModel.createRequest();
            requests.add(request);
        }
        if (!requests.isEmpty()) {
            Spider.create(haiNanPageProcessor)
                    .addRequest(requests.toArray(new Request[requests.size()]))
                    .addPipeline(hBasePipeline)
                    .thread(8)
                    .run();
        } else {
            log.warn("request is empty!");
        }
    }


    @Test
    public void testString() {
        String txt = "四川恒鑫工程管理咨询有限公司关于码头租赁项目合同公示";

        String projectName = StringUtils.substringBeforeLast(txt, "-");

        String project = StringUtils.defaultString(projectName, "");

        log.debug("project=={}", project);
    }

}
