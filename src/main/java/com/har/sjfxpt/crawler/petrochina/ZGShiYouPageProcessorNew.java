package com.har.sjfxpt.crawler.petrochina;

import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;

import java.util.List;

/**
 * Created by Administrator on 2018/1/2.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZY,
        sources = {
        }
)
public class ZGShiYouPageProcessorNew implements BasePageProcessor {

    public static final String SEED_URL = "";


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
        return SiteUtil.get().setTimeOut(10000);
    }
}
