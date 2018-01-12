package com.har.sjfxpt.crawler.ggzy.provincial;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
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

import static com.har.sjfxpt.crawler.ggzy.provincial.ChongQingPageProcessor.*;

/**
 * Created by Administrator on 2017/11/28.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZYCQ,
        sources = {
                @Source(url = GGZYCQ_URl1, type = "采购公告"),
                @Source(url = GGZYCQ_URl2, type = "答疑变更"),
                @Source(url = GGZYCQ_URl3, type = "采购结果公告"),
                @Source(url = GGZYCQ_URl4, type = "招标公告"),
                @Source(url = GGZYCQ_URl5, type = "答疑补遗"),
                @Source(url = GGZYCQ_URl6, type = "中标候选人"),
                @Source(url = GGZYCQ_URl7, type = "中标公示")
        }
)
public class ChongQingPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    final static int ARTICLE_NUM = 18;

    final static String GGZYCQ_URl1 = "http://www.cqggzy.com/web/services/PortalsWebservice/getInfoList?response=application/json&pageIndex=1&pageSize=18&siteguid=d7878853-1c74-4913-ab15-1d72b70ff5e7&categorynum=014005001&title=&infoC=";
    final static String GGZYCQ_URl2 = "http://www.cqggzy.com/web/services/PortalsWebservice/getInfoList?response=application/json&pageIndex=1&pageSize=18&siteguid=d7878853-1c74-4913-ab15-1d72b70ff5e7&categorynum=014005002&title=&infoC=";
    final static String GGZYCQ_URl3 = "http://www.cqggzy.com/web/services/PortalsWebservice/getInfoList?response=application/json&pageIndex=1&pageSize=18&siteguid=d7878853-1c74-4913-ab15-1d72b70ff5e7&categorynum=014005004&title=&infoC=";

    final static String GGZYCQ_URl4 = "http://www.cqggzy.com/web/services/PortalsWebservice/getInfoList?response=application/json&pageIndex=1&pageSize=18&siteguid=d7878853-1c74-4913-ab15-1d72b70ff5e7&categorynum=014001001&title=&infoC=";
    final static String GGZYCQ_URl5 = "http://www.cqggzy.com/web/services/PortalsWebservice/getInfoList?response=application/json&pageIndex=1&pageSize=18&siteguid=d7878853-1c74-4913-ab15-1d72b70ff5e7&categorynum=014001002&title=&infoC=";
    final static String GGZYCQ_URl6 = "http://www.cqggzy.com/web/services/PortalsWebservice/getInfoList?response=application/json&pageIndex=1&pageSize=18&siteguid=d7878853-1c74-4913-ab15-1d72b70ff5e7&categorynum=014001003&title=&infoC=";
    final static String GGZYCQ_URl7 = "http://www.cqggzy.com/web/services/PortalsWebservice/getInfoList?response=application/json&pageIndex=1&pageSize=18&siteguid=d7878853-1c74-4913-ab15-1d72b70ff5e7&categorynum=014001004&title=&infoC=";

    @Override
    public void handlePaging(Page page) {
        String type = (String) page.getRequest().getExtra("type");
        int currentPage = Integer.parseInt(StringUtils.substringBetween(page.getUrl().toString(), "&pageIndex=", "&pageSize="));
        if (currentPage == 1) {
            String pageUrl = page.getUrl().get();
            int pageCount = findPageCount(pageUrl);
            if (pageCount >= 2) {
                for (int i = 2; i <= pageCount; i++) {
                    String url = pageUrl.replaceAll("pageIndex=1", "pageIndex=" + i);
                    Request request = new Request(url);
                    request.putExtra("type", type);
                    page.addTargetRequest(request);
                }
            }
        }
    }

    public int findPageCount(String url) {
        String typeId = StringUtils.substringBetween(url, "&categorynum=", "&title=");
        String findCountUrl = "http://www.cqggzy.com/web/services/PortalsWebservice/getInfoListCount?response=application/json&siteguid=d7878853-1c74-4913-ab15-1d72b70ff5e7&categorynum=" + typeId + "&title=&infoC=";
        Page page = httpClientDownloader.download(new Request(findCountUrl), SiteUtil.get().setTimeOut(10000).toTask());
        JSONObject jsonObject = (JSONObject) JSONObject.parse(page.getRawText());
        int count = Integer.parseInt(JSONPath.eval(jsonObject, "$.return").toString());
        int cycleNum = count % ARTICLE_NUM == 0 ? count / ARTICLE_NUM : count / ARTICLE_NUM + 1;
        return cycleNum;
    }

    @Override
    public void handleContent(Page page) {
        String type = (String) page.getRequest().getExtra("type");
        List<BidNewsOriginal> dataItems = parseContent(page);
        dataItems.forEach(dataItem -> dataItem.setType(type));
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
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        String Json = StringUtils.substringBetween(page.getRawText(), "\"[", "]\"");
        String targets[] = StringUtils.substringsBetween(Json, "{", "}");
        for (String target : targets) {
            String href = "http://www.cqggzy.com" + StringUtils.substringBetween(target, "\"infourl\\\":\\\"", "\\\",").replace("\\", "");
            String title = StringUtils.substringBetween(target, "\"title\\\":\\\"", "\\\",");
            String date = StringUtils.substringBetween(target, "\"infodate\\\":\\\"", "\\\",");
            if (PageProcessorUtil.timeCompare(date)) {
                log.info("{} is not on the same day", href);
            } else {
                BidNewsOriginal ggzyCQDataItem = new BidNewsOriginal(href, SourceCode.GGZYCQ);
                ggzyCQDataItem.setTitle(title);
                if (date.length() == 10) {
                    date = date + DateTime.now().toString(" HH:mm");
                }
                ggzyCQDataItem.setDate(date);
                ggzyCQDataItem.setProvince("重庆");
                Page page1 = httpClientDownloader.download(new Request(ggzyCQDataItem.getUrl()), SiteUtil.get().setTimeOut(3000).toTask());
                Element element = page1.getHtml().getDocument().body();
                Elements elements = element.select("body > div:nth-child(4) > div > div.detail-block");
                String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                if (StringUtils.isNotBlank(formatContent)) {
                    Document doc = Jsoup.parse(formatContent);
                    for (Element h : doc.select("h4")) {
                        if (StringUtils.containsIgnoreCase(h.text(), "预算金额")) {
                            if (StringUtils.isNotBlank(h.children().text())) {
                                ggzyCQDataItem.setBudget(h.children().text());
                            }
                        }
                    }
                    if (formatContent.contains("<a>相关公告</a>")) {
                        formatContent = StringUtils.trim(StringUtils.removeAll(formatContent, "<li>(.+?)</li>"));
                        formatContent = StringUtils.removeAll(formatContent, "\\s");
                    }
                    ggzyCQDataItem.setFormatContent(formatContent);
                    dataItems.add(ggzyCQDataItem);
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
