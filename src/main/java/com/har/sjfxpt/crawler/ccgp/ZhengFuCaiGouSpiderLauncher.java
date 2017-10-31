package com.har.sjfxpt.crawler.ccgp;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
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

    Spider spider;

    public Spider getSpider() {
        return spider;
    }

    final String uuid = "ccgp-current";

    @PostConstruct
    public void init() {
        cleanSpider(uuid);

        DateTime dt = DateTime.now();
        PageData pageData = new PageData();
        pageData.setDate(dt.toString("yyyy:MM:dd"));
        String date = null;
        try {
            date = URLEncoder.encode(dt.toString("yyyy:MM:dd"), "utf-8");
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

        addSpider(spider);
    }

    public void start() {
        start(uuid);
    }

    public void history() {

    }

}
