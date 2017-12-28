package com.har.sjfxpt.crawler.ggzyprovincial.ggzyshandong;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/15.
 */
@Slf4j
@Component
public class GGZYShanDongSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.GGZYSHANDONG.toString().toLowerCase() + "-current";

    @Autowired
    GGZYShanDongPageProcessor ggzyShanDongPageProcessor;

    @Autowired
    GGZYShanDongPipeline ggzyShanDongPipeline;

    String[] urls = {
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=117&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=89&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=87&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=88&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=86&ext=",

            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=94&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=90&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=92&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=93&ext=",
            "http://www.sdggzyjy.gov.cn/queryContent_1-jyxx.jspx?title=&origin=&inDates=1&channelId=91&ext=",
    };

    /**
     * 爬取当日数据
     */
    public void start() {
        cleanSpider(uuid);
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            Request request = new Request(urls[i]);
            requests[i] = request;
        }
        Spider spider = Spider.create(ggzyShanDongPageProcessor)
                .addPipeline(ggzyShanDongPipeline)
                .addRequest(requests)
                .setUUID(uuid)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }

}
