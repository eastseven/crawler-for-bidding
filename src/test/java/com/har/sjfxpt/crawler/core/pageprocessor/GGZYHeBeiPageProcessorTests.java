package com.har.sjfxpt.crawler.core.pageprocessor;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
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

import java.util.List;

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



}
