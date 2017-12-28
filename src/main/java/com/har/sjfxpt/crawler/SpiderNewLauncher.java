package com.har.sjfxpt.crawler;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.pipeline.DataItemDtoPipeline;
import com.har.sjfxpt.crawler.core.processor.Source;
import com.har.sjfxpt.crawler.core.processor.SourceConfig;
import com.har.sjfxpt.crawler.core.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author dongqi
 */
@Slf4j
@Service
@Order(Ordered.LOWEST_PRECEDENCE - 2)
public class SpiderNewLauncher implements CommandLineRunner {

    @Autowired
    ApplicationContext ctx;

    @Autowired
    ExecutorService executorService;

    @Autowired
    ProxyService proxyService;

    @Autowired
    HttpClientDownloader downloader;

    private static final String basePackage = "com.har.sjfxpt.crawler";

    private Map<String, Spider> spiderMap = Maps.newConcurrentMap();

    public Map<String, Spider> getSpiders() {
        return this.spiderMap;
    }

    public void init() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(SourceConfig.class));
        for (BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
            String pageProcessorClassName = bd.getBeanClassName();
            log.debug(">>> pageProcessorClassName [{}], {}, {}", pageProcessorClassName, bd.getParentName(), bd.getFactoryBeanName());
            Object pageProcessor = null;

            try {
                pageProcessor = ctx.getBean(Class.forName(pageProcessorClassName));
            } catch (ClassNotFoundException e) {
                log.error("", e);
            }

            SourceConfig config = AnnotationUtils.findAnnotation(pageProcessor.getClass(), SourceConfig.class);

            final String uuid = "spider_" + config.code().name().toLowerCase();
            if (spiderMap.containsKey(uuid)) {
                log.warn(">>> spider uuid[{}] is exists, status is {}", uuid, spiderMap.get(uuid).getStatus());
                continue;
            }

            // 创建 Request 对象集合
            List<Request> requestList = Lists.newArrayList();
            Source[] sources = config.sources();
            if (ArrayUtils.isNotEmpty(sources)) {
                for (Source source : sources) {
                    String url = source.url();
                    Request request = new Request(url);
                    boolean isPost = source.post();
                    if (isPost) {
                        String json = source.postParams();
                        Map<String, Object> pageParams = JSONObject.parseObject(json, Map.class);
                        request.setMethod(HttpConstant.Method.POST);
                        request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
                        request.putExtra("pageParams", pageParams);
                    }

                    requestList.add(request);
                }
            }

            Spider spider = Spider.create((PageProcessor) pageProcessor).setUUID(uuid)
                    .setExecutorService(executorService).setExitWhenComplete(true)
                    .addRequest(requestList.toArray(new Request[requestList.size()]))
                    .addPipeline(ctx.getBean(DataItemDtoPipeline.class));

            if (config.useProxy()) {
                downloader.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxies()));
                spider.setDownloader(downloader);
            }

            spiderMap.put(uuid, spider);
        }
    }

    public void start() {
        init();
        if (spiderMap.isEmpty()) return;

        spiderMap.forEach((s, spider) -> spider.start());
    }

    @Override
    public void run(String... args) throws Exception {
        start();
    }
}
