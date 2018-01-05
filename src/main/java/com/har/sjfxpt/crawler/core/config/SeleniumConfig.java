package com.har.sjfxpt.crawler.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;

/**
 * @author dongqi
 */
@Slf4j
@Configuration
public class SeleniumConfig implements CommandLineRunner {

    @Bean
    SeleniumDownloader seleniumDownloader() {
        return new SeleniumDownloader();
    }

    @Override
    public void run(String... args) {
        System.setProperty("selenuim_config", "config.ini");
    }
}
