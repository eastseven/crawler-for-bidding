package com.har.sjfxpt.crawler.core;

import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.dongfeng.DongFengPageProcessor;
import com.har.sjfxpt.crawler.dongfeng.DongFengPipeline;
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
 * Created by Administrator on 2017/11/13.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class DongFengPageProcessorTests {

    @Autowired
    DongFengPageProcessor dongFengPageProcessor;

    @Autowired
    DongFengPipeline dongFengPipeline;

    @Autowired
    HBasePipeline hBasePipeline;


    @Test
    public void testDongFengAnnotation() {
        List<SourceModel> list = SourceConfigAnnotationUtils.find(dongFengPageProcessor.getClass());
        list.forEach(sourceModel -> log.debug(">>>{}", sourceModel.getUrl()));

        List<Request> requestList = list.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(dongFengPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(hBasePipeline)
                .run();
    }

    @Test
    public void testDongFeng() {
        String[] urls = {
                "http://jyzx.dfmbidding.com/zbgg/index_1.jhtml",
                "http://jyzx.dfmbidding.com/pbgs/index_1.jhtml",
                "http://jyzx.dfmbidding.com/zgys/index_1.jhtml",
                "http://jyzx.dfmbidding.com/bggg/index_1.jhtml",
        };
        Spider.create(dongFengPageProcessor)
                .addUrl(urls)
                .addPipeline(dongFengPipeline)
                .thread(THREAD_NUM)
                .run();
    }

    @Test
    public void testTypeJudgement() {
        String href = "http://jyzx.dfmbidding.com/zgys/index_5.jhtml";
        String type = dongFengPageProcessor.typeJudgment(href);
        log.info("type=={}", type);
    }
}
