package com.har.sjfxpt.crawler.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.service.ProxyService;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import com.har.sjfxpt.crawler.zgyj.ZGYeJinPageProcessor;
import com.har.sjfxpt.crawler.zgyj.ZGYeJinPipeline;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static com.har.sjfxpt.crawler.zgyj.ZGYeJinPageProcessor.POST_PARAMS_01;
import static com.har.sjfxpt.crawler.zgyj.ZGYeJinPageProcessor.URL_01;
import static com.har.sjfxpt.crawler.zgyj.ZGYeJinSpiderLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/10/27.
 */
@Slf4j
public class ZGYeJinPageProcessorTests extends SpiderApplicationTests {

    @Autowired
    ZGYeJinPageProcessor zgYeJinPageProcessor;

    @Autowired
    ZGYeJinPipeline zgYeJinPipeline;

    @Autowired
    HBasePipeline pipeline;

    String[] urls = {
            "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMoreZbs&xxposition=zbgg",
            "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMoreCgxx&xxposition=cgxx",
            "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMoreClarifypub&xxposition=cqgg",
            "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMorePub&xxposition=zhongbgg"
    };

    @Test
    public void testPostParams() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("xxposition", "zbgg");
        params.put("currpage", 1);
        params.put("sbsj1", "#");
        params.put("sbsj2", "#");

        String json = JSONObject.toJSONString(params, SerializerFeature.UseSingleQuotes);
        log.info(">>> {}", json);
    }

    //爬去当日的数据
    @Test
    public void testZGYeJinPageProcessor() {

        SourceModel source = new SourceModel();
        source.setUrl(URL_01);
        source.setType("采购公告");
        source.setPost(true);
        source.setJsonPostParams(POST_PARAMS_01);
        source.setNeedPlaceholderFields(new String[]{"sbsj1", "sbsj2"});

        HttpClientDownloader test = new HttpClientDownloader();
        test.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxy()));
        Spider.create(zgYeJinPageProcessor)
                .addRequest(source.createRequest())
                .addPipeline(pipeline)
                .setDownloader(test)
                .run();
    }

    //爬取13年至今的历史数据
    @Test
    public void testZGYeJinHistoryPageProcessor() {
        Request[] requests = new Request[urls.length];

        String date = new DateTime(new Date()).toString("yyyy-MM-dd");

        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i], "2013-01-01", date);
        }

        Spider.create(zgYeJinPageProcessor)
                .addRequest(requests)
                .addPipeline(zgYeJinPipeline)
                .thread(8)
                .run();
    }


    @Test
    public void testPageProcessor() {
        String url = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showZbsDetail&inviteid=40494E5EDE184218AF106A0DAD7FC9BF";
        log.info(">>> download {}", url);
        try {
            Document document = Jsoup.connect(url).timeout(60000).userAgent(SiteUtil.get().getUserAgent()).get();
            String html = document.html();
            Element root = document.body().select("body > div.main-news").first();
            String formatContent = PageProcessorUtil.formatElementsByWhitelist(root);
            String textContent = PageProcessorUtil.extractTextByWhitelist(root);

            log.info("formatContent=={}", formatContent);
            log.info("textContent=={}", textContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testTime() {
        String end = DateTime.now().minusDays(1).toString("yyyy-MM-dd");
        log.debug("end=={}", end);
    }

    @Autowired
    ProxyService proxyService;

    @Test
    public void testProxyService() {
        HttpClientDownloader test = new HttpClientDownloader();
        test.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxy()));
        String html = test.download("http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMoreZbs&xxposition=zbgg").get();
        log.debug("html=={}", html);
    }


}
