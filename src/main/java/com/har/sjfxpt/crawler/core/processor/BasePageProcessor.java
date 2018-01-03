package com.har.sjfxpt.crawler.core.processor;

import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * @author dongqi
 */
public interface BasePageProcessor extends PageProcessor {

    String KEY_DATA_ITEMS = "dataItemList";

    /**
     * 处理分页
     * @param page
     */
    void handlePaging(Page page);

    /**
     * 处理列表内容
     * @param page
     * @throws Exception
     */
    void handleContent(Page page) throws Exception;

    /**
     * 解析列表内容
     * @param items
     * @return
     */
    List parseContent(Elements items);
}
