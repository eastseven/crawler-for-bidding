package com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebeinew;

import com.alibaba.fastjson.JSONObject;
import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/26.
 */
@Slf4j
@Component@Deprecated
public class GGZYHeBeiSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.GGZYHEBEI.toString().toLowerCase() + "-current";

    @Autowired
    GGZYHeBeiPageProcessor ggzyHeBeiPageProcessor;

    @Autowired
    GGZYHeBeiPipeline ggzyHeBeiPipeline;

    String url = "http://www.hebpr.cn/inteligentsearch/rest/inteligentSearch/getFullTextDataNew";

    /**
     * 爬去当日数据
     */
    public void start() {
        cleanSpider(uuid);
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
        Spider spider = Spider.create(ggzyHeBeiPageProcessor)
                .addPipeline(ggzyHeBeiPipeline)
                .setUUID(uuid)
                .addRequest(requests)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }

    public static Request requestGenerator(String url, String typeId, int pageCount) {
        GGZYHeBeiPageParameter ggzyHeBeiPageParameter = new GGZYHeBeiPageParameter();
        ggzyHeBeiPageParameter.setToken("");
        ggzyHeBeiPageParameter.setPn(pageCount);
        ggzyHeBeiPageParameter.setRn(10);
        ggzyHeBeiPageParameter.setSdt(DateTime.now().minusDays(20).toString("yyyy-MM-dd") + " 00:00:00");
        ggzyHeBeiPageParameter.setEdt(DateTime.now().toString("yyyy-MM-dd") + " 23:59:59");
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
