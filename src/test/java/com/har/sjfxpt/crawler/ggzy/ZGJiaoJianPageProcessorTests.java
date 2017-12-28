package com.har.sjfxpt.crawler.ggzy;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import com.har.sjfxpt.crawler.zgjiaojian.ZGJiaoJianPageProcessor;
import com.har.sjfxpt.crawler.zgjiaojian.ZGJiaoJianPipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;
import static com.har.sjfxpt.crawler.zgjiaojian.ZGJiaoJianSpiderLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/12/28.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ZGJiaoJianPageProcessorTests {

    @Autowired
    ZGJiaoJianPageProcessor zgJiaoJianPageProcessor;

    @Autowired
    ZGJiaoJianPipeline zgJiaoJianPipeline;

    @Test
    public void testZGJiaoJianPageProcessor() {
        Request[] requests = {
                requestGenerator("http://empm.ccccltd.cn/PMS/downpage.shtml?id=J8Gis7a8zFp0/0+cu62h4ufCdjRQ/t9M5buuVsVwZbmhKSxRhbdvSgqcr+4yWYEPz0JTUNvOCTs="),
                requestGenerator("http://empm.ccccltd.cn/PMS/downpage.shtml?id=J8Gis7a8zFp0/0+cu62h4ufCdjRQ/t9M5buuVsVwZbmhKSxRhbdvSgqcr+4yWYEPeVgXu6xroO0=")
        };
        Spider.create(zgJiaoJianPageProcessor)
                .addRequest(requests)
                .thread(THREAD_NUM)
                .addPipeline(zgJiaoJianPipeline)
                .run();
    }


    @Test
    public void testDown() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Page page = httpClientDownloader.download(new Request("http://empm.ccccltd.cn/PMS/adjustdetail.shtml?id=J8Gis7a8zFp0/0+cu62h4ufCdjRQ/t9M1ax/h3zt9IrDbYVI91AqXryU61Tg8vPEH1E69FlQW7F9A5HVzwtNwjpnlP6RN96i/0swRvG8LnBKxkz+bqmqwFl7iR3mZy9C9eZkbXKaasw="), SiteUtil.get().setTimeOut(30000).toTask());
        Elements elements = page.getHtml().getDocument().body().select("body > div:nth-child(3)");
        log.info("elements =={}", PageProcessorUtil.formatElementsByWhitelist(elements.first()));
    }


}
