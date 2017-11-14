package com.har.sjfxpt.crawler.zgzt;

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
 * Created by Administrator on 2017/11/7.
 */
@Slf4j
@Component
public class ChinaTenderingAndBiddingLauncher extends BaseSpiderLauncher{

    final String uuid= SourceCode.ZGZT.toString().toLowerCase()+"-current";

    @Autowired
    ZGZhaoTouPageProcessor zgZhaoTouPageProcessor;

    @Autowired
    ZGZhaoTouPipeline zgZhaoTouPipeline;

    final int num=Runtime.getRuntime().availableProcessors();

    /**
     * 爬取当日数据
     */
    public void start(){
        String url = "http://www.cebpubservice.com/ctpsp_iiss/searchbusinesstypebeforedooraction/getStringMethod.do";

        Request[] requests={
                requestGenerator(url, "招标项目","今日"),
                requestGenerator(url, "招标公告","今日"),
                requestGenerator(url, "中标公告","今日"),
                requestGenerator(url, "开标记录","今日"),
                requestGenerator(url, "评标公示","今日")
        };

        Spider spider=Spider.create(zgZhaoTouPageProcessor)
                .addRequest(requests)
                .addPipeline(zgZhaoTouPipeline)
                .setUUID(uuid)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }

    /**
     * 爬取历史
     */
    public void fetchHistory(){
        String url = "http://www.cebpubservice.com/ctpsp_iiss/searchbusinesstypebeforedooraction/getStringMethod.do";

        Request[] requests={
                requestGenerator(url, "招标项目",""),
                requestGenerator(url, "招标公告",""),
                requestGenerator(url, "中标公告",""),
                requestGenerator(url, "开标记录",""),
                requestGenerator(url, "评标公示","")
        };

        Spider spider=Spider.create(zgZhaoTouPageProcessor)
                .addRequest(requests)
                .addPipeline(zgZhaoTouPipeline)
                .setUUID(uuid)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }


    public static Request requestGenerator(String url, String type,String date) {

        Request request = new Request(url);

        Map<String, Object> params = Maps.newHashMap();

        if(type.equals("招标项目")||type.equals("中标公告")||type.equals("开标记录")||type.equals("评标公示")){
            params.put("searchName", "");
            params.put("searchArea", "");
            params.put("searchIndustry", "");
            params.put("centerPlat", "");
            params.put("businessType", type);
            params.put("searchTimeStart", "");
            params.put("searchTimeStop", "");
            params.put("timeTypeParam", date);
            params.put("bulletinIssnTime", "");
            params.put("bulletinIssnTimeStart", "");
            params.put("bulletinIssnTimeStop", "");
            params.put("pageNo", 1);
            params.put("row", 15);
        }
        if(type.equals("招标公告")){
            params.put("searchName", "");
            params.put("searchArea", "");
            params.put("searchIndustry", "");
            params.put("centerPlat", "");
            params.put("businessType", type);
            params.put("searchTimeStart", "");
            params.put("searchTimeStop", "");
            params.put("timeTypeParam", "");
            params.put("bulletinIssnTime", "今日");
            params.put("bulletinIssnTimeStart", "");
            params.put("bulletinIssnTimeStop", "");
            params.put("pageNo", 1);
            params.put("row", 15);
        }
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
        request.putExtra("pageParams", params);
        return request;
    }


}
