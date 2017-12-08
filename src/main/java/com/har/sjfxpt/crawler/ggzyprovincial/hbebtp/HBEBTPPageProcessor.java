package com.har.sjfxpt.crawler.ggzyprovincial.hbebtp;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/12/6.
 */
@Slf4j
@Component
public class HBEBTPPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        if (!StringUtils.containsIgnoreCase(url, "?Paging")) {
            Elements elements = page.getHtml().getDocument().body().select("#Paging > div > div > table > tbody > tr");
            String pageCount = StringUtils.substringBetween(elements.text(), "/", " ");
            if (StringUtils.isNotBlank(pageCount)) {
                int pageNum = Integer.parseInt(pageCount);
                if (pageNum >= 2) {
                    if (pageNum >= 5) {
                        for (int i = 2; i <= 5; i++) {
                            String urlTarget = url + "?Paging=" + i;
                            page.addTargetRequest(urlTarget);
                        }
                    } else {
                        for (int i = 2; i <= pageNum; i++) {
                            String urlTarget = url + "?Paging=" + i;
                            page.addTargetRequest(urlTarget);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        String url = page.getUrl().get();
        Elements elements = page.getHtml().getDocument().body().select("#right > ul >li");
        List<HBEBTPDataItem> dataItems = parseContent(elements);
        String type = typeJudgment(url);
        dataItems.forEach(dataItem -> dataItem.setType(type));
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    public String typeJudgment(String url) {
        String typeFiled = StringUtils.substringAfter(url, "jyxx/");
        String typeFiledReal = StringUtils.substringBetween(typeFiled, "/", "/");
        String type = null;
        if (typeFiledReal.startsWith("002001")) {
            type = "招标项目";
        }
        if (typeFiledReal.startsWith("002002")) {
            type = "招标公告";
        }
        if (typeFiledReal.startsWith("002003")) {
            type = "澄清修改文件";
        }
        if (typeFiledReal.startsWith("002004")) {
            type = "中标候选人公示";
        }
        if (typeFiledReal.startsWith("002005")) {
            type = "中标结果公告";
        }
        return type;
    }

    @Override
    public List parseContent(Elements items) {
        List<HBEBTPDataItem> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                String title = element.select("a").attr("title");
                String date = element.select("span").text();
                if (!StringUtils.startsWith(href, "http:")) {
                    href = "http://www.hbbidcloud.com" + href;
                }
                HBEBTPDataItem hbebtpDataItem = new HBEBTPDataItem(href);
                hbebtpDataItem.setUrl(href);
                hbebtpDataItem.setTitle(title);
                hbebtpDataItem.setDate(PageProcessorUtil.dataTxt(date));
                if (PageProcessorUtil.timeCompare(hbebtpDataItem.getDate())) {
                    log.warn("{} is not the same day", hbebtpDataItem.getUrl());
                } else {
                    Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    try {
                        Elements elements = page.getHtml().getDocument().body().select("#tblInfo");
                        String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                        if (StringUtils.contains(formatContent, "阅读次数：")) {
                            formatContent = StringUtils.remove(formatContent, StringUtils.substringBetween(formatContent, "<h4>", "</h4>"));
                        }
                        if (StringUtils.isNotBlank(formatContent)) {
                            hbebtpDataItem.setFormatContent(formatContent);
                            dataItems.add(hbebtpDataItem);
                        }
                    } catch (Exception e) {
                        log.info("href=={}", href);
                    }
                }
            }
        }
        return dataItems;
    }

    @Override
    public void process(Page page) {
        handlePaging(page);
        handleContent(page);
    }

    @Override
    public Site getSite() {
        httpClientDownloader = new HttpClientDownloader();
        return SiteUtil.get().setSleepTime(10000);
    }
}
