package com.har.sjfxpt.crawler.ggzy.provincial;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
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
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.provincial.FuJianPageProcessor.*;

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
public class FuJianPageProcessor implements BasePageProcessor {

    public final static String GGZYFUJIAN_URL = "https://www.fjggfw.gov.cn/Website/AjaxHandler/BuilderHandler.ashx";

    public final static String POST_PARAMS_01 = "{'EndTime':'','TopTime':'','pageNo':'1','OPtype':'GetListNew','proArea':'-1','ProType':'-1','pageSize':'10','xmlx':'-1','announcementType':'-1','category':'GCJS','projectName':''}";
    public final static String POST_PARAMS_02 = "{'EndTime':'','TopTime':'','pageNo':'1','OPtype':'GetListNew','proArea':'-1','ProType':'-1','pageSize':'10','xmlx':'-1','announcementType':'-1','category':'ZFCG','projectName':''}";

    HttpClientDownloader httpClientDownloader;

    final static String PAGE_PARAMS = "pageParams";

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int pageNo = Integer.parseInt(pageParams.get("pageNo").toString());
        int pageSize = Integer.parseInt(pageParams.get("pageSize").toString());
        if (pageNo == 1) {
            JSONObject root = (JSONObject) JSONObject.parse(page.getRawText());
            int announcementNum = (int) JSONPath.eval(root, "$.total");
            int pageCount = announcementNum % pageSize == 0 ? announcementNum / pageSize : announcementNum / pageSize + 1;
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
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        Selectable data = page.getJson().jsonPath("$.data");
        List<String> stringList = data.all();
        for (String field : stringList) {
            JSONObject dataBean = (JSONObject) JSONObject.parse(field);
            String kind = JSONPath.eval(dataBean, "$.KIND").toString();
            String type = JSONPath.eval(dataBean, "$.TITLE").toString();
            String ggType = JSONPath.eval(dataBean, "$.GGTYPE").toString();
            String title = JSONPath.eval(dataBean, "$.NAME").toString();
            String m_id = JSONPath.eval(dataBean, "$.M_ID").toString();
            String date = JSONPath.eval(dataBean, "$.TM").toString();

            if (StringUtils.endsWithIgnoreCase(kind, "GCJS")) {
                if (StringUtils.containsIgnoreCase(m_id, ".0")) {
                    m_id = StringUtils.substringBefore(m_id, ".0");
                }
                String href = "https://www.fjggfw.gov.cn/Website/JYXX_" + kind + ".aspx?ID=" + m_id + "&GGTYPE=" + ggType;
                BidNewsOriginal ggzyFuJianDataItem = new BidNewsOriginal(href, SourceCode.GGZYFUJIAN);
                ggzyFuJianDataItem.setProvince("福建");
                ggzyFuJianDataItem.setTitle(title);
                ggzyFuJianDataItem.setType(type);
                ggzyFuJianDataItem.setDate(DateTime.parse(date, DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")).toString("yyyy-MM-dd HH:mm"));


                String getFormContentUrl = "https://www.fjggfw.gov.cn/Website/AjaxHandler/BuilderHandler.ashx?OPtype=GetGGInfoPC&ID=" + m_id + "&GGTYPE=" + ggType + "&url=AjaxHandler%2FBuilderHandler.ashx";
                try {
                    Page page1 = httpClientDownloader.download(new Request(getFormContentUrl), SiteUtil.get().setTimeOut(50000).toTask());
                    Selectable dataDetail = page1.getJson().jsonPath("$.data");
                    List<String> stringDetailList = dataDetail.all();
                    Selectable resultJsonNum = page1.getJson().jsonPath("$.result2");
                    int num = Integer.parseInt(resultJsonNum.toString());
                    String formatContentJson = stringDetailList.get(num - 1);
                    if (StringUtils.isNotBlank(formatContentJson)) {
                        ggzyFuJianDataItem.setFormatContent(PageProcessorUtil.formatElementsByWhitelist(formatContentJson));
                        dataItems.add(ggzyFuJianDataItem);
                    }
                } catch (Exception e) {
                    log.error("", e);
                    log.error("url={}", getFormContentUrl);
                }
            }

            if (StringUtils.endsWithIgnoreCase(kind, "ZFCG")) {
                String procode = JSONPath.eval(dataBean, "$.PROCODE").toString();
                String href = "https://www.fjggfw.gov.cn/Website/JYXX_Content/ZFCG.aspx?PROCODE=" + procode + "&GGTYPE=" + ggType;
                BidNewsOriginal ggzyFuJianDataItem = new BidNewsOriginal(href, SourceCode.GGZYFUJIAN);
                ggzyFuJianDataItem.setProvince("福建");
                ggzyFuJianDataItem.setTitle(title);
                ggzyFuJianDataItem.setType(type);
                ggzyFuJianDataItem.setDate(DateTime.parse(date, DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")).toString("yyyy-MM-dd HH:mm"));

                Request request = new Request("https://www.fjggfw.gov.cn/Website/AjaxHandler/BuilderHandler.ashx");
                Map<String, Object> pageParams = Maps.newHashMap();
                pageParams.put("OPtype", "GetJYXXContentZFCG");
                pageParams.put("PROCODE", procode);
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
                int filedCount = Integer.parseInt(ggType);
                try {
                    Page page1 = httpClientDownloader.download(request, SiteUtil.get().setTimeOut(30000).toTask());
                    String jsonContent = "";
                    if (filedCount == 3) {
                        Selectable data3 = page1.getJson().jsonPath("$.data3");
                        List<String> data3list = data3.all();
                        String context = data3list.get(0);
                        JSONObject jsonObject = (JSONObject) JSONObject.parse(context);
                        String purchaserName = JSONPath.eval(jsonObject, "$.PURCHASER_NAME").toString();
                        String supplierName = JSONPath.eval(jsonObject, "$.SUPPLIER_NAME").toString();
                        String contractAmount = JSONPath.eval(jsonObject, "$.CONTRACT_AMOUNT").toString();
                        String priceUnitText = JSONPath.eval(jsonObject, "$.PRICE_UNIT_TEXT").toString();
                        String currencyCodeText = JSONPath.eval(jsonObject, "$.CURRENCY_CODE_TEXT").toString();
                        String contractTerm = JSONPath.eval(jsonObject, "$.CONTRACT_TERM").toString();
                        String formatContent = "<div class=\"detail_content\"><table class=\"detail_Table\" cellspacing=\"1\" cellpadding=\"1\"><tbody><tr><th>采购人名称</th><td>" + purchaserName + "</td></tr><tr><th>中标（成交）供应商名称</th><td>" + supplierName + "</td></tr><tr><th>合同金额</th><td>" + contractAmount + priceUnitText + currencyCodeText + "</td></tr><tr><th>合同期限</th><td>" + contractTerm + "</td></tr></tbody></table></div>";
                        if (StringUtils.isNotBlank(purchaserName) || StringUtils.isNotBlank(supplierName)) {
                            ggzyFuJianDataItem.setTotalBidMoney(contractAmount + priceUnitText + currencyCodeText);
                            ggzyFuJianDataItem.setFormatContent(PageProcessorUtil.formatElementsByWhitelist(formatContent));
                            dataItems.add(ggzyFuJianDataItem);
                        }
                    } else {
                        Selectable dataList = page1.getJson().jsonPath("$.data");
                        List<String> datalist = dataList.all();
                        if (filedCount == 1 || filedCount == 4) {
                            jsonContent = datalist.get(0);
                        }
                        if (filedCount == 2) {
                            if (datalist.size() == 1) {
                                jsonContent = datalist.get(0).toString();
                            } else {
                                jsonContent = datalist.get(1).toString();
                            }
                        }
                        JSONObject jsonObject = (JSONObject) JSONObject.parse(jsonContent);
                        String formatContent = JSONPath.eval(jsonObject, "$.CONTENT").toString();
                        if (StringUtils.isNotBlank(formatContent)) {
                            ggzyFuJianDataItem.setFormatContent(PageProcessorUtil.formatElementsByWhitelist(formatContent));
                            dataItems.add(ggzyFuJianDataItem);
                        }
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
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
