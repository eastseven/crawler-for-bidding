package com.har.sjfxpt.crawler.ggzy.ggzypageprocessottests;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhlj.GGZYHLJPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.Map;

/**
 * Created by Administrator on 2017/12/5.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYHLJPageProcessorTests {

    @Autowired
    GGZYHLJPageProcessor ggzyhljPageProcessor;

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
                .thread(4)
                .run();
    }

    public static Request requestJudgment(String url) {
        Request request = new Request(url);
        String typeId = StringUtils.substringAfter(url, "type=");
        Map<String, String> pageParams = Maps.newHashMap();
        pageParams.put("bussinessType", "工程建设信息");
        switch (typeId) {
            case "1":
                pageParams.put("type", "交易公告");
            case "5":
                pageParams.put("type", "流标/废标公示");
            case "7":
                pageParams.put("type", "项目澄清");
            case "4":
                pageParams.put("type", "中标候选人公示");
            case "3":
                pageParams.put("type", "交易证明书");
        }
        request.putExtra("pageParams", pageParams);
        return request;
    }

}
