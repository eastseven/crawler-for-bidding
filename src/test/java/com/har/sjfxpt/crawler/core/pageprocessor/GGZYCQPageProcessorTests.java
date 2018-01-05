package com.har.sjfxpt.crawler.core.pageprocessor;

import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzycq.GGZYCQPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzycq.GGZYCQPipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.select.Elements;
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
 * Created by Administrator on 2017/11/28.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GGZYCQPageProcessorTests {

    @Autowired
    GGZYCQPageProcessor ggzycqPageProcessor;

    @Autowired
    GGZYCQPipeline GGZYCQPipeline;

    @Autowired
    HBasePipeline hBasePipeline;

    @Test
    public void testGGZYCQAnnotation() {
        List<SourceModel> list = SourceConfigAnnotationUtils.find(ggzycqPageProcessor.getClass());
        list.forEach(sourceModel -> log.debug(">>>{}", sourceModel.getUrl()));

        List<Request> requestList = list.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(ggzycqPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(hBasePipeline)
                .run();
    }


    @Test
    public void testCQPageProcessor() {
        String[] urls = {
                "http://www.cqggzy.com/web/services/PortalsWebservice/getInfoList?response=application/json&pageIndex=1&pageSize=18&siteguid=d7878853-1c74-4913-ab15-1d72b70ff5e7&categorynum=014005001&title=&infoC=&_=1511837748941",
                "http://www.cqggzy.com/web/services/PortalsWebservice/getInfoList?response=application/json&pageIndex=1&pageSize=18&siteguid=d7878853-1c74-4913-ab15-1d72b70ff5e7&categorynum=014001001&title=&infoC=&_=1511837779151"
        };
        Spider.create(ggzycqPageProcessor)
                .addPipeline(GGZYCQPipeline)
                .addUrl(urls)
                .thread(4)
                .run();
    }

    @Test
    public void test() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Page page = httpClientDownloader.download(new Request("http://www.cqggzy.com/xxhz/014001/014001001/014001001010/20171128/46088835-a22a-4a24-8a91-061c41607c84.html"), SiteUtil.get().toTask());
        Elements elements = page.getHtml().getDocument().body().select("body > div:nth-child(4) > div > div.detail-block");
        String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
        if (formatContent.contains("<a>相关公告</a>")) {
            String real = StringUtils.trim(StringUtils.removeAll(formatContent, "<li>(.+?)</li>"));
            String removeSpace = StringUtils.removeAll(real, "\\s");
            log.debug("removeSpace=={}", removeSpace);
        }
    }

    @Test
    public void testTime() {
        String time = DateTime.now().toString(" HH:mm");
        log.debug("time=={}", time);
    }

}
