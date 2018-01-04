package com.har.sjfxpt.crawler.suning;

import com.google.common.collect.Lists;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.suning.SuNingPageProcessor.*;

/**
 * Created by Administrator on 2017/11/13.
 *
 * @author luofei
 * @author dongqi
 */
@Slf4j
@Component
@SourceConfig(code = SourceCode.SUNING, useProxy = true, sources = {
        @Source(url = SEED_URL, post = true, postParams = POST_PARAMS_01, needPlaceholderFields = {"issue.updateStartDate", "issue.updateEndDate"}),
        @Source(url = SEED_URL, post = true, postParams = POST_PARAMS_02, needPlaceholderFields = {"issue.updateStartDate", "issue.updateEndDate"}),
        @Source(url = SEED_URL, post = true, postParams = POST_PARAMS_03, needPlaceholderFields = {"issue.updateStartDate", "issue.updateEndDate"})
})
public class SuNingPageProcessor implements BasePageProcessor {

    final static String PAGE_PARAMS = "pageParams";

    public final static String SEED_URL = "http://zb.suning.com/bid-web/searchIssue.htm";

    public static final String POST_PARAMS_01 = "{'issue.msgType':'m1','issue.updateStartDate':'','issue.updateEndDate':'','pageNum':'1'}";
    public static final String POST_PARAMS_02 = "{'issue.msgType':'m2','issue.updateStartDate':'','issue.updateEndDate':'','pageNum':'1'}";
    public static final String POST_PARAMS_03 = "{'issue.msgType':'m3','issue.updateStartDate':'','issue.updateEndDate':'','pageNum':'1'}";

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
                        Request request = new Request(SEED_URL);
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
        List<BidNewsOriginal> dataItems = parseContent(elements);
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
        List<BidNewsOriginal> dataItems = Lists.newArrayList();

        for (Element a : items) {
            String href = "http://zb.suning.com/bid-web/" + a.select("td.tdsubject > a").attr("href");

            BidNewsOriginal suNingDataItem = new BidNewsOriginal(href);
            suNingDataItem.setSourceCode(SourceCode.SUNING.name());
            suNingDataItem.setSource(SourceCode.SUNING.getValue());

            String title = a.select("td.tdsubject > a").text();
            String date = a.select("td:nth-child(3)").text();
            suNingDataItem.setTitle(title);
            suNingDataItem.setDate(PageProcessorUtil.dataTxt(date));
            suNingDataItem.setUrl(href);
            suNingDataItem.setProvince(ProvinceUtil.get(title));

            log.debug(">>> download url {}", href);
            try {
                Element element = Jsoup.connect(href).get().body();
                Element formatContentHtml = element.select("#idFrmMain > div > div.left-box > div.bcborder > div.txtcontent").first();
                if (formatContentHtml.text().contains("招标人：")) {
                    suNingDataItem.setPurchaser(StringUtils.substringBetween(formatContentHtml.text(), "招标人：", " "));
                }
                String dateDetail = PageProcessorUtil.dataTxt(formatContentHtml.select("p.date").text());
                if (StringUtils.isNotBlank(dateDetail)) {
                    suNingDataItem.setDate(PageProcessorUtil.dataTxt(dateDetail));
                }
                if (StringUtils.isNotBlank(formatContentHtml.toString())) {
                    suNingDataItem.setFormatContent(PageProcessorUtil.formatElementsByWhitelist(formatContentHtml));
                }
            } catch (Exception e) {
                log.error("", e);
                log.error(">>> url {}", href);
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
        return SiteUtil.get();
    }
}
