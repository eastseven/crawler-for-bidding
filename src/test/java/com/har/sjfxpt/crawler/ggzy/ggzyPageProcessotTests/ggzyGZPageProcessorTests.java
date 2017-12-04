package com.har.sjfxpt.crawler.ggzy.ggzyPageProcessotTests;

import com.har.sjfxpt.crawler.ggzyprovincial.ggzygz.ggzyGZPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzygz.ggzyGZPipeline;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.ggzyprovincial.ggzygz.ggzyGZSpiderLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/11/29.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ggzyGZPageProcessorTests {

    @Autowired
    ggzyGZPageProcessor ggzyGZPageProcessor;

    @Autowired
    ggzyGZPipeline ggzyGZPipeline;

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

        Spider.create(ggzyGZPageProcessor)
                .addRequest(requests)
                .addPipeline(ggzyGZPipeline)
                .thread(4)
                .run();
    }


}
