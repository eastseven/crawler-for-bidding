package com.har.sjfxpt.crawler.ccgp.provincial;

import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;

import static com.har.sjfxpt.crawler.ccgp.provincial.BeiJingPageProcessor.*;

/**
 * Created by Administrator on 2018/1/15.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.CCGPBEIJING,
        sources = {
                @Source(url = CCGPBJ_URL1, type = "招标公告"),
                @Source(url = CCGPBJ_URL2, type = "中标公告"),
                @Source(url = CCGPBJ_URL3, type = "合同公告"),
                @Source(url = CCGPBJ_URL4, type = "更正公告"),
                @Source(url = CCGPBJ_URL5, type = "废标公告"),
                @Source(url = CCGPBJ_URL6, type = "单一公告"),
                @Source(url = CCGPBJ_URL7, type = "其他公告"),
                @Source(url = CCGPBJ_URL8, type = "招标公告"),
                @Source(url = CCGPBJ_URL9, type = "中标公告"),
                @Source(url = CCGPBJ_URL10, type = "合同公告"),
                @Source(url = CCGPBJ_URL11, type = "更正公告"),
                @Source(url = CCGPBJ_URL12, type = "废标公告"),
                @Source(url = CCGPBJ_URL13, type = "单一公告"),
                @Source(url = CCGPBJ_URL14, type = "其他公告")
        }
)
public class BeiJingPageProcessor implements BasePageProcessor {

    final static String CCGPBJ_URL1 = "http://www.ccgp-beijing.gov.cn/xxgg/sjzfcggg/sjzbgg/index.html";
    final static String CCGPBJ_URL2 = "http://www.ccgp-beijing.gov.cn/xxgg/sjzfcggg/sjzbjggg/index.html";
    final static String CCGPBJ_URL3 = "http://www.ccgp-beijing.gov.cn/xxgg/sjzfcggg/sjhtgg/index.html";
    final static String CCGPBJ_URL4 = "http://www.ccgp-beijing.gov.cn/xxgg/sjzfcggg/sjgzgg/index.html";
    final static String CCGPBJ_URL5 = "http://www.ccgp-beijing.gov.cn/xxgg/sjzfcggg/sjfbgg/index.html";
    final static String CCGPBJ_URL6 = "http://www.ccgp-beijing.gov.cn/xxgg/sjzfcggg/sjdygg/index.html";
    final static String CCGPBJ_URL7 = "http://www.ccgp-beijing.gov.cn/xxgg/sjzfcggg/sjqtgg/index.html";
    final static String CCGPBJ_URL8 = "http://www.ccgp-beijing.gov.cn/xxgg/sjzfcggg/sjdygg/index.html";

    final static String CCGPBJ_URL9 = "http://www.ccgp-beijing.gov.cn/xxgg/qjzfcggg/qjzbgg/index.html";
    final static String CCGPBJ_URL10 = "http://www.ccgp-beijing.gov.cn/xxgg/qjzfcggg/qjzbjggg/index.html";
    final static String CCGPBJ_URL11 = "http://www.ccgp-beijing.gov.cn/xxgg/qjzfcggg/qjhtgg/index.html";
    final static String CCGPBJ_URL12 = "http://www.ccgp-beijing.gov.cn/xxgg/qjzfcggg/qjgzgg/index.html";
    final static String CCGPBJ_URL13 = "http://www.ccgp-beijing.gov.cn/xxgg/qjzfcggg/qjfbgg/index.html";
    final static String CCGPBJ_URL14 = "http://www.ccgp-beijing.gov.cn/xxgg/qjzfcggg/qjdygg/index.html";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        String type = page.getRequest().getExtra("type").toString();
        String currentPage = StringUtils.substringAfter(url, "/index");
        if (currentPage.equalsIgnoreCase(".html")) {
            //没取到列表总页数，固定循环5页
            for (int i = 1; i <= 5; i++) {
                String urlTarget = url.replace("index.html", "index_" + i + ".html");
                Request request = new Request(urlTarget);
                request.putExtra("type", type);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        String url = page.getUrl().get();
        String type = page.getRequest().getExtra("type").toString();
        Elements elements = page.getHtml().getDocument().body().select("body > ul > li");
        List<BidNewsOriginal> dataItems = parseContent(elements, url);
        dataItems.forEach(dataItem -> dataItem.setType(type));
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        return null;
    }

    public List parseContent(Elements items, String url) {
        List<BidNewsOriginal> bidNewsOriginalList = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("a").attr("href");
            try {
                if (StringUtils.isNotBlank(href)) {
                    if (StringUtils.startsWith(href, "./")) {
                        String urlPrefix = StringUtils.substringBefore(url, "/index");
                        String announcementUrl = urlPrefix + StringUtils.substringAfter(href, ".");

                        String title = element.select("a").text();
                        String date = element.select("span").text();
                        BidNewsOriginal bidNewsOriginal = new BidNewsOriginal(announcementUrl, SourceCode.CCGPBEIJING);
                        bidNewsOriginal.setTitle(title);
                        bidNewsOriginal.setDate(PageProcessorUtil.dataTxt(date));
                        bidNewsOriginal.setProvince("北京");
                        if (PageProcessorUtil.timeCompare(bidNewsOriginal.getDate())) {
                            log.warn("{} is not the same day", bidNewsOriginal.getUrl());
                            continue;
                        }
                        Page page = httpClientDownloader.download(new Request(announcementUrl), SiteUtil.get().setTimeOut(30000).toTask());
                        Elements elements = page.getHtml().getDocument().body().select("body > div:nth-child(2) > div:nth-child(3)");
                        String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                        if (StringUtils.isNotBlank(formatContent)) {
                            bidNewsOriginal.setFormatContent(formatContent);
                            bidNewsOriginalList.add(bidNewsOriginal);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("", e);
                log.error("url={}", href);
            }
        }
        return bidNewsOriginalList;
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
