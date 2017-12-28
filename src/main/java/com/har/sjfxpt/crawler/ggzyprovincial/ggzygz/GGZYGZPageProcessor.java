package com.har.sjfxpt.crawler.ggzyprovincial.ggzygz;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
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
import java.util.Map;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/11/29.
 */
@Slf4j
@Component
public class GGZYGZPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    final static String PAGE_PARAMS = "pageParams";

    @Override
    public void handlePaging(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int currentPage = Integer.parseInt(StringUtils.substringBetween(page.getUrl().toString(), "/queryContent_", ".jspx"));
        if (currentPage == 1) {
            Elements elements = page.getHtml().getDocument().body().select("body > div.main > div.news_box > div.pagesite > div > ul > li:nth-child(1) > a");
            int pageNum = Integer.parseInt(StringUtils.substringBetween(elements.text(), "/", "页"));
            if (pageNum >= 2) {
                for (int i = 2; i <= pageNum; i++) {
                    String url = page.getUrl().toString().replace("queryContent_1", "queryContent_" + i);
                    Request request = new Request(url);
                    request.putExtra(PAGE_PARAMS, pageParams);
                    page.addTargetRequest(request);
                }
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get(PAGE_PARAMS);
        Elements elements = page.getHtml().getDocument().body().select("#news_list1 > li");
        List<GGZYGZDataItem> dataItems = parseContent(elements);
        String businessType = pageParams.get("businessType");
        String type = pageParams.get("type");
        dataItems.forEach(dataItem -> dataItem.setBusinessType(businessType));
        dataItems.forEach(dataItem -> dataItem.setType(type));
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<GGZYGZDataItem> GGZYGZDataItems = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("a").attr("href");
            String title = element.select("a").attr("title");
            if (StringUtils.isNotBlank(href)) {
                GGZYGZDataItem GGZYGZDataItem = new GGZYGZDataItem(href);
                GGZYGZDataItem.setUrl(href);
                GGZYGZDataItem.setTitle(title);

                Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                String dataDetail = page.getHtml().getDocument().body().select("body > div.main > div.content_box > div.infos > span:nth-child(2)").text();
                GGZYGZDataItem.setDate(PageProcessorUtil.dataTxt(dataDetail));
                String source = page.getHtml().getDocument().body().select("body > div.main > div.content_box > div.infos > span:nth-child(3)").text();
                if (source.contains("来源平台：")) {
                    source = StringUtils.remove(source, "来源平台：");
                }
                GGZYGZDataItem.setSource(source);
                Elements elements = page.getHtml().getDocument().body().select("body > div.main > div.content_box");
                String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                if (StringUtils.isNotBlank(formatContent)) {
                    GGZYGZDataItem.setFormatContent(formatContent);
                    GGZYGZDataItems.add(GGZYGZDataItem);
                }
            }
        }
        return GGZYGZDataItems;
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
