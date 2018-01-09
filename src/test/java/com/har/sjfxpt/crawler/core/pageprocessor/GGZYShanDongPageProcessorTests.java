package com.har.sjfxpt.crawler.core.pageprocessor;

import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyshandong.GGZYShanDongPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyshandong.GGZYShanDongPipeline;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.List;
import java.util.stream.Collectors;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/15.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GGZYShanDongPageProcessorTests {

    @Autowired
    GGZYShanDongPageProcessor ggzyShanDongPageProcessor;

    @Autowired
    GGZYShanDongPipeline ggzyShanDongPipeline;

    @Autowired
    HBasePipeline hBasePipeline;

    String[] urls = {
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=117&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=89&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=87&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=88&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=86&ext=",

            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=94&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=90&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=92&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=93&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=91&ext=",
    };

    @Test
    public void testGGZYShanDongPageProcessor() {
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            Request request = new Request(urls[i]);
            requests[i] = request;
        }
        Spider.create(ggzyShanDongPageProcessor)
                .addRequest(requests)
                .thread(THREAD_NUM)
                .addPipeline(ggzyShanDongPipeline)
                .run();
    }

    @Test
    public void testAnnotation() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(ggzyShanDongPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(ggzyShanDongPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(hBasePipeline)
                .thread(8)
                .run();
    }

}
