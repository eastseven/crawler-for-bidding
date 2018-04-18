package com.har.sjfxpt.crawler.other;

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
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.other.ChinaMobilePageProcessor.*;

/**
 * @author dongqi
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.CM,
        sources = {
                @Source(url = SEED_URL1, post = true, postParams = POST_PARAMS_01, needPlaceholderFields = {"noticeBean.startDate", "noticeBean.endDate"}),
                @Source(url = SEED_URL2, post = true, postParams = POST_PARAMS_01, needPlaceholderFields = {"noticeBean.startDate", "noticeBean.endDate"}),
                @Source(url = SEED_URL3, post = true, postParams = POST_PARAMS_01, needPlaceholderFields = {"noticeBean.startDate", "noticeBean.endDate"}),
                @Source(url = SEED_URL4, post = true, postParams = POST_PARAMS_01, needPlaceholderFields = {"noticeBean.startDate", "noticeBean.endDate"}),
        }
)
public class ChinaMobilePageProcessor implements BasePageProcessor {

    final static String SEED_URL1 = "https://b2b.10086.cn/b2b/main/listVendorNoticeResult.html?noticeBean.noticeType=1";
    final static String SEED_URL2 = "https://b2b.10086.cn/b2b/main/listVendorNoticeResult.html?noticeBean.noticeType=2";
    final static String SEED_URL3 = "https://b2b.10086.cn/b2b/main/listVendorNoticeResult.html?noticeBean.noticeType=3";
    final static String SEED_URL4 = "https://b2b.10086.cn/b2b/main/listVendorNoticeResult.html?noticeBean.noticeType=7";

    public static final String POST_PARAMS_01 = "{'page.currentPage':'1','page.perPageSize':'20','noticeBean.sourceCH':'','noticeBean.source':'','noticeBean.title':'','noticeBean.startDate':'','noticeBean.endDate':''}";

    final static String PAGE_PARAMS = "pageParams";

    public static final String YYYYMMDD = "yyyy-MM-dd";

    final static String URL = "https://b2b.10086.cn/b2b/main/viewNoticeContent.html?noticeBean.id=";

    @Autowired
    HttpClientDownloader httpClientDownloader;

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

    @Override
    public void handlePaging(Page page) {
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtra(PAGE_PARAMS);
        int currentPage = Integer.parseInt(pageParams.get("page.currentPage").toString());
        if (currentPage == 1) {
            Elements pager = page.getHtml().getDocument().body().select("div.da_content_div_bg1");
            String totalSizeText = pager.select("input#totalRecordNum").attr("value");
            String totalPageText = "1";
            for (Element pagerLink : pager.select("a")) {
                String text = pagerLink.text();
                if (text.contains("尾页")) {
                    totalPageText = StringUtils.substringBetween(pagerLink.attr("onclick"), "(", ")");
                    break;
                }
            }
            log.debug(">>> size={}, page={}", totalSizeText, totalPageText);

            int totalPage = Integer.parseInt(totalPageText);
            for (int pageIndex = 2; pageIndex <= totalPage; pageIndex++) {
                Map<String, Object> params = Maps.newHashMap(pageParams);
                params.put("page.currentPage", pageIndex);
                Request request = new Request(page.getUrl().get());
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
                request.putExtra(PAGE_PARAMS, params);

                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Elements elements = page.getHtml().getDocument().body().select("table tr");
        if (elements.isEmpty()) {
            log.error("fetch error, elements is empty");
            return;
        }

        List<BidNewsOriginal> dataItemList = parseContent(elements);
        if (!dataItemList.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItemList);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        for (Element element : items) {
            if (!element.hasText()) continue;
            if (element.hasClass("zb_table_tr")) continue;
            if (!element.hasAttr("onclick")) continue;

            String id = StringUtils.substringBetween(element.attr("onclick"), "('", "')");
            String url = URL + id;
            try {
                String purchaser = element.select("td").get(0).text();
                String type = StringUtils.defaultString(element.select("td").get(1).text(), "其他");

                String title = element.select("td").get(2).select("a").attr("title");
                if (StringUtils.isBlank(title)) {
                    title = element.select("td").get(2).text();
                }
                String projectName = StringUtils.substringBefore(title, "_");

                String date = element.select("td").get(3).text();
                date = new DateTime(date).toString(YYYYMMDD);

                BidNewsOriginal dataItem = new BidNewsOriginal(url, SourceCode.CM);
                dataItem.setDate(PageProcessorUtil.dataTxt(date));
                dataItem.setTitle(title);
                dataItem.setProjectName(StringUtils.defaultString(projectName, ""));
                dataItem.setType(type);
                dataItem.setPurchaser(purchaser);
                dataItem.setProvince(ProvinceUtil.get(purchaser + " " + title));

                Document document = httpClientDownloader.download(new Request(url), SiteUtil.get().setTimeOut(60000).toTask()).getHtml().getDocument();
                Element root;
                if (!document.body().select("div#mobanDiv").isEmpty()) {
                    root = document.body().select("div#mobanDiv").first();
                } else {
                    root = document.body().select("div#container table").first();
                }
                String formatContent = PageProcessorUtil.formatElementsByWhitelist(root);
                dataItem.setFormatContent(formatContent);
                dataItems.add(dataItem);
            } catch (Exception e) {
                log.error("", e);
                log.error("url={}", url);
            }
        }
        return dataItems;
    }
}
