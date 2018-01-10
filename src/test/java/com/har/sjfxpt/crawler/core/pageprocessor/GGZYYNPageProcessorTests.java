package com.har.sjfxpt.crawler.core.pageprocessor;

import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyyn.GGZYYNPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Created by Administrator on 2017/11/30.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYYNPageProcessorTests {


    @Autowired
    GGZYYNPageProcessor ggzyYNPageProcessor;


    @Autowired
    HBasePipeline hBasePipeline;


    final static Pattern yyyymmddhhmmPattern = Pattern.compile("[0-9]{4}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}");

    @Test
    public void testTime() throws ParseException {
        String time = "20171130092025";
        Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(time);
        DateTime dateTime = new DateTime(date);
        log.debug("time=={}", dateTime.toString("yyyy-MM-dd HH:mm"));
        log.info("time={}", new DateTime(new SimpleDateFormat("yyyyMMddHH").parse("2017120615")).toString("yyyy-MM-dd HH:mm"));
    }

    @Test
    public void testTimeCompare() throws ParseException {
        String time = "2017-12-07 09:58";
        log.info("compare=={}", PageProcessorUtil.timeDetailCompare(time));
        log.info("time=={}", DateTime.now().toString("yyyy-MM-dd HH:mm"));
    }

    @Test
    public void testAnnotation() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(ggzyYNPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(ggzyYNPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(hBasePipeline)
                .thread(8)
                .run();
    }

}
