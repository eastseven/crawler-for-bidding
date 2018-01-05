package com.har.sjfxpt.crawler.chinamobile;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanUtil.YYYYMMDD;

/**
 * @author dongqi
 */
@Slf4j
@Service
@Deprecated
public class ChinaMobileSpiderLauncher extends BaseSpiderLauncher {

    final String SEED_URL = "https://b2b.10086.cn/b2b/main/listVendorNoticeResult.html?noticeBean.noticeType=";

    /**
     * 1 单一来源采购信息公告
     * 2 采购公告
     * 3 资格预审公告
     * 7 结果公示
     * 8 供应商信息收集公告
     */
    int[] types = {1, 2, 3, 7};

    @Autowired
    ChinaMobilePageProcessor pageProcessor;

    @Autowired
    ChinaMobilePipeline pipeline;

    final int num = Runtime.getRuntime().availableProcessors();

    public void init() {
        for (int type : types) {
            final String uuid = "cm-" + type + "-current";
            cleanSpider(uuid);

            Spider spider = Spider.create(pageProcessor).addPipeline(pipeline);
            spider.addRequest(getRequest(type));
            spider.thread(num);
            spider.setExitWhenComplete(true);
            spider.setUUID(uuid);

            addSpider(spider);
        }
    }

    public void start() {
        init();
        for (int type : types) {
            final String uuid = "cm-" + type + "-current";
            start(uuid);
        }
    }

    public void fetchHistory() {
        DateTime df = DateTime.now().minusDays(1);
        String endDate = df.toString(YYYYMMDD);
        String startDate = df.minusYears(5).toString(YYYYMMDD);

        fetchHistory(startDate, endDate);
    }

    public void fetchHistory(String startDate, String endDate) {
        final int num = Runtime.getRuntime().availableProcessors();
        for (int type : types) {
            Spider spider = Spider.create(pageProcessor).addPipeline(pipeline);
            spider.addRequest(getRequest(String.valueOf(type), pageParams(startDate, endDate)));
            spider.thread(num);
            spider.setExitWhenComplete(true);
            spider.start();

            addSpider(spider);
        }
    }

    public Spider fetchHistoryStartWith2013() {
        String startDate = "2013-01-01";
        String endDate = DateTime.now().minusDays(1).toString(YYYYMMDD);
        Spider spider = Spider.create(pageProcessor).addPipeline(pipeline);
        spider.addRequest(getRequest("", pageParams(startDate, endDate)));
        spider.thread(num);
        spider.setExitWhenComplete(true);

        return spider;
    }

    /**
     * 当天
     *
     * @param type
     * @return
     */
    Request getRequest(int type) {
        String date = DateTime.now().toString(YYYYMMDD);
        return getRequest(String.valueOf(type), pageParams(date, date));
    }

    Request getRequest(String type, Map<String, Object> params) {
        Request request = new Request(SEED_URL + type);
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
        request.putExtra("pageParams", params);

        return request;
    }

    Map<String, Object> pageParams(String startDate, String endDate) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("page.currentPage", 1);
        params.put("page.perPageSize", 20);
        params.put("noticeBean.sourceCH", "");
        params.put("noticeBean.source", "");
        params.put("noticeBean.title", "");
        params.put("noticeBean.startDate", startDate);
        params.put("noticeBean.endDate", endDate);
        return params;
    }

}
