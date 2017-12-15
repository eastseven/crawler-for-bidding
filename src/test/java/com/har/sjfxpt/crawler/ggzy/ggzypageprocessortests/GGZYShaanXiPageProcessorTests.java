package com.har.sjfxpt.crawler.ggzy.ggzypageprocessortests;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyshaanxi.GGZYShaanXiPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyshaanxi.GGZYShaanXiPipeline;
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

import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;
import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyshaanxi.GGZYShaanXiSpiderLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/12/15.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYShaanXiPageProcessorTests {

    @Autowired
    GGZYShaanXiPageProcessor ggzyShaanXiPageProcessor;

    @Autowired
    GGZYShaanXiPipeline ggzyShaanXiPipeline;

    String[] urls = {
            "http://www.sxggzyjy.cn/jydt/001001/001001001/001001001001/1.html",
            "http://www.sxggzyjy.cn/jydt/001001/001001001/001001001002/1.html",
            "http://www.sxggzyjy.cn/jydt/001001/001001001/001001001005/1.html",
            "http://www.sxggzyjy.cn/jydt/001001/001001001/001001001003/1.html",

            "http://www.sxggzyjy.cn/jydt/001001/001001004/001001004001/1.html",
            "http://www.sxggzyjy.cn/jydt/001001/001001004/001001004002/1.html",
            "http://www.sxggzyjy.cn/jydt/001001/001001004/001001004003/1.html"
    };

    @Test
    public void testGGZYShaanXiPageProcessor() {
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i]);
        }
        Spider.create(ggzyShaanXiPageProcessor)
                .addPipeline(ggzyShaanXiPipeline)
                .addRequest(requests)
                .thread(THREAD_NUM)
                .run();
    }

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


}
