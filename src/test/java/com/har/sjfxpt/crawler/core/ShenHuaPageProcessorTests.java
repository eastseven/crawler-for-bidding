package com.har.sjfxpt.crawler.core;

import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.model.BidNewsSpider;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.shenhua.ShenHuaPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Request;

import java.util.List;

/**
 * Created by Administrator on 2017/12/22.
 */
@Slf4j
public class ShenHuaPageProcessorTests extends SpiderApplicationTests {

    @Autowired
    ShenHuaPageProcessor shenHuaPageProcessor;

    @Autowired
    HBasePipeline pipeline;

    String[] urls = {
            "http://www.shenhuabidding.com.cn/bidweb/001/001001/1.html",
            "http://www.shenhuabidding.com.cn/bidweb/001/001002/1.html",
            "http://www.shenhuabidding.com.cn/bidweb/001/001003/1.html",
            "http://www.shenhuabidding.com.cn/bidweb/001/001004/1.html",
            "http://www.shenhuabidding.com.cn/bidweb/001/001005/1.html",
            "http://www.shenhuabidding.com.cn/bidweb/001/001006/1.html",
            "http://www.shenhuabidding.com.cn/bidweb/001/001007/1.html",
            "http://www.shenhuabidding.com.cn/bidweb/001/001008/1.html",
    };

    @Test
    public void testShenHuaPageProcessors() {
        List<SourceModel> list = SourceConfigAnnotationUtils.find(shenHuaPageProcessor.getClass());
        Assert.assertTrue(CollectionUtils.isNotEmpty(list));

        Request[] requests = list.parallelStream().map(SourceModel::createRequest).toArray(Request[]::new);
        BidNewsSpider.create(shenHuaPageProcessor).addRequest(requests).thread(list.size()).addPipeline(pipeline).run();
    }
}
