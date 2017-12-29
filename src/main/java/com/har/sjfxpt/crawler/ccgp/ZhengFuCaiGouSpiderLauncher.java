package com.har.sjfxpt.crawler.ccgp;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.core.model.DataItemDTO;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.service.DataItemService;
import com.har.sjfxpt.crawler.core.service.ProxyService;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

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
    ZhengFuCaiGouDownloader zhengFuCaiGouDownloader;

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
    final String DATE_PATTERN_FOR_ID = "yyyyMMdd";

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

        zhengFuCaiGouDownloader.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxies()));
        spider.setDownloader(zhengFuCaiGouDownloader);
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
        cleanSpider(uuid_history);

        Sort sort = new Sort(Sort.Direction.ASC, "date");
        int size = (int) pageDataRepository.count();
        Page<PageData> page = pageDataRepository.findAll(new PageRequest(0, size, Sort.Direction.ASC, "date"));
        Spider historySpider = getHistorySpider();
        if (page.hasContent()) {
            page.forEach(pageData -> {
                historySpider.addRequest(new Request(getUrl(pageData.getDate(), pageData.getDate())));
                log.info(">>> {} history fetch, add date {}", SourceCode.CCGP, pageData.getDateLong());
            });
            historySpider.setUUID(uuid_history);
        }

        addSpider(historySpider);
        return historySpider;
    }

    public Spider getHistorySpider() {
        Spider historySpider = Spider.create(pageProcessor).addPipeline(pipeline);
        zhengFuCaiGouDownloader.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxies()));
        historySpider.setDownloader(zhengFuCaiGouDownloader);
        historySpider.thread(proxyService.getAliyunProxies().length);
        return historySpider;
    }

    /**
     * 2013-01-01 to now
     */
    public Spider countPageData() {
        HttpClientDownloader downloader = new HttpClientDownloader();
        downloader.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxies()));
        Spider spider = Spider.create(pageDataProcessor).setExitWhenComplete(true);
        spider.setDownloader(downloader).thread(Runtime.getRuntime().availableProcessors() * 2);

        DateTime start = new DateTime("2013-01-01");
        DateTime end = DateTime.now();
        Duration duration = new Duration(start, end);

        final int days = (int) duration.getStandardDays();
        for (int day = 0; day < days; day++) {
            String id = start.plusDays(day).toString(DATE_PATTERN);

            if (pageDataRepository.exists(id)) {
                log.warn(">>> {} exists", id);
                continue;
            }

            String date = null;
            try {
                date = URLEncoder.encode(id, "utf-8");
            } catch (UnsupportedEncodingException e) {
                log.error("", e);
            }
            String params = "&start_time=" + date + "&end_time=" + date + "&page_index=1";
            String url = URL_PREFIX + params;

            PageData pageData = new PageData();
            pageData.setDateLong(Long.parseLong(id.replace(":", "")));
            pageData.setDate(id);
            pageData.setUrl(url);
            Request request = new Request(url);
            request.putExtra(PageData.class.getSimpleName(), pageData);

            spider.addRequest(request);
        }

        return spider;
    }

    public Spider countPageData(String... dateArray) {
        HttpClientDownloader downloader = new HttpClientDownloader();
        downloader.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxies()));
        Spider spider = Spider.create(pageDataProcessor).setExitWhenComplete(true);
        spider.setDownloader(downloader).thread(Runtime.getRuntime().availableProcessors() * 2);

        for (String id : dateArray) {
            String date = null;
            try {
                date = URLEncoder.encode(id, "utf-8");
            } catch (UnsupportedEncodingException e) {
                log.error("", e);
            }
            String params = "&start_time=" + date + "&end_time=" + date + "&page_index=1";
            String url = URL_PREFIX + params;

            PageData pageData = new PageData();
            pageData.setDateLong(Long.parseLong(id.replace(":", "")));
            pageData.setDate(id);
            pageData.setUrl(url);
            Request request = new Request(url);
            request.putExtra(PageData.class.getSimpleName(), pageData);

            spider.addRequest(request);
        }

        return spider;
    }

    String getUrl(String startDate, String endDate) {
        String start = null, end = null;
        try {
            start = URLEncoder.encode(startDate, "utf-8");
            end = URLEncoder.encode(endDate, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        }
        String params = "&start_time=" + start + "&end_time=" + end + "&page_index=1";
        String url = URL_PREFIX + params;
        return url;
    }


    @Autowired
    StringRedisTemplate stringRedisTemplate;

    final String names = "ccgp_history_fail_urls";

    public static final String cssQuery4List = "div.vT_z div.vT-srch-result div.vT-srch-result-list-con2 div.vT-srch-result-list ul.vT-srch-result-list-bid li";

    @Autowired
    ZhengFuCaiGouRepository repository;

    @Autowired
    DataItemService dataItemService;

    public void getRedisUrl() {
        long total = stringRedisTemplate.boundSetOps(names).size();
        for (int i = 0; i < total; i++) {
            String tabulationUrl = stringRedisTemplate.boundSetOps(names).pop();
            log.debug("total=={},tabulationUrl=={}", total, tabulationUrl);
            Request request = new Request(tabulationUrl);
            zhengFuCaiGouDownloader.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxies()));
            us.codecraft.webmagic.Page page = zhengFuCaiGouDownloader.download(request, SiteUtil.get().toTask());
            Document document = page.getHtml().getDocument();
            Elements elements = document.body().select(cssQuery4List);
            List<ZhengFuCaiGouDataItem> dataItemList = pageProcessor.parseContent(elements);
            if (!dataItemList.isEmpty()) {
                repository.save(dataItemList);
                log.info("ccgp save {} to mongodb", dataItemList.size());
                List<DataItemDTO> dtoList = dataItemList.stream().map(dataItem -> dataItem.dto()).collect(Collectors.toList());
                dataItemService.save2BidNewsOriginalTable(dtoList);
            }
        }
    }
}
