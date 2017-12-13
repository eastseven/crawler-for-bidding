package com.har.sjfxpt.crawler.ggzy.ggzypageprocessottests;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyfujian.GGZYFuJianContentAnnouncement;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyfujian.GGZYFuJianPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyfujian.GGZYFuJianPipeline;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyfujian.GGZYFuJianZFCGContentAnnouncement;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;
import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyfujian.GGZYFuJianSpiderLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/12/12.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYFuJianPageProcessorTests {

    @Autowired
    GGZYFuJianPageProcessor ggzyFuJianPageProcessor;

    @Autowired
    GGZYFuJianPipeline ggzyFuJianPipeline;

    @Test
    public void testGGZYFuJianPageProcessor() {
        String url = "https://www.fjggfw.gov.cn/Website/AjaxHandler/BuilderHandler.ashx";
        Request[] requests = {
                requestGenerator(url, DateTime.now().toString("yyyy-MM-dd"), "GCJS"),
                requestGenerator(url, DateTime.now().toString("yyyy-MM-dd"), "ZFCG")
        };
        Spider.create(ggzyFuJianPageProcessor)
                .addRequest(requests)
                .thread(THREAD_NUM)
                .addPipeline(ggzyFuJianPipeline)
                .run();
    }




    @Test
    public void testFuJian() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Page page = httpClientDownloader.download(new Request("https://www.fjggfw.gov.cn/Website/AjaxHandler/BuilderHandler.ashx?OPtype=GetGGInfoPC&ID=61426&GGTYPE=3&url=AjaxHandler%2FBuilderHandler.ashx"), SiteUtil.get().setTimeOut(30000).toTask());
        GGZYFuJianContentAnnouncement ggzyFuJianContentAnnouncement = JSONObject.parseObject(page.getRawText(), GGZYFuJianContentAnnouncement.class);
        int resultNum = ggzyFuJianContentAnnouncement.getResult2();
        log.info("{}", ggzyFuJianContentAnnouncement.getData().get(resultNum - 1).toString());
    }

    @Test
    public void testDownloadPage() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Request request = new Request("https://www.fjggfw.gov.cn/Website/AjaxHandler/BuilderHandler.ashx");
        Map<String, Object> pageParams = Maps.newHashMap();
        pageParams.put("OPtype", "GetJYXXContentZFCG");
        pageParams.put("PROCODE", "D03-12350000315580441D-20170928-105294-5");
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
        Page page = httpClientDownloader.download(request, SiteUtil.get().setTimeOut(30000).toTask());
        GGZYFuJianZFCGContentAnnouncement ggzyFuJianZFCGContentAnnouncement = JSONObject.parseObject(page.getRawText(), GGZYFuJianZFCGContentAnnouncement.class);
        GGZYFuJianZFCGContentAnnouncement.Data3Bean data3Bean = ggzyFuJianZFCGContentAnnouncement.getData3().get(0);
        String purchaser_name = data3Bean.getPURCHASER_NAME();
        String supplier_name = data3Bean.getSUPPLIER_NAME();
        int contract_amount = data3Bean.getCONTRACT_AMOUNT();
        String price_unit_text = data3Bean.getPRICE_UNIT_TEXT();
        String currency_code_text = data3Bean.getCURRENCY_CODE_TEXT();
        String contract_term = data3Bean.getCONTRACT_TERM();
        String formatContent = "<div class=\"detail_content\"><table class=\"detail_Table\" cellspacing=\"1\" cellpadding=\"1\"><tbody><tr><th>采购人名称</th><td>" + purchaser_name + "</td></tr><tr><th>中标（成交）供应商名称</th><td>" + supplier_name + "</td></tr><tr><th>合同金额</th><td>" + contract_amount + price_unit_text + currency_code_text + "</td></tr><tr><th>合同期限</th><td>" + contract_term + "</td></tr></tbody></table></div>";
        log.info("formatContent=={}", formatContent);
    }

    @Test
    public void testTime() throws ParseException {
        String time = "2017-12-13T10:55:44";
//        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
//        DateTime dateTime = DateTime.parse(time, dateTimeFormat);
//        log.info("time=={}",dateTime.toString("yyyy-MM-dd HH:mm"));
        log.info("time=={}",DateTime.parse(time,DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")).toString("yyyy-MM-dd HH:mm"));
    }


}
