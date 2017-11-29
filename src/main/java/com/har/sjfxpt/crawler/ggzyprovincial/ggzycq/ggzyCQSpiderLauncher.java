package com.har.sjfxpt.crawler.ggzyprovincial.ggzycq;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

/**
 * Created by Administrator on 2017/11/28.
 */
@Slf4j
@Component
public class ggzyCQSpiderLauncher extends BaseSpiderLauncher{

    private final String uuid= SourceCode.GGZYCQ.toString().toLowerCase()+"-current";

    @Autowired
    ggzyCQPageProcessor ggzyCQPageProcessor;

    @Autowired
    ggzyCQPipeline ggzyCQPipeline;

    final int num=Runtime.getRuntime().availableProcessors();

    final String[] urls={
            "http://www.cqggzy.com/web/services/PortalsWebservice/getInfoList?response=application/json&pageIndex=1&pageSize=18&siteguid=d7878853-1c74-4913-ab15-1d72b70ff5e7&categorynum=014005001&title=&infoC=&_=1511837748941",
            "http://www.cqggzy.com/web/services/PortalsWebservice/getInfoList?response=application/json&pageIndex=1&pageSize=18&siteguid=d7878853-1c74-4913-ab15-1d72b70ff5e7&categorynum=014001001&title=&infoC=&_=1511837779151"
    };

    /**
     * 爬取当日数据
     */
    public void start(){
        cleanSpider(uuid);
        Spider spider=Spider.create(ggzyCQPageProcessor)
                .addPipeline(ggzyCQPipeline)
                .setUUID(uuid)
                .addUrl(urls)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }
}
