package com.har.sjfxpt.crawler.ggzyprovincial.ggzycq;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/11/28.
 */
@Slf4j
@Component
public class GGZYCQPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    final static int ARTICLE_NUM = 18;

    @Override
    public void handlePaging(Page page) {

        String urlId = StringUtils.substringAfter(page.getUrl().toString(), "infoC=&_=");

        int currentPage = Integer.parseInt(StringUtils.substringBetween(page.getUrl().toString(), "&pageIndex=", "&pageSize="));

        if (currentPage == 1) {
            if (urlId.equalsIgnoreCase("1511837748941")) {
                Request request = new Request("http://www.cqggzy.com/web/services/PortalsWebservice/getInfoListCount?response=application/json&siteguid=d7878853-1c74-4913-ab15-1d72b70ff5e7&categorynum=014005001&title=&infoC=&_=1511841106613");
                Page page1 = httpClientDownloader.download(request, SiteUtil.get().toTask());
                Object size = JSONObject.parse(page1.getRawText());
                int announcementNum = Integer.parseInt(StringUtils.substringBetween(size.toString(), "\":", "}"));
                int pageNum = announcementNum % ARTICLE_NUM == 0 ? announcementNum / ARTICLE_NUM : announcementNum / ARTICLE_NUM + 1;
                log.debug("size=={}", pageNum);
                for (int i = 2; i < 10; i++) {
                    String url = page.getUrl().toString().replaceAll("pageIndex=1", "pageIndex=" + i);
                    page.addTargetRequest(url);
                }
            }
            if (urlId.equalsIgnoreCase("1511837779151")) {
                Request request = new Request("http://www.cqggzy.com/web/services/PortalsWebservice/getInfoListCount?response=application/json&siteguid=d7878853-1c74-4913-ab15-1d72b70ff5e7&categorynum=014001001&title=&infoC=&_=1511841357683");
                Page page1 = httpClientDownloader.download(request, SiteUtil.get().toTask());
                Object size = JSONObject.parse(page1.getRawText());
                int announcementNum = Integer.parseInt(StringUtils.substringBetween(size.toString(), "\":", "}"));
                int pageNum = announcementNum % ARTICLE_NUM == 0 ? announcementNum / ARTICLE_NUM : announcementNum / ARTICLE_NUM + 1;
                log.debug("size=={}", pageNum);
                for (int i = 2; i < 10; i++) {
                    String url = page.getUrl().toString().replaceAll("pageIndex=1", "pageIndex=" + i);
                    page.addTargetRequest(url);
                }
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        List<GGZYCQDataItem> dataItems = parseContent(page);
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

    public List parseContent(Page page) {
        List<GGZYCQDataItem> dataItems = Lists.newArrayList();
        String urlId = StringUtils.substringAfter(page.getUrl().toString(), "infoC=&_=");
        String Json = StringUtils.substringBetween(page.getRawText(), "\"[", "]\"");
        String targets[] = StringUtils.substringsBetween(Json, "{", "}");
        for (String target : targets) {
            String href = "http://www.cqggzy.com" + StringUtils.substringBetween(target, "\"infourl\\\":\\\"", "\\\",").replace("\\", "");
            String title = StringUtils.substringBetween(target, "\"title\\\":\\\"", "\\\",");
            String date = StringUtils.substringBetween(target, "\"infodate\\\":\\\"", "\\\",");
            if (PageProcessorUtil.timeCompare(date)) {
                log.info("{} is not on the same day", href);
            } else {
                GGZYCQDataItem GGZYCQDataItem = new GGZYCQDataItem(href);
                GGZYCQDataItem.setTitle(title);
                GGZYCQDataItem.setUrl(href);
                if (urlId.equalsIgnoreCase("1511837748941")) {
                    GGZYCQDataItem.setBusinessType("政府采购");
                    GGZYCQDataItem.setType("采购公告");
                }
                if (urlId.equalsIgnoreCase("1511837779151")) {
                    GGZYCQDataItem.setBusinessType("工程招投标");
                    GGZYCQDataItem.setType("招标公告");
                }
                if (date.length() == 10) {
                    date = date + DateTime.now().toString(" HH:mm");
                }
                GGZYCQDataItem.setDate(date);
                Page page1 = httpClientDownloader.download(new Request(GGZYCQDataItem.getUrl()), SiteUtil.get().toTask());
                Element element = page1.getHtml().getDocument().body();
                Elements elements = element.select("body > div:nth-child(4) > div > div.detail-block");
                String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                if (StringUtils.isNotBlank(formatContent)) {
                    Document doc = Jsoup.parse(formatContent);
                    for (Element h : doc.select("h4")) {
                        if (StringUtils.containsIgnoreCase(h.text(), "预算金额")) {
                            if (StringUtils.isNotBlank(h.children().text())) {
                                GGZYCQDataItem.setBudget(h.children().text());
                            }
                        }
                    }
                    if (formatContent.contains("<a>相关公告</a>")) {
                        formatContent = StringUtils.trim(StringUtils.removeAll(formatContent, "<li>(.+?)</li>"));
                        formatContent = StringUtils.removeAll(formatContent, "\\s");
                    }
                    GGZYCQDataItem.setFormatContent(formatContent);
                    dataItems.add(GGZYCQDataItem);
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
