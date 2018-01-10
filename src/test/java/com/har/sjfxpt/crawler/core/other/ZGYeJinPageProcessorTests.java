package com.har.sjfxpt.crawler.core.other;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.SpiderApplicationTests;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.model.BidNewsSpider;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.service.ProxyService;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.other.ZGYeJinPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/27.
 */
@Slf4j
public class ZGYeJinPageProcessorTests extends SpiderApplicationTests {

    @Autowired
    ZGYeJinPageProcessor zgYeJinPageProcessor;

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
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(zgYeJinPageProcessor.getClass());
        Assert.assertFalse(CollectionUtils.isEmpty(sourceModelList));
        sourceModelList.forEach(sourceModel -> log.debug(">>> {}", sourceModel));
        Request[] requests = sourceModelList.parallelStream().map(SourceModel::createRequest).toArray(Request[]::new);
        BidNewsSpider.create(zgYeJinPageProcessor).addRequest(requests).addPipeline(pipeline).thread(requests.length).run();
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
