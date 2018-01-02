package com.har.sjfxpt.crawler;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.model.BidNewsSpider;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
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
import java.util.stream.Collectors;

/**
 * @author dongqi
 * <p>
 * https://stackoverflow.com/questions/259140/scanning-java-annotations-at-runtime
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
    HttpClientDownloader httpClientDownloader;

    private static final String basePackage = "com.har.sjfxpt.crawler";

    private Map<String, BidNewsSpider> spiders = Maps.newConcurrentMap();

    public Map<String, BidNewsSpider> getSpiders() {
        return spiders;
    }

    public void init() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(SourceConfig.class));
        for (BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
            String pageProcessorClassName = bd.getBeanClassName();
            Object pageProcessor = null;

            try {
                pageProcessor = ctx.getBean(Class.forName(pageProcessorClassName));
            } catch (ClassNotFoundException e) {
                log.error("", e);
            }

            SourceConfig config = AnnotationUtils.findAnnotation(pageProcessor.getClass(), SourceConfig.class);
            if (config.disable()) {
                continue;
            }

            final String uuid = "spider_" + config.code().name().toLowerCase();
            if (spiders.containsKey(uuid)) {
                log.warn(">>> spider uuid[{}] is exists, status is {}", uuid, spiders.get(uuid).getStatus());
                continue;
            }

            // 创建 Request 对象集合
            List<Request> requestList = Lists.newArrayList();
            List<SourceModel> sourceModelList = Lists.newArrayList();
            Source[] sources = config.sources();
            if (ArrayUtils.isNotEmpty(sources)) {
                DateTime now = DateTime.now();
                for (Source source : sources) {
                    SourceModel sourceModel = new SourceModel();

                    String url = source.url();
                    sourceModel.setUrl(url);
                    sourceModel.setPost(source.post());

                    Request request = new Request(url);
                    if (source.post() && StringUtils.isNotBlank(source.postParams())) {
                        String json = source.postParams();
                        Map<String, Object> pageParams = JSONObject.parseObject(json, Map.class);

                        if (ArrayUtils.isNotEmpty(source.needPlaceholderFields())) {
                            for (String field : source.needPlaceholderFields()) {
                                pageParams.put(field, now.toString(source.dayPattern()));
                            }
                        }

                        request.setMethod(HttpConstant.Method.POST);
                        request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
                        request.putExtra("pageParams", pageParams);

                        sourceModel.setPostParams(pageParams);
                    }
                    sourceModel.setNeedPlaceholderFields(source.needPlaceholderFields());

                    requestList.add(request);
                    sourceModelList.add(sourceModel);
                }
            }

            Request[] requests = requestList.toArray(new Request[requestList.size()]);
            Spider spider = BidNewsSpider.create((PageProcessor) pageProcessor).setUUID(uuid)
                    .thread(executorService, 10)
                    .setExitWhenComplete(true)
                    .addRequest(requests)
                    .addPipeline(ctx.getBean(HBasePipeline.class));

            if (config.useProxy()) {
                httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxies()));
                spider.setDownloader(httpClientDownloader);
            }

            BidNewsSpider bidNewsSpider = (BidNewsSpider) spider;
            bidNewsSpider.setSourceModelList(sourceModelList);
            spiders.put(uuid, bidNewsSpider);

        }
    }

    public void start() {
        if (spiders.isEmpty()) return;

        spiders.forEach((uuid, spider) -> {
            if (!spider.getStatus().equals(Spider.Status.Running)) {
                List<SourceModel> sourceModelList = spider.getSourceModelList();
                List<Request> requestList = sourceModelList.stream().map(source -> {
                    Request request = new Request(source.getUrl());
                    Map<String, Object> pageParams = source.getPostParams();
                    if (!pageParams.isEmpty()) {
                        if (ArrayUtils.isNotEmpty(source.getNeedPlaceholderFields())) {
                            for (String field : source.getNeedPlaceholderFields()) {
                                source.getPostParams().put(field, DateTime.now().toString(source.getDayPattern()));
                            }
                        }

                        request.setMethod(HttpConstant.Method.POST);
                        request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
                        request.putExtra("pageParams", pageParams);
                    }
                    return request;
                }).collect(Collectors.toList());

                spider.addRequest(requestList.toArray(new Request[requestList.size()]));
                spider.start();
            }
            log.info(">>> uuid={}, status={}, startTime={}", uuid, spider.getStatus(), spider.getStartTime());
        });
    }

    @Override
    public void run(String... args) {
        init();
    }
}
