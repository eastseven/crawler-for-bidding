package com.har.sjfxpt.crawler.core.other;

import com.har.sjfxpt.crawler.core.annotation.SourceConfigModel;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.pipeline.MongoPipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.other.JinCaiWangPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/10/24.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class JinCaiWangPageProcessorTests {

    @Autowired
    HBasePipeline pipeline;

    @Autowired
    MongoPipeline mongoPipeline;

    @Autowired
    JinCaiWangPageProcessor jinCaiWangPageProcessor;


    @Test
    public void testAnnotation() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(jinCaiWangPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(jinCaiWangPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(mongoPipeline)
                .thread(8)
                .run();
    }

    @Test
    public void test() {
        Request request = new Request("http://www.cfcpn.com/plist/caigou?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=");
        Spider.create(jinCaiWangPageProcessor)
                .addRequest(request)
                .addPipeline(pipeline)
                .run();
    }

    @Test
    public void testJinCaiWangProcessor() {
        String[] urls = {
                "http://www.cfcpn.com/plist/caigou?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=",
                "http://www.cfcpn.com/plist/zhengji?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=",
                "http://www.cfcpn.com/plist/jieguo?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=",
                "http://www.cfcpn.com/plist/biangeng?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo="
        };
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            requests[i] = new Request(urls[i]);
        }
        Spider.create(jinCaiWangPageProcessor)
                .addRequest(requests)
                .addPipeline(pipeline)
                .thread(10)
                .run();
    }

    @Test
    public void testJinCaiWangValidation() {
        BidNewsOriginal bean = new BidNewsOriginal("http://www.cfcpn.com", SourceCode.JC);
        bean.setType("type");
        bean.setTitle("title");
        bean.setDate(DateTime.now().toString("yyyy-MM-dd HH:mm"));
        bean.setSource("123");
        bean.setSourceCode("123");
        bean.setFormatContent("123123");
        bean.setProvince("china");
        log.info(">>> {}", bean);

        Set<ConstraintViolation<BidNewsOriginal>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(bean);
        violations.forEach(violation -> log.error(">>> {}, {}", violation.getPropertyPath(), violation.getMessage()));

        Assert.assertFalse(violations.isEmpty());
    }
}
