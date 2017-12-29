package com.har.sjfxpt.crawler.ggzy;

import com.har.sjfxpt.crawler.zgrenshou.ZGRenShouPageProseccor;
import com.har.sjfxpt.crawler.zgrenshou.ZGRenShouPipeline;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/29.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ZGRenShouPageProcessorTests {

    @Autowired
    ZGRenShouPageProseccor zgRenShouPageProseccor;

    @Autowired
    ZGRenShouPipeline zgRenShouPipeline;

    String[] urls = {
            "http://cpmsx.e-chinalife.com/xycms/cggg/index_1.jhtml",
            "http://cpmsx.e-chinalife.com/xycms/jggs/index_1.jhtml",
            "http://cpmsx.e-chinalife.com/xycms/cqgg/index_1.jhtml",
    };

    @Test
    public void testZGRenShouProcessors() {
        Spider.create(zgRenShouPageProseccor)
                .addUrl(urls)
                .addPipeline(zgRenShouPipeline)
                .thread(THREAD_NUM)
                .run();
    }

}
