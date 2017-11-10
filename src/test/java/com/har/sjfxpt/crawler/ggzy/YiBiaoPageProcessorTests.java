package com.har.sjfxpt.crawler.ggzy;

import com.har.sjfxpt.crawler.yibiao.YiBiaoPageProcessor;
import com.har.sjfxpt.crawler.yibiao.YiBiaoPipeline;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Spider;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/11/9.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class YiBiaoPageProcessorTests {

    @Autowired
    YiBiaoPageProcessor yiBiaoPageProcessor;

    @Autowired
    YiBiaoPipeline yiBiaoPipeline;


    @Test
    public void testYiBiaoPageProcessor() {

        String url = "http://www.1-biao.com/data/AjaxTender.aspx?0.06563536587854646&hidtypeID=&hidIndustryID=&hidProvince=&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=1&hidPape=1&keyword=";

        Spider.create(yiBiaoPageProcessor)
                .addPipeline(yiBiaoPipeline)
                .addUrl(url)
                .thread(4)
                .run();

    }

    final static Pattern yyyymmddPattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");

    @Test
    public void testDate() {

        String date = "2017-11-09 17:54";

        Matcher matcher = yyyymmddPattern.matcher(date);
        String dataStr = null;
        if (matcher.find()) {
            dataStr = matcher.group();
        }

        log.debug("dataStr=={}", dataStr);

        DateTime dateTime = new DateTime("2017-11-09");

        log.debug("date=={}", new DateTime(new Date()).toString("yyyy-MM-dd"));

        DateTime dateTime1 = new DateTime(new DateTime(new Date()).toString("yyyy-MM-dd"));

        log.debug("result=={}", dateTime.isBefore(dateTime1));
    }


    @Test
    public void test(){
        scheduledTest();
    }

    @Scheduled(fixedDelay = 2000)
    public void scheduledTest(){
        log.debug("date=={}",new DateTime(new Date()).toString("HH:mm:ss"));
    }


}
