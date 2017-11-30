package com.har.sjfxpt.crawler.ggzy.ggzyPageProcessotTests;

import com.har.sjfxpt.crawler.ggzyprovincial.ggzyyn.ggzyYNPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Administrator on 2017/11/30.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ggzyYNPageProcessorTests {


    @Autowired
    ggzyYNPageProcessor ggzyYNPageProcessor;

    @Test
    public void testYNPageProcessor() {
        String[] urls = {
                "https://www.ynggzyxx.gov.cn/jyxx/jsgcZbgg?currentPage=1&area=000&industriesTypeCode=0&scrollValue=777",
                "https://www.ynggzyxx.gov.cn/jyxx/jsgcGzsx?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0",
                "https://www.ynggzyxx.gov.cn/jyxx/jsgcpbjggs?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0",
                "https://www.ynggzyxx.gov.cn/jyxx/jsgcZbjggs?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0",
                "https://www.ynggzyxx.gov.cn/jyxx/jsgcZbyc?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0",

                "https://www.ynggzyxx.gov.cn/jyxx/zfcg/cggg?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0",
                "https://www.ynggzyxx.gov.cn/jyxx/zfcg/gzsx?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0",
                "https://www.ynggzyxx.gov.cn/jyxx/zfcg/gzsx?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0",
        };
    }


}
