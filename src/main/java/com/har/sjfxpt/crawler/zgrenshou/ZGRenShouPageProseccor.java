package com.har.sjfxpt.crawler.zgrenshou;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.ProvinceUtil;
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

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/12/29.
 */
@Slf4j
@Component
public class ZGRenShouPageProseccor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        int pageNum = Integer.parseInt(StringUtils.substringBetween(url, "index_", ".jhtml"));
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("body > div:nth-child(2) > div.W980Middle.W980.center > div > div.padding10 > div.top10.txtCenter > div");
            int pageCount = Integer.parseInt(StringUtils.substringBetween(elements.text(), "1/", "页"));
            int cycleNum = pageCount >= 3 ? 3 : pageCount;
            for (int i = 2; i <= cycleNum; i++) {
                String urlTarget = url.replace("index_1", "index_" + i);
                page.addTargetRequest(urlTarget);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        String url = page.getUrl().get();
        Elements elements = page.getHtml().getDocument().body().select("body > div:nth-child(2) > div.W980Middle.W980.center > div > div.padding10 > div.List3 > ul > li");
        String type = typeJudgement(url);
        List<ZGRenShouDataItem> dataItems = parseContent(elements);
        dataItems.forEach(dataItem -> dataItem.setType(type));
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<ZGRenShouDataItem> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                if (!StringUtils.startsWith(href, "http:")) {
                    href = "http://cpmsx.e-chinalife.com" + href;
                }
                String title = element.select("a").attr("title");
                String date = element.select("span.right.gray").text();
                ZGRenShouDataItem zgRenShouDataItem = new ZGRenShouDataItem(href);
                zgRenShouDataItem.setUrl(href);
                zgRenShouDataItem.setTitle(title);
                zgRenShouDataItem.setDate(PageProcessorUtil.dataTxt(date));
                zgRenShouDataItem.setProvince(ProvinceUtil.get(title));

                if (PageProcessorUtil.timeCompare(zgRenShouDataItem.getDate())) {
                    log.warn("{} is not the sameday", zgRenShouDataItem.getUrl());
                } else {
                    Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    Elements elementsTime = page.getHtml().getDocument().body().select("body > div:nth-child(2) > div.W980Middle.W980.center > div > div:nth-child(2) > div.padding5.TxtCenter");
                    String dateDetail = PageProcessorUtil.dataTxt(elementsTime.text());
                    if (StringUtils.isNotBlank(dateDetail)) {
                        zgRenShouDataItem.setDate(dateDetail);
                    }
                    Elements elements = page.getHtml().getDocument().body().select("body > div:nth-child(2) > div.W980Middle.W980.center > div > div:nth-child(2) > div.Padding10.BorderCCCDot.F14");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        zgRenShouDataItem.setFormatContent(formatContent);
                        dataItems.add(zgRenShouDataItem);
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

    public String typeJudgement(String url) {
        String typeField = StringUtils.substringBetween(url, "xycms/", "/index");
        String type = null;
        switch (typeField) {
            case "cggg":
                type = "采购公告";
                break;
            case "jggs":
                type = "结果公告";
                break;
            case "cqgg":
                type = "澄清公告";
                break;
            default:
        }
        return type;
    }

}
