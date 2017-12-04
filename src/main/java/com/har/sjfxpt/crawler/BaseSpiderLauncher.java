package com.har.sjfxpt.crawler;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

import java.util.Map;

/**
 * @author dongqi
 */
@Slf4j
@Component
public class BaseSpiderLauncher implements DisposableBean, Runnable {

    private Map<String, Spider> spiderMap = Maps.newConcurrentMap();

    public Spider getSpider(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            log.warn("spider uuid is null, {}", uuid);
            return null;
        }

        if (spiderMap.containsKey(uuid)) {
            return spiderMap.get(uuid);
        }

        return null;
    }

    public void addSpider(Spider spider) {
        if (spider == null) return;

        if (StringUtils.isBlank(spider.getUUID())) {
            log.warn("spider uuid is null, {}", spider);
            return;
        }

        String uuid = spider.getUUID();
        if (!spiderMap.containsKey(uuid)) {
            spiderMap.put(uuid, spider);
            log.info("add spider {}, {}", spider.getUUID(), spider.getStatus());
        }

    }

    protected void printInfo() {
        spiderMap.forEach((uuid, spider) -> {
            String datetime = new DateTime(spider.getStartTime()).toString("yyyy-MM-dd HH:mm:ss");
            log.info(">>> Spider Info [{}] uuid={}, stats={}, page={}, thread={}, time={}",
                    spider, spider.getUUID(), spider.getStatus(), spider.getPageCount(), spider.getThreadAlive(), datetime);
        });
    }

    protected void cleanSpider(final String uuid) {
        Spider spider = spiderMap.remove(uuid);
        if (spider != null) {
            spider.close();
        }
    }

    protected void start(String uuid) {
        log.info("prepare start {}, current spider size {}", uuid, spiderMap.size());
        Spider spider = spiderMap.get(uuid);
        if (spider != null) {
            if (spider.getStatus().equals(Spider.Status.Running)) return;

            spider.start();
        }
    }

    @Override
    public void destroy() {
        String info = ">>> spider size " + spiderMap.size();

        for (Spider spider : spiderMap.values()) {
            try {
                if (spider != null) {
                    spider.close();
                }
            } catch (Exception e) {
                log.error("", e);
            }

            info += "\n>>> destroy spider " + spider.getUUID();
        }

        log.info("\n{}\n", info);
    }

    @Override
    public void run() {

    }
}
