package com.har.sjfxpt.crawler.dongfeng;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.ProvinceUtil;
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
 * Created by Administrator on 2017/11/13.
 */
@Slf4j
@Component
public class DongFengPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        int pageCount = Integer.parseInt(StringUtils.substringBetween(url, "index_", ".jhtml"));
        if (pageCount == 1) {
            Elements elements = page.getHtml().getDocument().body().select("#main > div.listPage.wrap > div.wrap01 > div.mleft > div > div.m-bd > div > div > ul:nth-child(3) > div > div > div.pag-txt");
            int pageNum = Integer.parseInt(StringUtils.substringBetween(elements.text(), "共计", "页"));
            int cycleNum = pageNum >= 3 ? 3 : pageNum;
            for (int i = 2; i <= cycleNum; i++) {
                String urlTarget = StringUtils.replace(url, "index_1", "index_" + i);
                page.addTargetRequest(urlTarget);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        String url = page.getUrl().get();
        Elements elements = page.getHtml().getDocument().body().select("#main > div.listPage.wrap > div.wrap01 > div.mleft > div > div.m-bd > div > div > ul > li");
        List<DongFengDataItem> dataItems = parseContent(elements);
        String type = typeJudgment(url);
        dataItems.forEach(dataItem -> dataItem.setType(type));
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    public String typeJudgment(String url) {
        String typeField = StringUtils.substringBetween(url, "com/", "/index_");
        String type = null;
        switch (typeField) {
            case "zbgg":
                type = "招标公告";
                break;
            case "pbgs":
                type = "中标候选人公示";
                break;
            case "zgys":
                type = "资格预审公告";
                break;
            case "bggg":
                type = "变更公告";
                break;
        }
        return type;
    }

    @Override
    public List parseContent(Elements items) {
        List<DongFengDataItem> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                String title = element.select("a").attr("title");
                String date = element.select("a > span.bidDate").text();
                DongFengDataItem dongFengDataItem = new DongFengDataItem(href);
                dongFengDataItem.setUrl(href);
                dongFengDataItem.setTitle(title);
                dongFengDataItem.setProvince(ProvinceUtil.get(title));
                dongFengDataItem.setDate(PageProcessorUtil.dataTxt(date));

                Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                Elements timeDetail = page.getHtml().getDocument().body().select("#main > div.listPage.wrap > div.wrap01 > div.mleft > div > div > div.ninfo-title > span");
                String dateDetail = PageProcessorUtil.dataTxt(timeDetail.text());
                if (PageProcessorUtil.timeCompare(dateDetail)) {
                    log.info("{} is not the same day", href);
                } else {
                    if (StringUtils.isNotBlank(dateDetail)) {
                        dongFengDataItem.setDate(dateDetail);
                    }
                    Elements elements = page.getHtml().getDocument().body().select("#main > div.listPage.wrap > div.wrap01 > div.mleft > div > div > div.ninfo-con");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        dongFengDataItem.setFormatContent(formatContent);
                        dataItems.add(dongFengDataItem);
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
