package com.har.sjfxpt.crawler.core.pageprocessor;

import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.pipeline.MongoPipeline;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.ggzy.provincial.ChongQingPageProcessor;
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
public class ChongQingPageProcessorTests {

    @Autowired
    ChongQingPageProcessor ChongQingPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    @Autowired
    MongoPipeline mongoPipeline;

    @Test
    public void testGGZYCQAnnotation() {
        List<SourceModel> list = SourceConfigAnnotationUtils.find(ChongQingPageProcessor.getClass());
        list.forEach(sourceModel -> log.debug(">>>{}", sourceModel.getUrl()));

        List<Request> requestList = list.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(ChongQingPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(mongoPipeline)
                .thread(8)
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
