package com.har.sjfxpt.crawler.ggzy.ggzypageprocessortests;

import com.alibaba.fastjson.JSONObject;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebeinew.GGZYHeBeiPageParameter;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebeinew.GGZYHeBeiPageProcessorNew;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/26.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYHeBeiPageProcessorTestsNew {

    @Autowired
    GGZYHeBeiPageProcessorNew ggzyHeBeiPageProcessorNew;

    @Test
    public void testggzyHeBeiPageProcessor() {
        String url = "http://www.hebpr.cn/inteligentsearch/rest/inteligentSearch/getFullTextDataNew";

        Request[] requests = {
                requestGenerator(url, "003005002001", 0),
                requestGenerator(url, "003005002002", 0),
                requestGenerator(url, "003005002003", 0),
                requestGenerator(url, "003005002004", 0),
                requestGenerator(url, "003005001001", 0),
                requestGenerator(url, "003005001002", 0),
                requestGenerator(url, "003005001003", 0),
                requestGenerator(url, "003005001004", 0)
        };

        Spider.create(ggzyHeBeiPageProcessorNew)
                .addRequest(requests)
                .thread(THREAD_NUM)
                .run();
    }

    public static Request requestGenerator(String url, String typeId, int pageCount) {
        GGZYHeBeiPageParameter ggzyHeBeiPageParameter = new GGZYHeBeiPageParameter();
        ggzyHeBeiPageParameter.setToken("");
        ggzyHeBeiPageParameter.setPn(pageCount);
        ggzyHeBeiPageParameter.setRn(10);
        ggzyHeBeiPageParameter.setSdt("2017-12-06 00:00:00");
        ggzyHeBeiPageParameter.setEdt("2017-12-26 23:59:59");
        ggzyHeBeiPageParameter.setFields("title");
        ggzyHeBeiPageParameter.setSort("{\"showdate\":\"0\"}");
        ggzyHeBeiPageParameter.setSsort("title");
        ggzyHeBeiPageParameter.setCl(200);
        List<GGZYHeBeiPageParameter.ConditionBean> conditionBeanList = Lists.newArrayList();
        GGZYHeBeiPageParameter.ConditionBean conditionBean = new GGZYHeBeiPageParameter.ConditionBean();
        conditionBean.setFieldName("categorynum");
        conditionBean.setIsLike(true);
        conditionBean.setLikeType(2);
        conditionBean.setEqual(typeId);
        conditionBeanList.add(conditionBean);
        ggzyHeBeiPageParameter.setCondition(conditionBeanList);
        ggzyHeBeiPageParameter.setTime(null);
        ggzyHeBeiPageParameter.setHighlights("title");
        ggzyHeBeiPageParameter.setStatistics(null);
        ggzyHeBeiPageParameter.setUnionCondition(null);
        ggzyHeBeiPageParameter.setAccuracy("");
        ggzyHeBeiPageParameter.setNoParticiple("0");
        ggzyHeBeiPageParameter.setSearchRange(null);
        ggzyHeBeiPageParameter.setIsBusiness(1);
        String json = JSONObject.toJSONString(ggzyHeBeiPageParameter);

        Request request = new Request(url);
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.json(json, "UTF-8"));
        request.putExtra("pageParams", ggzyHeBeiPageParameter);
        return request;
    }


}
