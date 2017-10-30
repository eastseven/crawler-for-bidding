package com.har.sjfxpt.crawler.zgyj;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Date;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/30.
 */
@Slf4j
@Component
public class ZGYeJinSpiderLauncher extends BaseSpiderLauncher{

    @Autowired
    ZGYeJinPageProcessor zgYeJinPageProcessor;

    @Autowired
    ZGYeJinPipeline zgYeJinPipeline;

    String[] urls = {
            "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMoreZbs&xxposition=zbgg",
            "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMoreCgxx&xxposition=cgxx",
            "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMoreClarifypub&xxposition=cqgg",
            "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMorePub&xxposition=zhongbgg"
    };

    final int num =Runtime.getRuntime().availableProcessors();

    //爬去当日的数据
    public void start(){

        Request[] requests = new Request[urls.length];

        String date=new DateTime(new Date()).toString("yyyy-MM-dd");

        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i],date,date);
        }

        Spider spider=Spider.create(zgYeJinPageProcessor)
                .addRequest(requests)
                .addPipeline(zgYeJinPipeline)
                .thread(num);
        spider.start();
        addSpider(spider);
    }

    //爬取13年至今的历史数据
    public void fetchHistory(){
        Request[] requests = new Request[urls.length];

        String date=DateTime.now().minusDays(1).toString("yyyy-MM-dd");

        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i],"2013-01-01",date);
        }

        Spider spider=Spider.create(zgYeJinPageProcessor)
                .addRequest(requests)
                .addPipeline(zgYeJinPipeline)
                .thread(num);
        spider.start();
        addSpider(spider);
    }


    //判断request
    public final static Request requestGenerator(String url,String dateBegin,String dateEnd) {
        Request request = new Request(url);
        Map<String, Object> params = Maps.newHashMap();
        if (url.contains("zbgg")) {
            params.put("actionType", "showMoreZbs");
            params.put("xxposition", "zbgg");
            params.put("currpage", 1);
            params.put("xxposition", "zbgg");
            params.put("sbsj1", dateBegin);
            params.put("sbsj2", dateEnd);
            params.put("type", "采购公告");
            request.setMethod(HttpConstant.Method.POST);
            request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
            request.putExtra("pageParams", params);
        }
        if (url.contains("cgxx")) {
            params.put("actionType", "showMoreCgxx");
            params.put("xxposition", "cgxx");
            params.put("currpage", 1);
            params.put("xxposition", "cgxx");
            params.put("fbrq1", dateBegin);
            params.put("fbrq2", dateEnd);
            params.put("type", "采购信息");
            request.setMethod(HttpConstant.Method.POST);
            request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
            request.putExtra("pageParams", params);
        }
        if (url.contains("cqgg")) {
            params.put("actionType", "showMoreClarifypub");
            params.put("xxposition", "cqgg");
            params.put("currpage", 1);
            params.put("xxposition", "cqgg");
            params.put("audittime", dateBegin);
            params.put("audittime2", dateEnd);
            params.put("type", "变更公告");
            request.setMethod(HttpConstant.Method.POST);
            request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
            request.putExtra("pageParams", params);
        }
        if (url.contains("zhongbgg")) {
            params.put("actionType", "showMorePub");
            params.put("xxposition", "zhongbgg");
            params.put("currpage", 1);
            params.put("xxposition", "cgxx");
            params.put("releasedate1", dateBegin);
            params.put("releasedate2", dateEnd);
            params.put("type", "结果公告");
            request.setMethod(HttpConstant.Method.POST);
            request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
            request.putExtra("pageParams", params);
        }

        return request;
    }

}
