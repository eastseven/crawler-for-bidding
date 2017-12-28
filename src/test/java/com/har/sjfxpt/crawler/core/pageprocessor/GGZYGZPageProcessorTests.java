package com.har.sjfxpt.crawler.core.pageprocessor;

import com.har.sjfxpt.crawler.ggzyprovincial.ggzygz.GGZYGZPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzygz.GGZYGZPipeline;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.ggzyprovincial.ggzygz.GGZYGZSpiderLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/11/29.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYGZPageProcessorTests {

    @Autowired
    GGZYGZPageProcessor GGZYGZPageProcessor;

    @Autowired
    GGZYGZPipeline GGZYGZPipeline;

    @Test
    public void testPageProcessor() {

        String[] urls = {
                "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=GP&businessType=JYGG&inDates=1&ext=&origin=ALL",
                "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=GP&businessType=ZSJGGS&inDates=1&ext=&origin=ALL",
                "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=GP&businessType=JYJGGS&inDates=1&ext=&origin=ALL",
                "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=GP&businessType=FBGG&inDates=1&ext=&origin=ALL",
                "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=CE&businessType=JYGG&inDates=1&ext=&origin=ALL",
                "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=CE&businessType=ZSJGGS&inDates=1&ext=&origin=ALL",
                "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=CE&businessType=JYJGGS&inDates=1&ext=&origin=ALL",
                "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=CE&businessType=FBGG&inDates=1&ext=&origin=ALL"
        };

        Request[] requests = new Request[urls.length];

        for (int i = 0; i < requests.length; i++) {
            requests[i] = requestGenerator(urls[i]);
        }

        Spider.create(GGZYGZPageProcessor)
                .addRequest(requests)
                .addPipeline(GGZYGZPipeline)
                .thread(4)
                .run();
    }


}
