package com.har.sjfxpt.crawler.ggzy;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import com.har.sjfxpt.crawler.ggzy.pipeline.HaiNanPipeline;
import com.har.sjfxpt.crawler.ggzy.processor.HaiNanPageProcessor;
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
public class HaiNanSpiderLauncher extends BaseSpiderLauncher{

    private final String uuid= SourceCode.CCGPHN.toString().toLowerCase()+"-current";

    @Autowired
    HaiNanPageProcessor haiNanPageProcessor;

    @Autowired
    HaiNanPipeline haiNanPipeline;

    final int num=Runtime.getRuntime().availableProcessors();

    /**
     * 爬去当日数据
     */
    public void start(){
        String date = new DateTime(new Date()).toString("yyyy-MM-dd");

        String url = "http://www.ccgp-hainan.gov.cn/cgw/cgw_list.jsp?currentPage=1&begindate=" + date + "&enddate=" + date + "&title=&bid_type=&proj_number=&zone=";

        Request request = new Request(url);

        Spider spider=Spider.create(haiNanPageProcessor)
                .addRequest(request)
                .addPipeline(haiNanPipeline)
                .setUUID(uuid)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }
}
