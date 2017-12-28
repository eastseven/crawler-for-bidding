package com.har.sjfxpt.crawler.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * @author dongqi
 */
@Slf4j
@Configuration
@EnableAsync
@EnableScheduling
public class AppConfig implements AsyncConfigurer {

    final int size = Runtime.getRuntime().availableProcessors() * 4;

    @Bean
    ExecutorService executorService() {
        final int max = size * 2;
        final int cap = max * 2;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(size);
        executor.setMaxPoolSize(max);
        executor.setQueueCapacity(cap);
        executor.setThreadNamePrefix("spider-");
        executor.initialize();
        log.info("ThreadPoolTaskExecutor param >>> core {}, max {}, cap {}", size, max, cap);
        return executor.getThreadPoolExecutor();
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(size);
        taskScheduler.setThreadNamePrefix("spider-task-");
        return taskScheduler;
    }

    @Override
    public Executor getAsyncExecutor() {
        return executorService();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }

    @Bean
    HttpClientDownloader downloader() {
        return new HttpClientDownloader();
    }
}
