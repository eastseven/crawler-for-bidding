package com.har.sjfxpt.crawler.dongfeng;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/21.
 */
@Slf4j
@Component
public class DongFengSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.DONGFENG.toString().toLowerCase() + "-current";

    @Autowired
    DongFengPageProcessor dongFengPageProcessor;

    @Autowired
    DongFengPipeline dongFengPipeline;

    String[] urls = {
            "http://jyzx.dfmbidding.com/zbgg/index_1.jhtml",
            "http://jyzx.dfmbidding.com/pbgs/index_1.jhtml",
            "http://jyzx.dfmbidding.com/zgys/index_1.jhtml",
            "http://jyzx.dfmbidding.com/bggg/index_1.jhtml",
    };

    /**
     * 爬取当日数据
     */
    public void start() {
        cleanSpider(uuid);
        Spider spider = Spider.create(dongFengPageProcessor)
                .addPipeline(dongFengPipeline)
                .setUUID(uuid)
                .addUrl(urls)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }

}
