package com.har.sjfxpt.crawler.core.commons;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

@Slf4j
public class DateTimeTests {

    @Test
    public void test() {
        DateTime dt = new DateTime("2017-12-27T10:30");
        String pattern = "yyyy-MM-dd HH:mm";
        String date = DateTime.now().toString(pattern);
        DateTime now = DateTimeFormat.forPattern(pattern).parseDateTime(date);

        log.info(">>> {} : {} = {}", dt.toString(pattern), now.toString(pattern), dt.compareTo(now));
    }

    @Test
    public void testTime() {
        log.debug("{}", DateTime.now().toString("yyyy-MM-dd") + " 23:59:59");
        log.debug("{}", DateTime.now().minusDays(20).toString("yyyy-MM-dd") + " 00:00:00");
    }

    @Test
    public void testMapToJson() {
        Map<String, Object> pageParams = Maps.newHashMap();
        pageParams.put("projectName", "");
        pageParams.put("date", "1day");
        pageParams.put("begin_time", "");
        pageParams.put("end_time", "");
        pageParams.put("projectType", "gcjs");
        pageParams.put("huanJie", "NOTICE");
        pageParams.put("pageIndex", 1);
        log.info(">>> original map {}", pageParams);
        String json = JSONObject.toJSONString(pageParams);
        Assert.assertNotNull(json);
        log.info(">>> {}", json);

        String jsonString = "{'date':'1day','huanJie':'NOTICE','pageIndex':1,'end_time':'','projectType':'gcjs','begin_time':'','projectName':''}";

        Map<String, Object> map = JSONObject.parseObject(jsonString, Map.class);
        log.info(">>> {}", map);

        String date = "#";//DateTime.now().toString("yyyy-MM-dd");
        pageParams = Maps.newHashMap();
        pageParams.put("TIMEBEGIN", date);
        pageParams.put("TIMEBEGIN_SHOW", date);
        pageParams.put("TIMEEND", date);
        pageParams.put("TIMEEND_SHOW", date);
        pageParams.put("DEAL_TIME", "01");
        //00 不限 01 工程建设 02 政府采购
        pageParams.put("DEAL_CLASSIFY", "01");
        pageParams.put("DEAL_STAGE", "0100");
        pageParams.put("DEAL_PROVINCE", "0");
        pageParams.put("DEAL_CITY", "0");
        pageParams.put("DEAL_PLATFORM", "0");
        pageParams.put("DEAL_TRADE", "0");
        pageParams.put("isShowAll", "1");
        pageParams.put("PAGENUMBER", 1);
        pageParams.put("FINDTXT", "");
        log.info(">>> original map {}", pageParams);
        json = JSONObject.toJSONString(pageParams, SerializerFeature.UseSingleQuotes);
        Assert.assertNotNull(json);
        log.info(">>> {}", json);
    }
}
