package com.har.sjfxpt.crawler.ggzy;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SimpleHttpClient;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CommonTests {

    @Test
    public void testSourceCode() {
        Assert.assertNotNull(SourceCode.CCGP);
        log.debug(">>> source code {}, {}", SourceCode.CCGP.toString(), SourceCode.CCGP.getValue());
    }

    @Test
    public void testCCGP() {
        String url = "http://www.ccgp.gov.cn/cggg/dfgg/cjgg/201710/t20171030_9077520.htm";
        SimpleHttpClient simpleHttpClient = new SimpleHttpClient();
        Page page = simpleHttpClient.get(url);
        Element element = page.getHtml().getDocument().body();

        //判断是否为新版css
        boolean isNewVersion = !element.select("div.vF_detail_content_container div.vF_detail_content").isEmpty();
        log.debug("isNewVersion {}", isNewVersion);
        String detailCssQuery = isNewVersion ? "div.vF_detail_content_container div.vF_detail_content" : "div.vT_detail_main div.vT_detail_content";

        String detailFormatContent = PageProcessorUtil.formatElementsByWhitelist(element.select(detailCssQuery).first());
        Assert.assertNotNull(detailFormatContent);
        Assert.assertTrue(StringUtils.isNotBlank(detailFormatContent));
        System.out.println(detailFormatContent);
    }

    @Test
    public void testPostMethod() {
        String url = "http://eportal.energyahead.com/wps/portal/ebid/!ut/p/c5/04_SB8K8xLLM9MSSzPy8xBz9CP0os3hHS1NnxxAzL29T00BTA08Pl1CLQDNnQ29_U6B8JE55kzATArrDQfbhVmFggFcebD5I3gAHcDTQ9_PIz03VL8iNMMgMSFcEAPi205o!/dl3/d3/L0lDU0NTQ1FvS1VRIS9JSFNBQ0lLRURNeW01dXBnLzRDMWI4SWtmb2lUSmVLQVEvN19BOTNDQVQ2Sks1TDk1MElUTFJQT1Q0M0cxNy9kZXRhaWw!/";

        Map<String, Object> params = Maps.newHashMap();
        params.put("documentId", "2324164");
        Request request = new Request(url);
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));

        HttpClientDownloader downloader = new HttpClientDownloader();
        Page page = downloader.download(request, SiteUtil.get().setCharset("utf-8").toTask());
        Assert.assertNotNull(page);
        log.debug("\n{}", page.getHtml().getDocument().body().html());
    }

    @Test
    public void testPageProcessorUtil() {
        String text = "发布时间：2017-10-30 10:03:36";
        String result = PageProcessorUtil.dataTxt(text);
        Assert.assertNotNull(result);
        log.debug("{}, {}", text, result);
    }

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

    @Test
    public void testPage() throws Exception {
        String formatContent = null, textContent = null;
        String url = "http://www.ggzy.gov.cn/information/html/b/210000/0202/201710/23/0021220ee1f022d8492c84760339ecd07e50.shtml";
        Assert.assertNotNull(url);

        //iframe
        url = "http://www.ggzy.gov.cn/information/html/b/620000/0104/201710/27/00628ef903f0607a4d118468b8aecbd6a54d.shtml";

        //img
        url = "http://www.ggzy.gov.cn/information/html/b/330000/0101/201710/27/00334e7e04a1a4b744ca919576e06dcc0b15.shtml";

        // iframe pdf
        url = "http://www.ggzy.gov.cn/information/html/b/510000/0101/201710/27/0051bb1827804c5c4450aed01b53e5f025f1.shtml";

        Document document = Jsoup.connect(url).get();
        Element content = document.body().select("#mycontent").first();

        log.debug("\n{}\n", content.html());

        boolean hasIframeTag = !content.getElementsByTag("iframe").isEmpty();
        log.debug("has iframe {}", hasIframeTag);

        boolean isImageTag = !content.getElementsByTag("img").isEmpty();
        log.debug("is image {}", isImageTag);

        if (hasIframeTag) {
            url = content.getElementsByTag("iframe").attr("src");
            log.debug("iframe url {}", url);
            Element body = Jsoup.connect(url).get().body();
            log.debug("\n{}\n", body.html());
            formatContent = PageProcessorUtil.formatElementsByWhitelist(body);
            textContent = PageProcessorUtil.extractTextByWhitelist(body);
        } else if (isImageTag) {
            log.debug("{}", content.select("img"));
            formatContent = content.select("img").toString();
            textContent = formatContent;
        } else {
            formatContent = PageProcessorUtil.formatElementsByWhitelist(content);
            textContent = PageProcessorUtil.extractTextByWhitelist(content);
        }

        Assert.assertNotNull(formatContent);
        Assert.assertNotNull(textContent);

        log.debug("\n=== format ===\n{}\n", formatContent);
        log.debug("\n=== text   ===\n{}\n", textContent);
    }
}
