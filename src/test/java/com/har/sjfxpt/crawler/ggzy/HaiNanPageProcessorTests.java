package com.har.sjfxpt.crawler.ggzy;

import com.har.sjfxpt.crawler.ggzy.pipeline.HaiNanPipeline;
import com.har.sjfxpt.crawler.ggzy.processor.HaiNanPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Request;

import java.util.Date;

/**
 * Created by Administrator on 2017/11/7.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class HaiNanPageProcessorTests {

    @Autowired
    HaiNanPageProcessor haiNanPageProcessor;

    @Autowired
    HaiNanPipeline haiNanPipeline;

    @Test
    public void testHaiNanPageProcessor() {
        String date = new DateTime(new Date()).toString("yyyy-MM-dd");

        String url = "http://www.ccgp-hainan.gov.cn/cgw/cgw_list.jsp?currentPage=1&begindate=" + date + "&enddate=" + date + "&title=&bid_type=&proj_number=&zone=";

        Request request = new Request(url);

        Spider.create(haiNanPageProcessor)
                .addRequest(request)
                .addPipeline(haiNanPipeline)
                .thread(4)
                .run();
    }
}
