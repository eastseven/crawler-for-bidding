package com.har.sjfxpt.crawler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.annotation.SourceConfigModel;
import com.har.sjfxpt.crawler.core.annotation.SourceConfigModelRepository;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.listener.FinishSpiderListener;
import com.har.sjfxpt.crawler.core.model.BidNewsSpider;
import com.har.sjfxpt.crawler.core.model.SpiderLog;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.pipeline.MongoPipeline;
import com.har.sjfxpt.crawler.core.repository.SpiderLogRepository;
import com.har.sjfxpt.crawler.core.service.ProxyService;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
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
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.util.List;
import java.util.Map;

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
    private ApplicationContext ctx;

    @Autowired
    private ProxyService proxyService;

    @Autowired
    private HttpClientDownloader httpClientDownloader;

    @Autowired
    private SeleniumDownloader seleniumDownloader;

    @Autowired
    private SourceConfigModelRepository sourceConfigModelRepository;

    @Autowired
    private SpiderLogRepository spiderLogRepository;

    @Autowired
    private FinishSpiderListener spiderListener;

    private static final String BASE_PACKAGE = "com.har.sjfxpt.crawler";

    private Map<String, Spider> spiders = Maps.newConcurrentMap();

    public Map<String, Spider> getSpiders() {
        return spiders;
    }

    public Spider getSpider(String id) {
        return spiders.get(id);
    }

    public void init() {
        List<Class> pageProcessorList = getPageProcessorClasses();
        for (Class pageProcessor : pageProcessorList) {
            SourceConfigModel config = SourceConfigAnnotationUtils.get(pageProcessor);
            if (config.isDisable()) {
                continue;
            }

            Spider spider = create(config, pageProcessor);
            if (spider != null) {
                try {
                    spider.start();
                    SpiderMonitor.instance().register(spider);
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            saveConfig(config);
        }

    }

    private List<Class> getPageProcessorClasses() {
        List<Class> classList = Lists.newArrayList();

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(SourceConfig.class));
        for (BeanDefinition bd : scanner.findCandidateComponents(BASE_PACKAGE)) {
            String pageProcessorClassName = bd.getBeanClassName();
            Object pageProcessor = null;

            try {
                Class cls = Class.forName(pageProcessorClassName);
                classList.add(cls);
                pageProcessor = ctx.getBean(cls);
            } catch (ClassNotFoundException e) {
                log.error("", e);
                log.error(">>> {}, {}, {}, {}", bd.getParentName(), pageProcessorClassName, bd.getDescription(), bd.getResourceDescription());
            }

            if (pageProcessor == null) {
                log.warn(">>> {} is null", pageProcessorClassName);
                continue;
            }
        }

        return classList;
    }

    private void saveConfig(SourceConfigModel config) {
        sourceConfigModelRepository.save(config);
    }

    public void saveSpiderLogs() {
        if (spiders.isEmpty()) return;

        spiders.forEach((uuid, spider) -> {
            saveSpiderLog(spider);
            log.info(">>> uuid={}, status={}, startTime={}", uuid, spider.getStatus(), spider.getStartTime());
        });
    }

    public Spider create(SourceConfigModel config, Class pageProcessor) {
        List<SourceModel> sourceModelList = config.getSources();
        if (sourceModelList.isEmpty()) return null;

        // 创建 Request 对象集合
        Request[] requests = sourceModelList.stream().map(SourceModel::createRequest).toArray(Request[]::new);
        final int num = 10;
        Spider spider = BidNewsSpider.create((PageProcessor) ctx.getBean(pageProcessor))
                .thread(num)
                .setExitWhenComplete(true)
                .addRequest(requests)
                .addPipeline(ctx.getBean(MongoPipeline.class))
                .addPipeline(ctx.getBean(HBasePipeline.class));


        // 由于SeleniumDownloader 不能使用代理，所以useSelenium 属性跟useProxy属性 互斥，都为true配置无效
        if (config.isUseSelenium() && !config.isUseProxy()) {
            spider.setDownloader(seleniumDownloader);
            log.debug(">>> {} use selenium downloader", config.getSourceCode());
        } else if (config.isUseProxy() && !config.isUseSelenium()) {
            httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxies()));
            spider.setDownloader(httpClientDownloader);
        }

        String uuid = "spider_" + config.getSourceCode().name().toLowerCase() + '_' + DateTime.now().toString("yyyyMMdd_HHmmss");
        spider.setUUID(uuid);
        spider.setExitWhenComplete(true);

        spiders.put(uuid, spider);
        return spider;
    }

    public void start() {
        init();
    }

    private void saveSpiderLog(Spider spider) {
        try {
            DateTime dt = DateTime.now();
            SpiderLog spiderLog = new SpiderLog();
            spiderLog.setUuid(spider.getUUID());
            spiderLog.setStatus(spider.getStatus().toString());
            spiderLog.setCurrentTime(dt.toString());
            spiderLog.setFetchDate(dt.toString("yyyy-MM-dd HH:mm"));
            spiderLog.setPageCount(spider.getPageCount());
            spiderLog.setThreadAlive(spider.getThreadAlive());
            spiderLog.setSite(spider.getSite().toString());

            spiderLogRepository.save(spiderLog);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void run(String... args) {
    }
}
