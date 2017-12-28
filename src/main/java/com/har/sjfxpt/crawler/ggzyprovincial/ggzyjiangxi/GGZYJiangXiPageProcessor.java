package com.har.sjfxpt.crawler.ggzyprovincial.ggzyjiangxi;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/12/14.
 */
@Slf4j
@Component
public class GGZYJiangXiPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    final static String PAGE_PARAMS = "pageParams";

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int pageNum = Integer.parseInt(StringUtils.substringBefore(StringUtils.substringAfterLast(url, "/"), ".html"));
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("#index");
            String totalPage = elements.text();
            int pageCount = Integer.parseInt(StringUtils.substringAfter(totalPage, "/"));
            if (pageCount >= 10) {
                for (int i = 2; i <= 10; i++) {
                    String urlTarget = StringUtils.substringBeforeLast(url, "/") + "/" + i + ".html";
                    Request request = new Request(urlTarget);
                    request.putExtra(PAGE_PARAMS, pageParams);
                    page.addTargetRequest(request);
                }
            } else {
                for (int i = 2; i <= pageCount; i++) {
                    String urlTarget = StringUtils.substringBeforeLast(url, "/") + "/" + i + ".html";
                    Request request = new Request(urlTarget);
                    request.putExtra(PAGE_PARAMS, pageParams);
                    page.addTargetRequest(request);
                }
            }

        }
    }

    @Override
    public void handleContent(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get(PAGE_PARAMS);
        Elements elements = page.getHtml().getDocument().body().select("#gengerlist > div.ewb-infolist > ul > li");
        List<GGZYJiangXiDataItem> dataItems = parseContent(elements);
        String type = pageParams.get("type");
        String businessType = pageParams.get("businessType");
        dataItems.forEach(dataItem -> {
            dataItem.setType(type);
            dataItem.setBusinessType(businessType);
        });
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<GGZYJiangXiDataItem> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("a").attr("href");
            String title = element.select("a").text();
            String date = element.select("span").text();
            if (StringUtils.isNotBlank(href)) {
                if (!StringUtils.startsWith(href, "http:")) {
                    href = "http://jxsggzy.cn" + href;
                }
                GGZYJiangXiDataItem ggzyJiangXiDataItem = new GGZYJiangXiDataItem(href);
                ggzyJiangXiDataItem.setUrl(href);
                ggzyJiangXiDataItem.setDate(PageProcessorUtil.dataTxt(date));
                if (PageProcessorUtil.timeCompare(ggzyJiangXiDataItem.getDate())) {
                    log.info("{} is not the same day", ggzyJiangXiDataItem.getUrl());
                } else {
                    ggzyJiangXiDataItem.setTitle(title);
                    Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    String dateParse = DateTime.parse(date, DateTimeFormat.forPattern("yyyy-MM-dd")).toString("yyyyMMdd");
                    String typeId = StringUtils.substringBetween(href, "jyxx/", "/" + dateParse);
                    String typeIdReal = StringUtils.substringAfter(typeId, "/");
                    Elements elements = null;
                    if ("002006001".equalsIgnoreCase(typeIdReal) || "002006002".equalsIgnoreCase(typeIdReal)
                            || "002006004".equalsIgnoreCase(typeIdReal) || "002006005".equalsIgnoreCase(typeIdReal)
                            || "002001001".equalsIgnoreCase(typeIdReal) || "002001003".equalsIgnoreCase(typeIdReal)
                            || "002001004".equalsIgnoreCase(typeIdReal) || "002002002".equalsIgnoreCase(typeIdReal)
                            || "002002005".equalsIgnoreCase(typeIdReal) || "002003001".equalsIgnoreCase(typeIdReal)
                            || "002003004".equalsIgnoreCase(typeIdReal) || "002005001".equalsIgnoreCase(typeIdReal)
                            || "002005004".equalsIgnoreCase(typeIdReal)) {
                        elements = page.getHtml().getDocument().body().select("body > div.container.clearfix.mt20 > div.ewb-detail-box > div.article-info > div");
                    }
                    if ("002006003".equalsIgnoreCase(typeIdReal) || "002001002".equalsIgnoreCase(typeIdReal)
                            || "002002003".equalsIgnoreCase(typeIdReal) || "002003002".equalsIgnoreCase(typeIdReal)
                            || "002003003".equalsIgnoreCase(typeIdReal) || "002005002".equalsIgnoreCase(typeIdReal)
                            || "002005003".equalsIgnoreCase(typeIdReal)) {
                        elements = page.getHtml().getDocument().body().select("body > div.container.clearfix.mt20 > div.ewb-detail-box");
                    }
                    if ("002006006".equalsIgnoreCase(typeIdReal)) {
                        elements = page.getHtml().getDocument().body().select("body > div.fui-content > div");
                    }
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        ggzyJiangXiDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyJiangXiDataItem);
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
}
