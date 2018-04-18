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
import org.jsoup.nodes.Document;
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

import static com.har.sjfxpt.crawler.ccgp.provincial.TianJinPageProcessor.*;

/**
 * Created by Administrator on 2018/1/17.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.CCGPTIANJIN,
        useSelenium = true,
        sources = {
                @Source(url = URL1, type = "采购公告"),
                @Source(url = URL2, type = "采购公告"),
                @Source(url = URL3, type = "更正公告"),
                @Source(url = URL4, type = "更正公告"),
                @Source(url = URL5, type = "采购结果公告"),
                @Source(url = URL6, type = "采购结果公告"),
                @Source(url = URL7, type = "合同及验收公告"),
                @Source(url = URL8, type = "合同及验收公告"),
                @Source(url = URL9, type = "单一来源公示"),
                @Source(url = URL10, type = "单一来源公示"),
        }
)
public class TianJinPageProcessor implements BasePageProcessor {

    final static String URL1 = "http://www.ccgp-tianjin.gov.cn/portal/topicView.do?method=view&view=Infor&id=1665&ver=2&st=1";
    final static String URL2 = "http://www.ccgp-tianjin.gov.cn/portal/topicView.do?method=view&view=Infor&id=1664&ver=2";
    final static String URL3 = "http://www.ccgp-tianjin.gov.cn/portal/topicView.do?method=view&view=Infor&id=1663&ver=2&st=1";
    final static String URL4 = "http://www.ccgp-tianjin.gov.cn/portal/topicView.do?method=view&view=Infor&id=1666&ver=2";
    final static String URL5 = "http://www.ccgp-tianjin.gov.cn/portal/topicView.do?method=view&view=Infor&id=2014&ver=2&st=1";
    final static String URL6 = "http://www.ccgp-tianjin.gov.cn/portal/topicView.do?method=view&view=Infor&id=2013&ver=2";
    final static String URL7 = "http://www.ccgp-tianjin.gov.cn/portal/topicView.do?method=view&view=Infor&id=2015&ver=2&st=1";
    final static String URL8 = "http://www.ccgp-tianjin.gov.cn/portal/topicView.do?method=view&view=Infor&id=2016&ver=2";
    final static String URL9 = "http://www.ccgp-tianjin.gov.cn/portal/topicView.do?method=view&view=Infor&id=2033&ver=2&st=1";
    final static String URL10 = "http://www.ccgp-tianjin.gov.cn/portal/topicView.do?method=view&view=Infor&id=2034&ver=2";

    @Autowired
    WebDriverPoolExt poolExt;

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
    }

    @Override
    public void handleContent(Page page) {
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        poolExt = new WebDriverPoolExt(5);
        String type = page.getRequest().getExtra("type").toString();
        String url = page.getUrl().get();
        for (int i = 1; i <= 5; i++) {
            WebDriver driver = null;
            try {
                driver = poolExt.get();
                driver.get(url);
                driver.findElement(By.id("goNum")).sendKeys(String.valueOf(i));
                WebElement webElement = driver.findElement(By.cssSelector("#pagesColumn > div > span.countPage > span > a"));
                webElement.click();
                Thread.sleep(1000);
                WebElement webElement1 = driver.findElement(By.cssSelector("#reflshPage"));
                String html = webElement1.getAttribute("innerHTML");
                Document document = Jsoup.parse(html);
                Elements elements = document.select("ul > li");
                List<BidNewsOriginal> bidNewsOriginalList = parseContent(elements);
                bidNewsOriginalList.forEach(bidNewsOriginal -> dataItems.add(bidNewsOriginal));
            } catch (InterruptedException e) {
                log.error("", e);
            } finally {
                if (driver != null) {
                    poolExt.returnToPool(driver);
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
        List<BidNewsOriginal> bidNewsOriginalList = Lists.newArrayList();
        for (Element element : items) {

            String href = element.select("a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                String id = StringUtils.substringBetween(href, "id=", "&ver");
                String ver = StringUtils.substringAfter(href, "ver=");
                String title = element.select("a").attr("title");
                String date = element.select("span").text();
                String url = "http://www.ccgp-tianjin.gov.cn/portal/documentView.do?method=view&id=" + id + "&ver=" + ver;
                BidNewsOriginal bidNewsOriginal = new BidNewsOriginal(url, SourceCode.CCGPTIANJIN);
                bidNewsOriginal.setTitle(title);
                bidNewsOriginal.setDate(PageProcessorUtil.dataTxt(date));
                bidNewsOriginal.setProvince("天津");

                if (PageProcessorUtil.timeCompare(bidNewsOriginal.getDate())) {
                    log.warn("{} is not the same day", bidNewsOriginal.getUrl());
                    continue;
                }
                try {
                    Page page = httpClientDownloader.download(new Request(url), SiteUtil.get().setTimeOut(20000).toTask());
                    Element element1 = page.getHtml().getDocument().body();
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(element1);
                    if (StringUtils.isNotBlank(formatContent)) {
                        bidNewsOriginal.setFormatContent(formatContent);
                        bidNewsOriginalList.add(bidNewsOriginal);
                    }
                } catch (Exception e) {
                    log.warn("", e);
                    log.warn("url={}", url);
                }
            }

        }
        return bidNewsOriginalList;
    }

    @Override
    public void process(Page page) {
        handleContent(page);
    }

    @Override
    public Site getSite() {
        httpClientDownloader = new HttpClientDownloader();
        return SiteUtil.get().setSleepTime(10000);
    }
}
