package com.har.sjfxpt.crawler.core.listener;

import lombok.extern.slf4j.Slf4j;
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
    @Override
    public void onSuccess(Request request) {
        log.info(">>> {}", request.getUrl());
    }

    @Override
    public void onError(Request request) {
        log.error(">>> {}", request.getUrl());
    }
}
