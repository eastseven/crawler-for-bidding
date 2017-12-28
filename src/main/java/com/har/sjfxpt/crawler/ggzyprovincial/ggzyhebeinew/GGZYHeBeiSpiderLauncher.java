package com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebeinew;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.THREAD_NUM;
import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebeinew.GGZYHeBeiPageProcessorNew.requestGenerator;

/**
 * Created by Administrator on 2017/12/26.
 */
@Slf4j
@Component
public class GGZYHeBeiSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.GGZYHEBEI.toString().toLowerCase() + "-current";

    @Autowired
    GGZYHeBeiPageProcessorNew ggzyHeBeiPageProcessorNew;

    @Autowired
    GGZYHeBeiPipeline ggzyHeBeiPipeline;

    String url = "http://www.hebpr.cn/inteligentsearch/rest/inteligentSearch/getFullTextDataNew";

    /**
     * 爬去当日数据
     */
    public void start() {
        cleanSpider(uuid);
        Request[] requests = {
                requestGenerator(url, "003005002001", 0),
                requestGenerator(url, "003005002002", 0),
                requestGenerator(url, "003005002003", 0),
                requestGenerator(url, "003005002004", 0),
                requestGenerator(url, "003005001001", 0),
                requestGenerator(url, "003005001002", 0),
                requestGenerator(url, "003005001003", 0),
                requestGenerator(url, "003005001004", 0)
        };
        Spider spider = Spider.create(ggzyHeBeiPageProcessorNew)
                .addPipeline(ggzyHeBeiPipeline)
                .setUUID(uuid)
                .addRequest(requests)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }

}
