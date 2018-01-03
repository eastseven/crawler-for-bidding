package com.har.sjfxpt.crawler.core;

import com.har.sjfxpt.crawler.chinaunicom.ChinaUnicomPageProcessor;
import com.har.sjfxpt.crawler.chinaunicom.ChinaUnicomPipeline;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/27.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ChinaUnicomTests {

    @Autowired
    ChinaUnicomPageProcessor chinaUnicomPageProcessor;

    @Autowired
    HBasePipeline chinaUnicomPipeline;

    String[] urls = {
            "http://www.chinaunicombidding.cn/jsp/cnceb/web/info1/infoList.jsp?page=1&type=1",
            "http://www.chinaunicombidding.cn/jsp/cnceb/web/info1/infoList.jsp?page=1&type=2",
            "http://www.chinaunicombidding.cn/jsp/cnceb/web/info1/infoList.jsp?page=1&type=3",
    };

    @Test
    public void testChinaUnicomPageProcessor() {
        Spider.create(chinaUnicomPageProcessor)
                .addUrl(urls)
                .addPipeline(chinaUnicomPipeline)
                .thread(THREAD_NUM)
                .run();
    }

}
