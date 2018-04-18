package com.har.sjfxpt.crawler.core.province;

import com.har.sjfxpt.crawler.ccgp.provincial.HeNanPageProcessor;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.downloader.WebDriverPoolExt;
import com.har.sjfxpt.crawler.core.pipeline.MongoPipeline;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2018/1/22.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class HeNanPageProcessorTests {

    @Autowired
    HeNanPageProcessor heNanPageProcessor;

    @Autowired
    MongoPipeline mongoPipeline;

    @Test
    public void testHeNanPageProcessor() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(heNanPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest).collect(Collectors.toList());
        Spider.create(heNanPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(mongoPipeline)
                .thread(8)
                .run();
    }

    @Test
    public void testDownloadPage() {
        WebDriverPoolExt webDriverPoolExt = new WebDriverPoolExt(1);
        String url = "http://www.ccgp-henan.gov.cn/henan/content?infoId=1516329417192771&channelCode=H610101&bz=0";
        try {
            WebDriver driver = webDriverPoolExt.get();
            driver.get(url);
            Thread.sleep(1000);
            WebElement webElement = driver.findElement(By.cssSelector("body > div.W1000.Center.Top10 > div.BorderEEE.BorderRedTop"));
            String html = webElement.getAttribute("innerHTML");
            log.debug("html={}", html);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJsoupContent() {
        String url = "http://www.ccgp-henan.gov.cn/henan/content?infoId=1516329417192771&channelCode=H610101&bz=0";
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Page page = httpClientDownloader.download(new Request(url), SiteUtil.get().setTimeOut(30000).toTask());
        String script = page.getHtml().getDocument().select("body > script:nth-child(5)").toString();
        String dateDetail = page.getHtml().getDocument().body().select("body > div.W1000.Center.Top10 > div.BorderEEE.BorderRedTop > div.TxtRight.Padding5 > span:nth-child(4)").text();
        log.debug("dateDetail={}", PageProcessorUtil.dataTxt(dateDetail));
        log.debug("script={}", script);
        String[] urls = StringUtils.substringsBetween(script, "$.get(\"", "\", function");
        if (StringUtils.isNotBlank(urls[0])) {
            if (!StringUtils.startsWith(urls[0], "http:")) {
                urls[0] = "http://www.ccgp-henan.gov.cn/" + urls[0];
            }
            Page page1 = httpClientDownloader.download(new Request(urls[0]), SiteUtil.get().setTimeOut(30000).toTask());
            Element element = page1.getHtml().getDocument().body();
            String formatContent = PageProcessorUtil.formatElementsByWhitelist(element);
            log.debug("formatContent={}", formatContent);
        }
    }


}
