package com.har.sjfxpt.crawler;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import java.util.List;

/**
 * @author dongqi
 */
@Slf4j
@Service
public class BaseSpiderLauncher implements DisposableBean {

    private List<Spider> spiderList = Lists.newArrayList();

    public void addSpider(Spider spider) {
        spiderList.add(spider);
    }

    public List<Spider> getSpiderList() {
        return spiderList;
    }

    @Override
    public void destroy() throws Exception {
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
