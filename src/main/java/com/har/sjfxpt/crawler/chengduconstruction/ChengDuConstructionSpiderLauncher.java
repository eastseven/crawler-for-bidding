package com.har.sjfxpt.crawler.chengduconstruction;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

/**
 * Created by Administrator on 2017/12/5.
 */
@Slf4j
@Component
public class ChengDuConstructionSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.CDJS.toString().toLowerCase() + "-current";

    @Autowired
    ChengDuConstructionPageProcessor chengDuConstructionPageProcessor;

    @Autowired
    ChengDuConstructionPipeline chengDuConstructionPipeline;

    final int num = Runtime.getRuntime().availableProcessors();

    String url = "http://tz.xmchengdu.gov.cn/zftz/newweb/AjaxProcess/IndexPageHandler.ashx";

    /**
     * 爬取当日数据
     */
    public void start() {
        cleanSpider(uuid);
        Request request = requestGenerator(url);
        Spider spider = Spider.create(chengDuConstructionPageProcessor)
                .addPipeline(chengDuConstructionPipeline)
                .setUUID(uuid)
                .addRequest(request)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }

    public static Request requestGenerator(String url) {
        Request request = new Request(url);

        Map<String, Object> param = Maps.newHashMap();

        param.put("requestType", "getXMZTBXX");
        param.put("pageIndex", 1);
        param.put("pageSize", 31);
        param.put("areaCode", "");
        param.put("type", "");
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(param, "UTF-8"));
        request.putExtra("pageParams", param);
        return request;
    }

}
