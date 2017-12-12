package com.har.sjfxpt.crawler.ggzyprovincial.ggzyyn;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/11/30.
 *
 * @author luo fei
 */
@Slf4j
@Component
public class GGZYYNPageProcessor implements BasePageProcessor {

    final static String PAGE_PARAMS = "pageParams";

    final int limit = 10;
    final int secondPage = 2;

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
            totalPage = totalPage >= limit ? limit : totalPage;
            for (int i = secondPage; i <= totalPage; i++) {
                String url = page.getUrl().toString().replace("currentPage=1", "currentPage=" + i);
                Request request = new Request(url);
                request.putExtra(PAGE_PARAMS, pageParams);
                page.addTargetRequest(request);
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
        dataItems.forEach(dataItem -> {
            dataItem.setType(type);
            dataItem.setBusinessType(businessType);
        });
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
                GGZYYNDataItem yuNanDataItem = new GGZYYNDataItem(url);
                yuNanDataItem.setUrl(url);
                String field2 = element.select("td:nth-child(2)").text();
                String field3 = element.select("td:nth-child(3)").text();
                String field4 = element.select("td:nth-child(4)").text();
                String field5 = element.select("td:nth-child(5)").text();
                String field6 = element.select("td:nth-child(6)").text();
                if ("招标公告".equals(type) || "更正事项".equals(type) || "采购公告".equals(type)) {
                    yuNanDataItem.setAnnouncementId(field2);
                    yuNanDataItem.setTitle(field3);
                    if ("更正事项".equals(type) && "政府采购".equals(businessType)) {
                        String date = DateTime.parse(field4, DateTimeFormat.forPattern("yyyyMMddHH")).toString("yyyy-MM-dd HH:mm");
                        yuNanDataItem.setDate(date);
                    } else {
                        yuNanDataItem.setDate(PageProcessorUtil.dataTxt(field4));
                    }
                    yuNanDataItem.setCloseTime(field5);
                    yuNanDataItem.setStatus(field6);
                }
                if ("评标报告".equals(type) || "开标记录".equals(type)) {

                    yuNanDataItem.setAnnouncementId(field2);
                    yuNanDataItem.setTitle(field3);
                    if ("评标报告".equals(type)) {
                        DateTime dateTime = DateTime.parse(field4, DateTimeFormat.forPattern("yyyyMMddHHmmss"));
                        if (StringUtils.isNotBlank(dateTime.toString())) {
                            yuNanDataItem.setDate(dateTime.toString("yyyy-MM-dd HH:mm"));
                        }
                    } else {
                        yuNanDataItem.setDate(PageProcessorUtil.dataTxt(field4));
                    }

                }
                if ("中标结果公告".equals(type) || "中标结果".equals(type)) {
                    yuNanDataItem.setTitle(field2);
                    yuNanDataItem.setDate(PageProcessorUtil.dataTxt(field3));
                }

                if ("招标异常".equals(type) || "异常公告".equals(type)) {
                    yuNanDataItem.setAnnouncementId(field2);
                    yuNanDataItem.setTitle(field3);
                    yuNanDataItem.setStatus(field4);
                    yuNanDataItem.setDate(PageProcessorUtil.dataTxt(field5));
                }

                //正文处理
                try {
                    Page page = httpClientDownloader.download(new Request(url), SiteUtil.get().setTimeOut(30000).toTask());
                    Elements contentElements = page.getHtml().getDocument().body().select("body > div.w1200s > div > div.detail_contect > div.con");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(contentElements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        //只抓当天的
                        if (PageProcessorUtil.timeCompare(yuNanDataItem.getDate())) {
                            log.warn("{} is not the same day", yuNanDataItem.getUrl());
                        } else {
                            try {
                                if (PageProcessorUtil.timeDetailCompare(yuNanDataItem.getDate())) {
                                    yuNanDataItem.setDate(DateTime.now().toString("yyyy-MM-dd HH:mm"));
                                }
                            } catch (ParseException e) {
                                log.error("", e);
                            }
                            yuNanDataItem.setFormatContent(formatContent);
                            dataItems.add(yuNanDataItem);
                        }

                        // 招标公告字段提取
                        for (Element td : Jsoup.parse(formatContent).select("td")) {
                            String tdText = td.text();
                            if (tdText.equalsIgnoreCase("本次发包估价：")) {
                                //TODO 存在一个公告内有多个标段的情况，每个标段都有 本次发包估价 字段，目前没有处理这种情况
                                String budget = td.nextElementSibling().text();
                                yuNanDataItem.setBudget(StringUtils.strip(budget));
                            }

                            if (tdText.equalsIgnoreCase("建设单位：")) {
                                String purchaser = td.nextElementSibling().text();
                                yuNanDataItem.setPurchaser(StringUtils.strip(purchaser));
                            }

                            if (tdText.equalsIgnoreCase("招标代理机构：")) {
                                String purchaserAgent = td.nextElementSibling().text();
                                yuNanDataItem.setPurchaserAgent(StringUtils.strip(purchaserAgent));
                            }
                        }
                    }

                    Elements html = page.getHtml().getDocument()
                            .select("div.w1200s").select("div.detail_contect").select("div.con");
                    if (!html.isEmpty()) {
                        for (Element td : html.select("table tr td")) {
                            String tdText = td.text();
                            if (tdText.equalsIgnoreCase("中标人：")) {
                                String bidCompanyName = td.nextElementSibling().text();
                                yuNanDataItem.setBidCompanyName(StringUtils.strip(bidCompanyName));
                            }

                            if (tdText.equalsIgnoreCase("中标价：")) {
                                log.debug("td.nextElementSibling {}", td.nextElementSibling());
                                log.debug("td {}", td);
                                String totalBidMoney = td.nextElementSibling().text();
                                yuNanDataItem.setTotalBidMoney(StringUtils.strip(totalBidMoney));
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("", e);
                    log.error("url {}", url);
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
