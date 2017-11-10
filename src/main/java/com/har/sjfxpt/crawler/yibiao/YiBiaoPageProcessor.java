package com.har.sjfxpt.crawler.yibiao;

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
import java.util.regex.Pattern;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/11/9.
 */
@Slf4j
@Component
public class YiBiaoPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    final static int ARTICLE_NUM = 50;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().toString();
        int num = Integer.parseInt(StringUtils.substringBetween(url, "hidPape=", "&keyword="));
        if (num == 1) {
            Element html = page.getHtml().getDocument().body();
            String jsScript = html.select("body > script").html();
            int announcementNum = Integer.parseInt(StringUtils.substringBetween(jsScript, "totalrecords: ", ","));
            log.debug("announcementNum=={}", announcementNum);
            int pagaNum = announcementNum % ARTICLE_NUM == 0 ? announcementNum / ARTICLE_NUM : announcementNum / ARTICLE_NUM + 1;
            log.debug("pagaNum=={}", pagaNum);
            for (int i = 2; i <= pagaNum; i++) {
                String urlTarget = StringUtils.replace(url, "hidPape=1", "hidPape=" + i);
                page.addTargetRequest(urlTarget);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Elements elements = page.getHtml().getDocument().body().select("body > dl");

        if (elements.isEmpty()) {
            log.error("fetch error, elements is empty");
            return;
        }
        List<YiBiaoDataItem> dataItems = parseContent(elements);
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<YiBiaoDataItem> dataItems = Lists.newArrayList();
        for (Element a : items) {
            String href = a.select("dt > a").attr("href");
            String date = a.select("dt > span").text();
            String title = a.select("dt > a").text();
            String province = a.select("dd > span.am-u-lg-4 > a").text();
            String industry = a.select("dd > span.am-u-lg-7 > a").text();
            String type = StringUtils.substringBetween(title, "[", "]");
            href = "http://www.1-biao.com/data/" + href;
            YiBiaoDataItem yiBiaoDataItem = new YiBiaoDataItem(href);
            yiBiaoDataItem.setUrl(href);
            yiBiaoDataItem.setDate(PageProcessorUtil.dataTxt(date));

            if (PageProcessorUtil.timeCompare(yiBiaoDataItem.getDate())) {
                log.warn("{} is not on the same day", href);
            } else {
                yiBiaoDataItem.setIndustry(industry);
                yiBiaoDataItem.setTitle(title);
                yiBiaoDataItem.setProvince(ProvinceUtil.get(province));
                yiBiaoDataItem.setType(type);

                Request request = new Request(href);
                Page page = httpClientDownloader.download(request, SiteUtil.get().toTask());
                try {
                    Elements elements = page.getHtml().getDocument().body().select("body > div.g-doc > div.g-bd > div.g-lit-mn.f-fl");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent) && !StringUtils.containsIgnoreCase(formatContent, "&lt;")&& !StringUtils.containsIgnoreCase(formatContent, "&gt;")) {
                        yiBiaoDataItem.setFormatContent(formatContent);
                        dataItems.add(yiBiaoDataItem);
                    }else {
                        log.warn("{} is wrong page",href);
                    }
                } catch (Exception e) {
                    log.warn("{} Download failed", href);
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
        return SiteUtil.get();
    }
}