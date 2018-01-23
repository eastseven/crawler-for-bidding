package com.har.sjfxpt.crawler.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.SpiderNewLauncher;
import com.spring4all.swagger.EnableSwagger2Doc;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.Spider;

import java.util.List;
import java.util.Map;

/**
 * @author dongqi
 */
@Slf4j
@RestController
@EnableSwagger2Doc
@RequestMapping("/spider")
public class BidNewsSpiderResource {

    @Autowired
    private SpiderNewLauncher spiderNewLauncher;

    private Map<String, Object> getSpiderResponse(Spider spider) {
        Map<String, Object> response = Maps.newConcurrentMap();
        response.put("status", spider.getStatus());
        response.put("pageCount", spider.getPageCount());
        response.put("site", spider.getSite().getDomain());
        response.put("startTime", new DateTime(spider.getStartTime()).toString("yyyy-MM-dd HH:mm:ss"));
        response.put("threadAlive", spider.getThreadAlive());
        response.put("uuid", spider.getUUID());
        return response;
    }

    @GetMapping
    public Object list() {
        List<Map<String, Object>> list = Lists.newArrayList();
        spiderNewLauncher.getSpiders().forEach((s, bidNewsSpider) -> list.add(getSpiderResponse(bidNewsSpider)));
        return list;
    }

    /*@GetMapping("/{sourceCode}")
    public Object get(@PathVariable String sourceCode) {
        log.debug(">>> api get spider info, {}", sourceCode);
        if (!sourceCode.startsWith("spider_")) {
            sourceCode = "spider_" + sourceCode;
        }

        BidNewsSpider spider = spiderNewLauncher.getSpider(sourceCode.toLowerCase());
        if (spider != null) {
            return getSpiderResponse(spider);
        }

        return null;
    }

    @GetMapping("/start/{sourceCode}")
    public Object start(@PathVariable String sourceCode) {
        if (!sourceCode.startsWith("spider_")) {
            sourceCode = "spider_" + sourceCode;
        }
        BidNewsSpider spider = spiderNewLauncher.getSpider(sourceCode.toLowerCase());
        if (spider == null) return null;

        if (!spider.getStatus().equals(Spider.Status.Running)) {
            spider.start();
        }

        return getSpiderResponse(spider);
    }

    @GetMapping("/stop/{sourceCode}")
    public Object stop(@PathVariable String sourceCode) {
        if (!sourceCode.startsWith("spider_")) {
            sourceCode = "spider_" + sourceCode;
        }

        BidNewsSpider spider = spiderNewLauncher.getSpider(sourceCode.toLowerCase());

        if (spider == null) return null;

        if (spider.getStatus().equals(Spider.Status.Running)) {
            spider.stop();
        }

        return getSpiderResponse(spider);
    }*/

    @GetMapping("/startAll")
    public Object startAll() {
        spiderNewLauncher.start();
        return true;
    }
}
