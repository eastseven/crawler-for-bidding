package com.har.sjfxpt.crawler.ggzy;

import com.har.sjfxpt.crawler.jcw.JinCaiWangPageProcessor;
import com.har.sjfxpt.crawler.jcw.JinCaiWangPipeline;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.Date;

/**
 * Created by Administrator on 2017/10/24.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class JinCaiWangPageProcessorTests {

    @Autowired
    JinCaiWangPageProcessor jinCaiWangPageProcessor;

    @Autowired
    JinCaiWangPipeline jinCaiWangPipeline;

    @Test
    public void testJinCaiWangProcessor() {
        String[] urls = {"http://www.cfcpn.com/plist/caigou?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=",
                "http://www.cfcpn.com/plist/zhengji?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=",
                "http://www.cfcpn.com/plist/jieguo?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=",
                "http://www.cfcpn.com/plist/biangeng?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo="};
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            Request request = new Request(urls[i]);
            if (urls[i].contains("caigou")) {
                request.putExtra("type", "采购");
            }
            if (urls[i].contains("zhengji")) {
                request.putExtra("type", "征集");
            }
            if (urls[i].contains("jieguo")) {
                request.putExtra("type", "结果");
            }
            if (urls[i].contains("biangeng")) {
                request.putExtra("type", "变更");
            }
            requests[i] = request;
        }
        Spider.create(jinCaiWangPageProcessor)
                .addRequest(requests)
                .addPipeline(jinCaiWangPipeline)
                .thread(10)
                .run();
    }

    @Test
    public void testStringUtil() {
        Date createTime = new Date();
        log.debug("createTime=={}", createTime);
        DateTime ct = new DateTime(createTime);
        String time = ct.toString("yyyyMMddHH");
        log.debug("time=={}", time);
    }


}
