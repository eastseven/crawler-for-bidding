package com.har.sjfxpt.crawler.core;

import com.har.sjfxpt.crawler.baowu.BaoWuPageProcessor;
import com.har.sjfxpt.crawler.baowu.BaoWuPipeline;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.baowu.BaoWuPageProcessor.*;
import static com.har.sjfxpt.crawler.baowu.BaoWuSpiderLauncher.requestGenerator;
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
    BaoWuPipeline baoWuPipeline;

    @Autowired
    HBasePipeline hBasePipeline;

    String[] urls = {
            "http://baowu.ouyeelbuy.com/baowu-shp/notice/purchaseMore",
            "http://baowu.ouyeelbuy.com/baowu-shp/notice/moreBiddingNoticeList",
            "http://baowu.ouyeelbuy.com/baowu-shp/notice/moreBiddingNoticeList1",
    };

    @Test
    public void testBaoWuPageprocessors() {
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i]);
        }
        Spider.create(baoWuPageProcessor)
                .addRequest(requests)
//                .addPipeline(baoWuPipeline)
                .thread(THREAD_NUM)
                .run();
    }

    @Test
    public void testAnnotationPageProcessors() {
        Assert.assertNotNull(baoWuPageProcessor);

        SourceModel sourceModel = new SourceModel();
        sourceModel.setUrl(SEED_URL2);
        sourceModel.setPost(true);
        sourceModel.setJsonPostParams(POST_PARAMS_03);
        Request request = sourceModel.createRequest();
        Spider.create(baoWuPageProcessor)
                .addRequest(request)
                .addPipeline(hBasePipeline)
                .run();
    }

    @Test
    public void testDate() {
        String date = "2017-12-22 00:00:00.0";
        log.info("date={}", PageProcessorUtil.dataTxt(date));
    }


}
