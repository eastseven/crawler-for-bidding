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
import org.jsoup.select.Elements;
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

import java.io.IOException;
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

    @Test
    public void testContent(){
        String test="/wps/contenthandler/ebid/!ut/p/digest!mNxE8R0tr4bpN964Me4g1g/pm/oid:--portletwindowid--@oid:6_A95CAT6JK55Q50IHDU8Q6C1KO5    /wps/contenthandler/ebid/!ut/p/digest!mNxE8R0tr4bpN964Me4g1g/um/secure/currentuser/profile?expandRefs=true                 首页 &gt;&gt; 公开招标公告 &gt;&gt; 详细信息            公开招标公告详细信息    项目名称 2018-2019年注水井钢丝绳作业技术服务标段二（F201701522-2）   项目编号 2017-0216529-THYT-0007403   一. 招标条件   本 服务项目 已按要求履行了相关报批及备案等手续，资金已落实，具备招标条件，现对其进行 公开招标 。   二. 概况与招标范围    1）吐哈油田公司 2018-2019年注水井钢丝绳作业技术服务标段二 项目 2）服务地点或区域：鲁克沁采油厂、三塘湖采油厂。 3）服务期限：2018年1月1日起至2019年12 月31日。 2.招标范围：鲁克沁采油厂、三塘湖采油厂2018-2019年注水井钢丝绳作业技术服务。    三. 投标人资格要求    1.在最近三年内没有骗取中标和严重违约及重大质量问题； 2.本次招标不接受联合体投标； 3.具有质量、环保、健康管理安全体系认证证书; 4.涵盖油水井钢丝作业技术服务相关范围； 5.关业绩证明； 6.财务状况良好。    四. 招标文件的获取    招标文件发售开始时间:2017-11-07 09:00:00.0  招标文件发售结束时间:2017-11-30 19:00:00.0   1.投标报名 1.1投标报名时间： 2017-10-31 09:00 至 2017-11-06 18:00 (北京时间); 1.2投标报名方式： (1)获得吐哈油田市场准入资格的供应商： 进入“吐哈油田招投标信息网”（网址：http://zb.tuha.net:8600/，在“供应商登录”窗口输入账号和密码，登录到“供应商业务中心”，进入投标报名模块，选择对应招标公告进行报名。 (2)未获得吐哈油田市场准入资格的供应商： 进入“吐哈油田市场管理信息系统”（网址：http://zb.tuha.net:8600/，在“供应商登录”窗口点击“新用户注册”，提交注册信息后由招投标部审核，审核通过后生成临时供应商账号并通过电子邮件通知。供应商凭临时账号登录到“供应商业务中心”，进入投标报名模块，选择对应招标公告进行报名。在投标报名截止时间前办理完毕吐哈市场准入，并在报名审核合格后方可购买招标文件，逾期视为自动放弃。 准入办理程序：详见吐哈油田招投标信息网（http://zb.tuha.net:8600/) 准入规章 栏目中的“吐哈油田市场准入办理程序及相关要求”。市场准入办理咨询及投标报名联系人：吴先生，联系电话：0995-8375213。 2.招标文件获取 (1).缴费 通过资格审查的供应商在接到吐哈油田公司招投标部通知后在规定日期内通过银行汇款或到指定银行现场缴费，每套售价人民币 1000元，售后不退。 开户银行：昆仑银行股份有限公司吐哈分行鄯善石油支行 账 户 名：中国石油天然气股份有限公司吐哈油田分公司 账 号：88322000057880000038 款项来源：标书咨询费 （格式要求：投标编号+标书咨询费） 缴费后持银行进账单到招投标部112房间开具发票。联系人：张玉红，联系电话： 0995-8372515。 (2).领取招标文件 办理时间：2017年 11 月 07 日 至 2017 年 11 月 11 日，上午9:00时至12:30时，下午15:30时至19:00时(节假日除外)。 办理地点：新疆吐鲁番地区鄯善县火车站镇吐哈油田公司招投标部204室。 办理方式：持缴费发票领取招标文件。    五. 投标文件的递交    投标文件递交截止时间:2017-11-27 15:30:00.0    1.投标文件递交截止时间：预计 2017年 11 月 27 日 15 时 30分，投标文件递交地点：新疆吐鲁番地区鄯善县火车站镇吐哈油田公司招投标部，方式为现场送达。投标人应按招标文件中规定时间递交应标函或弃标函，否则将按规定予以处理（联系人： 王茹 ；电话： 0995-8376908 ，传真： 0995-8376908 ，逾期未告知，按将规定给予相应处罚。 2.逾期送达的或者未送达指定地点的投标文件，招标人不予受理。 3.投标申请人在提交投标文件时，应提交投标保证金，银行账号见招标文件，投标保证金金额为： 14 万元，采用电汇方式，由本公司账户转出。    六. 开标        招标人：招标采购科   招标代理机构：   开标时间： 2017-11-27 15:30:00.0 开标地点： 新疆吐鲁番地区鄯善县火车站镇吐哈油田公司招投标部   地址： 新疆吐鲁番地区鄯善县火车站镇吐哈油田公司招投标部 邮编： 838202   联系人： 王茹     电话： 09958376908 传真： 09958376908   电子邮件： wangruth@petrochina.com.cn     开户银行： 新疆吐鲁番地区鄯善县火车站镇吐哈油田公司招投标部     账户名： 中国石油天然气股份有限公司吐哈油田分公司 账号： 88322000057880000038            附件";
        String[] removeText={
                "/wps/contenthandler/ebid/!ut/p/digest!mNxE8R0tr4bpN964Me4g1g/pm/oid:--portletwindowid--@oid:6_A95CAT6JK55Q50IHDU8Q6C1KO5",
                "/wps/contenthandler/ebid/!ut/p/digest!mNxE8R0tr4bpN964Me4g1g/um/secure/currentuser/profile?expandRefs=true",
                "首页 &gt;&gt; 公开招标公告 &gt;&gt; 详细信息",
                "                                 "
        };
        for (int i=0;i<removeText.length;i++){
            if(test.contains(removeText[i])){
                test=test.replace(removeText[i],"");
            }
        }
       log.info("test=={}",test);
    }

    @Test
    public void testTime(){
        String time=PageProcessorUtil.dataTxt("发布时间：2016-05-09");
        log.info("time={}",time);
        log.info("{}", PageProcessorUtil.dataTxt("2017-10-18 15:14"));
    }

    @Test
    public void testFormatText() throws IOException {
        HttpClientDownloader httpClientDownloader=new HttpClientDownloader();
        Request request = new Request("http://eportal.energyahead.com/wps/portal/ebid/!ut/p/c5/hY3LdoIwEEC_xQ_wJGgCdRkRgjwKCGJg4wnWglBelofy9cWebrUzyztzL4jAtCXvLwlvL1XJvwADkXgkKywTX9QNjF0Mt9pm_-aKsmDYeOLhU44C9M_34dF7fgHhS_7rf3D4ZAgE71pVnEEIImmyLP8s5mqy-ObOsX20pBoCPmAQHb0M1pb5XVrjynZ8JW-902hB92zUtlt-zXmB6wDVXarRNI-LtAupnZWH2r2SRPbaYO2-7gjSo8PCQZIrMiiEOShRcwUeoMrvbUyN5c3Za_eQoyz-ZI3OhiIt6PpuW5wG2WYeJpnG1cWwOzX0NASumW4qRlTlyrogwk1kfxiU7HeCRHQdZZ0wL8um39pCHo35uhfVfOGNC_FmoLqXWLSOw1xOrBmoCwYvTnrFFZn9AIuHl4s!/dl3/d3/L0lDU0dabVppbW1BIS9JTFNBQ0l3a0FnUWlRQ0NLSkFJRVlrQWdUaVFDQ0JKQUlJbFNRQ0FKMkZEUS80QzFiOFVBZy83X0E5M0NBVDZKSzVMOTUwSVRMUlBPVDQzRzE3L2RldGFpbA!!/");
        Map<String, Object> param = Maps.newHashMap();
        param.put("documentId", "1977971");
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(param, "UTF-8"));
        Page page = httpClientDownloader.download(request, SiteUtil.get().toTask());
        Element element = page.getHtml().getDocument().body();
        Element formatContentHtml = element.select("#wptheme_pageArea").first();
        String formatContent = PageProcessorUtil.formatElementsByWhitelist(formatContentHtml);
        String textContent =PageProcessorUtil.extractTextByWhitelist(formatContentHtml);
        String textContent1 =PageProcessorUtil.formatText(textContent);
        log.info("formatContent =={}",formatContent );
        log.info("textContent =={}",textContent );
        log.info("textContent =={}",textContent1 );
    }


}
