package com.har.sjfxpt.crawler.core.province;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ccgp.ccgpsc.CCGPSiChuanPageProcessor;
import com.har.sjfxpt.crawler.ccgp.ccgpsc.CCGPSiChuanPipeline;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.Map;

/**
 * Created by Administrator on 2017/11/8.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CCGPSiChuanPageProcessorTests {

    @Autowired
    CCGPSiChuanPageProcessor ccgpSiChuanPageProcessor;

    @Autowired
    CCGPSiChuanPipeline ccgpSiChuanPipeline;

    @Autowired
    HBasePipeline hBasePipeline;

    @Test
    public void testCCGPSiChuanAnnotation() {
        Assert.assertNotNull(ccgpSiChuanPageProcessor);

        SourceModel sourceModel = new SourceModel();
        sourceModel.setUrl("http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=cggg&rp=25&page=1");
        sourceModel.setType("采购公告");
        Request request = sourceModel.createRequest();
        Spider.create(ccgpSiChuanPageProcessor)
                .addRequest(request)
                .addPipeline(hBasePipeline)
                .run();
    }

    @Test
    public void testString() {
        String text = "２　　１";
        log.debug("text=={}", StringUtils.removeAll(text, "　"));
    }

}
