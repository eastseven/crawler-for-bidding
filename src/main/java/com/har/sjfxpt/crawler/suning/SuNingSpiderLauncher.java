package com.har.sjfxpt.crawler.suning;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

/**
 * Created by Administrator on 2017/11/13.
 */
@Slf4j
@Component
public class SuNingSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.SUNING.toString().toLowerCase() + "-current";

    @Autowired
    SuNingPageProcessor suNingPageProcessor;

    @Autowired
    SuNingPipeline suNingPipeline;

    final int num = Runtime.getRuntime().availableProcessors();

    /**
     * 爬取当日数据
     */
    public void start() {

        Request request1 = requestGenerator("http://zb.suning.com/bid-web/searchIssue.htm", DateTime.now().toString("yyyy-MM-dd"), "m2");
        Request request2 = requestGenerator("http://zb.suning.com/bid-web/searchIssue.htm", DateTime.now().toString("yyyy-MM-dd"), "m3");
        Request request3 = requestGenerator("http://zb.suning.com/bid-web/searchIssue.htm", DateTime.now().toString("yyyy-MM-dd"), "m1");

        Request[] requests = {request1, request2, request3};

        cleanSpider(uuid);
        Spider spider = Spider.create(suNingPageProcessor)
                .addPipeline(suNingPipeline)
                .setUUID(uuid)
                .addRequest(requests)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }

    /**
     * 爬取历史
     */
    public void fetchHistory() {
        Request request1 = historyRequestGenerator("http://zb.suning.com/bid-web/searchIssue.htm", "m1");
        Request request2 = historyRequestGenerator("http://zb.suning.com/bid-web/searchIssue.htm", "m2");
        Request request3 = historyRequestGenerator("http://zb.suning.com/bid-web/searchIssue.htm", "m3");

        Request[] requests = {request1, request2, request3};

        cleanSpider(uuid);
        Spider spider = Spider.create(suNingPageProcessor)
                .addPipeline(suNingPipeline)
                .setUUID(uuid)
                .addRequest(requests)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }


    public static Request requestGenerator(String url, String date, String type) {

        Request request = new Request(url);

        Map<String, Object> params = Maps.newHashMap();

        params.put("issue.msgType", type);
        params.put("issue.updateStartDate", date);
        params.put("issue.updateEndDate", date);
        params.put("pageNum", "1");

        request.setMethod(HttpConstant.Method.POST);

        request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));

        request.putExtra("pageParams", params);
        return request;
    }

    public static Request historyRequestGenerator(String url, String type) {

        Request request = new Request(url);

        Map<String, Object> params = Maps.newHashMap();

        params.put("issue.msgType", type);
        params.put("pageNum", "1");

        request.setMethod(HttpConstant.Method.POST);

        request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));

        request.putExtra("pageParams", params);

        return request;
    }

}
