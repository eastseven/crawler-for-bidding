package com.har.sjfxpt.crawler.ccgp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

@Slf4j
@Component
public class ZhengFuCaiGouDownloader extends HttpClientDownloader {

    @Autowired
    StringRedisTemplate redisTemplate;

    final String key = "ccgp_history_fail_urls";

    @Override
    public Page download(Request request, Task task) {
        Page page = super.download(request, task);
        log.debug(">>> {}, {}", request.getUrl(), page == null);
        return page;
    }

    @Override
    protected void onError(Request request) {
        redisTemplate.boundSetOps(key).add(request.getUrl());
        super.onError(request);
    }
}
