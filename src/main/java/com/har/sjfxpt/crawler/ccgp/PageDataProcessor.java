package com.har.sjfxpt.crawler.ccgp;

import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

@Slf4j
@Component
public class PageDataProcessor implements PageProcessor {

    @Autowired
    PageDataRepository repository;

    @Override
    public void process(Page page) {
        PageData pageData = (PageData) page.getRequest().getExtra(PageData.class.getSimpleName());
        if (pageData == null) {
            log.error("{} PageData is null", page.getUrl().get());
            return;
        }

        Element totalSize = page.getHtml().getDocument().body().select("body > div:nth-child(8) > div:nth-child(1) > div > p:nth-child(1)").first();
        String totalSizeText = totalSize.text();
        totalSizeText = StringUtils.substringBetween(totalSizeText, "共找到", "条内容");
        totalSizeText = StringUtils.trim(totalSizeText);
        Element pager = page.getHtml().getDocument().body().select("body > div:nth-child(8) > div:nth-child(1) > div > p.pager script").first();
        String totalPageText = pager.html();
        totalPageText = StringUtils.substringBetween(totalPageText, "size: ", ",");

        pageData.setPage(Integer.parseInt(totalPageText));
        pageData.setSize(Integer.parseInt(totalSizeText));

        repository.save(pageData);
        log.debug("{}", pageData);
    }

    @Override
    public Site getSite() {
        return SiteUtil.get().setSleepTime(RandomUtils.nextInt(5000, 10000));
    }
}
