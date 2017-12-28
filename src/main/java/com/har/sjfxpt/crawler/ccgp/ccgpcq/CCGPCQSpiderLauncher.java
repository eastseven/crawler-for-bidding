package com.har.sjfxpt.crawler.ccgp.ccgpcq;

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

import java.util.Map;

/**
 * Created by Administrator on 2017/12/1.
 */
@Slf4j
@Component
public class CCGPCQSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.CCGPCQ.toString().toLowerCase() + "-current";

    @Autowired
    CCGPCQPageProcessor ccgpcqPageProcessor;

    @Autowired
    CCGPCQPipeline ccgpcqPipeline;

    final int num = Runtime.getRuntime().availableProcessors();

    String[] urls = {
            "https://www.cqgp.gov.cn/gwebsite/api/v1/notices/stable?pi=1&ps=20&startDate=&timestamp=" + DateTime.now().getMillis() + "&type=100,200,201,202,203,204,205,206,207,309,400,401,402,3091,4001",
            "https://www.cqgp.gov.cn/gwebsite/api/v1/notices/stable?pi=1&ps=20&startDate=&timestamp=" + DateTime.now().getMillis() + "&type=301,303",
            "https://www.cqgp.gov.cn/gwebsite/api/v1/notices/stable?pi=1&ps=20&startDate=&timestamp=" + DateTime.now().getMillis() + "&type=300,302,304,3041,305,306,307,308"
    };

    /**
     * 爬取当天数据
     */
    public void start() {
        cleanSpider(uuid);
        Request[] requests = new Request[urls.length];
        String date = DateTime.now().toString("yyyy-MM-dd");
        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i], date);
        }
        Spider spider = Spider.create(ccgpcqPageProcessor)
                .addRequest(requests)
                .setUUID(uuid)
                .addPipeline(ccgpcqPipeline)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }

    public static Request requestGenerator(String url, String date) {
        String urlDetail = url.replace("startDate=", "startDate=" + date);
        String typeParams = StringUtils.substringAfter(url, "type=");
        Request request = new Request(urlDetail);
        Map<String, String> pageParams = Maps.newHashMap();
        if (typeParams.equalsIgnoreCase("100,200,201,202,203,204,205,206,207,309,400,401,402,3091,4001")) {
            pageParams.put("type", "采购公告");
        }
        if (typeParams.equalsIgnoreCase("301,303")) {
            pageParams.put("type", "采购预公示");
        }
        if (typeParams.equalsIgnoreCase("300,302,304,3041,305,306,307,308")) {
            pageParams.put("type", "采购结果公告");
        }
        request.putExtra("pageParams", pageParams);
        return request;
    }
}
