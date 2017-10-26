package com.har.sjfxpt.crawler.ggzy.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@ConfigurationProperties(prefix = "app.proxy.pool")
public class ProxyService {

    private List<String> ips = Lists.newArrayList();

    public List<String> getIps() {
        return ips;
    }

    public void setIps(List<String> ips) {
        this.ips = ips;
    }

    @Value("${app.proxy.pool.url}")
    String GET_PROXY_URL;

    HttpClientDownloader downloader = new HttpClientDownloader();

    public HttpClientDownloader getDownloader() {
        log.debug("{}", Arrays.toString(ips.toArray()));
        List<Proxy> proxyList = Lists.newArrayList();
        for (String ip : ips) {
            String host = StringUtils.substringBefore(ip, ":");
            String port = StringUtils.substringAfter(ip, ":");
            proxyList.add(new Proxy(host, Integer.parseInt(port)));
        }
        downloader.setProxyProvider(SimpleProxyProvider.from(proxyList.toArray(new Proxy[ips.size()])));
        return downloader;
    }

    public HttpClientDownloader getDownloader(String proxy) {
        String host = StringUtils.substringBefore(proxy, ":");
        String port = StringUtils.substringAfter(proxy, ":");
        return getDownloader(host, Integer.parseInt(port));
    }

    public HttpClientDownloader getDownloader(String host, int port) {
        downloader.setProxyProvider(SimpleProxyProvider.from(new Proxy(host, port)));
        return downloader;
    }

    public Proxy get() {
        try {
            String html = Jsoup.parse(new URL(GET_PROXY_URL), 10000).body().html();
            String host = html.split(":")[0];
            int port = Integer.valueOf(html.split(":")[1]);

            return new Proxy(host, port);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public Proxy getValidProxy() {
        HttpClientDownloader downloader = new HttpClientDownloader();
        while (true) {
            Proxy proxy = get();
            downloader.setProxyProvider(SimpleProxyProvider.from(proxy));
            try {
                downloader.download("https://www.baidu.com/", "UTF-8");
                return proxy;
            } catch (Exception e) {
                log.warn("无效代理 {}", proxy);
            }
        }
        //return null;
    }

    public Proxy[] getProxyWithZhiMa() {
        List<Proxy> proxyList = Lists.newArrayList();
        // http://http.zhimaruanjian.com
        String url = "http://http-webapi.zhimaruanjian.com/getip?num=25&type=2&pro=0&city=0&yys=0&port=1&pack=6212&ts=0&ys=0&cs=0&lb=0&sb=0&pb=4&mr=0";
        try {
            String jsonText = Jsoup.connect(url).userAgent(SiteUtil.get().getUserAgent()).timeout(600000).get().body().text();
            log.debug("{}", jsonText);
            JSONObject jsonObject = (JSONObject) JSONObject.parse(jsonText);
            JSONArray data = (JSONArray) jsonObject.get("data");
            for (Object object : data) {
                JSONObject json = (JSONObject) object;
                String host = json.getString("ip");
                int port = json.getIntValue("port");
                log.debug("{}", json);
                proxyList.add(new Proxy(host, port));
            }
        } catch (IOException e) {
            log.error("", e);
        }

        return proxyList.toArray(new Proxy[proxyList.size()]);
    }


}
