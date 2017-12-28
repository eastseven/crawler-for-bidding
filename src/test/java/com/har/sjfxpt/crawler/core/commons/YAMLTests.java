package com.har.sjfxpt.crawler.core.commons;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author dongqi
 *
 * YAML 语法
 *
 * 参考文章
 *   http://www.ruanyifeng.com/blog/2016/07/yaml.html
 *   https://dzone.com/articles/read-yaml-in-java-with-jackson
 */
@Slf4j
public class YAMLTests {

    @Test
    public void test() throws Exception {
        Path path = Paths.get("src/main/resources", "source.yml");
        Yaml yml = new Yaml();
        Object object = yml.load(new FileInputStream(path.toFile()));
        log.info(">>> {}, {}", object.getClass(), object);
    }
}
