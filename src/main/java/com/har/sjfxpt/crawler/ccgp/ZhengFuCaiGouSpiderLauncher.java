package com.har.sjfxpt.crawler.ccgp;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author dongqi
 */
@Slf4j
@Service
public class ZhengFuCaiGouSpiderLauncher extends BaseSpiderLauncher {

    final String URL_PREFIX = "http://search.ccgp.gov.cn/bxsearch?searchtype=1&bidSort=&buyerName=&projectId=&pinMu=&bidType=&dbselect=bidx&kw=&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=";

    @Autowired
    ZhengFuCaiGouPageProcessor pageProcessor;

    @Autowired
    PageDataProcessor pageDataProcessor;

    @Autowired
    ProxyService proxyService;

    @Autowired
    ExecutorService executorService;

    @Autowired
    ZhengFuCaiGouDownloader downloader;

    @Autowired
    ZhengFuCaiGouPipeline pipeline;

    @Autowired
    PageDataRepository pageDataRepository;

    Spider spider;

    public Spider getSpider() {
        return spider;
    }

    final String uuid = "ccgp-current";
    final String uuid_history = "ccgp-history";

    final String DATE_PATTERN = "yyyy:MM:dd";

    public void init() {
        cleanSpider(uuid);

        DateTime dt = DateTime.now();
        PageData pageData = new PageData();
        pageData.setDate(dt.toString(DATE_PATTERN));
        String date = null;
        try {
            date = URLEncoder.encode(dt.toString(DATE_PATTERN), "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        }

        String params = "&start_time=" + date + "&end_time=" + date + "&page_index=1";
        String url = URL_PREFIX + params;
        Request request = new Request(url);
        pageData.setUrl(url);
        request.putExtra(PageData.class.getSimpleName(), pageData);

        spider = Spider.create(pageProcessor).addPipeline(pipeline);

        downloader.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxies()));
        spider.setDownloader(downloader);
        log.info("ccgp use proxy {}, fetch {}", Arrays.toString(proxyService.getAliyunProxies()), pageData.getDate());

        spider.setExitWhenComplete(true);
        spider.addRequest(request);
        spider.setUUID(uuid);
        spider.thread(10);

        addSpider(spider);
    }

    public void start() {
        init();
        start(uuid);
    }

    public Spider history() {
        //cleanSpider(uuid_history);

        DateTime df = DateTime.now().minusDays(1);
        String end = df.toString(DATE_PATTERN);
        String start = df.minusMonths(3).toString(DATE_PATTERN);
        log.info(">>> ccgp history fetch start {} to {}", start, end);

        Spider historySpider = Spider.create(pageProcessor).addPipeline(pipeline);
        historySpider.setExecutorService(executorService);
        historySpider.thread(10);
        historySpider.setUUID(uuid_history);
        historySpider.addRequest(new Request(getUrl(start, end)));

        historySpider.start();

        return historySpider;
    }

    public void countPageData() {
        List<PageData> pageDataList = pageDataRepository.findAll(new Sort(Sort.Direction.ASC, "date"));
        if (CollectionUtils.isEmpty(pageDataList)) return;

        PageData first = pageDataList.get(0);
        log.debug(">>> {}", first);

        DateTime start = new DateTime(first.getDate().replace(":", "-"));

        int days = 30;
        Spider spider = Spider.create(pageDataProcessor).setExitWhenComplete(true);
        spider.setExecutorService(executorService);
        spider.thread(days);

        for (int day = 0; day < days; day++) {
            String date = null;
            String id = start.minusDays(day).toString(DATE_PATTERN);
            try {
                date = URLEncoder.encode(id, "utf-8");
            } catch (UnsupportedEncodingException e) {
                log.error("", e);
            }
            String params = "&start_time=" + date + "&end_time=" + date + "&page_index=1";
            String url = URL_PREFIX + params;

            PageData pageData = new PageData();
            pageData.setDate(id);
            pageData.setUrl(url);
            Request request = new Request(url);
            request.putExtra(PageData.class.getSimpleName(), pageData);

            spider.addRequest(request);
        }

        spider.run();
    }

    String getUrl(String startDate, String endDate) {
        String start = null, end = null;
        try {
            start = URLEncoder.encode(startDate, "utf-8");
            end   = URLEncoder.encode(endDate, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        }

        String params = "&start_time=" + start + "&end_time=" + end + "&page_index=1";
        String url = URL_PREFIX + params;
        return url;
    }
}
