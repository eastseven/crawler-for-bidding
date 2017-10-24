package com.har.sjfxpt.crawler.ggzy;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.chinamobile.ChinaMobilePageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

@Slf4j
public class ChinaMobilePageProcessorTests extends SpiderApplicationTests {

    @Autowired
    ChinaMobilePageProcessor pageProcessor;

    @Test
    public void test() {
        String url = "https://b2b.10086.cn/b2b/main/listVendorNoticeResult.html?noticeBean.noticeType=2";
        Assert.assertNotNull(url);

        Map<String, Object> params = Maps.newHashMap();
        params.put("page.currentPage",1);
        params.put("page.perPageSize",20);
        params.put("noticeBean.sourceCH","");
        params.put("noticeBean.source","");
        params.put("noticeBean.title","");
        params.put("noticeBean.startDate","2017-10-24");
        params.put("noticeBean.endDate","2017-10-24");
        Request request = new Request(url);
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
        request.putExtra("pageParams", params);

        Spider.create(pageProcessor)
                .addRequest(request)
                .run();
    }
}
