package com.har.sjfxpt.crawler.zgjiaojian;

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

import static com.har.sjfxpt.crawler.zgjiaojian.ZGJiaoJianPageProcessor.*;


/**
 * Created by Administrator on 2017/12/28.
 *
 * @author luofei
 * @author dongqi
 */
@Slf4j
@Component
@SourceConfig(code = SourceCode.ZGJIAOJIAN, sources = {
        @Source(url = URL_01, post = true, postParams = POST_PARAMS_01),
        @Source(url = URL_02, post = true, postParams = POST_PARAMS_02)
})
public class ZGJiaoJianPageProcessor implements BasePageProcessor {

    public static final String URL_01 = "http://empm.ccccltd.cn/PMS/downpage.shtml?id=J8Gis7a8zFp0/0+cu62h4ufCdjRQ/t9M5buuVsVwZbmhKSxRhbdvSgqcr+4yWYEPz0JTUNvOCTs=";
    public static final String URL_02 = "http://empm.ccccltd.cn/PMS/downpage.shtml?id=J8Gis7a8zFp0/0+cu62h4ufCdjRQ/t9M5buuVsVwZbmhKSxRhbdvSgqcr+4yWYEPeVgXu6xroO0=";

    public static final String POST_PARAMS_01 = "{'VENUS_PAGE_NO_KEY':'1','VENUS_PAGE_SIZE_KEY':'20','channelId':'2013300100000000035'}";
    public static final String POST_PARAMS_02 = "{'VENUS_PAGE_NO_KEY':'1','VENUS_PAGE_SIZE_KEY':'20','channelId':'2013300100000000034'}";

    HttpClientDownloader httpClientDownloader;

    public static final String PAGE_PARAMS = "pageParams";

    @Override
    public void handlePaging(Page page) {
        Map<String, Object> params = (Map<String, Object>) page.getRequest().getExtras().get("pageParams");
        int pageNum = Integer.parseInt(params.get("VENUS_PAGE_NO_KEY").toString());
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("#aspnetForm > div > div.List_Right_Main > div > div.Context_Middle > div.page_num > input[type=\"hidden\"]:nth-child(15)");
            int pageCount = Integer.parseInt(elements.attr("value"));
            int cycleNum = pageCount >= 3 ? 3 : pageCount;
            for (int i = 2; i <= cycleNum; i++) {
                String url = page.getUrl().get();
                Map<String, Object> pageParams = Maps.newHashMap(params);
                pageParams.put("VENUS_PAGE_NO_KEY", i);
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
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get("pageParams");
        Elements elements = page.getHtml().getDocument().body().select("#aspnetForm > div > div.List_Right_Main > div > div.Context_Middle > div:nth-child(1) > ul > li");
        List<BidNewsOriginal> dataItems = parseContent(elements);
        String channelId = pageParams.get("channelId").toString();
        String type = null;
        if (channelId.equalsIgnoreCase("2013300100000000035")) {
            type = "中标公告";
        }
        if (channelId.equalsIgnoreCase("2013300100000000034")) {
            type = "招标信息";
        }
        final String finalType = type;
        dataItems.forEach(dataItem -> dataItem.setType(finalType));
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
            String hrefField = element.select("span.dotspan > a").attr("href");
            String href = hrefParse(hrefField);
            if (StringUtils.isNotBlank(href)) {
                String date = element.select("span.time").text();
                String title = element.select("span.dotspan > a").attr("title");
                if (StringUtils.containsIgnoreCase(date, "(")) {
                    date = StringUtils.substringBetween(date, "(", ")");
                }
                BidNewsOriginal zgJiaoJianDataItem = new BidNewsOriginal(href, SourceCode.ZGJIAOJIAN);
                zgJiaoJianDataItem.setTitle(title);
                zgJiaoJianDataItem.setProvince(ProvinceUtil.get(title));
                zgJiaoJianDataItem.setDate(PageProcessorUtil.dataTxt(date));

                Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                String dateDetail = page.getHtml().getDocument().body().select("body > div:nth-child(2)").text();
                dateDetail = PageProcessorUtil.dataTxt(dateDetail);
                if (StringUtils.isNotBlank(dateDetail)) {
                    zgJiaoJianDataItem.setDate(dateDetail);
                }
                if (PageProcessorUtil.timeCompare(zgJiaoJianDataItem.getDate())) {
                    log.warn("{} in not the same day", zgJiaoJianDataItem.getUrl());
                } else {
                    Elements elements = page.getHtml().getDocument().body().select("body > div:nth-child(3)");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        zgJiaoJianDataItem.setFormatContent(formatContent);
                        dataItems.add(zgJiaoJianDataItem);
                    }
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

    public String hrefParse(String hrefField) {
        String href = null;
        String hrefType = StringUtils.substringBetween(hrefField, "javaScript:", "('");
        String value = StringUtils.substringBetween(hrefField, "('", "','null");
        if (StringUtils.containsIgnoreCase(value, "\\r\\n")) {
            value = StringUtils.remove(value, "\\r\\n");
        }
        switch (hrefType) {
            case "showAnnounceDetail":
                href = "http://empm.ccccltd.cn/PMS/biddetail.shtml?id=" + value;
                break;
            case "goAdjustBidResultDetail":
                href = "http://empm.ccccltd.cn/PMS/adjustdetail.shtml?id=" + value;
                break;
            default:
        }
        return href;
    }

}
