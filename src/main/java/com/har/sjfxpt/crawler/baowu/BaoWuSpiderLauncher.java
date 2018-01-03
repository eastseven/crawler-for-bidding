package com.har.sjfxpt.crawler.baowu;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/25.
 */
@Slf4j
@Component
@Deprecated
public class BaoWuSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.BAOWU.toString().toLowerCase() + "-current";

    @Autowired
    BaoWuPageProcessor baoWuPageProcessor;

    @Autowired
    BaoWuPipeline baoWuPipeline;

    String[] urls = {
            "http://baowu.ouyeelbuy.com/baowu-shp/notice/purchaseMore",
            "http://baowu.ouyeelbuy.com/baowu-shp/notice/moreBiddingNoticeList",
            "http://baowu.ouyeelbuy.com/baowu-shp/notice/moreBiddingNoticeList1",
    };

    /**
     * 爬取当日数据
     */
    public void start() {
        cleanSpider(uuid);
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i]);
        }
        Spider spider = Spider.create(baoWuPageProcessor)
                .addPipeline(baoWuPipeline)
                .addRequest(requests)
                .setUUID(uuid)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }

    public static Request requestGenerator(String url) {
        Map<String, Object> pageParams = Maps.newHashMap();
        String typeField = StringUtils.substringAfterLast(url, "/");
        switch (typeField) {
            case "purchaseMore":
                pageParams.put("type", "purchase");
                pageParams.put("pageNow", "1");
                pageParams.put("jqMthod", "newsList");
                break;
            case "moreBiddingNoticeList":
                pageParams.put("type", "0");
                pageParams.put("pageNow", "1");
                pageParams.put("jqMthod", "newsList");
                break;
            case "moreBiddingNoticeList1":
                url = "http://baowu.ouyeelbuy.com/baowu-shp/notice/moreBiddingNoticeList";
                pageParams.put("type", "1");
                pageParams.put("pageNow", "1");
                pageParams.put("jqMthod", "newsList");
                break;
        }
        Request request = new Request(url);
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
        request.putExtra("pageParams", pageParams);
        return request;
    }


}
