package com.har.sjfxpt.crawler.ggzyprovincial.ggzyyn;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/11/30.
 */
@Slf4j
@Component
public class GGZYYNPageProcessor implements BasePageProcessor {

    final static String PAGE_PARAMS = "pageParams";

    @Autowired
    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get("pageParams");
        log.debug("type=={},bussinessType=={}", pageParams.get("type"), pageParams.get("businessType"));
        int currentPage = Integer.parseInt(StringUtils.substringBetween(page.getUrl().toString(), "currentPage=", "&area"));
        if (currentPage == 1) {
            Elements elements = page.getHtml().getDocument().body().select("div.biaogie > div > div > a:nth-child(7)");
            int totalPage = Integer.parseInt(elements.text());
            if (totalPage >= 20) {
                for (int i = 2; i <= 20; i++) {
                    String url = page.getUrl().toString().replace("currentPage=1", "currentPage=" + i);
                    Request request = new Request(url);
                    request.putExtra(PAGE_PARAMS, pageParams);
                    page.addTargetRequest(request);
                }
            } else {
                for (int i = 2; i <= totalPage; i++) {
                    String url = page.getUrl().toString().replace("currentPage=1", "currentPage=" + i);
                    Request request = new Request(url);
                    request.putExtra(PAGE_PARAMS, pageParams);
                    page.addTargetRequest(request);
                }
            }

        }
    }

    @Override
    public void handleContent(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get("pageParams");
        String type = pageParams.get("type");
        String businessType = pageParams.get("businessType");
        Elements elements = page.getHtml().getDocument().body().select("#data_tab > tbody > tr");
        List<GGZYYNDataItem> dataItems = parseContent(elements, pageParams);
        dataItems.forEach(dataItem -> dataItem.setType(type));
        dataItems.forEach(dataItem -> dataItem.setBusinessType(businessType));
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        return null;
    }

    public List parseContent(Elements items, Map<String, String> pageParams) {
        String type = pageParams.get("type");
        String businessType = pageParams.get("businessType");
        List<GGZYYNDataItem> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                String url = "https://www.ynggzyxx.gov.cn" + href;
                GGZYYNDataItem GGZYYNDataItem = new GGZYYNDataItem(url);
                GGZYYNDataItem.setUrl(url);
                String field2 = element.select("td:nth-child(2)").text();
                String field3 = element.select("td:nth-child(3)").text();
                String field4 = element.select("td:nth-child(4)").text();
                String field5 = element.select("td:nth-child(5)").text();
                String field6 = element.select("td:nth-child(6)").text();
                if (type.equals("招标公告") || type.equals("更正事项") || type.equals("采购公告")) {
                    GGZYYNDataItem.setAnnouncementId(field2);
                    GGZYYNDataItem.setTitle(field3);
                    if (type.equals("更正事项") && businessType.equals("政府采购")) {
                        try {
                            GGZYYNDataItem.setDate(new DateTime(new SimpleDateFormat("yyyyMMddHH").parse(field4)).toString("yyyy-MM-dd HH:mm"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        GGZYYNDataItem.setDate(PageProcessorUtil.dataTxt(field4));
                    }
                    GGZYYNDataItem.setCloseTime(field5);
                    GGZYYNDataItem.setStatus(field6);
                }
                if (type.equals("评标报告") || type.equals("开标记录")) {

                    GGZYYNDataItem.setAnnouncementId(field2);
                    GGZYYNDataItem.setTitle(field3);
                    if (type.equals("评标报告")) {
                        DateTime dateTime = null;
                        try {
                            dateTime = new DateTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(field4));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (StringUtils.isNotBlank(dateTime.toString())) {
                            GGZYYNDataItem.setDate(dateTime.toString("yyyy-MM-dd HH:mm"));
                        }
                    } else {
                        GGZYYNDataItem.setDate(PageProcessorUtil.dataTxt(field4));
                    }

                }
                if (type.equals("中标结果公告") || type.equals("中标结果")) {
                    GGZYYNDataItem.setTitle(field2);
                    GGZYYNDataItem.setDate(PageProcessorUtil.dataTxt(field3));
                }
                if (type.equals("招标异常") || type.equals("异常公告")) {
                    GGZYYNDataItem.setAnnouncementId(field2);
                    GGZYYNDataItem.setTitle(field3);
                    GGZYYNDataItem.setStatus(field4);
                    GGZYYNDataItem.setDate(PageProcessorUtil.dataTxt(field5));
                }
                Page page = httpClientDownloader.download(new Request(url), SiteUtil.get().setTimeOut(30000).toTask());
                Elements contentElements = page.getHtml().getDocument().body().select("body > div.w1200s > div");
                String formatContent = PageProcessorUtil.formatElementsByWhitelist(contentElements.first());
                if (StringUtils.isNotBlank(formatContent)) {
                    if (PageProcessorUtil.timeCompare(GGZYYNDataItem.getDate())) {
                        log.warn("{} is not the same day", GGZYYNDataItem.getUrl());
                    } else {
                        GGZYYNDataItem.setFormatContent(formatContent);
                        dataItems.add(GGZYYNDataItem);
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
        return SiteUtil.get().setSleepTime(10000);
    }
}
