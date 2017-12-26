package com.har.sjfxpt.crawler.sgcc;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.concurrent.ExecutorService;

/**
 * @author dongqi
 * 国家电网公司 电子商务平台
 */
@Slf4j
@Service
public class StateGridSpiderLauncher extends BaseSpiderLauncher {

    public static final String BID_URL = "http://ecp.sgcc.com.cn/topic_project_list.jsp?columnName=topic10";
    public static final String WIN_URL = "http://ecp.sgcc.com.cn/topic_news_list.jsp?columnName=topic23&column_code1=014001007&column_code2=014002003";

    /**
     * 招标 1
     * http://ecp.sgcc.com.cn/topic_project_list.jsp?columnName=topic10
     */
    private Spider bidSpider;

    /**
     * 中标 2
     * http://ecp.sgcc.com.cn/topic_news_list.jsp?columnName=topic23&column_code1=014001007&column_code2=014002003
     */
    private Spider winSpider;

    @Autowired
    StateGridPageProcessor pageProcessor;

    @Autowired
    StateGridPipeline pipeline;

    @Autowired
    ExecutorService executorService;

    public void start() {
       initBidSpider(false);
       initWinSpider(false);

       start(SourceCode.SGCC.name().toLowerCase() + "-bid-current");
       start(SourceCode.SGCC.name().toLowerCase() + "-win-current");
    }

    public Spider initBidSpider(boolean fetchAll) {
        String uuid = SourceCode.SGCC.name().toLowerCase() + "-bid-current";
        bidSpider = getSpider(uuid);
        if (bidSpider == null) {
            bidSpider = Spider.create(pageProcessor).setUUID(uuid);
            bidSpider.addPipeline(pipeline);
            bidSpider.thread(executorService, 5);
            Request request = new Request(BID_URL);
            request.putExtra("type", "招标");
            request.putExtra("fetchAll", fetchAll);
            bidSpider.addRequest(request);

            addSpider(bidSpider);
        }

        return bidSpider;
    }

    public Spider initWinSpider(boolean fetchAll) {
        String uuid = SourceCode.SGCC.name().toLowerCase() + "-win-current";
        winSpider = getSpider(uuid);
        if (winSpider == null) {
            winSpider = Spider.create(pageProcessor).setUUID(uuid);
            winSpider.addPipeline(pipeline);
            winSpider.thread(executorService, 5);
            Request request = new Request(WIN_URL);
            request.putExtra("type", "中标");
            request.putExtra("fetchAll", fetchAll);
            winSpider.addRequest(request);

            addSpider(winSpider);
        }
        return winSpider;
    }

    public void fetchAll() {
        initBidSpider(true).start();
        initWinSpider(true).start();
    }
}
