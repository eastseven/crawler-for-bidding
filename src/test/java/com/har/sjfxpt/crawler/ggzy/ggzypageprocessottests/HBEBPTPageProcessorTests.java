package com.har.sjfxpt.crawler.ggzy.ggzypageprocessottests;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import com.har.sjfxpt.crawler.ggzyprovincial.hbebtp.HBEBTPPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.hbebtp.HBEBTPPipeline;
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

import java.util.List;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/6.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class HBEBPTPageProcessorTests {

    @Autowired
    HBEBTPPageProcessor HBEBTPPageProcessor;

    @Autowired
    HBEBTPPipeline hbebtpPipeline;

    @Test
    public void testHBEBPTPageProcessor() {
        String url = "http://www.hbbidcloud.com/hbcloud/jyxx/00200";
        List<String> lists = Lists.newArrayList();
        List<String> listsDetail = Lists.newArrayList();
        for (int i = 1; i <= 5; i++) {
            lists.add(url + i);
        }
        for (int i = 1; i <= 7; i++) {
            for (int j = 0; j < lists.size(); j++) {
                String urlTarget = lists.get(j);
                String filed = StringUtils.substringAfter(urlTarget, "jyxx/");
                listsDetail.add(urlTarget + "/" + filed + "00" + i + "/");
            }
        }

        Request[] requests = new Request[listsDetail.size()];
        for (int i = 0; i < listsDetail.size(); i++) {
            Request request = new Request(listsDetail.get(i));
            requests[i] = request;
        }
        Spider.create(HBEBTPPageProcessor)
                .addRequest(requests)
                .thread(THREAD_NUM)
                .addPipeline(hbebtpPipeline)
                .run();
    }

    @Test
    public void testHttpClientDownloader() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        String url = "http://www.hbbidcloud.com/hbcloud/infodetail/?infoid=99126c71-568c-4749-b43e-35c078ab250c&categoryNum=002001001";
        Page page = httpClientDownloader.download(new Request(url), SiteUtil.get().setTimeOut(30000).toTask());
        Elements elements = page.getHtml().getDocument().body().select("#tblInfo");
        String content = PageProcessorUtil.formatElementsByWhitelist(elements.first());
        if (StringUtils.contains(content, "阅读次数：")) {
            content = StringUtils.remove(content, StringUtils.substringBetween(content, "<h4>", "</h4>"));
            log.info("content=={}", content);
        }
    }
}
