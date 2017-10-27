package com.har.sjfxpt.crawler.chinamobile;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanUtil.YYYYMMDD;

@Slf4j
@Service
public class ChinaMobileSpiderLauncher {

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

    public void start() {
        final int num = Runtime.getRuntime().availableProcessors();
        for (int type : types) {
            Spider spider = Spider.create(pageProcessor).addPipeline(pipeline);
            spider.addRequest(getRequest(type));
            spider.thread(num);
            spider.start();
        }
    }

    public void fetchHistory() {
        DateTime df = DateTime.now().minusDays(1);
        String endDate = df.toString(YYYYMMDD);
        String startDate = df.minusMonths(6).toString(YYYYMMDD);

        final int num = Runtime.getRuntime().availableProcessors();
        for (int type : types) {
            Spider spider = Spider.create(pageProcessor).addPipeline(pipeline);
            spider.addRequest(getRequest(type, pageParams(startDate, endDate)));
            spider.thread(num);
            spider.start();
        }
    }

    /**
     * 当天
     * @param type
     * @return
     */
    Request getRequest(int type) {
        String date = DateTime.now().toString(YYYYMMDD);
        return getRequest(type, pageParams(date, date));
    }

    Request getRequest(int type, Map<String, Object> params) {
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
