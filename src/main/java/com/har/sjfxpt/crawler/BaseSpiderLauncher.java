package com.har.sjfxpt.crawler;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

import java.util.List;

/**
 * @author dongqi
 */
@Slf4j
@Component
public class BaseSpiderLauncher implements DisposableBean {

    private final String SPIDER_UUID_KEY = "spider_uuid";

    @Autowired
    StringRedisTemplate redisTemplate;

    private List<Spider> spiderList = Lists.newCopyOnWriteArrayList();

    public void addSpider(Spider spider) {
        if (spider == null) return;

        if (StringUtils.isBlank(spider.getUUID())) {
            log.warn("spider uuid is null, {}", spider);
            return;
        }

        String uuid = spider.getUUID();
        if (redisTemplate.boundSetOps(SPIDER_UUID_KEY).add(uuid) > 0) {
            spiderList.add(spider);
            log.info("add spider {}, {}", spider.getUUID(), spider);
        }

    }

    public List<Spider> getSpiderList() {
        return spiderList;
    }

    protected void printInfo() {
        spiderList.forEach(spider -> {
            String datetime = new DateTime(spider.getStartTime()).toString("yyyy-MM-dd HH:mm:ss");
            log.info("\tSpider Info\tuuid={}, status={}, pageCount={}, threadAlive={}, startTime={}", spider.getUUID(), spider.getStatus(), spider.getPageCount(), spider.getThreadAlive(), datetime);
        });
    }

    protected void cleanSpider(final String uuid) {
        spiderList.forEach(spider -> {
            if (spider.getUUID().equalsIgnoreCase(uuid)) {
                spider.stop();
                spider.close();
            }
        });
    }

    protected void start(String uuid) {
        log.info("prepare start {}, current spider size {}", uuid, spiderList.size());
        spiderList.forEach(spider -> {
            if (spider.getUUID().equalsIgnoreCase(uuid)) {
                if (!spider.getStatus().equals(Spider.Status.Running)) {
                    spider.start();
                }
            }
        });
    }

    @Override
    public void destroy() throws Exception {
        String info = ">>> spider size " + spiderList.size();

        for (Spider spider : spiderList) {
            try {
                if (spider != null) {
                    spider.close();
                }
            } catch (Exception e) {
                log.error("", e);
            }

            redisTemplate.boundSetOps(SPIDER_UUID_KEY).remove(spider.getUUID());
            info += "\n>>> destroy spider " + spider.getUUID();
        }

        log.info("\n{}\n", info);
    }
}
