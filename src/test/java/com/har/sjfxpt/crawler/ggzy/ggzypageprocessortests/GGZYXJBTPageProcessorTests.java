package com.har.sjfxpt.crawler.ggzy.ggzypageprocessortests;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyxjbt.GGZYXJBTPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyxjbt.GGZYXJBTPipeline;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;
import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyxjbt.GGZYXJBTSpiderLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/12/18.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYXJBTPageProcessorTests {

    @Autowired
    GGZYXJBTPageProcessor ggzyxjbtPageProcessor;

    @Autowired
    GGZYXJBTPipeline ggzyxjbtPipeline;

    String[] urls = {
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001002/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001003/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001004/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001005/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001006/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001007/?Paging=1",

            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002006/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002002/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002003/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002004/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002005/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002007/?Paging=1",
    };

    @Test
    public void testGGZYXJBTPageProcessor() {
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i]);
        }
        Spider.create(ggzyxjbtPageProcessor)
                .addRequest(requests)
                .thread(THREAD_NUM)
                .addPipeline(ggzyxjbtPipeline)
                .run();
    }



    @Test
    public void testUrlType() {
        String date = "[2017-12-14]";
        if(date.contains("[")){
            date=StringUtils.substringBetween(date,"[","]");
        }
        log.info("date=={}", PageProcessorUtil.dataTxt(date));
    }
}
