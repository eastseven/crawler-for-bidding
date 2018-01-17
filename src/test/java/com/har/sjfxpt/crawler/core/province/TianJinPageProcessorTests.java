package com.har.sjfxpt.crawler.core.province;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ccgp.provincial.TianJinPageProcessor;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.config.SeleniumConfig;
import com.har.sjfxpt.crawler.core.downloader.WebDriverPoolExt;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2018/1/17.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TianJinPageProcessorTests {

    @Autowired
    TianJinPageProcessor tianJinPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    @Test
    public void testTianJinPageProcessor() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(tianJinPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(tianJinPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(hBasePipeline)
                .thread(8)
                .run();
    }

    @Autowired
    WebDriverPoolExt pool;

    @Test
    public void testSelenium() {
        pool = new WebDriverPoolExt(5);
        for (int i = 1; i <= 5; i++) {
            WebDriver driver = null;
            try {
                driver = pool.get();
                log.debug("i={}", i);
                driver.get("http://www.ccgp-tianjin.gov.cn/portal/topicView.do?method=view&view=Infor&id=1664&ver=2&stmp=1516158386884");
                driver.findElement(By.id("goNum")).sendKeys(String.valueOf(i));
                WebElement webElement = driver.findElement(By.cssSelector("#pagesColumn > div > span.countPage > span > a"));
                webElement.click();
                Thread.sleep(1000);
                WebElement webElement1 = driver.findElement(By.cssSelector("#reflshPage"));
                String html = webElement1.getAttribute("innerHTML");
                Document document = Jsoup.parse(html);
                Elements elements = document.select("ul > li");
                for (Element element : elements) {
                    log.debug("html={}", element.html());
                }
            } catch (InterruptedException e) {
                log.error("", e);
            } finally {
                if (driver != null) {
                    pool.returnToPool(driver);
                }
            }
        }
    }
}
