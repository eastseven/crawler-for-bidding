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

/**
 * @author dongqi
 */
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

        Element body = page.getHtml().getDocument().body();
        Element pager = body.select("p.pager").first();
        String totalPageText = pager.html();
        totalPageText = StringUtils.substringBetween(totalPageText, "size: ", ",");

        pageData.setPage(Integer.parseInt(totalPageText));
        pageData.setSize(Integer.parseInt(totalPageText) * 20);

        repository.save(pageData);
        log.debug("{}", pageData);
    }

    @Override
    public Site getSite() {
        return SiteUtil.get().setSleepTime(RandomUtils.nextInt(15, 30) * 1000);
    }
}
