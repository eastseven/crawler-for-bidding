package com.har.sjfxpt.crawler;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.DisposableBean;
import us.codecraft.webmagic.Spider;

import java.util.List;

/**
 * @author dongqi
 */
@Slf4j
public class BaseSpiderLauncher implements DisposableBean {

    private List<Spider> spiderList = Lists.newCopyOnWriteArrayList();

    public void addSpider(Spider spider) {
        if (spider == null) return;

        if (StringUtils.isBlank(spider.getUUID())) {
            log.warn("spider uuid is null, {}", spider);
            return;
        }
        log.info("add spider {}, {}", spider.getUUID(), spider);
        spiderList.add(spider);
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
        log.info(">>> spider size {}", spiderList.size());

        for (Spider spider : spiderList) {
            if (spider.getStatus().equals(Spider.Status.Running)) {
                spider.stop();
            }
            Thread.sleep(1234L);
            spider.close();
            log.info(">>> destroy spider {}", spider.getUUID());
        }
    }
}
