package com.har.sjfxpt.crawler.ggzy.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Html;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Slf4j
@Service
public class ProxyService {

    @Value("${app.proxy.pool.url}") String GET_PROXY_URL;

    public HttpClientDownloader getDownloader() {
        HttpClientDownloader downloader = new HttpClientDownloader();
        downloader.setProxyProvider(SimpleProxyProvider.from(getProxyWithZhiMa()));
        return downloader;
    }

    public Proxy get() {
        try {
            String html = Jsoup.parse(new URL(GET_PROXY_URL), 10000).body().html();
            log.debug("{}", html);

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
                Html html = downloader.download("https://www.baidu.com/", "UTF-8");
                log.debug("{}", html);
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
                JSONObject json = (JSONObject)object;
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
