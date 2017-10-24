package com.har.sjfxpt.crawler.ggzy;

import com.har.sjfxpt.crawler.jcw.JinCaiWangPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Spider;

/**
 * Created by Administrator on 2017/10/24.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class JinCaiWangPageProcessorTests {

    @Autowired
    JinCaiWangPageProcessor jinCaiWangPageProcessor;


    @Test
    public void testJinCaiWangProcessor(){
        Spider.create(jinCaiWangPageProcessor)
                .addUrl("http://www.cfcpn.com/plist/caigou","http://www.cfcpn.com/plist/zhengji","http://www.cfcpn.com/plist/jieguo","http://www.cfcpn.com/plist/biangeng")
                .thread(10)
                .run();
    }

}
