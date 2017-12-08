package com.har.sjfxpt.crawler.ggzyprovincial.ggzyhb;

import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;

import java.util.List;

/**
 * Created by Administrator on 2017/12/6.
 */
@Slf4j
@Component
public class GGZYHBPageProcessor implements BasePageProcessor {
    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        if (!StringUtils.containsIgnoreCase(url, "?Paging")) {
            Elements elements = page.getHtml().getDocument().body().select("#Paging > div > div > table > tbody > tr");
            String pageCount = StringUtils.substringBetween(elements.text(), "/", " ");
            if (StringUtils.isNotBlank(pageCount)) {
                int pageNum = Integer.parseInt(pageCount);
                if (pageNum >= 2) {
                    for (int i = 2; i <= pageNum; i++) {
                        String urlTarget = url + "?Paging=" + i;
                        
                    }
                }
            }
        }
    }

    @Override
    public void handleContent(Page page) {

    }

    @Override
    public List parseContent(Elements items) {
        return null;
    }

    @Override
    public void process(Page page) {
        handlePaging(page);
        handleContent(page);
    }

    @Override
    public Site getSite() {
        return SiteUtil.get().setSleepTime(10000);
    }
}
