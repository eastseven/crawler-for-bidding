package com.har.sjfxpt.crawler.shenhua;

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
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;

/**
 * Created by Administrator on 2017/12/22.
 */
@Slf4j
@Component
@SourceConfig(code = SourceCode.SHENHUA, sources = {
        @Source(url = "http://www.shenhuabidding.com.cn/bidweb/001/001001/1.html", type = "资格预审公告"),
        @Source(url = "http://www.shenhuabidding.com.cn/bidweb/001/001002/1.html", type = "招标公告"),
        @Source(url = "http://www.shenhuabidding.com.cn/bidweb/001/001003/1.html", type = "非招标公告"),
        @Source(url = "http://www.shenhuabidding.com.cn/bidweb/001/001004/1.html", type = "变更公告"),
        @Source(url = "http://www.shenhuabidding.com.cn/bidweb/001/001005/1.html", type = "候选人公示"),
        @Source(url = "http://www.shenhuabidding.com.cn/bidweb/001/001006/1.html", type = "中标公告"),
        @Source(url = "http://www.shenhuabidding.com.cn/bidweb/001/001007/1.html", type = "终止公告"),
        @Source(url = "http://www.shenhuabidding.com.cn/bidweb/001/001008/1.html", type = "拟单一来源公示"),
})
public class ShenHuaPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        int pageCount = Integer.parseInt(StringUtils.substringBefore(StringUtils.substringAfterLast(url, "/"), ".html"));
        if (pageCount == 1) {
            Elements elements = page.getHtml().getDocument().body().select("#index");
            int pageNum = Integer.parseInt(StringUtils.substringAfter(elements.text(), "/"));
            int cycleNum = pageNum >= 4 ? 4 : pageNum;
            for (int i = 2; i <= cycleNum; i++) {
                String urlTarget = url.replace("1.html", i + ".html");
                page.addTargetRequest(urlTarget);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        String url = page.getUrl().get();
        Elements elements = page.getHtml().getDocument().body().select("body > div.container.mt20 > div > div.right.ml20 > div.right-bd > ul.right-items > li");
        List<BidNewsOriginal> dataItems = parseContent(elements);
        String type = typeJudgement(url);
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
            String href = element.select("div > a.infolink").attr("href");
            if (StringUtils.isNotBlank(href)) {
                if (!StringUtils.startsWith(href, "http:")) {
                    href = "http://www.shenhuabidding.com.cn" + href;
                }
                String title = element.select("div > a.infolink").attr("title");
                String date = element.select("span").text();
                BidNewsOriginal shenHuaDataItem = new BidNewsOriginal(href);
                shenHuaDataItem.setSource(SourceCode.SHENHUA.getValue());
                shenHuaDataItem.setSourceCode(SourceCode.SHENHUA.name());
                shenHuaDataItem.setUrl(href);
                shenHuaDataItem.setTitle(title);
                shenHuaDataItem.setProvince(ProvinceUtil.get(title));
                shenHuaDataItem.setDate(PageProcessorUtil.dataTxt(date));

                Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                Elements dateDetail = page.getHtml().getDocument().body().select("body > div.container.mt20 > div.row > div.article > div.article-info > p");
                String dateField = StringUtils.substringBetween(dateDetail.text(), "【发布时间：", " 阅读次数：");
                if (StringUtils.isNotBlank(dateField)) {
                    shenHuaDataItem.setDate(PageProcessorUtil.dataTxt(dateField));
                }
                if (PageProcessorUtil.timeCompare(shenHuaDataItem.getDate())) {
                    log.warn("{} is not the same day", shenHuaDataItem.getUrl());
                } else {
                    Elements elements = page.getHtml().getDocument().body().select("body > div.container.mt20 > div.row > div.article > div.article-info > div");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        if (formatContent.contains("无标题文档")) {
                            formatContent = StringUtils.remove(formatContent, "无标题文档");
                        }
                        shenHuaDataItem.setFormatContent(formatContent);
                        dataItems.add(shenHuaDataItem);
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

    public String typeJudgement(String url) {
        String type = null;
        String typeId = StringUtils.substringBetween(url, "001/", "/");
        switch (typeId) {
            case "001002":
                type = "招标公告";
                break;
            case "001001":
                type = "资格预审公告";
                break;
            case "001003":
                type = "非招标公告";
                break;
            case "001004":
                type = "变更公告";
                break;
            case "001005":
                type = "候选人公示";
                break;
            case "001006":
                type = "中标公告";
                break;
            case "001007":
                type = "终止公告";
                break;
            case "001008":
                type = "拟单一来源公示";
                break;
            default:
        }
        return type;
    }
}
