package com.har.sjfxpt.crawler.zgly;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/21.
 */
@Slf4j
@Component
public class ZGLvYeSpiderLauncher extends BaseSpiderLauncher {

    final String uuid = SourceCode.ZGLVYE.toString().toLowerCase() + "-current";

    @Autowired
    ZGLvYePageProcessor zgLvYePageProcessor;

    @Autowired
    ZGLvYePipeline zgLvYePipeline;

    String[] urls = {
            "http://ec.chalieco.com/b2b/web/two/indexinfoAction.do?actionType=showMoreCgxx&xxposition=cgxx",
            "http://ec.chalieco.com/b2b/web/two/indexinfoAction.do?actionType=showMoreZbs&xxposition=zbgg",
            "http://ec.chalieco.com/b2b/web/two/indexinfoAction.do?actionType=showMoreClarifypub&xxposition=cqgg",
            "http://ec.chalieco.com/b2b/web/two/indexinfoAction.do?actionType=showMorePub&xxposition=zhongbgg"
    };

    /**
     * 爬取当日数据
     */
    public void start() {
        cleanSpider(uuid);
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i], DateTime.now().toString("yyyy-MM-dd"), DateTime.now().toString("yyyy-MM-dd"));
        }
        Spider spider = Spider.create(zgLvYePageProcessor)
                .addRequest(requests)
                .addPipeline(zgLvYePipeline)
                .setUUID(uuid)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }

    public static Request requestGenerator(String url, String beginDate, String endDate) {
        Request request = new Request(url);
        String typeField = StringUtils.substringAfter(url, "xxposition=");
        Map<String, Object> pageParams = Maps.newHashMap();
        switch (typeField) {
            case "cgxx":
                pageParams.put("currpage", "1");
                pageParams.put("xxposition", "cgxx");
                pageParams.put("xxmc", "");
                pageParams.put("fbrq1", beginDate);
                pageParams.put("fbrq2", endDate);
                pageParams.put("type", "采购信息");
                break;
            case "zbgg":
                pageParams.put("currpage", "1");
                pageParams.put("xxposition", "zbgg");
                pageParams.put("zbmc", "");
                pageParams.put("sbsj1", beginDate);
                pageParams.put("sbsj2", endDate);
                pageParams.put("type", "采购公告");
                break;
            case "cqgg":
                pageParams.put("currpage", "1");
                pageParams.put("xxposition", "cqgg");
                pageParams.put("pubdesc", "");
                pageParams.put("audittime", beginDate);
                pageParams.put("audittime2", endDate);
                pageParams.put("type", "变更公告");
                break;
            case "zhongbgg":
                pageParams.put("currpage", "1");
                pageParams.put("xxposition", "zhongbgg");
                pageParams.put("pubdesc", "");
                pageParams.put("releasedate1", beginDate);
                pageParams.put("releasedate2", endDate);
                pageParams.put("type", "结果公告");
                break;
        }
        request.putExtra("pageParams", pageParams);
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
        return request;
    }

}
