package com.har.sjfxpt.crawler.core.pageprocessor;

import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.pipeline.MongoPipeline;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.ggzy.provincial.HNPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/11/29.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class HNPageProcessorTests {

    @Autowired
    HNPageProcessor ggzyHNPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    @Autowired
    MongoPipeline mongoPipeline;

    String[] urls = {
            "http://www.ggzy.hi.gov.cn/ggzy/ggzy/jgzbgg/index_1.jhtml",
            "http://www.ggzy.hi.gov.cn/ggzy/ggzy/jgzbgs/index_1.jhtml",
            "http://www.ggzy.hi.gov.cn/ggzy/ggzy/cggg/index_1.jhtml",
            "http://www.ggzy.hi.gov.cn/ggzy/ggzy/cgzbgg/index_1.jhtml"
    };

    @Test
    public void testHttpdown() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Page page = httpClientDownloader.download(new Request("http://www.ggzy.hi.gov.cn/ggzy/ggzy/cgzbgg/index_1.jhtml"), SiteUtil.get().toTask());
        log.info("html=={}", page.getHtml().getDocument().body().html());
    }

    @Test
    public void testAnnotation() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(ggzyHNPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(ggzyHNPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(mongoPipeline)
                .thread(8)
                .run();
    }


}
