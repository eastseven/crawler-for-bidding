package com.har.sjfxpt.crawler.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.chinamobile.ChinaMobilePageProcessor;
import com.har.sjfxpt.crawler.chinamobile.ChinaMobilePipeline;
import com.har.sjfxpt.crawler.chinamobile.ChinaMobileSpiderLauncher;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanUtil.YYYYMMDD;

/**
 * 中国移动采购与招标网测试类
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ChinaMobileTests {

    @Autowired
    ChinaMobileSpiderLauncher spiderLauncher;

    @Autowired
    ChinaMobilePageProcessor pageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    @Autowired
    ChinaMobilePipeline pipeline;

    @Test
    public void testAnnotation() {
        List<SourceModel> list = SourceConfigAnnotationUtils.find(pageProcessor.getClass());
        List<Request> requests = Lists.newArrayList();
        for (SourceModel sourceModel : list) {
            Request request = sourceModel.createRequest();
            requests.add(request);
        }
        if (!requests.isEmpty()) {
            Spider.create(pageProcessor)
                    .addRequest(requests.toArray(new Request[requests.size()]))
                    .addPipeline(hBasePipeline)
                    .thread(8)
                    .run();
        } else {
            log.warn("request is empty!");
        }
    }


    @Test
    public void test() {
        String url = "https://b2b.10086.cn/b2b/main/listVendorNoticeResult.html?noticeBean.noticeType=2";
        Assert.assertNotNull(url);

        Map<String, Object> params = Maps.newHashMap();
        params.put("page.currentPage", 1);
        params.put("page.perPageSize", 20);
        params.put("noticeBean.sourceCH", "");
        params.put("noticeBean.source", "");
        params.put("noticeBean.title", "");
        params.put("noticeBean.startDate", "2017-10-24");
        params.put("noticeBean.endDate", "2017-10-24");
        Request request = new Request(url);
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
        request.putExtra("pageParams", params);

        Spider.create(pageProcessor)
                .addRequest(request)
                .run();
    }

    @Test
    public void testPipeline() {
        Assert.assertNotNull(pipeline);
        String date = DateTime.now().toString(YYYYMMDD);
        String url = "https://b2b.10086.cn/b2b/main/listVendorNoticeResult.html?noticeBean.noticeType=2";

        Map<String, Object> params = Maps.newHashMap();
        params.put("page.currentPage", 1);
        params.put("page.perPageSize", 20);
        params.put("noticeBean.sourceCH", "");
        params.put("noticeBean.source", "");
        params.put("noticeBean.title", "");
        params.put("noticeBean.startDate", date);
        params.put("noticeBean.endDate", date);
        Request request = new Request(url);
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
        request.putExtra("pageParams", params);

        Spider.create(pageProcessor)
                .addRequest(request)
                .addPipeline(pipeline)
                .run();
    }

    @Test
    public void testPage() throws Exception {
        String url = "https://b2b.10086.cn/b2b/main/viewNoticeContent.html?noticeBean.id=400519";
        Document document = Jsoup.connect(url).timeout(60000).userAgent(SiteUtil.get().getUserAgent()).get();

        Element root = document.body().select("div#mobanDiv").first();
        String formatContent = PageProcessorUtil.formatElementsByWhitelist(root);
        String textContent = PageProcessorUtil.extractTextByWhitelist(root);

        log.debug("\n{}", formatContent);
        log.debug("\n{}", textContent);

        Assert.assertNotNull(formatContent);
    }
}
