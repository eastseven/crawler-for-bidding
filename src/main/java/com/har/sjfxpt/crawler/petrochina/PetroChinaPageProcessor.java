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
 * @author dongqi
 *
 * 中国石油物资采购网
 * http://eportal.energyahead.com/wps/portal/eportal
 */
@Slf4j
@Component
@SourceConfig(code = SourceCode.ZSY, useSelenium = false, sources = {
        @Source(type = "", url = "http://eportal.energyahead.com/wps/portal/ebid/wcm_search/bidnotice_publish")
})
public class PetroChinaPageProcessor implements BasePageProcessor {

    @Override
    public void handlePaging(Page page) {

    }

    @Override
    public void handleContent(Page page) throws Exception {

    }

    @Override
    public List parseContent(Elements items) {
        return null;
    }

    @Override
    public void process(Page page) {
        log.debug(">>> url {}", page.getUrl().get());
        log.debug(">>> \n{}", page.getHtml().getDocument().body().html());
    }

    @Override
    public Site getSite() {
        return SiteUtil.get();
    }
}
