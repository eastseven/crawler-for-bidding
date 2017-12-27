package com.har.sjfxpt.crawler.chinaunicom;

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
 * Created by Administrator on 2017/12/27.
 */
@Slf4j
@Component
public class ChinaUnicomPageProcessor implements BasePageProcessor {


    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        int pageNum = Integer.parseInt(StringUtils.substringAfter(url, "page="));
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("body > table > tbody > tr:nth-child(2) > td:nth-child(2) > table > tbody > tr > td:nth-child(2) > table:nth-child(2) > tbody > tr:nth-child(3) > td > table > tbody > tr > td:nth-child(1)");
            int pageCount = Integer.parseInt(StringUtils.substringBetween(elements.text(), "共 ", " 页"));
            log.info("pageCount=={}", pageCount);
            for (int i = 2; i <= pageCount; i++) {
                String urlTarget = url.replace("page=1", "page=" + i);
                page.addTargetRequest(urlTarget);
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
