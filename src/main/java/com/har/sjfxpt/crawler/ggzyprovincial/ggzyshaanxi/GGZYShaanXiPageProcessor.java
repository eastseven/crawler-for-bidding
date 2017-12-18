package com.har.sjfxpt.crawler.ggzyprovincial.ggzyshaanxi;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/12/15.
 */
@Slf4j
@Component
public class GGZYShaanXiPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    final static String PAGE_PARAMS = "pageParams";

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int pageNum = Integer.parseInt(StringUtils.substringBefore(StringUtils.substringAfterLast(url, "/"), ".html"));
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("#index");
            if (!elements.isEmpty()) {
                int pageCount = Integer.parseInt(StringUtils.substringAfter(elements.text(), "/"));
                int cycleCount = pageCount >= 4 ? 4 : pageCount;
                log.info("cycleCount=={}", cycleCount);
                for (int i = 2; i <= cycleCount; i++) {
                    String urlTarget = url.replace("1.html", i + ".html");
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
        Elements elements = page.getHtml().getDocument().body().select("#categorypagingcontent > div.ewb-list > ul > li");
        List<GGZYShaanXiDataItem> dataItems = parseContent(elements);
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
        List<GGZYShaanXiDataItem> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                if (!StringUtils.startsWith(href, "http:")) {
                    href = "http://www.sxggzyjy.cn" + href;
                }
                String title = element.select("a").attr("title");
                GGZYShaanXiDataItem ggzyShaanXiDataItem = new GGZYShaanXiDataItem(href);
                ggzyShaanXiDataItem.setUrl(href);
                ggzyShaanXiDataItem.setTitle(title);

                Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                try {
                    String dataInformation = page.getHtml().getDocument().body().select("body > div.ewb-container > div.ewb-main > div.info-source").text();
                    String dataReal = StringUtils.substringBetween(dataInformation, "信息时间：", "】");
                    if (StringUtils.isNotBlank(dataReal)) {
                        ggzyShaanXiDataItem.setDate(PageProcessorUtil.dataTxt(dataReal));
                    }
                    if (PageProcessorUtil.timeCompare(ggzyShaanXiDataItem.getDate())) {
                        log.info("{} is not the sameday", ggzyShaanXiDataItem.getUrl());
                    } else {
                        Elements elements = page.getHtml().getDocument().body().select("#mainContent");
                        String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                        if (StringUtils.isNotBlank(formatContent)) {
                            ggzyShaanXiDataItem.setFormatContent(formatContent);
                            dataItems.add(ggzyShaanXiDataItem);
                        }
                    }

                } catch (Exception e) {
                    log.info("url=={}", ggzyShaanXiDataItem.getUrl());
                    log.info("e{}", e);
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
