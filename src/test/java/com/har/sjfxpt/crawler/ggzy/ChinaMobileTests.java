package com.har.sjfxpt.crawler.ggzy;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.chinamobile.ChinaMobilePageProcessor;
import com.har.sjfxpt.crawler.chinamobile.ChinaMobilePipeline;
import com.har.sjfxpt.crawler.chinamobile.ChinaMobileSpiderLauncher;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
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

import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanUtil.YYYYMMDD;

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
    ChinaMobilePipeline pipeline;

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
}
