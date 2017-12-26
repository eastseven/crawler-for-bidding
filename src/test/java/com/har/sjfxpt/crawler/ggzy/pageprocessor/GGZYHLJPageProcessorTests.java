package com.har.sjfxpt.crawler.ggzy.pageprocessor;

import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhlj.GGZYHLJPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhlj.GGZYHLJPipeline;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyhlj.GGZYHLJSpiderLauncher.requestJudgment;

/**
 * Created by Administrator on 2017/12/5.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYHLJPageProcessorTests {

    @Autowired
    GGZYHLJPageProcessor ggzyhljPageProcessor;

    @Autowired
    GGZYHLJPipeline ggzyhljPipeline;

    @Test
    public void testGGZYHLJPageProcessor() {

        String[] urls = {
                "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=1",
                "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=5",
                "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=7",
                "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=4",
                "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=3",
        };

        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestJudgment(urls[i]);
        }

        Spider.create(ggzyhljPageProcessor)
                .addRequest(requests)
                .addPipeline(ggzyhljPipeline)
                .thread(4)
                .run();
    }


}
