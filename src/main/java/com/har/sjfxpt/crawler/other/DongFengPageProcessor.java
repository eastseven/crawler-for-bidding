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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;

import static com.har.sjfxpt.crawler.other.DongFengPageProcessor.*;

/**
 * Created by Administrator on 2017/11/13.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.DONGFENG,
        sources = {
                @Source(url = DONGFENG_URL1),
                @Source(url = DONGFENG_URL2),
                @Source(url = DONGFENG_URL3),
                @Source(url = DONGFENG_URL4),
        }
)
public class DongFengPageProcessor implements BasePageProcessor {

    @Autowired
    HttpClientDownloader httpClientDownloader;

    public final static String DONGFENG_URL1 = "http://jyzx.dfmbidding.com/zbgg/index_1.jhtml";
    public final static String DONGFENG_URL2 = "http://jyzx.dfmbidding.com/pbgs/index_1.jhtml";
    public final static String DONGFENG_URL3 = "http://jyzx.dfmbidding.com/zgys/index_1.jhtml";
    public final static String DONGFENG_URL4 = "http://jyzx.dfmbidding.com/bggg/index_1.jhtml";

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        int pageCount = Integer.parseInt(StringUtils.substringBetween(url, "index_", ".jhtml"));
        if (pageCount == 1) {
            Elements elements = page.getHtml().getDocument().body().select("#main > div.listPage.wrap > div.wrap01 > div.mleft > div > div.m-bd > div > div > ul:nth-child(3) > div > div > div.pag-txt");
            int pageNum = Integer.parseInt(StringUtils.substringBetween(elements.text(), "共计", "页"));
            int cycleNum = pageNum >= 3 ? 3 : pageNum;
            for (int i = 2; i <= cycleNum; i++) {
                String urlTarget = StringUtils.replace(url, "index_1", "index_" + i);
                page.addTargetRequest(urlTarget);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        String url = page.getUrl().get();
        Elements elements = page.getHtml().getDocument().body().select("#main > div.listPage.wrap > div.wrap01 > div.mleft > div > div.m-bd > div > div > ul > li");
        List<BidNewsOriginal> dataItems = parseContent(elements);
        String type = typeJudgment(url);
        dataItems.forEach(dataItem -> dataItem.setType(type));
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    public String typeJudgment(String url) {
        String typeField = StringUtils.substringBetween(url, "com/", "/index_");
        String type = null;
        switch (typeField) {
            case "zbgg":
                type = "招标公告";
                break;
            case "pbgs":
                type = "中标候选人公示";
                break;
            case "zgys":
                type = "资格预审公告";
                break;
            case "bggg":
                type = "变更公告";
                break;
            default:
        }
        return type;
    }

    @Override
    public List parseContent(Elements items) {
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                try {
                    String title = element.select("a").attr("title");
                    String date = element.select("a > span.bidDate").text();
                    BidNewsOriginal dongFengDataItem = new BidNewsOriginal(href, SourceCode.DONGFENG);
                    dongFengDataItem.setTitle(title);
                    dongFengDataItem.setProvince(ProvinceUtil.get(title));
                    dongFengDataItem.setDate(PageProcessorUtil.dataTxt(date));


                    Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    Elements timeDetail = page.getHtml().getDocument().body().select("#main > div.listPage.wrap > div.wrap01 > div.mleft > div > div > div.ninfo-title > span");
                    String dateDetail = PageProcessorUtil.dataTxt(timeDetail.text());
                    if (PageProcessorUtil.timeCompare(dateDetail)) {
                        log.info("{} is not the same day", href);
                        continue;
                    }
                    if (StringUtils.isNotBlank(dateDetail)) {
                        dongFengDataItem.setDate(dateDetail);
                    }
                    Elements elements = page.getHtml().getDocument().body().select("#main > div.listPage.wrap > div.wrap01 > div.mleft > div > div > div.ninfo-con");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        dongFengDataItem.setFormatContent(formatContent);
                        dataItems.add(dongFengDataItem);
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
