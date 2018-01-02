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
        Map<String, Object> params = Maps.newHashMap();
        params.put("VENUS_PAGE_NO_KEY", "1");
        params.put("VENUS_PAGE_SIZE_KEY", "20");
        params.put("channelId", "2013300100000000035");
        String json = JSONObject.toJSONString(params, SerializerFeature.UseSingleQuotes);
        log.debug("json=={}", json);
        Map<String, Object> pageParams = JSONObject.parseObject(json, Map.class);
        log.debug("pageParams={}", pageParams);
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
