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

import static com.har.sjfxpt.crawler.ggzy.provincial.XZPageProcessor.*;

/**
 * Created by Administrator on 2017/12/5.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZYXZ,
        sources = {
                @Source(url = GGZYXZ_URL1, type = "招标/资审公告"),
                @Source(url = GGZYXZ_URL2, type = "交易结果公告"),
                @Source(url = GGZYXZ_URL3, type = "招标/招标文件澄清"),
                @Source(url = GGZYXZ_URL4, type = "资格预审结果"),
                @Source(url = GGZYXZ_URL5, type = "采购/资审公告"),
                @Source(url = GGZYXZ_URL6, type = "中标公告"),
                @Source(url = GGZYXZ_URL7, type = "采购合同"),
                @Source(url = GGZYXZ_URL8, type = "更正事项"),
        }
)
public class XZPageProcessor implements BasePageProcessor {

    final static String GGZYXZ_URL1 = "http://www.xzggzy.gov.cn:9090/zbzsgg/index_1.jhtml";
    final static String GGZYXZ_URL2 = "http://www.xzggzy.gov.cn:9090/jyjggg/index_1.jhtml";
    final static String GGZYXZ_URL3 = "http://www.xzggzy.gov.cn:9090/zbwjcq/index_1.jhtml";
    final static String GGZYXZ_URL4 = "http://www.xzggzy.gov.cn:9090/zgysjg/index_1.jhtml";
    final static String GGZYXZ_URL5 = "http://www.xzggzy.gov.cn:9090/cggg/index_1.jhtml";
    final static String GGZYXZ_URL6 = "http://www.xzggzy.gov.cn:9090/zbgg/index_1.jhtml";
    final static String GGZYXZ_URL7 = "http://www.xzggzy.gov.cn:9090/cght/index_1.jhtml";
    final static String GGZYXZ_URL8 = "http://www.xzggzy.gov.cn:9090/gzsx/index_1.jhtml";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String type = page.getRequest().getExtra("type").toString();
        String url = page.getUrl().get();
        int count = Integer.parseInt(StringUtils.substringBetween(url, "/index_", ".jhtml"));
        if (count == 1) {
            Elements elements = page.getHtml().getDocument().body().select("body > div.content-old > div.jyxxcontent-old > div.article-content-old > div.pagesite > div > ul > li:nth-child(1) > a");
            int pageCount = Integer.parseInt(StringUtils.substringBetween(elements.text(), "/", "页"));
            if (pageCount >= 2) {
                int cycleNum = pageCount >= 10 ? 10 : 2;
                for (int i = 2; i <= cycleNum; i++) {
                    String urlTarget = url.replace("index_1", "index_" + i);
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
        Elements elements = page.getHtml().getDocument().body().select("body > div.content-old > div.jyxxcontent-old > div.article-content-old > ul >li");
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

            String url = element.select("a").attr("href");
            String title = element.select("a").attr("title");
            String date = element.select("div").text();
            if (StringUtils.isNotBlank(url)) {
                try {
                    BidNewsOriginal ggzyxzDataItem = new BidNewsOriginal(url, SourceCode.GGZYXZ);
                    ggzyxzDataItem.setUrl(url);
                    ggzyxzDataItem.setTitle(title);
                    ggzyxzDataItem.setProvince("西藏");
                    if (StringUtils.isNotBlank(date)) {
                        ggzyxzDataItem.setDate(PageProcessorUtil.dataTxt(date));
                    }
                    if (PageProcessorUtil.timeCompare(ggzyxzDataItem.getDate())) {
                        log.warn("{} is not the same day", ggzyxzDataItem.getUrl());
                        continue;
                    }
                    Page page = httpClientDownloader.download(new Request(url), SiteUtil.get().setTimeOut(20000).toTask());
                    Elements elements = page.getHtml().getDocument().body().select("body > div.content > div.div-content > div.div-article2 > div");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        ggzyxzDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyxzDataItem);
                    }
                } catch (Exception e) {
                    log.error("", e);
                    log.error("url={}", url);
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
