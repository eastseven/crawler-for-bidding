package com.har.sjfxpt.crawler.core;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.core.listener.MonitorSpiderListener;
import com.har.sjfxpt.crawler.core.pipeline.GongGongZiYuanMongoPipeline;
import com.har.sjfxpt.crawler.core.pipeline.GongGongZiYuanPipeline;
import com.har.sjfxpt.crawler.core.processor.GongGongZiYuanPageProcessor;
import com.har.sjfxpt.crawler.core.utils.GongGongZiYuanUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.scheduler.RedisScheduler;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.BUSINESS_TYPE_01;
import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.BUSINESS_TYPE_02;
import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanUtil.SEED_URL;
import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanUtil.YYYYMMDD;

/**
 * @author dongqi
 */
@Slf4j
@Service
public class GongGongZiYuanSpiderLauncher extends BaseSpiderLauncher {

    final int num = Runtime.getRuntime().availableProcessors() * 4;

    final String[] types = {BUSINESS_TYPE_01, BUSINESS_TYPE_02};

    @Autowired
    ApplicationContext context;

    @Autowired
    ExecutorService executorService;

    @Autowired
    RedisScheduler redisScheduler;

    /**
     * 获取当天的抓取请求
     *
     * @param type
     * @return
     */
    Request getRequest(String type) {
        Map<String, Object> params = GongGongZiYuanUtil.getPageParamsByType(type);
        Request request = new Request(SEED_URL);
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.putExtra("pageParams", params);

        return request;
    }

    Request getRequest(Map<String, Object> params) {
        Request request = new Request(SEED_URL);
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.putExtra("pageParams", params);

        return request;
    }

    Request getRequest(String type, String date) {
        Map<String, Object> params = GongGongZiYuanUtil.getPageParamsByType(type, date);
        return getRequest(params);
    }

    public void init() {
        for (String type : types) {
            final String uuid = "ggzy-" + type + "-current";
            cleanSpider(uuid);

            Spider spider = getGongGongZiYuanSpider();
            spider.addRequest(getRequest(type));
            spider.thread(num);
            spider.setUUID(uuid);

            addSpider(spider);
        }
    }

    /**
     * 按type抓取当天的数据
     */
    public void start() {
        init();
        for (String type : types) {
            final String uuid = "ggzy-" + type + "-current";
            start(uuid);
            log.info("ggzy {} spider start", type);
        }
    }

    public void startByDate(String date) {
        for (String type : types) {
            String uuid = "ggzy-" + type + '-' + date;
            cleanSpider(uuid);
            Spider spider = getGongGongZiYuanSpider();
            spider.addRequest(getRequest(type, date));
            spider.setUUID(uuid);

            addSpider(spider);
        }
    }

    /**
     * 按type抓取昨天之前近3个月的数据
     */
    public void fetchHistory() {
        String begin = DateTime.now().minusMonths(3).toString(YYYYMMDD);
        String end = DateTime.now().minusDays(1).toString(YYYYMMDD);
        log.info("fetch history between {} to {}", begin, end);

        for (String type : types) {
            fetchHistory(type, begin, end);
        }
    }

    public void fetchHistory(String type, String begin, String end) {
        String uuid = "ggzy-" + type + "-history-" + begin + '-' + end;
        cleanSpider(uuid);

        Spider spider = getGongGongZiYuanSpider();
        spider.addRequest(getRequest(GongGongZiYuanUtil.getPageParamsByType(type, begin, end)));
        spider.thread(num);
        addSpider(spider);
    }

    Spider getGongGongZiYuanSpider() {
        Spider spider = Spider.create(context.getBean(GongGongZiYuanPageProcessor.class));
        spider.addPipeline(context.getBean(GongGongZiYuanMongoPipeline.class));
        spider.addPipeline(context.getBean(GongGongZiYuanPipeline.class));
        spider.setSpiderListeners(Lists.newArrayList(context.getBean(MonitorSpiderListener.class)));
        spider.setExitWhenComplete(true);

        return spider;
    }

}
