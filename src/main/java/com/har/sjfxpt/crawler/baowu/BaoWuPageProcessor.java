package com.har.sjfxpt.crawler.baowu;

import com.alibaba.fastjson.JSONObject;
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
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.baowu.BaoWuPageProcessor.*;
import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/12/22.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.BAOWU,
        sources = {
                @Source(url = SEED_URL1, post = true, postParams = POST_PARAMS_01),
                @Source(url = SEED_URL2, post = true, postParams = POST_PARAMS_02),
                @Source(url = SEED_URL2, post = true, postParams = POST_PARAMS_03),
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
            BaoWuAnnouncementPageOne baoWuAnnouncementPageOne = JSONObject.parseObject(page.getRawText(), BaoWuAnnouncementPageOne.class);
            int pages = baoWuAnnouncementPageOne.getObj().getPages();
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

    public BidNewsOriginal bidNewsOriginalGenerate(String href, String title, String purchaser, String date, String type) {
        if (StringUtils.isNotBlank(href)) {
            href = "http://rfq.ouyeelbuy.com/rfqNotice/bidListInfo?id=" + href;
            BidNewsOriginal baoWuDataItem = new BidNewsOriginal(href);
            baoWuDataItem.setUrl(href);
            baoWuDataItem.setSourceCode(SourceCode.BAOWU.name());
            baoWuDataItem.setSource(SourceCode.BAOWU.getValue());
            baoWuDataItem.setTitle(title);
            baoWuDataItem.setPurchaser(purchaser);
            baoWuDataItem.setDate(PageProcessorUtil.dataTxt(date));
            baoWuDataItem.setProvince(ProvinceUtil.get(title));
            baoWuDataItem.setType(type);

            if (PageProcessorUtil.timeCompare(baoWuDataItem.getDate())) {
                log.info("{} is not the same day", baoWuDataItem.getUrl());
            } else {
                Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
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

    public BidNewsOriginal bidNewsOriginalGenerateOther(String href, String title, String purchaser, String date, String typeField) {
        if (StringUtils.isNotBlank(href)) {
            BidNewsOriginal baoWuDataItem = new BidNewsOriginal(href);
            baoWuDataItem.setUrl(href);
            baoWuDataItem.setTitle(title);
            baoWuDataItem.setPurchaser(purchaser);
            baoWuDataItem.setSourceCode(SourceCode.BAOWU.name());
            baoWuDataItem.setSource(SourceCode.BAOWU.getValue());
            baoWuDataItem.setDate(PageProcessorUtil.dataTxt(date));
            baoWuDataItem.setProvince(ProvinceUtil.get(title));

            if (PageProcessorUtil.timeCompare(baoWuDataItem.getDate())) {
                log.info("{} is not the same day", baoWuDataItem.getUrl());
            } else {
                if ("0".equalsIgnoreCase(typeField)) {
                    baoWuDataItem.setType("招标公告");
                    Page page1 = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    Element element = page1.getHtml().getDocument().body();
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(element);
                    if (StringUtils.isNotBlank(formatContent)) {
                        baoWuDataItem.setFormatContent(formatContent);
                        return baoWuDataItem;
                    }
                }
                if ("1".equalsIgnoreCase(typeField)) {
                    baoWuDataItem.setType("中标结果");
                    Page page1 = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
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
                BaoWuAnnouncementPageOne baoWuAnnouncementPageOne = JSONObject.parseObject(page.getRawText(), BaoWuAnnouncementPageOne.class);
                List<BaoWuAnnouncementPageOne.ObjBean.NewsPageBean> newsPageBeanList = baoWuAnnouncementPageOne.getObj().getNewsPage();
                for (BaoWuAnnouncementPageOne.ObjBean.NewsPageBean newsPageBean : newsPageBeanList) {
                    BidNewsOriginal baoWuDataItem = bidNewsOriginalGenerate(newsPageBean.getId(), newsPageBean.getTitle(), newsPageBean.getOuName(), newsPageBean.getIssueDate(), "采购公告");
                    if (baoWuDataItem != null) {
                        dataItems.add(baoWuDataItem);
                    }
                }
                List<BaoWuAnnouncementPageOne.ObjBean.ListBean> listBeans = baoWuAnnouncementPageOne.getObj().getList();
                for (BaoWuAnnouncementPageOne.ObjBean.ListBean listBean : listBeans) {
                    BidNewsOriginal baoWuDataItem = bidNewsOriginalGenerate(listBean.getId(), listBean.getTitle(), listBean.getOuName(), listBean.getIssueDate(), "采购公告");
                    if (baoWuDataItem != null) {
                        dataItems.add(baoWuDataItem);
                    }
                }
            } else {
                BaoWuAnnouncementPageAnother baoWuAnnouncementPageAnother = JSONObject.parseObject(page.getRawText(), BaoWuAnnouncementPageAnother.class);
                List<BaoWuAnnouncementPageAnother.ObjBean.ListBean> listBeans = baoWuAnnouncementPageAnother.getObj().getList();
                for (BaoWuAnnouncementPageAnother.ObjBean.ListBean listBean : listBeans) {
                    BidNewsOriginal baoWuDataItem = bidNewsOriginalGenerate(listBean.getId(), listBean.getTitle(), listBean.getOuName(), listBean.getIssueDate(), "采购公告");
                    if (baoWuDataItem != null) {
                        dataItems.add(baoWuDataItem);
                    }
                }
            }
        } else {
            if (pageParams.get("pageNow").toString().equalsIgnoreCase("1")) {
                BaoWuAnnouncementOther baoWuAnnouncementOther = JSONObject.parseObject(page.getRawText(), BaoWuAnnouncementOther.class);
                List<BaoWuAnnouncementOther.ObjBean.NewsPageBean> newsPageBeanList = baoWuAnnouncementOther.getObj().getNewsPage();
                for (BaoWuAnnouncementOther.ObjBean.NewsPageBean newsPageBean : newsPageBeanList) {
                    BidNewsOriginal baoWuDataItem = bidNewsOriginalGenerateOther(newsPageBean.getNoticeUrl(), newsPageBean.getNoticeName(), newsPageBean.getOuName(), newsPageBean.getIssueDate(), typeField);
                    if (baoWuDataItem != null) {
                        dataItems.add(baoWuDataItem);
                    }
                }
            } else {
                BaoWuAnnouncementOtherNext baoWuAnnouncementOtherNext = JSONObject.parseObject(page.getRawText(), BaoWuAnnouncementOtherNext.class);
                List<BaoWuAnnouncementOtherNext.ObjBean.ListBean> listBeans = baoWuAnnouncementOtherNext.getObj().getList();
                for (BaoWuAnnouncementOtherNext.ObjBean.ListBean listBean : listBeans) {
                    BidNewsOriginal baoWuDataItem = bidNewsOriginalGenerateOther(listBean.getNoticeUrl(), listBean.getNoticeName(), listBean.getOuName(), listBean.getIssueDate(), typeField);
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
