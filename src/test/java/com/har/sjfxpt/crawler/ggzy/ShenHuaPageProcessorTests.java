package com.har.sjfxpt.crawler.ggzy;

import com.har.sjfxpt.crawler.shenhua.ShenHuaPageProcessor;
import com.har.sjfxpt.crawler.shenhua.ShenHuaPipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/22.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ShenHuaPageProcessorTests {

    @Autowired
    ShenHuaPageProcessor shenHuaPageProcessor;

    @Autowired
    ShenHuaPipeline shenHuaPipeline;

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
        Spider.create(shenHuaPageProcessor)
                .addUrl(urls)
                .thread(THREAD_NUM)
                .addPipeline(shenHuaPipeline)
                .run();
    }
}
