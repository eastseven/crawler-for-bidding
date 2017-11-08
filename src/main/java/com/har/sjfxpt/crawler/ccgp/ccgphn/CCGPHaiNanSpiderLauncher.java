package com.har.sjfxpt.crawler.ccgp.ccgphn;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.Date;

/**
 * Created by Administrator on 2017/11/7.
 */
@Slf4j
@Component
public class CCGPHaiNanSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.CCGPHN.toString().toLowerCase() + "-current";

    @Autowired
    CCGPHaiNanPageProcessor CCGPHaiNanPageProcessor;

    @Autowired
    CCGPHaiNanPipeline CCGPHaiNanPipeline;

    final int num = Runtime.getRuntime().availableProcessors();

    /**
     * 爬取当日数据
     */
    public void start() {
        String date = new DateTime(new Date()).toString("yyyy-MM-dd");

        String url = "http://www.ccgp-hainan.gov.cn/cgw/cgw_list.jsp?currentPage=1&begindate=" + date + "&enddate=" + date + "&title=&bid_type=&proj_number=&zone=";

        Request request = new Request(url);

        Spider spider = Spider.create(CCGPHaiNanPageProcessor)
                .addRequest(request)
                .addPipeline(CCGPHaiNanPipeline)
                .setUUID(uuid)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }
}
