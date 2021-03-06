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
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.provincial.GZPageProcessor.*;

/**
 * Created by Administrator on 2017/11/29.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZYGZ,
        sources = {
                @Source(url = GGZYGUIZHOU_URL1, type = "交易公告"),
                @Source(url = GGZYGUIZHOU_URL2, type = "资审结果公示"),
                @Source(url = GGZYGUIZHOU_URL3, type = "交易结果公示"),
                @Source(url = GGZYGUIZHOU_URL4, type = "流标公示"),
                @Source(url = GGZYGUIZHOU_URL5, type = "交易公告"),
                @Source(url = GGZYGUIZHOU_URL6, type = "资审结果公示"),
                @Source(url = GGZYGUIZHOU_URL7, type = "交易结果公示"),
                @Source(url = GGZYGUIZHOU_URL8, type = "流标公示")
        }
)
public class GZPageProcessor implements BasePageProcessor {

    final static String GGZYGUIZHOU_URL1 = "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=GP&businessType=JYGG&inDates=1&ext=&origin=ALL";
    final static String GGZYGUIZHOU_URL2 = "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=GP&businessType=ZSJGGS&inDates=1&ext=&origin=ALL";
    final static String GGZYGUIZHOU_URL3 = "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=GP&businessType=JYJGGS&inDates=1&ext=&origin=ALL";
    final static String GGZYGUIZHOU_URL4 = "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=GP&businessType=FBGG&inDates=1&ext=&origin=ALL";
    final static String GGZYGUIZHOU_URL5 = "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=CE&businessType=JYGG&inDates=1&ext=&origin=ALL";
    final static String GGZYGUIZHOU_URL6 = "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=CE&businessType=ZSJGGS&inDates=1&ext=&origin=ALL";
    final static String GGZYGUIZHOU_URL7 = "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=CE&businessType=JYJGGS&inDates=1&ext=&origin=ALL";
    final static String GGZYGUIZHOU_URL8 = "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=CE&businessType=FBGG&inDates=1&ext=&origin=ALL";


    HttpClientDownloader httpClientDownloader;

    final static String PAGE_PARAMS = "pageParams";

    @Override
    public void handlePaging(Page page) {
        String type = (String) page.getRequest().getExtra("type");
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtra(PAGE_PARAMS);
        int currentPage = Integer.parseInt(StringUtils.substringBetween(page.getUrl().toString(), "/queryContent_", ".jspx"));
        if (currentPage == 1) {
            Elements elements = page.getHtml().getDocument().body().select("body > div.main > div.news_box > div.pagesite > div > ul > li:nth-child(1) > a");
            int pageNum = Integer.parseInt(StringUtils.substringBetween(elements.text(), "/", "页"));
            if (pageNum >= 2) {
                for (int i = 2; i <= pageNum; i++) {
                    String url = page.getUrl().toString().replace("queryContent_1", "queryContent_" + i);
                    Request request = new Request(url);
                    request.putExtra(PAGE_PARAMS, pageParams);
                    request.putExtra("type", type);
                    page.addTargetRequest(request);
                }
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        String type = (String) page.getRequest().getExtra("type");
        Elements elements = page.getHtml().getDocument().body().select("#news_list1 > li");
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
        List<BidNewsOriginal> ggzyGZDataItems = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("a").attr("href");
            if (StringUtils.isBlank(href)) continue;
            String title = element.select("a").attr("title");
            try {
                BidNewsOriginal ggzyGZDataItem = new BidNewsOriginal(href, SourceCode.GGZYGZ);
                ggzyGZDataItem.setUrl(href);
                ggzyGZDataItem.setTitle(title);
                ggzyGZDataItem.setProvince("贵州");

                Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                String dataDetail = page.getHtml().getDocument().body().select("body > div.main > div.content_box > div.infos > span:nth-child(2)").text();
                ggzyGZDataItem.setDate(PageProcessorUtil.dataTxt(dataDetail));
                Elements elements = page.getHtml().getDocument().body().select("body > div.main > div.content_box");
                String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                if (StringUtils.isNotBlank(formatContent)) {
                    ggzyGZDataItem.setFormatContent(formatContent);
                    ggzyGZDataItems.add(ggzyGZDataItem);
                }
            } catch (Exception e) {
                log.error("", e);
                log.error("url={}", href);
            }
        }
        return ggzyGZDataItems;
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
