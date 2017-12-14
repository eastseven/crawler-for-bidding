package com.har.sjfxpt.crawler.ggzy.ggzypageprocessortests;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyjiangxi.GGZYJiangXiPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyjiangxi.GGZYJiangXiPipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
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
import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyjiangxi.GGZYJiangXiSpiderLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/12/14.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYJiangXiPageProcessorTests {

    @Autowired
    GGZYJiangXiPageProcessor ggzyJiangXiPageProcessor;

    @Autowired
    GGZYJiangXiPipeline ggzyJiangXiPipeline;

    String[] urls = {
            "http://jxsggzy.cn/web/jyxx/002006/002006001/1.html",
            "http://jxsggzy.cn/web/jyxx/002006/002006002/1.html",
            "http://jxsggzy.cn/web/jyxx/002006/002006003/1.html",
            "http://jxsggzy.cn/web/jyxx/002006/002006004/1.html",
            "http://jxsggzy.cn/web/jyxx/002006/002006005/1.html",
            "http://jxsggzy.cn/web/jyxx/002006/002006006/1.html",

            "http://jxsggzy.cn/web/jyxx/002001/002001001/1.html",
            "http://jxsggzy.cn/web/jyxx/002001/002001002/1.html",
            "http://jxsggzy.cn/web/jyxx/002001/002001003/1.html",
            "http://jxsggzy.cn/web/jyxx/002001/002001004/1.html",

            "http://jxsggzy.cn/web/jyxx/002002/002002002/1.html",
            "http://jxsggzy.cn/web/jyxx/002002/002002003/1.html",
            "http://jxsggzy.cn/web/jyxx/002002/002002005/1.html",

            "http://jxsggzy.cn/web/jyxx/002003/002003001/1.html",
            "http://jxsggzy.cn/web/jyxx/002003/002003002/1.html",
            "http://jxsggzy.cn/web/jyxx/002003/002003003/1.html",
            "http://jxsggzy.cn/web/jyxx/002003/002003004/1.html",

            "http://jxsggzy.cn/web/jyxx/002005/002005001/1.html",
            "http://jxsggzy.cn/web/jyxx/002005/002005002/1.html",
            "http://jxsggzy.cn/web/jyxx/002005/002005003/1.html",
            "http://jxsggzy.cn/web/jyxx/002005/002005004/1.html",
    };

    @Test
    public void testGGZYJiangXiPageProcessor() {
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i]);
        }
        Spider.create(ggzyJiangXiPageProcessor)
                .addRequest(requests)
                .addPipeline(ggzyJiangXiPipeline)
                .thread(THREAD_NUM)
                .run();
    }



    @Test
    public void testStringUtils() {
        String test = "http://jxsggzy.cn/web/jyxx/002006/002006005/20171212/3040460e-8619-4f09-b2a4-e4872058c04b.html";
        String timeParse = DateTime.parse("2017-12-12", DateTimeFormat.forPattern("yyyy-MM-dd")).toString("yyyyMMdd");
        log.info("timeParse=={}", timeParse);
        String typeId = StringUtils.substringBetween(test, "002006/", "/" + timeParse);
        log.info("typeId=={}", typeId);
    }

    @Test
    public void testJson() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Page page = httpClientDownloader.download(new Request("http://jxsggzy.cn/jxggzy/services/JyxxWebservice/getList?response=application/json&pageIndex=1&pageSize=22&area=&prepostDate=2017-12-14&nxtpostDate=2017-12-14&xxTitle=&categorynum=002006"), SiteUtil.get().toTask());
        String JsonContent = page.getRawText();
    }

}
