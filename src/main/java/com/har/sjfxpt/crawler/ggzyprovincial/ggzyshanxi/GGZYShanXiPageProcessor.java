package com.har.sjfxpt.crawler.ggzyprovincial.ggzyshanxi;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/12/12.
 * @author luofei
 * @author dongqi
 */
@Slf4j
@Component
public class GGZYShanXiPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    final static String PAGE_PARAMS = "pageParams";

    final static String PREFIX = "http://prec.sxzwfw.gov.cn";

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int pageNum = Integer.parseInt(pageParams.get("pageIndex").toString());
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("#Page_TotalPage");
            int totalPage = Integer.parseInt(elements.attr("value").toString());
            for (int i = 2; i <= totalPage; i++) {
                Map<String, Object> nextPage = Maps.newHashMap(pageParams);
                nextPage.put("pageIndex", i);
                Request request = new Request(url);
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
                request.putExtra("pageParams", nextPage);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Elements elements = page.getHtml().getDocument().body().select("table tbody tr");
        if (elements.isEmpty()) {
            log.error("fetch error, elements is empty");
            return;
        }
        List<GGZYShanXiDataItem> dataItems = parseContent(elements);
        Object extra = page.getRequest().getExtra(PAGE_PARAMS);
        if (extra != null) {
            Map<String, Object> map = (Map<String, Object>) extra;
            String huanJie = (String) map.get("huanJie");
            if (!CollectionUtils.isEmpty(dataItems)) {
                dataItems.parallelStream().forEach(data -> {
                    if ("NOTICE".equalsIgnoreCase(huanJie)) {
                        data.setType("交易公告");
                    } else {
                        data.setType("交易结果");
                    }
                });

                dataItems.forEach(dataItem -> log.debug(">>> {}", dataItem));
                page.putField(KEY_DATA_ITEMS, dataItems);
            }
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<GGZYShanXiDataItem> dataItems = Lists.newArrayList();
        if (items.isEmpty()) {
            return dataItems;
        }

        List<String> urls = Lists.newArrayList();
        for (Element element : items) {
            Elements tds = element.select("td");
            String projectCode = tds.get(0).text();
            String title = tds.get(1).select("a").attr("title");
            String href = tds.get(1).select("a").attr("href");
            String date = tds.get(2).text();

            date = PageProcessorUtil.dataTxt(date);

            String url = PREFIX + href;
            GGZYShanXiDataItem dataItem = new GGZYShanXiDataItem(url);
            dataItem.setDate(date);
            dataItem.setTitle(title);
            dataItem.setProjectCode(projectCode);

            urls.add(url);
            try {
                Spider spider = OOSpider.create(getSite(), GGZYShanXiDataItem.class);
                GGZYShanXiDataItem _dataItem = spider.get(url);
                spider.close();
                dataItem.setProjectName(_dataItem.getProjectName());
                dataItem.setPurchaser(_dataItem.getPurchaser());
                dataItem.setFormatContent(_dataItem.getFormatContent());
            } catch (Exception e) {
                log.error("{} fetch content fail", url);
            }

            dataItems.add(dataItem);
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
