package com.har.sjfxpt.crawler.ggzy.provincial;

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

import static com.har.sjfxpt.crawler.ggzy.provincial.GanSuPageProcessor.*;

/**
 * Created by Administrator on 2017/12/17.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZYGANSU,
        sources = {
                @Source(url = GGZYGANSU_URL1, post = true, postParams = POST_PARAMS_01, type = "资格预审公告"),
                @Source(url = GGZYGANSU_URL1, post = true, postParams = POST_PARAMS_02, type = "招标公告"),
                @Source(url = GGZYGANSU_URL1, post = true, postParams = POST_PARAMS_03, type = "更正公告"),

                @Source(url = GGZYGANSU_URL2, post = true, postParams = POST_PARAMS_04, type = "资格预审公示"),
                @Source(url = GGZYGANSU_URL3, post = true, postParams = POST_PARAMS_05, type = "中标结果公告"),

                @Source(url = GGZYGANSU_URL3, post = true, postParams = POST_PARAMS_06, type = "中标结果更正公告"),
                @Source(url = GGZYGANSU_URL4, post = true, postParams = POST_PARAMS_07, type = "采购（资格预审）公告"),
                @Source(url = GGZYGANSU_URL5, post = true, postParams = POST_PARAMS_07, type = "更正事项"),
                @Source(url = GGZYGANSU_URL6, post = true, postParams = POST_PARAMS_07, type = "中标(成交)结果公告"),
        }
)
public class GanSuPageProcessor implements BasePageProcessor {

    final static String GGZYGANSU_URL1 = "http://www.gsggfw.cn/w/bid/tenderAnnQuaInqueryAnn/pageList?pageNo=1&pageSize=20";
    final static String GGZYGANSU_URL2 = "http://www.gsggfw.cn/w/bid/qualiInqueryResult/pageList?pageNo=1&pageSize=20";
    final static String GGZYGANSU_URL3 = "http://www.gsggfw.cn/w/bid/winResultAnno/pageList?pageNo=1&pageSize=20";

    final static String GGZYGANSU_URL4 = "http://www.gsggfw.cn/w/bid/purchaseQualiInqueryAnn/pageList?pageNo=1&pageSize=20";
    final static String GGZYGANSU_URL5 = "http://www.gsggfw.cn/w/bid/correctionItem/pageList?pageNo=1&pageSize=20";
    final static String GGZYGANSU_URL6 = "http://www.gsggfw.cn/w/bid/bidDealAnnounce/pageList?pageNo=1&pageSize=20";

    public static final String POST_PARAMS_01 = "{'filterparam':'{\\'areaCode\\':\\'620000\\',\\'assortmentindex\\':\\'0\\',\\'workNotice\\':{\\'bulletinType\\':\\'2\\',\\'noticeNature\\':\\'1\\'}}'}";
    public static final String POST_PARAMS_02 = "{'filterparam':'{\\'areaCode\\':\\'620000\\',\\'assortmentindex\\':\\'1\\',\\'workNotice\\':{\\'bulletinType\\':\\'1\\',\\'noticeNature\\':\\'1\\'}}'}";
    public static final String POST_PARAMS_03 = "{'filterparam':'{\\'areaCode\\':\\'620000\\',\\'assortmentindex\\':\\'1\\',\\'workNotice\\':{\\'bulletinType\\':\\'\\',\\'noticeNature\\':\\'2\\'}}'}";
    public static final String POST_PARAMS_04 = "{'filterparam':'{\\'areaCode\\':\\'620000\\',\\'assortmentindex\\':\\'3\\',\\'workNotice\\':{\\'bulletinType\\':\\'\\',\\'noticeNature\\':\\'2\\'}}'}";
    public static final String POST_PARAMS_05 = "{'filterparam':'{\\'areaCode\\':\\'620000\\',\\'assortmentindex\\':\\'3\\',\\'workNotice\\':{\\'bulletinType\\':\\'3\\',\\'noticeNature\\':\\'1\\'}}'}";
    public static final String POST_PARAMS_06 = "{'filterparam':'{\\'areaCode\\':\\'620000\\',\\'assortmentindex\\':\\'3\\',\\'workNotice\\':{\\'bulletinType\\':\\'3\\',\\'noticeNature\\':\\'2\\'}}'}";

    public static final String POST_PARAMS_07 = "{'filterparam':'{\\'areaCode\\':\\'620000\\',\\'assortmentindex\\':\\'\\',\\'workNotice\\':{\\'bulletinType\\':\\'1\\',\\'noticeNature\\':\\'1\\'}}'}";

    final static int PAGE_SIZE = 20;

    final static String PAGE_PARAMS = "pageParams";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        String type = (String) page.getRequest().getExtra("type");
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtra(PAGE_PARAMS);
        int pageNum = Integer.parseInt(StringUtils.substringBetween(url, "pageNo=", "&pageSize="));
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("body > div.tradpage > ul > li.disabled.controls");
            int announcementCount = Integer.parseInt(StringUtils.substringBetween(elements.text(), "共 ", " 条"));
            int pageCount = announcementCount % PAGE_SIZE == 0 ? announcementCount / PAGE_SIZE : announcementCount / PAGE_SIZE + 1;
            int cycleNum = pageCount >= 6 ? 6 : pageCount;
            for (int i = 2; i <= cycleNum; i++) {
                String urlTarget = url.replace("pageNo=1", "pageNo=" + i);
                Request request = new Request(urlTarget);
                request.setMethod(HttpConstant.Method.POST);
                String jsonFiled = pageParams.get("filterparam").toString();
                Map<String, Object> filterparam = Maps.newHashMap();
                filterparam.put("filterparam", jsonFiled);
                request.setRequestBody(HttpRequestBody.form(filterparam, "UTF-8"));
                request.putExtra("type", type);
                request.putExtra(PAGE_PARAMS, pageParams);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        String type = (String) page.getRequest().getExtra("type");
        Elements elements = page.getHtml().getDocument().body().select("body > div.trad-sear-con > ul > li");
        if (elements.isEmpty()) {
            log.warn("{} elements is empty", page.getUrl().get());
            return;
        }
        List<BidNewsOriginal> dataItems = parseContent(elements);
        dataItems.forEach(dataItem -> dataItem.setType(type));
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String hrefFiled = element.select("a").attr("onclick");
            String href = StringUtils.substringBetween(hrefFiled, "='", "'");
            if (StringUtils.isNotBlank(href)) {
                if (!StringUtils.startsWith(href, "http:")) {
                    href = "http://www.gsggfw.cn" + href;
                }
                try {
                    String title = element.select("a").attr("title");
                    String date = element.select("span").text();
                    BidNewsOriginal ggzyGanSuDataItem = new BidNewsOriginal(href, SourceCode.GGZYGANSU);
                    ggzyGanSuDataItem.setProvince("甘肃");
                    ggzyGanSuDataItem.setTitle(title);
                    ggzyGanSuDataItem.setDate(PageProcessorUtil.dataTxt(date));
                    if (PageProcessorUtil.timeCompare(ggzyGanSuDataItem.getDate())) {
                        log.warn("{} is not the same day", ggzyGanSuDataItem.getUrl());
                        continue;
                    }
                    Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    Elements elements = page.getHtml().getDocument().body().select("body > div.mod-content.clear > div.mod-cont-lft.clear > div.mod-arti-area > div.mod-arti-body");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    Elements elements1 = elements.select("div.arti-des-con iframe");
                    if (!elements1.isEmpty()) {
                        String iframeUrl = elements1.attr("src");
                        Page page1 = httpClientDownloader.download(new Request(iframeUrl), SiteUtil.get().setTimeOut(30000).toTask());
                        Element element1 = page1.getHtml().getDocument().body();
                        String formatContentAdd = PageProcessorUtil.formatElementsByWhitelist(element1);
                        formatContent = formatContent + formatContentAdd;
                    }
                    if (StringUtils.isNotBlank(formatContent)) {
                        ggzyGanSuDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyGanSuDataItem);
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
