package com.har.sjfxpt.crawler.suning;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.ProvinceUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import com.sun.org.apache.regexp.internal.RE;
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

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/11/13.
 */
@Slf4j
@Component
public class SuNingPageProcessor implements BasePageProcessor {

    final static String PAGE_PARAMS = "pageParams";

    final static String url = "http://zb.suning.com/bid-web/searchIssue.htm";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        Element element = page.getHtml().getDocument().body();

        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);

        int pageNum = Integer.parseInt((String) pageParams.get("pageNum"));

        if (pageNum == 1) {
            Elements elements = element.select("#idFrmMain > div.it-content.clearfix > div.left-box > div:nth-child(3) > div.listcontent > div > div > a");
            int pageSize = 0;
            for (Element a : elements) {
                if (a.text().contains("下一页")) {
                    pageSize = Integer.parseInt(a.previousElementSibling().text());
                }
            }

            if (pageSize != 0) {
                log.debug("pageSize=={}", pageSize);
                if (pageSize > 1) {
                    for (int i = 2; i <= pageSize; i++) {
                        pageParams.put("pageNum", i + "");
                        Request request = new Request(url);
                        request.setMethod(HttpConstant.Method.POST);
                        request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
                        request.putExtra(PAGE_PARAMS, pageParams);
                        page.addTargetRequest(request);
                    }
                }
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        Elements elements = page.getHtml().getDocument().body().select("#idFrmMain > div.it-content.clearfix > div.left-box > div:nth-child(3) > div.listcontent > table > tbody > tr");
        if (elements.isEmpty()) {
            log.error("fetch error, elements is empty");
            return;
        }
        List<SuNingDataItem> dataItems = parseContent(elements);
        String typeNum = (String) pageParams.get("issue.msgType");
        if (typeNum.equals("m2") || typeNum.equals("m1")) {
            dataItems.forEach(dataItem -> dataItem.setType("招标"));
        }
        if (typeNum.equals("m3")) {
            dataItems.forEach(dataItem -> dataItem.setType("中标"));
        }
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<SuNingDataItem> dataItems = Lists.newArrayList();

        for (Element a : items) {
            String href = "http://zb.suning.com/bid-web/" + a.select("td.tdsubject > a").attr("href");
            SuNingDataItem suNingDataItem = new SuNingDataItem(href);
            String title = a.select("td.tdsubject > a").text();
            String date = a.select("td:nth-child(3)").text();
            suNingDataItem.setTitle(title);
            suNingDataItem.setDate(date);
            suNingDataItem.setUrl(href);
            suNingDataItem.setProvince(ProvinceUtil.get(title));

            Request request = new Request(href);
            Page page = httpClientDownloader.download(request, SiteUtil.get().toTask());
            Element element = page.getHtml().getDocument().body();
            Element formatContentHtml = element.select("#idFrmMain > div > div.left-box > div.bcborder > div.txtcontent").first();
            if (formatContentHtml.text().contains("招标人：")) {
                suNingDataItem.setPurchaser(StringUtils.substringBetween(formatContentHtml.text(), "招标人：", " "));
            }
            String dateDetail = PageProcessorUtil.dataTxt(formatContentHtml.select("p").text());
            if (StringUtils.isNotBlank(dateDetail)) {
                suNingDataItem.setDate(dateDetail);
            }
            if (StringUtils.isNotBlank(formatContentHtml.toString())) {
                suNingDataItem.setFormatContent(PageProcessorUtil.formatElementsByWhitelist(formatContentHtml));
            }
            dataItems.add(suNingDataItem);
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
        return SiteUtil.get();
    }
}
