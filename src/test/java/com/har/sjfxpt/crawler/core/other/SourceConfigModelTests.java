package com.har.sjfxpt.crawler.core.other;

import com.alibaba.fastjson.JSON;
import com.har.sjfxpt.crawler.core.SpiderApplicationTests;
import com.har.sjfxpt.crawler.core.annotation.SourceConfigModel;
import com.har.sjfxpt.crawler.core.annotation.SourceConfigModelRepository;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.other.JinCaiWangPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class SourceConfigModelTests extends SpiderApplicationTests {

    @Autowired
    SourceConfigModelRepository repository;

    @Test
    public void test() {
        SourceConfigModel configModel = SourceConfigAnnotationUtils.get(JinCaiWangPageProcessor.class);
        repository.save(configModel);

        List<SourceConfigModel> list = repository.findAll();
        Assert.assertTrue(CollectionUtils.isNotEmpty(list));
        list.forEach(sourceConfigModel -> log.info(">>> \n{}", JSON.toJSONString(sourceConfigModel, true)));
    }
}
