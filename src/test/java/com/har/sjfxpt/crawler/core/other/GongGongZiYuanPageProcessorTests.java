package com.har.sjfxpt.crawler.core.other;

import com.har.sjfxpt.crawler.core.SpiderApplicationTests;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.downloader.GongGongZiYuanPageDownloader;
import com.har.sjfxpt.crawler.core.model.DataItem;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.processor.GongGongZiYuanPageProcessor;
import com.har.sjfxpt.crawler.core.repository.DataItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.core.processor.GongGongZiYuanPageProcessor.POST_PARAMS_01;
import static com.har.sjfxpt.crawler.core.processor.GongGongZiYuanPageProcessor.SEED_URL;

@Slf4j
public class GongGongZiYuanPageProcessorTests extends SpiderApplicationTests {

    @Autowired
    GongGongZiYuanPageDownloader gongGongZiYuanPageDownloader;

    @Autowired
    DataItemRepository repository;

    @Autowired
    GongGongZiYuanPageProcessor pageProcessor;

    @Autowired
    HBasePipeline pipeline;

    @Test
    public void testPageProcessor() {
        Assert.assertNotNull(pageProcessor);

        SourceModel sourceModel = new SourceModel();
        sourceModel.setUrl(SEED_URL);
        sourceModel.setPost(true);
        sourceModel.setJsonPostParams(POST_PARAMS_01);
        sourceModel.setNeedPlaceholderFields(new String[]{"TIMEEND_SHOW", "TIMEBEGIN_SHOW", "TIMEEND", "TIMEBEGIN"});
        sourceModel.setDayPattern("yyyy-MM-dd");

        Request request = sourceModel.createRequest();
        Spider.create(pageProcessor).addRequest(request).addPipeline(pipeline).run();
    }

    @Test
    public void testDownload() {
        Assert.assertNotNull(gongGongZiYuanPageDownloader);
        DataItem dataItem = repository.findAll(new PageRequest(0, 1)).iterator().next();
        Assert.assertNotNull(dataItem);
        gongGongZiYuanPageDownloader.download(dataItem);

    }
}
