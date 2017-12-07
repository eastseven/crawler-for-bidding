package com.har.sjfxpt.crawler.ggzy.ggzypageprocessottests;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhb.GGZYHBPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.List;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/6.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYHBPageProcessorTests {

    @Autowired
    GGZYHBPageProcessor ggzyhbPageProcessor;

    @Test
    public void test() {
        String url = "http://www.hbbidcloud.com/hbcloud/jyxx/00200";
        List<String> lists = Lists.newArrayList();
        List<String> listsDetail = Lists.newArrayList();
        for (int i = 1; i <= 5; i++) {
            lists.add(url + i);
        }
        for (int i = 1; i <= 7; i++) {
            for (int j = 0; j < lists.size(); j++) {
                String urlTarget = lists.get(j);
                String filed = StringUtils.substringAfter(urlTarget, "jyxx/");
                listsDetail.add(urlTarget + "/" + filed + "00" + i + "/");
            }
        }

        Request[] requests = new Request[listsDetail.size()];
        for (int i = 0; i < listsDetail.size(); i++) {
            Request request = new Request(listsDetail.get(i));
            requests[i] = request;
        }
        Spider.create(ggzyhbPageProcessor)
                .addRequest(requests)
                .thread(THREAD_NUM)
                .run();
    }
}
