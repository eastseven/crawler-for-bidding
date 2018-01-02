package com.har.sjfxpt.crawler.core.downloader;

import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.repository.DataItemRepository;
import com.har.sjfxpt.crawler.core.service.ProxyService;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.SimpleHttpClient;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;

/**
 * @author dongqi
 */
@Slf4j
@Component
public class GongGongZiYuanPageDownloader {

    final int ten = 10;

    @Autowired
    ExecutorService executorService;

    @Autowired
    DataItemRepository dataItemRepository;

    @Autowired
    ProxyService proxyService;

    private SimpleHttpClient simpleHttpClient;

    @PostConstruct
    public void init() {
        simpleHttpClient = new SimpleHttpClient(SiteUtil.get().setTimeOut(60000).setSleepTime(123));
    }

    private Document getDocument(String url) {
        return simpleHttpClient.get(url).getHtml().getDocument();
    }

    public void download(BidNewsOriginal dataItem) {
        String formatContent = null;
        Document document = null;
        try {
            log.debug("download {}, {}", dataItem.getId(), dataItem.getUrl());
            document = getDocument(dataItem.getUrl());

            String pubDate = document.select("body > div.detail > p.p_o > span:nth-child(1)").text();
            if (pubDate.contains("时间：")) {
                pubDate = StringUtils.substringAfter(pubDate, "：");
            }
            if (pubDate.trim().length() == ten) {
                pubDate = pubDate + " " + DateTime.now().toString("HH:mm");
            }

            try {
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(pubDate);
                dataItem.setDate(pubDate);
            } catch (Exception e) {
                log.error("", e);
            }

            if (document.body().select("#mycontent").isEmpty()) {
                throw new Exception(dataItem.getUrl() + " can not access, maybe 403 or 404...");
            }

            Element content = document.body().select("#mycontent").first();
            boolean hasIframeTag = !content.getElementsByTag("iframe").isEmpty();
            boolean isImageTag = !content.getElementsByTag("img").isEmpty();
            if (hasIframeTag) {
                //iframe
                String url = content.getElementsByTag("iframe").attr("src");
                log.debug("iframe url {}", url);
                if (StringUtils.endsWithIgnoreCase(url, ".pdf")) {
                    log.info("fetch content, {} ignore", url);
                    return;
                }

                Element body = getDocument(url).body();
                formatContent = PageProcessorUtil.formatElementsByWhitelist(body);
            } else if (isImageTag) {
                //img
                formatContent = content.select("img").toString();
            } else {
                formatContent = PageProcessorUtil.formatElementsByWhitelist(content);
            }

            dataItem.setFormatContent(formatContent);
        } catch (Exception e) {
            log.error("{} download html content fail, {}", dataItem.getId(), dataItem.getUrl());
        }

    }
}
