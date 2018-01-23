package com.har.sjfxpt.crawler.other;

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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;

/**
 * Created by Administrator on 2017/12/27.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.CU,
        sources = {
                @Source(url = "http://www.chinaunicombidding.cn/jsp/cnceb/web/info1/infoList.jsp?page=1&type=1"),
                @Source(url = "http://www.chinaunicombidding.cn/jsp/cnceb/web/info1/infoList.jsp?page=1&type=2"),
                @Source(url = "http://www.chinaunicombidding.cn/jsp/cnceb/web/info1/infoList.jsp?page=1&type=3"),
        }
)
public class ChinaUnicomPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        int pageNum = Integer.parseInt(StringUtils.substringBetween(url, "page=", "&type="));
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("body > table > tbody > tr:nth-child(2) > td:nth-child(2) > table > tbody > tr > td:nth-child(2) > table:nth-child(2) > tbody > tr:nth-child(3) > td > table > tbody > tr > td:nth-child(1)");
            int pageCount = Integer.parseInt(StringUtils.substringBetween(elements.text(), "共 ", " 页"));
            int cycleNum = pageCount >= 15 ? 15 : pageCount;
            for (int i = 2; i <= cycleNum; i++) {
                String urlTarget = url.replace("page=1", "page=" + i);
                page.addTargetRequest(urlTarget);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        String url = page.getUrl().get();
        Elements elements = page.getHtml().getDocument().body().select("#div1 > table > tbody > tr");
        List<BidNewsOriginal> dataItems = parseContent(elements);
        String typeId = StringUtils.substringAfter(url, "type=");
        String type = null;
        if ("1".equalsIgnoreCase(typeId)) {
            type = "招标公告";
        }
        if ("2".equalsIgnoreCase(typeId)) {
            type = "结果公告";
        }
        if ("3".equalsIgnoreCase(typeId)) {
            type = "单一来源采购征求意见公示";
        }
        String finalType = type;
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
            String onclick = element.select("td:nth-child(1) > span").attr("onclick");
            String href = StringUtils.substringBetween(onclick, "window.open(\"", "\",\"\",\"height");
            if (StringUtils.isNotBlank(href)) {
                if (!StringUtils.startsWith(href, "http:")) {
                    href = "http://www.chinaunicombidding.cn" + href;
                }
                try {
                    String title = element.select("td:nth-child(1) > span").attr("title");
                    String date = element.select("td:nth-child(2)").text();
                    String provice = element.select("td:nth-child(3)").text();

                    BidNewsOriginal chinaUnicomDataItem = new BidNewsOriginal(href, SourceCode.CU);
                    chinaUnicomDataItem.setTitle(title);
                    chinaUnicomDataItem.setDate(PageProcessorUtil.dataTxt(date));
                    chinaUnicomDataItem.setProvince(ProvinceUtil.get(provice));

                    if (PageProcessorUtil.timeCompare(chinaUnicomDataItem.getDate())) {
                        log.warn("{} is not the same day", chinaUnicomDataItem.getUrl());
                        continue;
                    }

                    Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    String dateDetail = page.getHtml().getDocument().body().select("body > div > table > tbody > tr > td:nth-child(2) > span").text();
                    if (StringUtils.isNotBlank(dateDetail)) {
                        chinaUnicomDataItem.setDate(PageProcessorUtil.dataTxt(dateDetail));
                    }
                    Elements elements = page.getHtml().getDocument().body().select("body > div > p:nth-child(4)");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        chinaUnicomDataItem.setFormatContent(formatContent);
                        dataItems.add(chinaUnicomDataItem);
                    }
                } catch (Exception e) {
                    log.error("", e);
                    log.error("href={}", href);
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
