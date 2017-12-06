package com.har.sjfxpt.crawler.ggzyprovincial.ggzyhlj;

import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/5.
 */
@Slf4j
@Component
public class GGZYHLJPageProcessor implements BasePageProcessor {

    final static String PAGE_PARAMS = "pageParams";

    @Override
    public void handlePaging(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get("pageParams");
        String url = page.getUrl().get();
        int pageNum = Integer.parseInt(StringUtils.substringBetween(url, "pageNo=", "&type"));
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("body > div > div.content_box > div.main_wrap > div.news_inf > div > div > span");
            String elementsText = elements.text();
            int pageCount = Integer.parseInt(StringUtils.substringBetween(elementsText, "/", "页"));
            for (int i = 2; i <= pageCount; i++) {
                String urlTarget = url.replace("pageNo=1", "pageNo=" + i);
                Request request = new Request(urlTarget);
                request.putExtra(PAGE_PARAMS, "pageParams");
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get("pageParams");
        Elements elements = page.getHtml().getDocument().body().select("body > div > div.content_box > div.main_wrap > div.news_inf > div > ul > li");
        List<GGZYHLJDataItem> dataItems = parseContent(elements);
    }

    @Override
    public List parseContent(Elements items) {
        for (Element element : items) {
            log.info("element=={}", element);
        }
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
