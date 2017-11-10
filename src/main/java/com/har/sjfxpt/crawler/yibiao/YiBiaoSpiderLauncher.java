package com.har.sjfxpt.crawler.yibiao;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

/**
 * Created by Administrator on 2017/11/10.
 */
@Slf4j
@Component
public class YiBiaoSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.YIBIAO.toString().toLowerCase() + "-current";

    @Autowired
    YiBiaoPageProcessor yiBiaoPageProcessor;

    @Autowired
    YiBiaoPipeline yiBiaoPipeline;

    final int num = Runtime.getRuntime().availableProcessors();

    /**
     * 爬取当日数据
     */
    public void start() {

        String url = "http://www.1-biao.com/data/AjaxTender.aspx?0.06563536587854646&hidtypeID=&hidIndustryID=&hidProvince=&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=1&hidPape=1&keyword=";

        Spider spider = Spider.create(yiBiaoPageProcessor)
                .addPipeline(yiBiaoPipeline)
                .addUrl(url)
                .setUUID(uuid)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }

}
