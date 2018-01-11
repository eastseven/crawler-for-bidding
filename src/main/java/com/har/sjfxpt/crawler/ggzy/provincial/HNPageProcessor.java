package com.har.sjfxpt.crawler.ggzy.provincial;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
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

import static com.har.sjfxpt.crawler.ggzy.provincial.HNPageProcessor.*;

/**
 * Created by Administrator on 2017/11/29.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZYHN,
        sources = {
                @Source(url = GGZYHAINAN_URL1, type = "招标公告"),
                @Source(url = GGZYHAINAN_URL2, type = "中标公示"),
                @Source(url = GGZYHAINAN_URL3, type = "采购公告"),
                @Source(url = GGZYHAINAN_URL4, type = "中标公告"),
        }
)
public class HNPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    final static String GGZYHAINAN_URL1 = "http://www.ggzy.hi.gov.cn/ggzy/ggzy/jgzbgg/index_1.jhtml";
    final static String GGZYHAINAN_URL2 = "http://www.ggzy.hi.gov.cn/ggzy/ggzy/jgzbgs/index_1.jhtml";
    final static String GGZYHAINAN_URL3 = "http://www.ggzy.hi.gov.cn/ggzy/ggzy/cggg/index_1.jhtml";
    final static String GGZYHAINAN_URL4 = "http://www.ggzy.hi.gov.cn/ggzy/ggzy/cgzbgg/index_1.jhtml";

    @Override
    public void handlePaging(Page page) {
        String type = page.getRequest().getExtra("type").toString();
        int currentPage = Integer.parseInt(StringUtils.substringBetween(page.getUrl().toString(), "/index_", ".jhtml"));
        if (currentPage == 1) {
            Elements elements = page.getHtml().getDocument().body().select("body > div.containerNobg > div:nth-child(3) > div.w740 > table > tbody > tr:nth-child(11) > td > div > div");
            int size = Integer.parseInt(StringUtils.substringBetween(elements.text(), "/", "页"));
            int cycleNum = size >= 10 ? 10 : size;
            for (int i = 2; i <= cycleNum; i++) {
                String url = StringUtils.substringBefore(page.getUrl().toString(), "/index_") + "/index_" + i + ".jhtml";
                Request request = new Request(url);
                request.putExtra("type", type);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        String type = page.getRequest().getExtra("type").toString();
        Elements elements = page.getHtml().getDocument().body().select("body > div.containerNobg > div:nth-child(3) > div.w740 > table > tbody >tr");
        if (elements.isEmpty()) {
            log.error("fetch error, elements is empty");
            return;
        }
        List<BidNewsOriginal> dataItems = parseContent(elements);
        dataItems.forEach(dataItem -> dataItem.setType(type));
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
            String href = element.select("td > a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                String title = element.select("td > a").attr("title");
                String date = element.select(" td:nth-child(4)").text();
                log.debug("date={}", date);
                if (PageProcessorUtil.timeCompare(date)) {
                    log.info("{} is not on the same day", href);
                } else {
                    BidNewsOriginal ggzyHNDataItem = new BidNewsOriginal(href, SourceCode.GGZYHN);
                    ggzyHNDataItem.setUrl(href);
                    ggzyHNDataItem.setTitle(title);
                    ggzyHNDataItem.setProvince("海南");
                    if (date.length() == 10) {
                        date = PageProcessorUtil.dataTxt(date);
                    }
                    ggzyHNDataItem.setDate(date);
                    try {
                        Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                        Elements elements = page.getHtml().getDocument().body().select("body > div.container > div > div.newsTex > div.newsCon");
                        String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                        if (StringUtils.isNotBlank(formatContent)) {
                            ggzyHNDataItem.setFormatContent(formatContent);
                            dataItems.add(ggzyHNDataItem);
                        }
                    } catch (Exception e) {
                        log.warn("e{}", e);
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
