package com.har.sjfxpt.crawler.core.pageprocessor;

import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyfujian.GGZYFuJianPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
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
 * Created by Administrator on 2017/12/12.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYFuJianPageProcessorTests {

    @Autowired
    GGZYFuJianPageProcessor ggzyFuJianPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    @Test
    public void testGGZYFuJianAnnotation() {
        List<SourceModel> list = SourceConfigAnnotationUtils.find(ggzyFuJianPageProcessor.getClass());

        List<Request> requestList = list.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        log.debug("requestList={}", requestList);
        Spider.create(ggzyFuJianPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(hBasePipeline)
                .run();
    }


    @Test
    public void testTime() {
        String time = "2017-12-13T10:55:44";
//        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
//        DateTime dateTime = DateTime.parse(time, dateTimeFormat);
//        log.info("time=={}",dateTime.toString("yyyy-MM-dd HH:mm"));
        log.info("time=={}", DateTime.parse(time, DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")).toString("yyyy-MM-dd HH:mm"));
    }


}
