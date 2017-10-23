package com.har.sjfxpt.crawler.ggzy.downloader;

import com.har.sjfxpt.crawler.ggzy.model.DataItem;
import com.har.sjfxpt.crawler.ggzy.repository.DataItemRepository;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

/**
 * @author dongqi
 */
@Slf4j
@Component
public class PageDownloader {

    @Autowired
    ExecutorService executorService;

    @Autowired
    DataItemRepository dataItemRepository;

    public void download(DataItem dataItem) {
        if (StringUtils.isNotBlank(dataItem.getTextContent())) return;
        if (StringUtils.isNotBlank(dataItem.getFormatContent())) return;
        if (StringUtils.isNotBlank(dataItem.getHtml())) return;

        String html = null;
        Document document = null;
        try {
            log.info("download {}, {}", dataItem.getId(), dataItem.getUrl());
            document = Jsoup.connect(dataItem.getUrl()).userAgent(SiteUtil.get().getUserAgent()).timeout(60000).get();

            String pubDate = document.select("body > div.detail > p.p_o > span:nth-child(1)").text();
            pubDate = StringUtils.removeAll(pubDate, "发布时间：");
            dataItem.setPubDate(StringUtils.defaultString(pubDate, DateTime.now().toString("yyyy-MM-dd HH:mm")));

            html = document.html();
            dataItem.setHtml(html);

            Element content = document.body().select("#mycontent").first();
            dataItem.setFormatContent(PageProcessorUtil.formatElementsByWhitelist(content));
            dataItem.setTextContent(PageProcessorUtil.extractTextByWhitelist(content));
        } catch (Exception e) {
            log.error("", e);
            log.error("{} download html content fail", dataItem.getId());
            return;
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
