package com.har.sjfxpt.crawler.ggzy.ggzypageprocessortests;

import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebeinew.GGZYHeBeiPageProcessorNew;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebeinew.GGZYHeBeiPipeline;
import lombok.extern.slf4j.Slf4j;
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

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;
import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebeinew.GGZYHeBeiPageProcessorNew.requestGenerator;

/**
 * Created by Administrator on 2017/12/26.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYHeBeiPageProcessorNewTestsNew {

    @Autowired
    GGZYHeBeiPageProcessorNew ggzyHeBeiPageProcessorNew;

    @Autowired
    GGZYHeBeiPipeline ggzyHeBeiPipeline;

    @Test
    public void testggzyHeBeiPageProcessor() {
        String url = "http://www.hebpr.cn/inteligentsearch/rest/inteligentSearch/getFullTextDataNew";

        Request[] requests = {
                requestGenerator(url, "003005002001", 0),
                requestGenerator(url, "003005002002", 0),
                requestGenerator(url, "003005002003", 0),
                requestGenerator(url, "003005002004", 0),
                requestGenerator(url, "003005001001", 0),
                requestGenerator(url, "003005001002", 0),
                requestGenerator(url, "003005001003", 0),
                requestGenerator(url, "003005001004", 0)
        };

        Spider.create(ggzyHeBeiPageProcessorNew)
                .addRequest(requests)
                .addPipeline(ggzyHeBeiPipeline)
                .thread(THREAD_NUM)
                .run();
    }


    @Test
    public void testHttpClientDown() throws InterruptedException {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        String href = "http://www.hebpr.cn/fwdt/003005/003005002/003005002004/20171222/3d22055e-26c4-47ca-9d84-b37e8ebef0e7.html";
        Page page1 = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
        Elements elements = page1.getHtml().getDocument().body().select("#hideDeil");
        String style = elements.attr("style");
        log.info("style=={}", style);
    }


}
