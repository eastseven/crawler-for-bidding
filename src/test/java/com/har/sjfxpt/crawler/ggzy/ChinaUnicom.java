package com.har.sjfxpt.crawler.ggzy;

import com.har.sjfxpt.crawler.chinaunicom.ChinaUnicomPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Spider;

/**
 * Created by Administrator on 2017/12/27.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ChinaUnicom {

    @Autowired
    ChinaUnicomPageProcessor chinaUnicomPageProcessor;

    String[] urls = {
            "http://www.chinaunicombidding.cn/jsp/cnceb/web/info1/infoList.jsp?page=1",
            "http://www.chinaunicombidding.cn/jsp/cnceb/web/info1/infoList.jsp?page=1",
    };

    @Test
    public void testChinaUnicomPageProcessor() {
        Spider.create(chinaUnicomPageProcessor);
    }

}
