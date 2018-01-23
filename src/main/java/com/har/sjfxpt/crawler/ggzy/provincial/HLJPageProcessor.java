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

import static com.har.sjfxpt.crawler.ggzy.provincial.HLJPageProcessor.*;

/**
 * Created by Administrator on 2017/12/5.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZYHLJ,
        sources = {
                @Source(url = GGZYHLJ_URL1, type = "交易公告"),
                @Source(url = GGZYHLJ_URL2, type = "交易证明书"),
                @Source(url = GGZYHLJ_URL3, type = "中标候选人公示"),
                @Source(url = GGZYHLJ_URL4, type = "流标/废标公示"),
                @Source(url = GGZYHLJ_URL5, type = "项目澄清")
        }
)
public class HLJPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    final static String GGZYHLJ_URL1 = "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=1";
    final static String GGZYHLJ_URL2 = "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=3";
    final static String GGZYHLJ_URL3 = "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=4";
    final static String GGZYHLJ_URL4 = "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=5";
    final static String GGZYHLJ_URL5 = "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=7";

    @Override
    public void handlePaging(Page page) {
        String type = (String) page.getRequest().getExtra("type");
        String url = page.getUrl().get();
        int pageNum = Integer.parseInt(StringUtils.substringBetween(url, "pageNo=", "&type"));
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("body > div > div.content_box > div.main_wrap > div.news_inf > div > div > span");
            String elementsText = elements.text();
            int pageCount = Integer.parseInt(StringUtils.substringBetween(elementsText, "/", "页"));
            int cycleCount = pageCount >= 5 ? 5 : pageCount;
            for (int i = 2; i <= cycleCount; i++) {
                String urlTarget = url.replace("pageNo=1", "pageNo=" + i);
                Request request = new Request(urlTarget);
                request.putExtra("type", type);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        String type = (String) page.getRequest().getExtra("type");
        Elements elements = page.getHtml().getDocument().body().select("body > div > div.content_box > div.main_wrap > div.news_inf > div > ul > li");
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
            try {
                String url = element.select("a").attr("href");
                if (StringUtils.isNotBlank(url)) {
                    String href = "http://hljggzyjyw.gov.cn" + url;
                    String title = element.select("a").attr("title");
                    String date = element.select("span.date").text();
                    BidNewsOriginal ggzyhljDataItem = new BidNewsOriginal(href, SourceCode.GGZYHLJ);
                    ggzyhljDataItem.setTitle(title);
                    ggzyhljDataItem.setProvince("黑龙江");
                    ggzyhljDataItem.setDate(PageProcessorUtil.dataTxt(date));
                    if (PageProcessorUtil.timeCompare(ggzyhljDataItem.getDate())) {
                        log.warn("{} is not the same day", ggzyhljDataItem.getUrl());
                        continue;
                    }
                    Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    Elements elements = page.getHtml().getDocument().select("#contentdiv");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        ggzyhljDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyhljDataItem);
                    }
                }
            } catch (Exception e) {
                log.error("", e);
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
