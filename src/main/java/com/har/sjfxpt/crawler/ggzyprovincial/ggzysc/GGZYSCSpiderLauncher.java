package com.har.sjfxpt.crawler.ggzyprovincial.ggzysc;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

/**
 * Created by Administrator on 2017/11/28.
 */
@Slf4j
@Component
public class GGZYSCSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.GGZYSC.toString().toLowerCase() + "-current";

    @Autowired
    GGZYSCPageProcessor GGZYSCPageProcessor;

    @Autowired
    GGZYSCPipeline GGZYSCPipeline;

    final int num = Runtime.getRuntime().availableProcessors();

    final String[] urls = {
            "http://www.scztb.gov.cn/Info/GetInfoListNew?keywords=&times=1&timesStart=&timesEnd=&province=&area=&businessType=project&informationType=&page=1&parm=" + DateTime.now().getMillis(),
            "http://www.scztb.gov.cn/Info/GetInfoListNew?keywords=&times=1&timesStart=&timesEnd=&province=&area=&businessType=purchase&informationType=&page=1&parm=" + DateTime.now().getMillis()
    };

    /**
     * 爬取当日数据
     */
    public void start() {
        cleanSpider(uuid);
        Spider spider = Spider.create(GGZYSCPageProcessor)
                .addPipeline(GGZYSCPipeline)
                .setUUID(uuid)
                .addUrl(urls)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }


}
