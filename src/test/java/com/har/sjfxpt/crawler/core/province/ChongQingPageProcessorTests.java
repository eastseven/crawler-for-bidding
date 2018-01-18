package com.har.sjfxpt.crawler.core.province;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ccgp.provincial.CQPageProcessor;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Administrator on 2017/11/30.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ChongQingPageProcessorTests {


    @Autowired
    CQPageProcessor CQPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    @Test
    public void testCCGPCQAnnotation() {
        List<SourceModel> list = SourceConfigAnnotationUtils.find(CQPageProcessor.getClass());
        List<Request> requests = Lists.newArrayList();
        for (SourceModel sourceModel : list) {
            Request request = sourceModel.createRequest();
            requests.add(request);
        }
        if (!requests.isEmpty()) {
            Spider.create(CQPageProcessor)
                    .addRequest(requests.toArray(new Request[requests.size()]))
                    .addPipeline(hBasePipeline)
                    .thread(8)
                    .run();
        } else {
            log.warn("request is empty!");
        }
    }


    @Test
    public void testHref() throws UnsupportedEncodingException {
        String title = "重庆市永川区疾病预防控制中心电感耦合等离子体质谱仪采购(17A0749)预公示";
        String code = URLEncoder.encode(title.getBytes().toString(), "utf-8");
        String href = "https://www.cqgp.gov.cn/notices/detail/512574234195881984?title=" + code;
        log.debug("href=={}", href);
    }


}
