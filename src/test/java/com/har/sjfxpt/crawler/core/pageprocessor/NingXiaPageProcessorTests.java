package com.har.sjfxpt.crawler.core.pageprocessor;

import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.ggzy.provincial.NingXiaPageProcessor;
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
 * Created by Administrator on 2017/12/17.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class NingXiaPageProcessorTests {

    @Autowired
    NingXiaPageProcessor ningXiaPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    String[] urls = {
            "http://www.nxggzyjy.org/ningxiaweb/002/002001/002001001/1.html",
            "http://www.nxggzyjy.org/ningxiaweb/002/002001/002001002/1.html",
            "http://www.nxggzyjy.org/ningxiaweb/002/002001/002001003/1.html",

            "http://www.nxggzyjy.org/ningxiaweb/002/002002/002002001/1.html",
            "http://www.nxggzyjy.org/ningxiaweb/002/002002/002002002/1.html",
            "http://www.nxggzyjy.org/ningxiaweb/002/002002/002002003/1.html",
    };


    /**
     * ​​​​​​​​ss=\​​​​​​​​u200B
     */
    @Test
    public void outPut() {
        String url = "http://www.nxggzyjy.org/ningxiaweb/002/002002/002002003/1.html";
        String typeId = StringUtils.substringBetween(StringUtils.substringAfter(url, "002/"), "/", "/");
        log.info("typeId=={}", typeId);
    }

    @Test
    public void testAnnotation() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(ningXiaPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(ningXiaPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(hBasePipeline)
                .thread(8)
                .run();
    }


}
