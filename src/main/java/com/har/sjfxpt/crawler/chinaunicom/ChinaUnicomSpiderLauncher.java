package com.har.sjfxpt.crawler.chinaunicom;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/27.
 */
@Slf4j
@Component
public class ChinaUnicomSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.CU.toString().toLowerCase() + "-current";

    @Autowired
    ChinaUnicomPageProcessor chinaUnicomPageProcessor;

    @Autowired
    ChinaUnicomPipeline chinaUnicomPipeline;

    String[] urls = {
            "http://www.chinaunicombidding.cn/jsp/cnceb/web/info1/infoList.jsp?page=1&type=1",
            "http://www.chinaunicombidding.cn/jsp/cnceb/web/info1/infoList.jsp?page=1&type=2",
            "http://www.chinaunicombidding.cn/jsp/cnceb/web/info1/infoList.jsp?page=1&type=3",
    };

    /**
     * 爬取当日数据
     */
    public void start() {
        cleanSpider(uuid);
        Spider spider = Spider.create(chinaUnicomPageProcessor)
                .addPipeline(chinaUnicomPipeline)
                .setUUID(uuid)
                .addUrl(urls)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }

}
