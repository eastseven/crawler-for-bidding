package com.har.sjfxpt.crawler.core.model;

import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * @author dongqi
 */
@Slf4j
public class BidNewsSpider extends Spider {

    private List<SourceModel> sourceModelList = Lists.newArrayList();

    public List<SourceModel> getSourceModelList() {
        return sourceModelList;
    }

    public void setSourceModelList(List<SourceModel> sourceModelList) {
        this.sourceModelList = sourceModelList;
    }

    public static BidNewsSpider create(PageProcessor pageProcessor) {
        return new BidNewsSpider(pageProcessor);
    }

    /**
     * create a spider with pageProcessor.
     *
     * @param pageProcessor pageProcessor
     */
    public BidNewsSpider(PageProcessor pageProcessor) {
        super(pageProcessor);
    }
}
