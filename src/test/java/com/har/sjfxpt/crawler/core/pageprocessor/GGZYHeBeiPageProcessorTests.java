package com.har.sjfxpt.crawler.core.pageprocessor;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebeinew.GGZYHeBeiPageParameter;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebeinew.GGZYHeBeiPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2018/1/8.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYHeBeiPageProcessorTests {

    @Autowired
    GGZYHeBeiPageProcessor ggzyHeBeiPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    @Test
    public void testJson() {
        GGZYHeBeiPageParameter ggzyHeBeiPageParameter = new GGZYHeBeiPageParameter();
        ggzyHeBeiPageParameter.setToken("");
        ggzyHeBeiPageParameter.setPn(0);
        ggzyHeBeiPageParameter.setRn(10);
        ggzyHeBeiPageParameter.setSdt(DateTime.now().toString("yyyy-MM-dd") + " 00:00:00");
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
        conditionBean.setEqual("003005002001");
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
        String json = JSONObject.toJSONString(ggzyHeBeiPageParameter, SerializerFeature.UseSingleQuotes);
        log.debug("json={}", json);
    }

    @Test
    public void testPostParams() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(GGZYHeBeiPageProcessor.class);
        sourceModelList.forEach(sourceModel -> log.debug(">>> {}", JSONObject.toJSONString(sourceModel, true)));
        sourceModelList.forEach(sourceModel -> log.debug(">>> {}", JSONObject.toJSONString(sourceModel.createRequest(), true)));
    }

    @Test
    public void testAnnotation() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(ggzyHeBeiPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        for (Request request : requestList) {
            log.debug("request={}", request);
        }
        Spider.create(ggzyHeBeiPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(hBasePipeline)
                .thread(8)
                .run();
    }

}
