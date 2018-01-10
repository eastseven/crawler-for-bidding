package com.har.sjfxpt.crawler.core;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ccgp.ZhengFuCaiGouPageProcessor;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class ZhengFuCaiGouPageProcessorTests extends SpiderApplicationTests {


    @Autowired
    ZhengFuCaiGouPageProcessor pageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;



    @Test
    public void testCCGPAnnotation() {
        List<SourceModel> list = SourceConfigAnnotationUtils.find(pageProcessor.getClass());
        List<Request> requests = Lists.newArrayList();
        for (SourceModel sourceModel : list) {
            Request request = sourceModel.createRequest();
            requests.add(request);
        }
        if (!requests.isEmpty()) {
            Spider.create(pageProcessor)
                    .addRequest(requests.toArray(new Request[requests.size()]))
                    .addPipeline(hBasePipeline)
                    .thread(8)
                    .run();
        } else {
            log.warn("request is empty!");
        }
    }


    //    @Before
    public void init() {
        String url = "http://search.ccgp.gov.cn/bxsearch?searchtype=1&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=0&dbselect=bidx&kw=&start_time=2017%3A10%3A30&end_time=2017%3A10%3A30&timeType=0&displayZone=&zoneId=&pppStatus=0&agentName=";
        HttpClientDownloader downloader = new HttpClientDownloader();
        String html = downloader.download(url).getDocument().html();
        File file = Paths.get("src/test/data", "ccgp-list.html").toFile();

        try {
            if (!file.exists()) file.createNewFile();

            if (FileUtils.sizeOf(file) == 0L) {
                FileUtils.writeStringToFile(file, html, "utf-8");
            }

        } catch (IOException e) {
            log.error("", e);
        }
    }
}
