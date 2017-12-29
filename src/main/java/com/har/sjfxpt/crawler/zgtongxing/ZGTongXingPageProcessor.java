package com.har.sjfxpt.crawler.zgtongxing;

import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;

import java.util.List;

/**
 * Created by Administrator on 2017/12/29.
 */
@Slf4j
@Component
public class ZGTongXingPageProcessor implements BasePageProcessor {
    @Override
    public void handlePaging(Page page) {

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
