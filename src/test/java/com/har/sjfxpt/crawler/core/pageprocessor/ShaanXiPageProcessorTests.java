package com.har.sjfxpt.crawler.core.pageprocessor;

import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.ggzy.provincial.ShaanXiPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
 * Created by Administrator on 2017/12/15.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ShaanXiPageProcessorTests {

    @Autowired
    ShaanXiPageProcessor shaanXiPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    @Test
    public void testDate() {
        String date = "【信息时间：2017-12-13】 【我要打印】 【关闭】";
//        DateTime dateTime = DateTime.parse(date, DateTimeFormat.forPattern("MM-dd"));
//        log.info("dateTime=={}",dateTime);
        String dataReal = StringUtils.substringBetween(date, "信息时间：", "】");
        log.info("date=={}", PageProcessorUtil.dataTxt(dataReal));
    }

    @Test
    public void testDownload() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Page page = httpClientDownloader.download(new Request("http://www.sxggzyjy.cn/jydt/001001/001001001/001001001002/20171214/ff808081604e2cf6016052c970b20fb0.html"), SiteUtil.get().toTask());
        String dataInformation = page.getHtml().getDocument().body().select("body > div.ewb-container > div.ewb-main > div.info-source").text();
        log.info("dataInformation=={}", dataInformation);
        String dataReal = StringUtils.substringBetween(dataInformation, "信息时间：", "】");
        log.info("dataReal=={}", dataReal);
    }

    @Test
    public void testAnnotation() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(shaanXiPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(shaanXiPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(hBasePipeline)
                .thread(8)
                .run();
    }


}
