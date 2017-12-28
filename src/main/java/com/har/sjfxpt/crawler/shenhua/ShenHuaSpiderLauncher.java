package com.har.sjfxpt.crawler.shenhua;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/22.
 */
@Slf4j
@Component
public class ShenHuaSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.SHENHUA.toString().toLowerCase() + "-current";

    @Autowired
    ShenHuaPageProcessor shenHuaPageProcessor;

    @Autowired
    ShenHuaPipeline shenHuaPipeline;

    String[] urls = {
            "http://www.shenhuabidding.com.cn/bidweb/001/001001/1.html",
            "http://www.shenhuabidding.com.cn/bidweb/001/001002/1.html",
            "http://www.shenhuabidding.com.cn/bidweb/001/001003/1.html",
            "http://www.shenhuabidding.com.cn/bidweb/001/001004/1.html",
            "http://www.shenhuabidding.com.cn/bidweb/001/001005/1.html",
            "http://www.shenhuabidding.com.cn/bidweb/001/001006/1.html",
            "http://www.shenhuabidding.com.cn/bidweb/001/001007/1.html",
            "http://www.shenhuabidding.com.cn/bidweb/001/001008/1.html",
    };

    /**
     * 爬取当日数据
     */
    public void start() {
        cleanSpider(uuid);
        Spider spider = Spider.create(shenHuaPageProcessor)
                .addPipeline(shenHuaPipeline)
                .setUUID(uuid)
                .addUrl(urls)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }

}
