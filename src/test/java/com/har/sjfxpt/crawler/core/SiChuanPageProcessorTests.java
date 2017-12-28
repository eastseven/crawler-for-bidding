package com.har.sjfxpt.crawler.core;

import com.har.sjfxpt.crawler.core.processor.SiChuanPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Spider;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SiChuanPageProcessorTests {

    @Autowired
    SiChuanPageProcessor pageProcessor;

    @Test
    public void test() {
        String url = "http://www.scztb.gov.cn/Info/GetInfoList?keywords=&times=&timesStart=2017-10-19&timesEnd=2017-10-19&province=&area=&businessType=project&informationType=&page=1&parm=";
        Assert.assertNotNull(url);

        Spider.create(pageProcessor).addUrl(url).run();
    }
}
