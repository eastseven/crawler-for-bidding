package com.har.sjfxpt.crawler.ggzy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Html;

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

    @Test
    public void testProxy() {
        //Proxy proxy = new Proxy("120.25.206.189", 3128);
        Proxy proxy = new Proxy("120.26.162.31", 3333);
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(proxy));
        Html html = httpClientDownloader.download("https://www.baidu.com", "UTF-8");
        Assert.assertNotNull(html);

        log.debug("\n{}", html);
    }
}
