package com.har.sjfxpt.crawler.chinamobile;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzy.model.DataItem;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.ProvinceUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

@Slf4j
@Component
public class ChinaMobilePageProcessor implements BasePageProcessor {

    final static String PAGE_PARAMS = "pageParams";

    final static String URL = "https://b2b.10086.cn/b2b/main/viewNoticeContent.html?noticeBean.id=";

    @Override
    public void process(Page page) {
        handlePaging(page);

        Elements elements = page.getHtml().getDocument().body().select("table tr");
        List<DataItem> dataItemList = parseContent(elements);
        if (!dataItemList.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItemList);
        }
    }

    @Override
    public Site getSite() {
        return SiteUtil.get();
    }

    @Override
    public void handlePaging(Page page) {
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtra(PAGE_PARAMS);
        int currentPage = (int) pageParams.get("page.currentPage");
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
    public List parseContent(Elements items) {
        List<ChinaMobileDataItem> dataItems = Lists.newArrayList();
        for (Element element : items) {
            if (element.isBlock()) continue;
            if (element.hasClass("zb_table_tr")) continue;
            String id = StringUtils.substringBetween(element.attr("onclick"), "(", ")");
            String url = URL + id;
            String purchaser = element.select("td").get(0).text();
            String type = StringUtils.defaultString(element.select("td").get(1).text(), "其他");
            String title = element.select("td").get(2).select("a").attr("title");
            String date = element.select("td").get(3).text();

            try {
                Jsoup.connect(url).timeout(60000).userAgent(SiteUtil.get().getUserAgent()).get().body().select("div#mobanDiv");
            } catch (IOException e) {
                log.error("", e);
            }

            ChinaMobileDataItem dataItem = new ChinaMobileDataItem(url);
            dataItem.setDate(date);
            dataItem.setTitle(title);
            dataItem.setType(type);
            dataItem.setPurchaser(purchaser);
            dataItem.setProvince(ProvinceUtil.get(purchaser + title));

            dataItems.add(dataItem);
            log.debug(">>> {}", dataItem);
        }
        return dataItems;
    }
}
