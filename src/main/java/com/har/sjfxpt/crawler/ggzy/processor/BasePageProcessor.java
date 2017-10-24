package com.har.sjfxpt.crawler.ggzy.processor;

import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

public interface BasePageProcessor extends PageProcessor {

    void handlePaging(Page page);

    List parseContent(Elements items);
}
