package com.har.sjfxpt.crawler.ggzy;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.zgzt.ZGZhaoTouPageProcessor;
import lombok.extern.slf4j.Slf4j;
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

/**
 * Created by Administrator on 2017/11/1.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ZGZhaoTouPageProcessorTests {

    @Autowired
    ZGZhaoTouPageProcessor zgZhaoTouPageProcessor;

    @Test
    public void testZGZhaoTouPageProcessor() {

        String url = "http://www.cebpubservice.com/ctpsp_iiss/searchbusinesstypebeforedooraction/getSearch.do#";

        Request request = new Request(url);

        Map<String,Object> params= Maps.newHashMap();

        params.put("searchName","");
        params.put("searchArea","");
        params.put("searchIndustry","");
        params.put("centerPlat","");
        params.put("businessType","招标项目");
        params.put("searchTimeStart","2017-11-02");
        params.put("searchTimeStop","");
        params.put("timeTypeParam","");
        params.put("bulletinIssnTime","");
        params.put("bulletinIssnTimeStart","");
        params.put("bulletinIssnTimeStop","");
        params.put("pageNo",1);
        params.put("row",15);

        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(params,"UTF-8"));
        request.putExtra("pageParams",params);

        Spider.create(zgZhaoTouPageProcessor)
                .addRequest(request)
                .thread(4)
                .run();
    }

}
