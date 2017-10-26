package com.har.sjfxpt.crawler.ccgp;

import com.har.sjfxpt.crawler.ggzy.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;

/**
 * @author dongqi
 */
@Slf4j
@Service
public class ZhengFuCaiGouSpiderLauncher {

    @Autowired
    PageDataProcessor pageDataProcessor;


    @Autowired
    ProxyService proxyService;

    @Autowired
    ExecutorService executorService;

    public void test() {
        DateTime df = DateTime.now().minusDays(1);
        int day = 30;
        for (int index = 0; index < day; index++) {
            final int days = index;
            executorService.execute(() -> {

                String dateText = df.minusDays(days).toString("yyyy:MM:dd");
                String date = null;
                try {
                    date = URLEncoder.encode(dateText, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                PageData pageData = new PageData();
                pageData.setDate(dateText);

                String url = "http://search.ccgp.gov.cn/bxsearch?searchtype=1&bidSort=&buyerName=&projectId=&pinMu=&bidType=&dbselect=bidx&kw=&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=";
                String params = "&start_time=" + date + "&end_time=" + date + "&page_index=1";
                url = url + params;
                Request request = new Request(url);
                pageData.setUrl(url);
                request.putExtra(PageData.class.getSimpleName(), pageData);

                Spider spider = Spider.create(pageDataProcessor);
                HttpClientDownloader downloader = new HttpClientDownloader();
                downloader.setProxyProvider(SimpleProxyProvider.from(proxyService.getValidProxy()));
                spider.setDownloader(downloader);
                spider.addRequest(request);
                spider.runAsync();
            });
        }
    }
}
