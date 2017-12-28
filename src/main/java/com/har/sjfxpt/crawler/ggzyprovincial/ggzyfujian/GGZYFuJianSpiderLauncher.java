package com.har.sjfxpt.crawler.ggzyprovincial.ggzyfujian;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
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
 * Created by Administrator on 2017/12/13.
 */
@Slf4j
@Component
public class GGZYFuJianSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.GGZYFUJIAN.toString().toLowerCase() + "-current";

    @Autowired
    GGZYFuJianPageProcessor ggzyFuJianPageProcessor;

    @Autowired
    GGZYFuJianPipeline ggzyFuJianPipeline;


    /**
     * 爬取当日数据
     */
    public void start() {
        cleanSpider(uuid);
        String url = "https://www.fjggfw.gov.cn/Website/AjaxHandler/BuilderHandler.ashx";
        Request[] requests = {
                requestGenerator(url, DateTime.now().toString("yyyy-MM-dd"), "GCJS"),
                requestGenerator(url, DateTime.now().toString("yyyy-MM-dd"), "ZFCG")
        };
        Spider spider = Spider.create(ggzyFuJianPageProcessor)
                .addPipeline(ggzyFuJianPipeline)
                .setUUID(uuid)
                .addRequest(requests)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }

    public static Request requestGenerator(String url, String date, String type) {
        Request request = new Request(url);
        Map<String, Object> pageParams = Maps.newHashMap();
        pageParams.put("OPtype", "GetListNew");
        pageParams.put("pageNo", "1");
        pageParams.put("pageSize", "10");
        pageParams.put("proArea", "-1");
        pageParams.put("category", type);
        pageParams.put("announcementType", "-1");
        pageParams.put("ProType", "-1");
        pageParams.put("xmlx", "-1");
        pageParams.put("projectName", "");
        pageParams.put("TopTime", date + " 00:00:00");
        pageParams.put("EndTime", date + " 23:59:59");
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
        request.putExtra("pageParams", pageParams);
        return request;
    }

}
