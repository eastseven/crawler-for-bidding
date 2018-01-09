package com.har.sjfxpt.crawler.ggzyprovincial.ggzyxjbt;

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
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyxjbt.GGZYXJBTPageProcessor.*;

/**
 * Created by Administrator on 2017/12/18.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZYXJBT,
        sources = {
                @Source(url = GGZYXJBT_URL1, type = "招标公告"),
                @Source(url = GGZYXJBT_URL2, type = "答疑澄清"),
                @Source(url = GGZYXJBT_URL3, type = "中标候选人公示"),
                @Source(url = GGZYXJBT_URL4, type = "中标结果公告"),
                @Source(url = GGZYXJBT_URL5, type = "资格预审公示"),
                @Source(url = GGZYXJBT_URL6, type = "变更公告"),
                @Source(url = GGZYXJBT_URL7, type = "单一来源公示"),
                @Source(url = GGZYXJBT_URL8, type = "采购公告"),
                @Source(url = GGZYXJBT_URL9, type = "变更公告"),
                @Source(url = GGZYXJBT_URL10, type = "答疑澄清"),
                @Source(url = GGZYXJBT_URL11, type = "结果公示"),
                @Source(url = GGZYXJBT_URL12, type = "合同公示"),
        }
)
public class GGZYXJBTPageProcessor implements BasePageProcessor {

    final static String GGZYXJBT_URL1 = "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001002/?Paging=1";
    final static String GGZYXJBT_URL2 = "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001003/?Paging=1";
    final static String GGZYXJBT_URL3 = "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001004/?Paging=1";
    final static String GGZYXJBT_URL4 = "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001005/?Paging=1";
    final static String GGZYXJBT_URL5 = "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001006/?Paging=1";
    final static String GGZYXJBT_URL6 = "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001007/?Paging=1";
    final static String GGZYXJBT_URL7 = "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002006/?Paging=1";
    final static String GGZYXJBT_URL8 = "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002002/?Paging=1";
    final static String GGZYXJBT_URL9 = "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002003/?Paging=1";
    final static String GGZYXJBT_URL10 = "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002004/?Paging=1";
    final static String GGZYXJBT_URL11 = "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002005/?Paging=1";
    final static String GGZYXJBT_URL12 = "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002007/?Paging=1";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        String type = page.getRequest().getExtra("type").toString();
        int pageNum = Integer.parseInt(StringUtils.substringAfter(url, "Paging="));
        if (pageNum == 1) {
            String pageCountContent = page.getHtml().getDocument().body().select("body > table:nth-child(12) > tbody > tr > td:nth-child(3) > table.top10 > tbody > tr:nth-child(2) > td > div > div > div > table > tbody > tr > td:nth-child(25)").text();
            if (StringUtils.isNotBlank(pageCountContent)) {
                int pageCount = Integer.parseInt(StringUtils.substringAfter(pageCountContent, "/"));
                int cycleCount = pageCount >= 3 ? 3 : pageCount;
                for (int i = 2; i <= cycleCount; i++) {
                    String urlTarget = url.replace("Paging=1", "Paging=" + i);
                    Request request = new Request(urlTarget);
                    request.putExtra("type", type);
                    page.addTargetRequest(request);
                }
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        String type = page.getRequest().getExtra("type").toString();
        Elements elements = page.getHtml().getDocument().body().select("body > table:nth-child(12) > tbody > tr > td:nth-child(3) > table.top10 > tbody > tr:nth-child(2) > td > div > table > tbody > tr");
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
            String href = element.select("td:nth-child(2) >a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                if (!StringUtils.startsWith(href, "http://ggzy.xjbt.gov.cn")) {
                    href = "http://ggzy.xjbt.gov.cn" + href;
                }
                String title = element.select("td:nth-child(2) >a").attr("title");
                String date = element.select("td:nth-child(3)").text();
                if (date.contains("[")) {
                    date = StringUtils.substringBetween(date, "[", "]");
                }
                BidNewsOriginal ggzyxjbtDataItem = new BidNewsOriginal(href, SourceCode.GGZYXJBT);
                ggzyxjbtDataItem.setUrl(href);
                ggzyxjbtDataItem.setTitle(title);
                ggzyxjbtDataItem.setDate(PageProcessorUtil.dataTxt(date));
                ggzyxjbtDataItem.setProvince("新疆");

                if (PageProcessorUtil.timeCompare(ggzyxjbtDataItem.getDate())) {
                    log.info("{} is not the same day", ggzyxjbtDataItem.getUrl());
                } else {
                    Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    Elements elements = page.getHtml().getDocument().body().select("#TDContent");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent) && StringUtils.isNotBlank(title)) {
                        ggzyxjbtDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyxjbtDataItem);
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
