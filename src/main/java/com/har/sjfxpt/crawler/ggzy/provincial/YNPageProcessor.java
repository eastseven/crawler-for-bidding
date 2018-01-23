package com.har.sjfxpt.crawler.ggzy.provincial;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.text.ParseException;
import java.util.List;

import static com.har.sjfxpt.crawler.ggzy.provincial.YNPageProcessor.*;

/**
 * Created by Administrator on 2017/11/30.
 *
 * @author luo fei
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZYYN,
        sources = {
                @Source(url = GGZYYN_URL1, type = "招标公告,工程建设"),
                @Source(url = GGZYYN_URL2, type = "更正事项,工程建设"),
                @Source(url = GGZYYN_URL3, type = "评标报告,工程建设"),
                @Source(url = GGZYYN_URL4, type = "中标结果公告,工程建设"),
                @Source(url = GGZYYN_URL5, type = "招标异常,工程建设"),
                @Source(url = GGZYYN_URL6, type = "采购公告,政府采购"),
                @Source(url = GGZYYN_URL7, type = "更正事项,政府采购"),
                @Source(url = GGZYYN_URL8, type = "开标记录,政府采购"),
                @Source(url = GGZYYN_URL9, type = "中标结果,政府采购"),
                @Source(url = GGZYYN_URL10, type = "异常公告,政府采购"),
        }
)
public class YNPageProcessor implements BasePageProcessor {

    final static String GGZYYN_URL1 = "https://www.ynggzyxx.gov.cn/jyxx/jsgcZbgg?currentPage=1&area=000&industriesTypeCode=0&scrollValue=777";
    final static String GGZYYN_URL2 = "https://www.ynggzyxx.gov.cn/jyxx/jsgcGzsx?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0";
    final static String GGZYYN_URL3 = "https://www.ynggzyxx.gov.cn/jyxx/jsgcpbjggs?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0";
    final static String GGZYYN_URL4 = "https://www.ynggzyxx.gov.cn/jyxx/jsgcZbjggs?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0";
    final static String GGZYYN_URL5 = "https://www.ynggzyxx.gov.cn/jyxx/jsgcZbyc?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0";
    final static String GGZYYN_URL6 = "https://www.ynggzyxx.gov.cn/jyxx/zfcg/cggg?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0";
    final static String GGZYYN_URL7 = "https://www.ynggzyxx.gov.cn/jyxx/zfcg/gzsx?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0";
    final static String GGZYYN_URL8 = "https://www.ynggzyxx.gov.cn/jyxx/zfcg/kbjl?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0";
    final static String GGZYYN_URL9 = "https://www.ynggzyxx.gov.cn/jyxx/zfcg/zbjggs?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0";
    final static String GGZYYN_URL10 = "https://www.ynggzyxx.gov.cn/jyxx/zfcg/zfcgYcgg?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0";


    final int limit = 10;
    final int secondPage = 2;

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String type = page.getRequest().getExtra("type").toString();
        int currentPage = Integer.parseInt(StringUtils.substringBetween(page.getUrl().toString(), "currentPage=", "&area"));
        if (currentPage == 1) {
            Elements elements = page.getHtml().getDocument().body().select("div.biaogie > div > div > a:nth-child(7)");
            int totalPage = Integer.parseInt(elements.text());
            totalPage = totalPage >= limit ? limit : totalPage;
            for (int i = secondPage; i <= totalPage; i++) {
                String url = page.getUrl().toString().replace("currentPage=1", "currentPage=" + i);
                Request request = new Request(url);
                request.putExtra("type", type);
                page.addTargetRequest(request);
            }

        }
    }

    @Override
    public void handleContent(Page page) {
        String type = page.getRequest().getExtra("type").toString();
        Elements elements = page.getHtml().getDocument().body().select("#data_tab > tbody > tr");
        List<BidNewsOriginal> dataItems = parseContent(elements, type);
        String typeField = StringUtils.substringBefore(type, ",");
        dataItems.forEach(dataItem -> dataItem.setType(typeField));
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

    public List parseContent(Elements items, String typeField) {
        String type = StringUtils.substringBefore(typeField, ",");
        String businessType = StringUtils.substringAfter(typeField, ",");
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                String url = "https://www.ynggzyxx.gov.cn" + href;
                BidNewsOriginal yuNanDataItem = new BidNewsOriginal(url, SourceCode.GGZYYN);
                yuNanDataItem.setProvince("云南");
                String field2 = element.select("td:nth-child(2)").text();
                String field3 = element.select("td:nth-child(3)").text();
                String field4 = element.select("td:nth-child(4)").text();
                String field5 = element.select("td:nth-child(5)").text();
                if ("招标公告".equals(type) || "更正事项".equals(type) || "采购公告".equals(type)) {
                    yuNanDataItem.setTitle(field3);
                    if ("更正事项".equals(type) && "政府采购".equals(businessType)) {
                        String date = DateTime.parse(field4, DateTimeFormat.forPattern("yyyyMMddHH")).toString("yyyy-MM-dd HH:mm");
                        yuNanDataItem.setDate(date);
                    } else {
                        yuNanDataItem.setDate(PageProcessorUtil.dataTxt(field4));
                    }
                }
                if ("评标报告".equals(type) || "开标记录".equals(type)) {
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
                    yuNanDataItem.setTitle(field3);
                    yuNanDataItem.setDate(PageProcessorUtil.dataTxt(field5));
                }

                //正文处理
                try {
                    Page page = httpClientDownloader.download(new Request(url), SiteUtil.get().setTimeOut(30000).toTask());
                    //body > div.w1200s
                    Elements contentElements = page.getHtml().getDocument().body().select("body > div.w1200s");
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
        httpClientDownloader = new HttpClientDownloader();
        return SiteUtil.get().setSleepTime(10000);
    }
}
