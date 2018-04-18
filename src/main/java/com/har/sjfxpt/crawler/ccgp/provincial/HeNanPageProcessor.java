package com.har.sjfxpt.crawler.ccgp.provincial;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.downloader.WebDriverPoolExt;
import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;

import static com.har.sjfxpt.crawler.ccgp.provincial.HeNanPageProcessor.*;

/**
 * Created by Administrator on 2018/1/22.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.CCGPHENAN,
        useSelenium = true,
        sources = {
                @Source(url = URL1, type = "采购公告"),
                @Source(url = URL2, type = "结果公告"),
                @Source(url = URL3, type = "变更公告"),
        }
)
public class HeNanPageProcessor implements BasePageProcessor {

    final static String URL1 = "http://www.ccgp-henan.gov.cn/henan/ggcx?appCode=H60&channelCode=0101&bz=0&pageSize=20&pageNo=";
    final static String URL2 = "http://www.ccgp-henan.gov.cn/henan/ggcx?appCode=H60&channelCode=0102&bz=0&pageSize=10&pageNo=";
    final static String URL3 = "http://www.ccgp-henan.gov.cn/henan/ggcx?appCode=H60&channelCode=0103&bz=0&pageSize=20&pageNo=";

    @Autowired
    WebDriverPoolExt webDriverPoolExt;

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {

    }

    @Override
    public void handleContent(Page page) {
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        webDriverPoolExt = new WebDriverPoolExt(5);
        String url = page.getUrl().get();
        String type = page.getRequest().getExtra("type").toString();
        for (int i = 1; i <= 10; i++) {
            WebDriver driver = null;
            try {
                driver = webDriverPoolExt.get();
                driver.get(url + i);
                WebElement webElement = driver.findElement(By.cssSelector("body > div.W1000.Center.Top8 > div.W780.Right > div.BorderBlue.NoBorderTop.Padding5 > div.List2"));
                String html = webElement.getAttribute("innerHTML");
                Elements elements = Jsoup.parse(html).select(" ul > li");
                List<BidNewsOriginal> bidNewsOriginalList = parseContent(elements);
                bidNewsOriginalList.forEach(bidNewsOriginal -> dataItems.add(bidNewsOriginal));
            } catch (InterruptedException e) {
                log.error("", e);
            } finally {
                if (driver != null) {
                    webDriverPoolExt.returnToPool(driver);
                }
            }
        }
        if (!dataItems.isEmpty()) {
            dataItems.forEach(bidNewsOriginal -> bidNewsOriginal.setType(type));
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                if (!StringUtils.startsWith(href, "http:")) {
                    href = "http://www.ccgp-henan.gov.cn" + href;
                    String title = element.select("a").text();
                    String date = element.select("span").text();

                    BidNewsOriginal bidNewsOriginal = new BidNewsOriginal(href, SourceCode.CCGPHENAN);
                    bidNewsOriginal.setTitle(title);
                    bidNewsOriginal.setProvince("河南");
                    bidNewsOriginal.setDate(PageProcessorUtil.dataTxt(date));
                    if (PageProcessorUtil.timeCompare(bidNewsOriginal.getDate())) {
                        log.warn("{} is not the same day", bidNewsOriginal.getUrl());
                        continue;
                    }
                    
                    try {
                        Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                        String dateDetail = page.getHtml().getDocument().body().select("body > div.W1000.Center.Top10 > div.BorderEEE.BorderRedTop > div.TxtRight.Padding5 > span:nth-child(4)").text();
                        String conversionDate = PageProcessorUtil.dataTxt(dateDetail);
                        if (StringUtils.isNotBlank(conversionDate)) {
                            bidNewsOriginal.setDate(conversionDate);
                        }
                        String script = page.getHtml().getDocument().select("body > script:nth-child(5)").toString();
                        String[] urls = StringUtils.substringsBetween(script, "$.get(\"", "\", function");
                        if (StringUtils.isNotBlank(urls[0])) {
                            if (!StringUtils.startsWith(urls[0], "http:")) {
                                urls[0] = "http://www.ccgp-henan.gov.cn/" + urls[0];
                            }
                            Page page1 = httpClientDownloader.download(new Request(urls[0]), SiteUtil.get().setTimeOut(30000).toTask());
                            Element formatContentElement = page1.getHtml().getDocument().body();
                            String formatContent = PageProcessorUtil.formatElementsByWhitelist(formatContentElement);
                            if (StringUtils.isNotBlank(formatContent)) {
                                bidNewsOriginal.setFormatContent(formatContent);
                                dataItems.add(bidNewsOriginal);
                            }
                        }
                    } catch (Exception e) {
                        log.error("", e);
                        log.error("url={}", href);
                    }
                }
            }
        }
        return dataItems;
    }


    @Override
    public void process(Page page) {
        handleContent(page);
    }

    @Override
    public Site getSite() {
        httpClientDownloader = new HttpClientDownloader();
        return SiteUtil.get().setSleepTime(20000);
    }
}
