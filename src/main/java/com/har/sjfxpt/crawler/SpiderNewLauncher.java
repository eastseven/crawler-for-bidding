package com.har.sjfxpt.crawler;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.annotation.SourceConfigModel;
import com.har.sjfxpt.crawler.core.annotation.SourceConfigModelRepository;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.model.BidNewsSpider;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.service.ProxyService;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author dongqi
 * <p>
 * https://stackoverflow.com/questions/259140/scanning-java-annotations-at-runtime
 */
@Slf4j
@Service
@Order(Ordered.LOWEST_PRECEDENCE - 5)
public class SpiderNewLauncher implements CommandLineRunner {

    @Autowired
    ApplicationContext ctx;

    @Autowired
    ExecutorService executorService;

    @Autowired
    ProxyService proxyService;

    @Autowired
    HttpClientDownloader httpClientDownloader;

    @Autowired
    SourceConfigModelRepository sourceConfigModelRepository;

    private static final String BASE_PACKAGE = "com.har.sjfxpt.crawler";

    private Map<String, BidNewsSpider> spiders = Maps.newConcurrentMap();

    public Map<String, BidNewsSpider> getSpiders() {
        return spiders;
    }

    public void init() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(SourceConfig.class));
        for (BeanDefinition bd : scanner.findCandidateComponents(BASE_PACKAGE)) {
            String pageProcessorClassName = bd.getBeanClassName();
            Object pageProcessor = null;

            log.debug(">>> {}, {}, {}, {}", bd.getParentName(), pageProcessorClassName, bd.getDescription(), bd.getResourceDescription());

            try {
                Class cls = Class.forName(pageProcessorClassName);
                pageProcessor = ctx.getBean(cls);
            } catch (ClassNotFoundException e) {
                log.error("", e);
            }

            if (pageProcessor == null) {
                log.warn(">>> {} is null", pageProcessorClassName);
                continue;
            }

            SourceConfigModel config = SourceConfigAnnotationUtils.get(pageProcessor.getClass());
            if (config.isDisable()) {
                continue;
            }

            final String uuid = "spider_" + config.getSourceCode().name().toLowerCase();
            if (spiders.containsKey(uuid)) {
                log.warn(">>> spider uuid[{}] is exists, status is {}", uuid, spiders.get(uuid).getStatus());
                continue;
            }

            List<SourceModel> sourceModelList = config.getSources();
            if (sourceModelList.isEmpty()) continue;

            // 创建 Request 对象集合
            Request[] requests = sourceModelList.stream().map(SourceModel::createRequest).toArray(Request[]::new);
            Spider spider = BidNewsSpider.create((PageProcessor) pageProcessor).setUUID(uuid)
                    .thread(executorService, 10)
                    .setExitWhenComplete(true)
                    .addRequest(requests)
                    .addPipeline(ctx.getBean(HBasePipeline.class));

            if (config.isUseProxy()) {
                httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxies()));
                spider.setDownloader(httpClientDownloader);
            }

            BidNewsSpider bidNewsSpider = (BidNewsSpider) spider;
            bidNewsSpider.setSourceModelList(sourceModelList);
            spiders.put(uuid, bidNewsSpider);

            saveConfig(config);
        }
    }

    private void saveConfig(SourceConfigModel config) {
        sourceConfigModelRepository.save(config);
    }

    public void start() {
        if (spiders.isEmpty()) return;

        spiders.forEach((uuid, spider) -> {
            if (!spider.getStatus().equals(Spider.Status.Running)) {
                List<SourceModel> sourceModelList = spider.getSourceModelList();
                Request[] requests = sourceModelList.stream().map(SourceModel::createRequest).toArray(Request[]::new);
                spider.addRequest(requests);

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
