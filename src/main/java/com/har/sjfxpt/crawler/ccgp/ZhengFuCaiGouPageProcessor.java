package com.har.sjfxpt.crawler.ccgp;

import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.SimpleHttpClient;
import us.codecraft.webmagic.Site;

import java.io.IOException;
import java.util.List;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * 中国政府采购网
 *
 * @author dongqi
 */
@Slf4j
@Component
public class ZhengFuCaiGouPageProcessor implements BasePageProcessor {

    public static final String cssQuery4List = "div.vT_z div.vT-srch-result div.vT-srch-result-list-con2 div.vT-srch-result-list ul.vT-srch-result-list-bid li";

    private SimpleHttpClient simpleHttpClient;

    @Autowired
    PageDataRepository repository;

    @Autowired
    ZhengFuCaiGouRepository zhengFuCaiGouRepository;

    @Override
    public void process(Page page) {
        //处理分页
        handlePaging(page);

        //处理列表
        handleContent(page);
    }

    @Override
    public Site getSite() {
        simpleHttpClient = new SimpleHttpClient(SiteUtil.get().setTimeOut(60000));
        Site site = SiteUtil.get().setTimeOut(60000);
        //site.setSleepTime(RandomUtils.nextInt(10, 30) * 1000);
        return site;
    }

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        log.debug(">>> url {}", url);
        if (!StringUtils.contains(url, "search.ccgp.gov.cn")) return;

        Element pager = page.getHtml().getDocument().body().select("p.pager script").first();
        String totalPageText = pager.html();
        totalPageText = StringUtils.substringBetween(totalPageText, "size: ", ",");

        try {
            //记录每天数据总数及分页总数，便于后面排查及统计
            PageData pageData = (PageData) page.getRequest().getExtra(PageData.class.getSimpleName());
            if (pageData != null) {
                pageData.setPage(Integer.parseInt(totalPageText));
                pageData.setSize(Integer.parseInt(totalPageText) * 20);
                repository.save(pageData);
                log.debug("{}", pageData);
            }
        } catch (Exception e) {
            log.error("", e);
        }

        String pageIndexText = StringUtils.substringAfterLast(url, "=");
        String firstPage = "1";
        if (firstPage.equalsIgnoreCase(pageIndexText)) {
            int totalPage = Integer.parseInt(totalPageText);
            for (int pageIndex = 2; pageIndex <= totalPage; pageIndex++) {
                String requestUrl = StringUtils.substringBeforeLast(url, "=") + "=" + pageIndex;
                log.info("ccgp size {}, total {}, pager url {}", (Integer.parseInt(totalPageText) * 20), totalPageText, requestUrl);
                page.addTargetRequest(requestUrl);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        //提取列表字段内容
        Document document = page.getHtml().getDocument();
        Elements elements = document.body().select(cssQuery4List);
        List<ZhengFuCaiGouDataItem> dataItemList = parseContent(elements);
        if (!dataItemList.isEmpty()) {
            dataItemList.forEach(dataItem -> {
                try {
                    //减少http请求，避免过早被要求输入验证码
                    ZhengFuCaiGouDataItem dataInDB = zhengFuCaiGouRepository.findOne(dataItem.getId());
                    if (dataInDB == null) {
                        download(dataItem);
                    } else if (dataInDB != null && StringUtils.isBlank(dataInDB.getFormatContent())) {
                        download(dataItem);
                    }
                } catch (Exception e) {
                    log.error("ccgp {} download fail", dataItem.getId());
                }
            });

            page.putField(KEY_DATA_ITEMS, dataItemList);
        }
    }

    private void download(ZhengFuCaiGouDataItem dataItem) throws IOException {
        Document document = simpleHttpClient.get(dataItem.getUrl()).getHtml().getDocument();
        if (StringUtils.equalsAnyIgnoreCase(document.title(), "安全验证")) {
            log.warn("ccgp verification {}", dataItem.getUrl());
            return;
        }

        Element element = document.body();
        String detailCssQuery = "div.vF_detail_content_container > div.vF_detail_content";
        String summaryCssQuery = "div.vF_detail_main table";

        String summaryFormatContent = PageProcessorUtil.formatElementsByWhitelist(element.select(summaryCssQuery).first());
        String detailFormatContent = PageProcessorUtil.formatElementsByWhitelist(element.select(detailCssQuery).first());
        String detailTextContent = PageProcessorUtil.extractTextByWhitelist(element.select(detailCssQuery).first());

        dataItem.setSummaryFormatContent(summaryFormatContent);
        dataItem.setFormatContent(detailFormatContent);
        dataItem.setTextContent(detailTextContent);

    }

    @Override
    public List<ZhengFuCaiGouDataItem> parseContent(Elements items) {
        List<ZhengFuCaiGouDataItem> dataItemList = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("a").attr("href");
            String id = DigestUtils.md5Hex(href);

            String title = element.select("a").first().text();
            if (StringUtils.isBlank(title)) {
                log.error("ccgp url {} fetch title fail", href);
                continue;
            }

            ZhengFuCaiGouDataItem dataItem = ZhengFuCaiGouDataItem.builder()
                    .id(id).url(href).title(title)
                    .build();

            String text = element.select("span").text();
            String[] lines = text.split("\\|");
            String pubDate = null, purchaser = null, purchaserAgent = null;
            String type = null;
            String province = null;
            String industry = null;
            for (int index = 0; index < lines.length; index++) {
                String line = lines[index];
                switch (index) {
                    case 0:
                        DateTime dt = DateTime.parse(line.trim(), DateTimeFormat.forPattern("yyyy.MM.dd HH:mm:ss"));
                        pubDate = dt.toString("yyyy-MM-dd HH:mm");
                        dataItem.setPubDate(pubDate);
                        dataItem.setDate(dt.toString("yyyy-MM-dd"));
                        break;
                    case 1:
                        purchaser = StringUtils.removeAll(line, "采购人：");
                        purchaser = StringUtils.trimToEmpty(purchaser);
                        dataItem.setPurchaser(purchaser);
                        break;
                    case 2:
                        for (String value : line.split(" ")) {
                            if (StringUtils.isBlank(value)) continue;
                            if (StringUtils.contains(value, "代理机构：")) {
                                purchaserAgent = StringUtils.removeAll(value, "代理机构：");
                                dataItem.setPurchaserAgent(purchaserAgent);
                            } else {
                                type = StringUtils.trim(value);
                                dataItem.setType(type);
                            }
                        }
                        break;
                    case 3:
                        province = StringUtils.trim(line);
                        dataItem.setProvince(province);
                        break;
                    case 4:
                        industry = StringUtils.trim(line);
                        dataItem.setIndustry(industry);
                        break;
                    default:
                        break;
                }
            }

            dataItemList.add(dataItem);
        }
        return dataItemList;
    }
}
