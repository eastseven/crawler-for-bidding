package com.har.sjfxpt.crawler.ggzy;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ccgp.ccgpsc.CCGPSiChuanPageProcessor;
import com.har.sjfxpt.crawler.ccgp.ccgpsc.CCGPSiChuanPipeline;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.Map;

/**
 * Created by Administrator on 2017/11/8.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CCGPSiChuanPageProcessorTests {

    @Autowired
    CCGPSiChuanPageProcessor ccgpSiChuanPageProcessor;

    @Autowired
    CCGPSiChuanPipeline ccgpSiChuanPipeline;

    @Test
    public void testSiChuanPageProcessor() {

        String[] urls = {
                "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=cggg&rp=25&page=1",
                "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=jggg&rp=25&page=1",
                "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=gzgg&rp=25&page=1"
        };
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < requests.length; i++) {
            Map<String, Object> params = Maps.newHashMap();
            Request request = new Request(urls[i]);
            if (urls[i].contains("cggg")) {
                params.put("type", "采购公告");
            }
            if (urls[i].contains("jggg")) {
                params.put("type", "结果公告");
            }
            if (urls[i].contains("gzgg")) {
                params.put("type", "更正公告");
            }
            request.putExtra("pageParams",params);
            requests[i]=request;
        }

        Spider.create(ccgpSiChuanPageProcessor)
                .addPipeline(ccgpSiChuanPipeline)
                .addRequest(requests)
                .thread(8)
                .run();
    }

}
