package com.har.sjfxpt.crawler.ggzy.downloader;

import com.har.sjfxpt.crawler.ggzy.model.DataItem;
import com.har.sjfxpt.crawler.ggzy.repository.DataItemRepository;
import com.har.sjfxpt.crawler.ggzy.service.ProxyService;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.SimpleHttpClient;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

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
        simpleHttpClient.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxies()));
    }

    private Document getDocument(String url) {
        return simpleHttpClient.get(url).getHtml().getDocument();
    }

    public void download(DataItem dataItem) {
        if (StringUtils.isNotBlank(dataItem.getTextContent())) return;
        if (StringUtils.isNotBlank(dataItem.getFormatContent())) return;
        if (StringUtils.isNotBlank(dataItem.getHtml())) return;

        String html = null, formatContent = null, textContent = null;
        Document document = null;
        try {
            log.debug("download {}, {}", dataItem.getId(), dataItem.getUrl());
            document = getDocument(dataItem.getUrl());

            String pubDate = document.select("body > div.detail > p.p_o > span:nth-child(1)").text();
            pubDate = StringUtils.substringAfter(pubDate, "：");
            if (pubDate.trim().length() == ten) {
                pubDate = pubDate + " " + DateTime.now().toString("HH:mm");
            }
            dataItem.setPubDate(StringUtils.defaultString(pubDate, DateTime.now().toString("yyyy-MM-dd HH:mm")));

            html = document.html();
            dataItem.setHtml(html);

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
                textContent = PageProcessorUtil.extractTextByWhitelist(body);
            } else if (isImageTag) {
                //img
                formatContent = content.select("img").toString();
                textContent = formatContent;
            } else {
                formatContent = PageProcessorUtil.formatElementsByWhitelist(content);
                textContent = PageProcessorUtil.extractTextByWhitelist(content);
            }

            dataItem.setFormatContent(formatContent);
            dataItem.setTextContent(textContent);
        } catch (Exception e) {
            log.error("", e);
            log.error("{} download html content fail, {}", dataItem.getId(), dataItem.getUrl());
        }

    }

    public void download() {
        final int size = 10;
        Page<DataItem> first = dataItemRepository.findByHtmlIsNull(new PageRequest(0, size));
        if (!first.hasContent()) {
            return;
        }

        final int totalPages = first.getTotalPages();
        log.info("total page {}, total size {}", totalPages, first.getTotalElements());

        for (int page = 0; page < totalPages; page++) {
            Page<DataItem> dataItemPage = dataItemRepository.findByHtmlIsNull(new PageRequest(page, size));
            download(dataItemPage);
        }
    }

    public void download(int page, int size) {
        Page<DataItem> dataItemPage = dataItemRepository.findByTextContentIsNull(new PageRequest(page, size));
        if (dataItemPage != null && dataItemPage.hasContent()) {
            download(dataItemPage);
        }
    }

    public void download(Page<DataItem> page) {
        final int totalPages = page.getTotalPages();
        executorService.execute(() -> {
            page.forEach(dataItem -> {
                download(dataItem);
            });
            dataItemRepository.save(page);
            log.info("download page {} done, total size {}", totalPages, page.getTotalElements());
        });
    }
}
