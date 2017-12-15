package com.har.sjfxpt.crawler.ggzy.ggzypageprocessortests;

import com.har.sjfxpt.crawler.ggzyprovincial.ggzyshandong.GGZYShanDongPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyshandong.GGZYShanDongPipeline;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/15.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GGZYShanDongPageProcessorTests {

    @Autowired
    GGZYShanDongPageProcessor ggzyShanDongPageProcessor;

    @Autowired
    GGZYShanDongPipeline ggzyShanDongPipeline;

    String[] urls = {
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=117&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=89&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=87&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=88&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=86&ext=",

            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=94&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=90&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=92&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=93&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=91&ext=",
    };

    @Test
    public void testGGZYShanDongPageProcessor() {
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            Request request = new Request(urls[i]);
            requests[i] = request;
        }
        Spider.create(ggzyShanDongPageProcessor)
                .addRequest(requests)
                .thread(THREAD_NUM)
                .addPipeline(ggzyShanDongPipeline)
                .run();
    }
}
