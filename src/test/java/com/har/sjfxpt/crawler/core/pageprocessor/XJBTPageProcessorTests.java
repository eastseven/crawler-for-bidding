package com.har.sjfxpt.crawler.core.pageprocessor;

import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.ggzy.provincial.XJBTPageProcessor;
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
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/12/18.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class XJBTPageProcessorTests {

    @Autowired
    XJBTPageProcessor XJBTPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

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
    public void testUrlType() {
        String date = "[2017-12-14]";
        if (date.contains("[")) {
            date = StringUtils.substringBetween(date, "[", "]");
        }
        log.info("date=={}", PageProcessorUtil.dataTxt(date));
    }

    @Test
    public void testAnnotation() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(XJBTPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(XJBTPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(hBasePipeline)
                .thread(8)
                .run();
    }
}
