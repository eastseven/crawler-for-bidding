package com.har.sjfxpt.crawler.core.listener;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;

/**
 * Created by Administrator on 2018/1/10.
 *
 * @author luofei
 */
@Slf4j
@Component
public class FinishSpiderListener implements SpiderListener {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    final static String KEY_SUCCESS = "log_success";
    final static String KEY_FAIL = "log_fail";

    @Override
    public void onSuccess(Request request) {
        log.debug(">>> {}", request.getUrl());
        String msg = DateTime.now().toString("yyyy-MM-dd HH:mm:ss") + ": " + request.getUrl();
        stringRedisTemplate.boundValueOps(KEY_SUCCESS).set(msg);
    }

    @Override
    public void onError(Request request) {
        log.error(">>> {}", request.getUrl());
        String msg = DateTime.now().toString("yyyy-MM-dd HH:mm:ss") + ": " + request.getUrl();
        stringRedisTemplate.boundValueOps(KEY_FAIL).set(msg);
    }
}
