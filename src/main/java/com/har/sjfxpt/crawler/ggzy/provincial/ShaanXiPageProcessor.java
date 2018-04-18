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

import static com.har.sjfxpt.crawler.ggzy.provincial.ShaanXiPageProcessor.*;

/**
 * Created by Administrator on 2017/12/15.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZYSHAANXI,
        sources = {
                @Source(url = GGZYSHAANXI_URL1, type = "招标/资审公告"),
                @Source(url = GGZYSHAANXI_URL2, type = "澄清/变更公告"),
                @Source(url = GGZYSHAANXI_URL3, type = "中标/成交公示"),
                @Source(url = GGZYSHAANXI_URL4, type = "中标候选人公示"),
                @Source(url = GGZYSHAANXI_URL5, type = "采购公告"),
                @Source(url = GGZYSHAANXI_URL6, type = "澄清/变更公告"),
                @Source(url = GGZYSHAANXI_URL7, type = "中标/成交公示"),
        }
)
public class ShaanXiPageProcessor implements BasePageProcessor {

    final static String GGZYSHAANXI_URL1 = "http://www.sxggzyjy.cn/jydt/001001/001001001/001001001001/1.html";
    final static String GGZYSHAANXI_URL2 = "http://www.sxggzyjy.cn/jydt/001001/001001001/001001001002/1.html";
    final static String GGZYSHAANXI_URL3 = "http://www.sxggzyjy.cn/jydt/001001/001001001/001001001003/1.html";
    final static String GGZYSHAANXI_URL4 = "http://www.sxggzyjy.cn/jydt/001001/001001001/001001001005/1.html";

    final static String GGZYSHAANXI_URL5 = "http://www.sxggzyjy.cn/jydt/001001/001001004/001001004001/1.html";
    final static String GGZYSHAANXI_URL6 = "http://www.sxggzyjy.cn/jydt/001001/001001004/001001004002/1.html";
    final static String GGZYSHAANXI_URL7 = "http://www.sxggzyjy.cn/jydt/001001/001001004/001001001003/1.html";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        String type = page.getRequest().getExtra("type").toString();
        int pageNum = Integer.parseInt(StringUtils.substringBefore(StringUtils.substringAfterLast(url, "/"), ".html"));
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("#index");
            if (!elements.isEmpty()) {
                int pageCount = Integer.parseInt(StringUtils.substringAfter(elements.text(), "/"));
                int cycleCount = pageCount >= 4 ? 4 : pageCount;
                for (int i = 2; i <= cycleCount; i++) {
                    String urlTarget = url.replace("1.html", i + ".html");
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
        Elements elements = page.getHtml().getDocument().body().select("#categorypagingcontent > div.ewb-list > ul > li");
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
                    href = "http://www.sxggzyjy.cn" + href;
                }
                String title = element.select("a").attr("title");
                BidNewsOriginal ggzyShaanXiDataItem = new BidNewsOriginal(href, SourceCode.GGZYSHAANXI);
                ggzyShaanXiDataItem.setUrl(href);
                ggzyShaanXiDataItem.setTitle(title);
                ggzyShaanXiDataItem.setProvince("陕西");
                try {
                    Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    String dataInformation = page.getHtml().getDocument().body().select("body > div.ewb-container > div.ewb-main > div.info-source").text();
                    String dataReal = StringUtils.substringBetween(dataInformation, "信息时间：", "】");
                    if (StringUtils.isNotBlank(dataReal)) {
                        ggzyShaanXiDataItem.setDate(PageProcessorUtil.dataTxt(dataReal));
                    }
                    if (PageProcessorUtil.timeCompare(ggzyShaanXiDataItem.getDate())) {
                        log.info("{} is not the sameday", ggzyShaanXiDataItem.getUrl());
                        continue;
                    }
                    Elements elements = page.getHtml().getDocument().body().select("#mainContent");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        ggzyShaanXiDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyShaanXiDataItem);
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
