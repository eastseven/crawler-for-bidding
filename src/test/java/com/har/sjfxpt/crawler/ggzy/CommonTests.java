package com.har.sjfxpt.crawler.ggzy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CommonTests {

    @Test
    public void test() {
        String text = "递交时间：2017-10-23 15:10";
        Matcher m = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}").matcher(text);
        Assert.assertTrue(m.find());

        String date = StringUtils.substring(text, m.start(), m.end());
        System.out.println(text + " : " + date);
    }

    @Test
    public void testJson() {
        String url = "http://http-webapi.zhimaruanjian.com/getip?num=25&type=2&pro=0&city=0&yys=0&port=1&pack=6212&ts=0&ys=0&cs=0&lb=0&sb=0&pb=4&mr=0";
        Assert.assertNotNull(url);

    }
}
