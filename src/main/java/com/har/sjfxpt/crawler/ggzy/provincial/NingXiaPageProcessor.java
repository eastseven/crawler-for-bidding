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

import static com.har.sjfxpt.crawler.ggzy.provincial.NingXiaPageProcessor.*;

/**
 * Created by Administrator on 2017/12/17.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZYNINGXIA,
        sources = {
                @Source(url = GGZYNINGXIA_URL1, type = "招标/资审公告"),
                @Source(url = GGZYNINGXIA_URL2, type = "澄清/变更公告"),
                @Source(url = GGZYNINGXIA_URL3, type = "中标公示/公告"),

                @Source(url = GGZYNINGXIA_URL4, type = "采购公告"),
                @Source(url = GGZYNINGXIA_URL5, type = "澄清/变更公告"),
                @Source(url = GGZYNINGXIA_URL6, type = "中标/成交公示")
        }
)
public class NingXiaPageProcessor implements BasePageProcessor {

    final static String GGZYNINGXIA_URL1 = "http://www.nxggzyjy.org/ningxiaweb/002/002001/002001001/1.html";
    final static String GGZYNINGXIA_URL2 = "http://www.nxggzyjy.org/ningxiaweb/002/002001/002001002/1.html";
    final static String GGZYNINGXIA_URL3 = "http://www.nxggzyjy.org/ningxiaweb/002/002001/002001003/1.html";
    final static String GGZYNINGXIA_URL4 = "http://www.nxggzyjy.org/ningxiaweb/002/002002/002002001/1.html";
    final static String GGZYNINGXIA_URL5 = "http://www.nxggzyjy.org/ningxiaweb/002/002002/002002002/1.html";
    final static String GGZYNINGXIA_URL6 = "http://www.nxggzyjy.org/ningxiaweb/002/002002/002001003/1.html";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        String type = page.getRequest().getExtra("type").toString();
        int pageNum = Integer.parseInt(StringUtils.substringBefore(StringUtils.substringAfterLast(url, "/"), ".html"));
        if (pageNum == 1) {
            String pageCountField = page.getHtml().getDocument().body().select("#index").text();
            int pageCount = Integer.parseInt(StringUtils.substringAfter(pageCountField, "/"));
            int cycleCount = pageCount >= 3 ? 3 : pageCount;
            for (int i = 2; i <= cycleCount; i++) {
                String urlTarget = url.replace("1.html", i + ".html");
                Request request = new Request(urlTarget);
                request.putExtra("type", type);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        String type = page.getRequest().getExtra("type").toString();
        Elements elements = page.getHtml().getDocument().body().select("#showList > ul > li");
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
            String href = element.select("a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                if (!StringUtils.startsWith(href, "http:")) {
                    href = "http://www.nxggzyjy.org" + href;
                }
                String title = element.text();
                BidNewsOriginal ggzyNingXiaDataItem = new BidNewsOriginal(href, SourceCode.GGZYNINGXIA);
                ggzyNingXiaDataItem.setUrl(href);
                ggzyNingXiaDataItem.setProvince("宁夏");
                ggzyNingXiaDataItem.setTitle(title);

                try {
                    Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    String dateContext = page.getHtml().getDocument().body().select("body > div:nth-child(4) > div > div.ewb-main-bar").text();
                    String dateReal = StringUtils.substringBetween(dateContext, "【信息时间：", "】");
                    ggzyNingXiaDataItem.setDate(PageProcessorUtil.dataTxt(dateReal));
                    if (PageProcessorUtil.timeCompare(ggzyNingXiaDataItem.getDate())) {
                        log.info("{} is not the same day", ggzyNingXiaDataItem.getUrl());
                        continue;
                    }
                    Elements elements = page.getHtml().getDocument().body().select("#gonggaoid");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        ggzyNingXiaDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyNingXiaDataItem);
                    }
                } catch (Exception e) {
                    log.error("", e);
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
