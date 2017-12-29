package com.har.sjfxpt.crawler.core.processor;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.downloader.GongGongZiYuanPageDownloader;
import com.har.sjfxpt.crawler.core.model.BidNewOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.service.PageDataService;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.core.processor.GongGongZiYuanPageProcessor.*;
import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * 全国公共资源交易平台
 * http://deal.ggzy.gov.cn/ds/deal/dealList.jsp
 *
 * @author dongqi
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZY,
        sources = {
                @Source(url = SEED_URL, post = true, postParams = POST_PARAMS_01, needPlaceholderFields = {"TIMEEND_SHOW", "TIMEBEGIN_SHOW", "TIMEEND", "TIMEBEGIN"}),
                @Source(url = SEED_URL, post = true, postParams = POST_PARAMS_02, needPlaceholderFields = {"TIMEEND_SHOW", "TIMEBEGIN_SHOW", "TIMEEND", "TIMEBEGIN"})
        }
)
public class GongGongZiYuanPageProcessor implements BasePageProcessor {

    public static final String DEAL_CLASSIFY = "DEAL_CLASSIFY";

    public static final String PAGE_NUMBER = "PAGENUMBER";

    public static final String SEED_URL = "http://deal.ggzy.gov.cn/ds/deal/dealList.jsp";

    public static final String POST_PARAMS_01 = "{'DEAL_CITY':'0','TIMEEND_SHOW':'#','DEAL_STAGE':'0100','DEAL_TIME':'01','FINDTXT':'','DEAL_CLASSIFY':'01','TIMEBEGIN_SHOW':'#','DEAL_TRADE':'0','DEAL_PLATFORM':'0','isShowAll':'1','TIMEEND':'#','TIMEBEGIN':'#','PAGENUMBER':1,'DEAL_PROVINCE':'0'}";

    public static final String POST_PARAMS_02 = "{'DEAL_CITY':'0','TIMEEND_SHOW':'#','DEAL_STAGE':'0200','DEAL_TIME':'01','FINDTXT':'','DEAL_CLASSIFY':'02','TIMEBEGIN_SHOW':'#','DEAL_TRADE':'0','DEAL_PLATFORM':'0','isShowAll':'1','TIMEEND':'#','TIMEBEGIN':'#','PAGENUMBER':1,'DEAL_PROVINCE':'0'}";

    final String KEY_PAGE_PARAMS = "pageParams";

    @Autowired
    GongGongZiYuanPageDownloader gongGongZiYuanPageDownloader;

    @Autowired
    PageDataService pageDataService;

    @Override
    public void process(Page page) {
        //获取列表内容
        handleContent(page);

        //处理分页
        handlePaging(page);

    }

    @Override
    public List<BidNewOriginal> parseContent(Elements items) {
        List<BidNewOriginal> dataItemList = Lists.newArrayList();

        for (Element item : items) {
            String title = item.select("h4 a").text();
            String href = item.select("h4 a").attr("href");
            href = StringUtils.replace(href, "/a/", "/b/");
            String date = item.select("h4 span").text();

            BidNewOriginal dataItem = new BidNewOriginal(href);
            dataItem.setSource(SourceCode.GGZY.getValue());
            dataItem.setSourceCode(SourceCode.GGZY.name());
            dataItem.setTitle(title);
            dataItem.setDate(date);

            String cssQuery = "p.p_tw span";
            for (Element element : item.select(cssQuery)) {
                String text = element.text();
                if (StringUtils.contains(text, "省份")) {
                    String province = element.nextElementSibling().text();
                    dataItem.setProvince(province);
                }

                if (StringUtils.contains(text, "信息类型")) {
                    String infoType = element.nextElementSibling().text();
                    dataItem.setType(StringUtils.defaultString(infoType, "其他"));
                }

                if (StringUtils.contains(text, "行业")) {
                    String industry = element.nextElementSibling().text();
                    dataItem.setOriginalIndustryCategory(StringUtils.defaultString(industry, "其他"));
                }
            }

            gongGongZiYuanPageDownloader.download(dataItem);
            dataItemList.add(dataItem);
        }

        return dataItemList;
    }

    @Override
    public void handlePaging(Page page) {
        Map extra = (Map) page.getRequest().getExtra(KEY_PAGE_PARAMS);
        Elements totalSize = page.getHtml().getDocument().body().select("div#publicl div.contp span.span_left:nth-child(1)");
        Elements totalPage = page.getHtml().getDocument().body().select("div#publicl div.contp span.span_right");

        int sizeNum = Integer.parseInt(totalSize.select("b").text());
        int currentPageNum = Integer.parseInt(StringUtils.substringBefore(totalPage.select("b").text(), "/"));
        int pageNum = Integer.parseInt(StringUtils.substringAfter(totalPage.select("b").text(), "/"));

        log.info("type {}, current page {}/{}, total size={}", extra.get(DEAL_CLASSIFY), currentPageNum, pageNum, sizeNum);
        try {
            pageDataService.save(extra.get("TIMEBEGIN").toString(), sizeNum, pageNum, page.getUrl().get());
        } catch (Exception e) {
            log.error("", e);
        }

        if (currentPageNum == 1) {
            final int start = 2;
            Map<String, Object> firstPage = (Map<String, Object>) page.getRequest().getExtra(KEY_PAGE_PARAMS);
            for (int index = start; index <= pageNum; index++) {
                Map<String, Object> nextPage = Maps.newHashMap(firstPage);
                nextPage.put(PAGE_NUMBER, index);
                Request request = new Request(SEED_URL);
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(nextPage, "UTF-8"));
                request.putExtra(KEY_PAGE_PARAMS, nextPage);

                page.addTargetRequest(request);
                log.debug("add next page {}, {}", index, nextPage);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        final String css = "div#publicl div.publicont div";
        Elements items = page.getHtml().getDocument().body().select(css);
        List<BidNewOriginal> dataItemList = parseContent(items);
        if (!dataItemList.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItemList);
        }
    }

    @Override
    public Site getSite() {
        return SiteUtil.get().setTimeOut(60 * 60 * 1000).addHeader("content-type", "application/x-www-form-urlencoded");
    }
}
