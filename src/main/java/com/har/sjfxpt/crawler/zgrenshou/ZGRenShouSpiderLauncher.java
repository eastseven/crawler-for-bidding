package com.har.sjfxpt.crawler.zgrenshou;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/29.
 */
@Slf4j
@Component
public class ZGRenShouSpiderLauncher extends BaseSpiderLauncher {

    final String uuid = SourceCode.ZGRENSHOU.toString().toLowerCase() + "-current";

    @Autowired
    ZGRenShouPageProseccor zgRenShouPageProseccor;

    @Autowired
    ZGRenShouPipeline zgRenShouPipeline;

    String[] urls = {
            "http://cpmsx.e-chinalife.com/xycms/cggg/index_1.jhtml",
            "http://cpmsx.e-chinalife.com/xycms/jggs/index_1.jhtml",
            "http://cpmsx.e-chinalife.com/xycms/cqgg/index_1.jhtml",
    };

    /**
     * 爬取当日数据
     */
    public void start() {
        cleanSpider(uuid);
        Spider spider = Spider.create(zgRenShouPageProseccor)
                .addUrl(urls)
                .addPipeline(zgRenShouPipeline)
                .setUUID(uuid)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }

}
