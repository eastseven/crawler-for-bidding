package com.har.sjfxpt.crawler.baowu;

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
import com.har.sjfxpt.crawler.core.utils.ProvinceUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
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

import static com.har.sjfxpt.crawler.baowu.BaoWuPageProcessor.POST_PARAMS_01;
import static com.har.sjfxpt.crawler.baowu.BaoWuPageProcessor.SEED_URL1;

/**
 * Created by Administrator on 2017/12/22.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.BAOWU,
        sources = {
                @Source(url = SEED_URL1, post = true, postParams = POST_PARAMS_01),
                //数据有误暂时放弃抓取
//                @Source(url = SEED_URL2, post = true, postParams = POST_PARAMS_02),
//                @Source(url = SEED_URL2, post = true, postParams = POST_PARAMS_03),
        }
)
public class BaoWuPageProcessor implements BasePageProcessor {

    final static String PAGE_PARAMS = "pageParams";

    HttpClientDownloader httpClientDownloader;

    public static final String SEED_URL1 = "http://baowu.ouyeelbuy.com/baowu-shp/notice/purchaseMore";

    public static final String SEED_URL2 = "http://baowu.ouyeelbuy.com/baowu-shp/notice/moreBiddingNoticeList";

    public static final String POST_PARAMS_01 = "{'jqMthod':'newsList','type':'purchase','pageNow':'1'}";

    public static final String POST_PARAMS_02 = "{'jqMthod':'newsList','type':'0','pageNow':'1'}";

    public static final String POST_PARAMS_03 = "{'jqMthod':'newsList','type':'1','pageNow':'1'}";

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        log.info("url={}", url);
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int pageNow = Integer.parseInt(pageParams.get("pageNow").toString());
        if (pageNow == 1) {
            JSONObject root = (JSONObject) JSONObject.parse(page.getRawText());
            int pages = (int) JSONPath.eval(root, "$.obj.pages");
            log.debug("pages={}", pages);
            int cycleNum = pages >= 3 ? 3 : pages;
            for (int i = 2; i <= cycleNum; i++) {
                Map<String, Object> pageParamsNew = Maps.newHashMap();
                pageParamsNew.put("type", pageParams.get("type"));
                pageParamsNew.put("pageNow", i);
                pageParamsNew.put("jqMthod", pageParams.get("jqMthod"));
                Request request = new Request(url);
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(pageParamsNew, "UTF-8"));
                request.putExtra(PAGE_PARAMS, pageParamsNew);
                page.addTargetRequest(request);
            }
        }

    }

    @Override
    public void handleContent(Page page) {
        List<BidNewsOriginal> dataItems = parseContent(page);
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    public BidNewsOriginal bidNewsOriginalGenerate(String field, String type) {
        JSONObject pageBean = (JSONObject) JSONObject.parse(field);
        String id = JSONPath.eval(pageBean, "$.id").toString();
        String title = JSONPath.eval(pageBean, "$.title").toString();
        String ouName = JSONPath.eval(pageBean, "$.ouName").toString();
        String issueDate = JSONPath.eval(pageBean, "$.issueDate").toString();
        if (StringUtils.isNotBlank(id)) {
            id = "http://rfq.ouyeelbuy.com/rfqNotice/bidListInfo?id=" + id;
            BidNewsOriginal baoWuDataItem = new BidNewsOriginal(id, SourceCode.BAOWU);
            baoWuDataItem.setTitle(title);
            baoWuDataItem.setPurchaser(ouName);
            baoWuDataItem.setDate(PageProcessorUtil.dataTxt(issueDate));
            baoWuDataItem.setProvince(ProvinceUtil.get(title));
            baoWuDataItem.setType(type);

            if (PageProcessorUtil.timeCompare(baoWuDataItem.getDate())) {
                log.info("{} is not the same day", baoWuDataItem.getUrl());
            } else {
                Page page = httpClientDownloader.download(new Request(id), SiteUtil.get().setTimeOut(30000).toTask());
                Elements elements = page.getHtml().getDocument().body().select("body > div.xj_content > div > div.left");
                String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                if (StringUtils.isNotBlank(formatContent)) {
                    baoWuDataItem.setFormatContent(formatContent);
                    return baoWuDataItem;
                }
            }
        }
        return null;
    }

    public BidNewsOriginal bidNewsOriginalGenerateOther(String field, String typeField) {
        JSONObject pageBean = (JSONObject) JSONObject.parse(field);
        String noticeUrl = JSONPath.eval(pageBean, "$.noticeUrl").toString();
        String noticeName = JSONPath.eval(pageBean, "$.noticeName").toString();
        String ouName = JSONPath.eval(pageBean, "$.ouName").toString();
        String issueDate = JSONPath.eval(pageBean, "$.issueDate").toString();
        if (StringUtils.isNotBlank(noticeUrl)) {
            BidNewsOriginal baoWuDataItem = new BidNewsOriginal(noticeUrl, SourceCode.BAOWU);
            baoWuDataItem.setTitle(noticeName);
            baoWuDataItem.setPurchaser(ouName);
            baoWuDataItem.setDate(PageProcessorUtil.dataTxt(issueDate));
            baoWuDataItem.setProvince(ProvinceUtil.get(noticeName));

            if (PageProcessorUtil.timeCompare(baoWuDataItem.getDate())) {
                log.info("{} is not the same day", baoWuDataItem.getUrl());
            } else {
                if ("0".equalsIgnoreCase(typeField)) {
                    baoWuDataItem.setType("招标公告");
                    Page page1 = httpClientDownloader.download(new Request(noticeUrl), SiteUtil.get().setTimeOut(30000).toTask());
                    Element element = page1.getHtml().getDocument().body();
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(element);
                    if (StringUtils.isNotBlank(formatContent)) {
                        baoWuDataItem.setFormatContent(formatContent);
                        return baoWuDataItem;
                    }
                }
                if ("1".equalsIgnoreCase(typeField)) {
                    baoWuDataItem.setType("中标结果");
                    Page page1 = httpClientDownloader.download(new Request(noticeUrl), SiteUtil.get().setTimeOut(30000).toTask());
                    Elements elements = page1.getHtml().getDocument().body().select("body > div > div > table:nth-child(1) > tbody > tr > th > div > table > tbody > tr > td > div");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        baoWuDataItem.setFormatContent(formatContent);
                        return baoWuDataItem;
                    }
                }
            }
        }
        return null;
    }


    public List parseContent(Page page) {
        log.info("request=={}", page.getRequest());
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        String typeField = pageParams.get("type").toString();
        if ("purchase".equalsIgnoreCase(typeField)) {
            if (pageParams.get("pageNow").toString().equalsIgnoreCase("1")) {
                Selectable newsPage = page.getJson().jsonPath("$.obj.newsPage");
                List<String> stringList = newsPage.all();
                for (String field : stringList) {
                    BidNewsOriginal baoWuDataItem = bidNewsOriginalGenerate(field, "采购公告");
                    if (baoWuDataItem != null) {
                        dataItems.add(baoWuDataItem);
                    }
                }
                Selectable list = page.getJson().jsonPath("$.obj.list");
                List<String> lists = list.all();
                for (String field : lists) {
                    BidNewsOriginal baoWuDataItem = bidNewsOriginalGenerate(field, "采购公告");
                    if (baoWuDataItem != null) {
                        dataItems.add(baoWuDataItem);
                    }
                }
            } else {
                Selectable list = page.getJson().jsonPath("$.obj.list");
                List<String> lists = list.all();
                for (String field : lists) {
                    BidNewsOriginal baoWuDataItem = bidNewsOriginalGenerate(field, "采购公告");
                    if (baoWuDataItem != null) {
                        dataItems.add(baoWuDataItem);
                    }
                }
            }
        } else {
            if (pageParams.get("pageNow").toString().equalsIgnoreCase("1")) {
                Selectable newsPage = page.getJson().jsonPath("$.obj.newsPage");
                List<String> stringList = newsPage.all();
                for (String field : stringList) {
                    BidNewsOriginal baoWuDataItem = bidNewsOriginalGenerateOther(field, typeField);
                    if (baoWuDataItem != null) {
                        dataItems.add(baoWuDataItem);
                    }
                }
            } else {
                Selectable list = page.getJson().jsonPath("$.obj.list");
                List<String> stringList = list.all();
                for (String field : stringList) {
                    BidNewsOriginal baoWuDataItem = bidNewsOriginalGenerateOther(field, typeField);
                    if (baoWuDataItem != null) {
                        dataItems.add(baoWuDataItem);
                    }
                }
            }
        }
        return dataItems;
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
        return SiteUtil.get().setSleepTime(10000).addHeader("content-type", "application/x-www-form-urlencoded");
    }
}
