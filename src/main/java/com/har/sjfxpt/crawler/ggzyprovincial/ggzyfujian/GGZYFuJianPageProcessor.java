package com.har.sjfxpt.crawler.ggzyprovincial.ggzyfujian;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyfujian.GGZYFuJianPageProcessor.*;

/**
 * Created by Administrator on 2017/12/12.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZYFUJIAN,
        sources = {
                @Source(url = GGZYFUJIAN_URL, post = true, postParams = POST_PARAMS_01, dateStartField = "TopTime", dateEndField = "EndTime", dayScope = "1D"),
                @Source(url = GGZYFUJIAN_URL, post = true, postParams = POST_PARAMS_02, dateStartField = "TopTime", dateEndField = "EndTime", dayScope = "1D"),
        }
)
public class GGZYFuJianPageProcessor implements BasePageProcessor {

    public final static String GGZYFUJIAN_URL = "https://www.fjggfw.gov.cn/Website/AjaxHandler/BuilderHandler.ashx";

    public final static String POST_PARAMS_01 = "{'EndTime':'2018-01-05 23:59:59','TopTime':'2018-01-05 00:00:00','pageNo':'1','OPtype':'GetListNew','proArea':'-1','ProType':'-1','pageSize':'10','xmlx':'-1','announcementType':'-1','category':'GCJS','projectName':''}";
    public final static String POST_PARAMS_02 = "{'EndTime':'2018-01-05 23:59:59','TopTime':'2018-01-05 00:00:00','pageNo':'1','OPtype':'GetListNew','proArea':'-1','ProType':'-1','pageSize':'10','xmlx':'-1','announcementType':'-1','category':'ZFCG','projectName':''}";

    HttpClientDownloader httpClientDownloader;

    final static String PAGE_PARAMS = "pageParams";

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int pageNo = Integer.parseInt(pageParams.get("pageNo").toString());
        int pageSize = Integer.parseInt(pageParams.get("pageSize").toString());
        if (pageNo == 1) {
            GGZYFuJianAnnouncement ggzyFuJianAnnouncement = JSONObject.parseObject(page.getRawText(), GGZYFuJianAnnouncement.class);
            int announcementNum = ggzyFuJianAnnouncement.getTotal();
            int pageCount = announcementNum % pageSize == 0 ? announcementNum / pageSize : announcementNum / pageSize + 1;
            log.debug("pageCount=={}", pageCount);
            for (int i = 2; i <= pageCount; i++) {
                pageParams.put("pageNo", i);
                Request request = new Request(url);
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
                request.putExtra(PAGE_PARAMS, pageParams);
                page.addTargetRequest(request);
            }
        }


    }

    @Override
    public void handleContent(Page page) {
        List<GGZYFuJianDataItem> dataItems = Lists.newArrayList();
        GGZYFuJianAnnouncement ggzyFuJianAnnouncement = JSONObject.parseObject(page.getRawText(), GGZYFuJianAnnouncement.class);
        List<GGZYFuJianAnnouncement.DataBean> dataBeanList = ggzyFuJianAnnouncement.getData();
        for (GGZYFuJianAnnouncement.DataBean dataBean : dataBeanList) {
            String kind = dataBean.getKIND();
            String type = dataBean.getTITLE();
            String ggtype = dataBean.getGGTYPE();
            String title = dataBean.getNAME();
            int m_id = dataBean.getM_ID();
            String source = dataBean.getPLATFORM_NAME();
            String date = dataBean.getTM();
            String procode = (String) dataBean.getPROCODE();


            if (StringUtils.endsWithIgnoreCase(kind, "GCJS")) {
                String href = "https://www.fjggfw.gov.cn/Website/JYXX_" + kind + ".aspx?ID=" + m_id + "&GGTYPE=" + ggtype;
                GGZYFuJianDataItem ggzyFuJianDataItem = new GGZYFuJianDataItem(href);
                ggzyFuJianDataItem.setUrl(href);
                ggzyFuJianDataItem.setTitle(title);
                ggzyFuJianDataItem.setType(type);
                ggzyFuJianDataItem.setSource(source);
                ggzyFuJianDataItem.setDate(DateTime.parse(date, DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")).toString("yyyy-MM-dd HH:mm"));
                ggzyFuJianDataItem.setBusinessType("工程建设");
                String getFormContentUrl = "https://www.fjggfw.gov.cn/Website/AjaxHandler/BuilderHandler.ashx?OPtype=GetGGInfoPC&ID=" + m_id + "&GGTYPE=" + ggtype + "&url=AjaxHandler%2FBuilderHandler.ashx";
                Page page1 = httpClientDownloader.download(new Request(getFormContentUrl), SiteUtil.get().setTimeOut(50000).toTask());
                GGZYFuJianContentAnnouncement ggzyFuJianContentAnnouncement = JSONObject.parseObject(page1.getRawText(), GGZYFuJianContentAnnouncement.class);
                log.debug("getFormContentUrl=={}", getFormContentUrl);
                int resultNum = ggzyFuJianContentAnnouncement.getResult2();
                String formatContent = ggzyFuJianContentAnnouncement.getData().get(resultNum - 1).toString();
                if (StringUtils.isNotBlank(formatContent)) {
                    ggzyFuJianDataItem.setFormatContent(formatContent);
                    dataItems.add(ggzyFuJianDataItem);
                }
            }
            if (StringUtils.endsWithIgnoreCase(kind, "ZFCG")) {
                String href = "https://www.fjggfw.gov.cn/Website/JYXX_Content/ZFCG.aspx?PROCODE=" + procode + "&GGTYPE=" + ggtype;
                GGZYFuJianDataItem ggzyFuJianDataItem = new GGZYFuJianDataItem(href);
                ggzyFuJianDataItem.setUrl(href);
                ggzyFuJianDataItem.setTitle(title);
                ggzyFuJianDataItem.setType(type);
                ggzyFuJianDataItem.setSource(source);
                ggzyFuJianDataItem.setDate(DateTime.parse(date, DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")).toString("yyyy-MM-dd HH:mm"));
                ggzyFuJianDataItem.setBusinessType("政府采购");
                Request request = new Request("https://www.fjggfw.gov.cn/Website/AjaxHandler/BuilderHandler.ashx");
                Map<String, Object> pageParams = Maps.newHashMap();
                pageParams.put("OPtype", "GetJYXXContentZFCG");
                pageParams.put("PROCODE", procode);
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
                Page page1 = httpClientDownloader.download(request, SiteUtil.get().setTimeOut(30000).toTask());
                GGZYFuJianZFCGContentAnnouncement ggzyFuJianZFCGContentAnnouncement = JSONObject.parseObject(page1.getRawText(), GGZYFuJianZFCGContentAnnouncement.class);
                int filedCount = Integer.parseInt(ggtype);
                String content = "";
                if (filedCount == 3) {
                    GGZYFuJianZFCGContentAnnouncement.Data3Bean data3Bean = ggzyFuJianZFCGContentAnnouncement.getData3().get(0);
                    String purchaser_name = data3Bean.getPURCHASER_NAME();
                    String supplier_name = data3Bean.getSUPPLIER_NAME();
                    int contract_amount = data3Bean.getCONTRACT_AMOUNT();
                    String price_unit_text = data3Bean.getPRICE_UNIT_TEXT();
                    String currency_code_text = data3Bean.getCURRENCY_CODE_TEXT();
                    String contract_term = data3Bean.getCONTRACT_TERM();
                    String formatContent = "<div class=\"detail_content\"><table class=\"detail_Table\" cellspacing=\"1\" cellpadding=\"1\"><tbody><tr><th>采购人名称</th><td>" + purchaser_name + "</td></tr><tr><th>中标（成交）供应商名称</th><td>" + supplier_name + "</td></tr><tr><th>合同金额</th><td>" + contract_amount + price_unit_text + currency_code_text + "</td></tr><tr><th>合同期限</th><td>" + contract_term + "</td></tr></tbody></table></div>";
                    if (StringUtils.isNotBlank(formatContent)) {
                        ggzyFuJianDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyFuJianDataItem);
                    }
                } else {
                    if (filedCount == 1 || filedCount == 4) {
                        content = ggzyFuJianZFCGContentAnnouncement.getData().get(0).toString();
                    }
                    if (filedCount == 2) {
                        if (ggzyFuJianZFCGContentAnnouncement.getData().size() == 1) {
                            content = ggzyFuJianZFCGContentAnnouncement.getData().get(0).toString();
                        } else {
                            content = ggzyFuJianZFCGContentAnnouncement.getData().get(1).toString();
                        }
                    }
                    GGZYFuJianZFCGDetailAnnouncement ggzyFuJianZFCGDetailAnnouncement = JSONObject.parseObject(content, GGZYFuJianZFCGDetailAnnouncement.class);
                    String formatContent = ggzyFuJianZFCGDetailAnnouncement.getCONTENT();
                    if (StringUtils.isNotBlank(formatContent)) {
                        ggzyFuJianDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyFuJianDataItem);
                    }
                }

            }

            if (!dataItems.isEmpty()) {
                page.putField(KEY_DATA_ITEMS, dataItems);
            } else {
                log.warn("fetch {} no data", page.getUrl().get());
            }
        }
    }

    @Override
    public List parseContent(Elements items) {

        return null;
    }

    @Override
    public void process(Page page) {
        handlePaging(page);
        handleContent(page);
    }

    @Override
    public Site getSite() {
        httpClientDownloader = new HttpClientDownloader();
        return SiteUtil.get().setSleepTime(10000);
    }
}
