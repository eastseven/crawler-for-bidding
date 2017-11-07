package com.har.sjfxpt.crawler.ggzy.config;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author dongqi
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.industry")
public class KeyWordsProperties {

    List<String> categories = Lists.newArrayList();
}
