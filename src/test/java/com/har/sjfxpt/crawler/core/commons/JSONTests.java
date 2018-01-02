package com.har.sjfxpt.crawler.core.commons;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/29.
 */
@Slf4j
public class JSONTests {

    @Test
    public void mapTransformJson() {
        Map<String, Object> pageParams = Maps.newHashMap();
        pageParams.put("type", "0");
        pageParams.put("pageNow", "1");
        pageParams.put("jqMthod", "newsList");
        String json = JSONObject.toJSONString(pageParams, SerializerFeature.UseSingleQuotes);
        log.debug("json=={}", json);
        Map<String, Object> Params = JSONObject.parseObject(json, Map.class);
        log.debug("pageParams={}", Params);
    }


    @Test
    public void testArrayAndList() {
        String[] array = {"123", "456"};
        List<String> list = Arrays.asList(array);
        for (String s : list) {
            log.debug("s={}", s);
        }
        String[] array1 = list.toArray(new String[list.size()]);
        for (int i = 0; i < array1.length; i++) {
            log.debug("s1={}", array1[i]);
        }
    }
}
