package com.har.sjfxpt.crawler.ccgp.provincial;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
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

import static com.har.sjfxpt.crawler.ccgp.provincial.HBPageProcessor.*;

/**
 * Created by Administrator on 2018/1/17.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.CCGPHEBEI,
        sources = {
                @Source(url = URL1, post = true, postParams = POST_PARAMS_1, type = "招标公告"),
                @Source(url = URL1, post = true, postParams = POST_PARAMS_2, type = "招标公告"),
                @Source(url = URL2, post = true, postParams = POST_PARAMS_3, type = "中标公告"),
                @Source(url = URL2, post = true, postParams = POST_PARAMS_4, type = "中标公告"),
                @Source(url = URL3, post = true, postParams = POST_PARAMS_5, type = "更正公告"),
                @Source(url = URL3, post = true, postParams = POST_PARAMS_6, type = "更正公告"),
                @Source(url = URL4, post = true, postParams = POST_PARAMS_7, type = "废标公告"),
                @Source(url = URL4, post = true, postParams = POST_PARAMS_8, type = "废标公告"),
                @Source(url = URL5, post = true, postParams = POST_PARAMS_9, type = "单一来源")
        }
)
public class HBPageProcessor implements BasePageProcessor {

    final static String URL1 = "http://www.ccgp-hebei.gov.cn/zfcg/web/getBidingList_1.html";
    final static String URL2 = "http://www.ccgp-hebei.gov.cn/zfcg/web/getBidWinAnncList_1.html";
    final static String URL3 = "http://www.ccgp-hebei.gov.cn/zfcg/web/getCorrectionAnncList_1.html";
    final static String URL4 = "http://www.ccgp-hebei.gov.cn/zfcg/web/getCancelBidAnncList_1.html";
    final static String URL5 = "http://www.ccgp-hebei.gov.cn/zfcg/web/getSingleSourceList_1.html";

    final static String POST_PARAMS_1 = "{'levelFlag':'first','citycode':'130000000','cityname':'省本级'}";
    final static String POST_PARAMS_2 = "{'levelFlag':'second','citycode':'130000000','cityname':'省本级'}";
    final static String POST_PARAMS_3 = "{'levelFlag2':'first','citycode':'130000000','cityname':'省本级'}";
    final static String POST_PARAMS_4 = "{'levelFlag2':'second','citycode':'130000000','cityname':'省本级'}";
    final static String POST_PARAMS_5 = "{'recordFlag':'first','citycode':'130000000','cityname':'省本级'}";
    final static String POST_PARAMS_6 = "{'recordFlag':'second','citycode':'130000000','cityname':'省本级'}";
    final static String POST_PARAMS_7 = "{'cancellFlag':'first','citycode':'130000000','cityname':'省本级'}";
    final static String POST_PARAMS_8 = "{'cancellFlag':'second','citycode':'130000000','cityname':'省本级'}";
    final static String POST_PARAMS_9 = "{'citycode':'130000000','cityname':'省本级'}";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        int pageCount = Integer.parseInt(StringUtils.substringBetween(url, "_", ".html"));
        if (pageCount == 1) {
            String type = page.getRequest().getExtra("type").toString();
            Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtra("pageParams");
            for (int i = 2; i <= 3; i++) {
                String urlTarget = StringUtils.replace(url, String.valueOf(pageCount), String.valueOf(i));
                Request request = new Request(urlTarget);
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
                request.putExtra("pageParams", pageParams);
                request.putExtra("type", type);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        String type = page.getRequest().getExtra("type").toString();
        Elements elements = elementsJudge(page, type);
        List<BidNewsOriginal> dataItems = parseContent(elements, type);
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    public Elements elementsJudge(Page page, String type) {
        Elements elements = null;
        if ("招标公告".equalsIgnoreCase(type)) {
            elements = page.getHtml().getDocument().body().select("#moredingannctable > tbody > tr");
        } else {
            elements = page.getHtml().getDocument().body().select("body > table > tbody > tr > td > table > tbody > tr:nth-child(2) > td:nth-child(2) > table:nth-child(2) > tbody > tr");
        }
        return elements;
    }

    public String urlJudge(String onclick, String type) {
        String href = null;
        if (StringUtils.isNotBlank(onclick)) {
            String[] params = StringUtils.substringsBetween(onclick, "'", "'");
            switch (type) {
                case "招标公告":
                    href = "http://www.ccgp-hebei.gov.cn/zfcg/" + params[1] + "/bidingAnncDetail_" + params[0] + ".html";
                    break;
                case "中标公告":
                    href = "http://www.ccgp-hebei.gov.cn/zfcg/bidWinAnncDetail_" + params[0] + ".html";
                    break;
                case "更正公告":
                    href = "http://www.ccgp-hebei.gov.cn/zfcg/correctionAnncDetail_" + params[0] + ".html";
                    break;
                case "废标公告":
                    href = "http://www.ccgp-hebei.gov.cn/zfcg/cancelBidAnncDetail_" + params[0] + ".html";
                    break;
                case "单一来源":
                    href = "http://www.ccgp-hebei.gov.cn/zfcg/" + params[1] + "/singleSourceDetail_" + params[0] + ".html";
                    break;
                default:
            }
        }
        return href;
    }


    @Override
    public List parseContent(Elements items) {
        return null;
    }

    public List parseContent(Elements items, String type) {
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String onclick = element.attr("onclick");
            String href = urlJudge(onclick, type);
            if (StringUtils.isNotBlank(href)) {
                String title = element.text();
                BidNewsOriginal bidNewsOriginal = new BidNewsOriginal(href, SourceCode.CCGPHEBEI);
                bidNewsOriginal.setTitle(title);
                bidNewsOriginal.setProvince("河北");
                bidNewsOriginal.setType(type);

                try {
                    Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    Elements elements = page.getHtml().getDocument().body().select("body > table > tbody > tr > td > table > tbody > tr:nth-child(4) > td > table");
                    Elements elements1 = page.getHtml().getDocument().body().select("body > table > tbody > tr > td > table > tbody > tr:nth-child(4) > td > table > tbody > tr:nth-child(7) > td > span");
                    if (StringUtils.isNotBlank(elements1.text())) {
                        bidNewsOriginal.setDate(PageProcessorUtil.dataTxt(elements1.text()));
                    }
                    if (PageProcessorUtil.timeCompare(bidNewsOriginal.getDate())) {
                        log.warn("{} is not the same day", bidNewsOriginal.getUrl());
                        continue;
                    }
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        bidNewsOriginal.setFormatContent(formatContent);
                        dataItems.add(bidNewsOriginal);
                    }
                } catch (Exception e) {
                    log.error("", e);
                    log.error("url={}", href);
                }
            }
        }
        return dataItems;
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
