package com.har.sjfxpt.crawler.core.province;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ccgp.provincial.SiChuanPageProcessor;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.List;

/**
 * Created by Administrator on 2017/11/8.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SiChuanPageProcessorTests {

    @Autowired
    SiChuanPageProcessor siChuanPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    @Test
    public void testCCGPSiChuanAnnotation() {
        Assert.assertNotNull(siChuanPageProcessor);

        SourceModel sourceModel = new SourceModel();
        sourceModel.setUrl("http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=cggg&rp=25&page=1");
        sourceModel.setType("采购公告");
        Request request = sourceModel.createRequest();
        Spider.create(siChuanPageProcessor)
                .addRequest(request)
                .addPipeline(hBasePipeline)
                .run();
    }

    @Test
    public void testCCGPSiChuanPageProcessor() {
        List<SourceModel> list = SourceConfigAnnotationUtils.find(siChuanPageProcessor.getClass());
        List<Request> requests = Lists.newArrayList();
        for (SourceModel sourceModel : list) {
            Request request = sourceModel.createRequest();
            requests.add(request);
        }
        if (!requests.isEmpty()) {
            Spider.create(siChuanPageProcessor)
                    .addRequest(requests.toArray(new Request[requests.size()]))
                    .addPipeline(hBasePipeline)
                    .thread(8)
                    .run();

        } else {
            log.warn("request is empty!");
        }
    }

}
