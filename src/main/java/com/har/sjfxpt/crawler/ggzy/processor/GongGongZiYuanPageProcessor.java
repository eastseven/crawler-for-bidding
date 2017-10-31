package com.har.sjfxpt.crawler.ggzy.processor;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzy.downloader.GongGongZiYuanPageDownloader;
import com.har.sjfxpt.crawler.ggzy.model.DataItem;
import com.har.sjfxpt.crawler.ggzy.service.PageDataService;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
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

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;
import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanUtil.*;

/**
 * 全国公共资源交易平台
 * http://deal.ggzy.gov.cn/ds/deal/dealList.jsp
 *
 * @author dongqi
 */
@Slf4j
@Component
public class GongGongZiYuanPageProcessor implements BasePageProcessor {

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
    public List<DataItem> parseContent(Elements items) {
        List<DataItem> dataItemList = Lists.newArrayList();

        for (Element item : items) {
            String title = item.select("h4 a").text();
            String href = item.select("h4 a").attr("href");
            href = StringUtils.replace(href, "/a/", "/b/");
            String date = item.select("h4 span").text();

            DataItem dataItem = DataItem.builder()
                    .title(title).id(DigestUtils.md5Hex(href)).url(href).date(date)
                    .build();
            String cssQuery = "p.p_tw span";
            for (Element element : item.select(cssQuery)) {
                String text = element.text();
                if (StringUtils.contains(text, "省份")) {
                    String province = element.nextElementSibling().text();
                    dataItem.setProvince(province);
                }

                if (StringUtils.contains(text, "来源平台")) {
                    String source = element.nextElementSibling().text();
                    dataItem.setSource(StringUtils.defaultString(source, "全国公共资源交易平台"));
                }

                if (StringUtils.contains(text, "业务类型")) {
                    String businessType = element.nextElementSibling().text();
                    dataItem.setBusinessType(businessType);
                }

                if (StringUtils.contains(text, "信息类型")) {
                    String infoType = element.nextElementSibling().text();
                    dataItem.setInfoType(infoType);
                }

                if (StringUtils.contains(text, "行业")) {
                    String industry = element.nextElementSibling().text();
                    dataItem.setIndustry(StringUtils.defaultString(industry, "其他"));
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

        int sizeNum        = Integer.parseInt(totalSize.select("b").text());
        int currentPageNum = Integer.parseInt(StringUtils.substringBefore(totalPage.select("b").text(), "/"));
        int pageNum        = Integer.parseInt(StringUtils.substringAfter(totalPage.select("b").text(), "/"));

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
        List<DataItem> dataItemList = parseContent(items);
        if (!dataItemList.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItemList);
        }
    }

    @Override
    public Site getSite() {
        return SiteUtil.get().setTimeOut(60*60*1000).addHeader("content-type", "application/x-www-form-urlencoded");
    }
}
