package com.har.sjfxpt.crawler.ggzy;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.baowu.BaoWuPageProcessor;
import com.har.sjfxpt.crawler.baowu.BaoWuPipeline;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

import static com.har.sjfxpt.crawler.baowu.BaoWuSpiderLauncher.requestGenerator;
import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;

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
                .addPipeline(baoWuPipeline)
                .thread(THREAD_NUM)
                .run();
    }

    @Test
    public void testDate() {
        String date = "2017-12-22 00:00:00.0";
        log.info("date={}", PageProcessorUtil.dataTxt(date));
    }


}
