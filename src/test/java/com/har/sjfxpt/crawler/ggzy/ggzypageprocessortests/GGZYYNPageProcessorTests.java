package com.har.sjfxpt.crawler.ggzy.ggzypageprocessortests;

import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyyn.GGZYYNPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyyn.GGZYYNPipeline;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyyn.GGZYYNSpiderLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/11/30.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYYNPageProcessorTests {


    @Autowired
    GGZYYNPageProcessor GGZYYNPageProcessor;

    @Autowired
    GGZYYNPipeline GGZYYNPipeline;

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
                "https://www.ynggzyxx.gov.cn/jyxx/zfcg/kbjl?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0",
                "https://www.ynggzyxx.gov.cn/jyxx/zfcg/zbjggs?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0",
                "https://www.ynggzyxx.gov.cn/jyxx/zfcg/zfcgYcgg?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0"
        };

        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            Request request = requestGenerator(urls[i]);
            requests[i] = request;
        }

        Spider.create(GGZYYNPageProcessor)
                .addRequest(requests)
                .addPipeline(GGZYYNPipeline)
                .thread(4)
                .run();
    }


    final static Pattern yyyymmddhhmmPattern = Pattern.compile("[0-9]{4}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}");

    @Test
    public void testTime() throws ParseException {
        String time = "20171130092025";
        Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(time);
        DateTime dateTime = new DateTime(date);
        log.debug("time=={}", dateTime.toString("yyyy-MM-dd HH:mm"));
        log.info("time={}", new DateTime(new SimpleDateFormat("yyyyMMddHH").parse("2017120615")).toString("yyyy-MM-dd HH:mm"));
    }

    @Test
    public void testTimeCompare() throws ParseException {
        String time = "2017-12-07 09:58";
        log.info("compare=={}", PageProcessorUtil.timeDetailCompare(time));
        log.info("time=={}",DateTime.now().toString("yyyy-MM-dd HH:mm"));
    }

}
