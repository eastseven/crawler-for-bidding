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

import static com.har.sjfxpt.crawler.ggzy.provincial.ShanDongPageProcessor.*;

/**
 * Created by Administrator on 2017/12/15.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZYSHANDONG,
        sources = {
                @Source(url = GGZYSHANDONG_URL1),
                @Source(url = GGZYSHANDONG_URL2),
                @Source(url = GGZYSHANDONG_URL3),
                @Source(url = GGZYSHANDONG_URL4),
                @Source(url = GGZYSHANDONG_URL5),
                @Source(url = GGZYSHANDONG_URL6),
                @Source(url = GGZYSHANDONG_URL7),
                @Source(url = GGZYSHANDONG_URL8),
                @Source(url = GGZYSHANDONG_URL9),
                @Source(url = GGZYSHANDONG_URL10)
        }
)
public class ShanDongPageProcessor implements BasePageProcessor {

    final static String GGZYSHANDONG_URL1 = "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=117&ext=";
    final static String GGZYSHANDONG_URL2 = "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=89&ext=";
    final static String GGZYSHANDONG_URL3 = "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=87&ext=";
    final static String GGZYSHANDONG_URL4 = "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=88&ext=";
    final static String GGZYSHANDONG_URL5 = "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=86&ext=";

    final static String GGZYSHANDONG_URL6 = "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=94&ext=";
    final static String GGZYSHANDONG_URL7 = "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=90&ext=";
    final static String GGZYSHANDONG_URL8 = "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=92&ext=";
    final static String GGZYSHANDONG_URL9 = "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=93&ext=";
    final static String GGZYSHANDONG_URL10 = "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=91&ext=";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        int pageNum = Integer.parseInt(StringUtils.substringBetween(url, "queryContent_", "-jyxx.jspx"));
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("body > div.content > div.jyxxcontent > div > div > ul > li:nth-child(1) > a");
            int pageCount = Integer.parseInt(StringUtils.substringBetween(elements.text(), "/", "页"));
            if (pageCount >= 2) {
                for (int i = 2; i <= pageCount; i++) {
                    String urlTarget = StringUtils.replace(url, "queryContent_1", "queryContent_" + i);
                    page.addTargetRequest(urlTarget);
                }
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Elements elements = page.getHtml().getDocument().body().select("body > div.content > div.jyxxcontent > div > ul >li");
        List<BidNewsOriginal> dataItems = parseContent(elements);
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
            String href = element.select("div.article-list3-t > a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                String title = element.select("div.article-list3-t > a").text();
                String date = element.select("div.article-list3-t > div").text();
                String type = element.select("div.article-list3-t2 > div:nth-child(3)").text();

                BidNewsOriginal dataItem = new BidNewsOriginal(href, SourceCode.GGZYSHANDONG);
                dataItem.setUrl(href);
                dataItem.setTitle(title);
                dataItem.setDate(PageProcessorUtil.dataTxt(date));
                dataItem.setProvince("山东");
                dataItem.setType(StringUtils.substringAfter(type, "："));
                if (PageProcessorUtil.timeCompare(dataItem.getDate())) {
                    log.warn("{} is not the same day", dataItem.getUrl());
                    continue;
                }
                try {
                    Page page = httpClientDownloader.download(new Request(dataItem.getUrl()), SiteUtil.get().setTimeOut(30000).toTask());
                    Elements elements = page.getHtml().getDocument().body().select("body > div.content > div.div-content.clearfix > div:nth-child(5) > div.div-article2");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        dataItem.setFormatContent(formatContent);
                        dataItems.add(dataItem);
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
