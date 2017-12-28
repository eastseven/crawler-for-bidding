package com.har.sjfxpt.crawler.core.commons;

import com.alibaba.fastjson.JSONObject;
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
    }
}
