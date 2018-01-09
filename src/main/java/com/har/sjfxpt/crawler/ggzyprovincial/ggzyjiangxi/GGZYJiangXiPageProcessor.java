package com.har.sjfxpt.crawler.ggzyprovincial.ggzyjiangxi;

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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyjiangxi.GGZYJiangXiPageProcessor.*;

/**
 * Created by Administrator on 2017/12/14.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZYJIANGXI,
        sources = {
                @Source(url = GGZYJIANGXI_URL1, type = "采购公告"),
                @Source(url = GGZYJIANGXI_URL2, type = "变更公告"),
                @Source(url = GGZYJIANGXI_URL3, type = "答疑澄清"),
                @Source(url = GGZYJIANGXI_URL4, type = "结果公示"),
                @Source(url = GGZYJIANGXI_URL5, type = "单一来源公示"),
                @Source(url = GGZYJIANGXI_URL6, type = "合同公示"),
                @Source(url = GGZYJIANGXI_URL7, type = "招标公告"),
                @Source(url = GGZYJIANGXI_URL8, type = "答疑澄清"),
                @Source(url = GGZYJIANGXI_URL9, type = "文件下载"),
                @Source(url = GGZYJIANGXI_URL10, type = "中标公示"),
                @Source(url = GGZYJIANGXI_URL11, type = "招标公告"),
                @Source(url = GGZYJIANGXI_URL12, type = "补遗书"),
                @Source(url = GGZYJIANGXI_URL13, type = "中标公示"),
                @Source(url = GGZYJIANGXI_URL14, type = "资格预审公告/招标公告"),
                @Source(url = GGZYJIANGXI_URL15, type = "澄清补遗"),
                @Source(url = GGZYJIANGXI_URL16, type = "文件下载"),
                @Source(url = GGZYJIANGXI_URL17, type = "中标候选人公示"),
                @Source(url = GGZYJIANGXI_URL18, type = "招标公告"),
                @Source(url = GGZYJIANGXI_URL19, type = "答疑澄清"),
                @Source(url = GGZYJIANGXI_URL20, type = "文件下载"),
                @Source(url = GGZYJIANGXI_URL21, type = "结果公示"),
        }
)
public class GGZYJiangXiPageProcessor implements BasePageProcessor {

    final static String GGZYJIANGXI_URL1 = "http://jxsggzy.cn/web/jyxx/002006/002006001/1.html";
    final static String GGZYJIANGXI_URL2 = "http://jxsggzy.cn/web/jyxx/002006/002006002/1.html";
    final static String GGZYJIANGXI_URL3 = "http://jxsggzy.cn/web/jyxx/002006/002006003/1.html";
    final static String GGZYJIANGXI_URL4 = "http://jxsggzy.cn/web/jyxx/002006/002006004/1.html";
    final static String GGZYJIANGXI_URL5 = "http://jxsggzy.cn/web/jyxx/002006/002006005/1.html";
    final static String GGZYJIANGXI_URL6 = "http://jxsggzy.cn/web/jyxx/002006/002006006/1.html";

    final static String GGZYJIANGXI_URL7 = "http://jxsggzy.cn/web/jyxx/002001/002001001/1.html";
    final static String GGZYJIANGXI_URL8 = "http://jxsggzy.cn/web/jyxx/002001/002001002/1.html";
    final static String GGZYJIANGXI_URL9 = "http://jxsggzy.cn/web/jyxx/002001/002001003/1.html";
    final static String GGZYJIANGXI_URL10 = "http://jxsggzy.cn/web/jyxx/002001/002001004/1.html";

    final static String GGZYJIANGXI_URL11 = "http://jxsggzy.cn/web/jyxx/002002/002002002/1.html";
    final static String GGZYJIANGXI_URL12 = "http://jxsggzy.cn/web/jyxx/002002/002002003/1.html";
    final static String GGZYJIANGXI_URL13 = "http://jxsggzy.cn/web/jyxx/002002/002002005/1.html";

    final static String GGZYJIANGXI_URL14 = "http://jxsggzy.cn/web/jyxx/002003/002003001/1.html";
    final static String GGZYJIANGXI_URL15 = "http://jxsggzy.cn/web/jyxx/002003/002003002/1.html";
    final static String GGZYJIANGXI_URL16 = "http://jxsggzy.cn/web/jyxx/002003/002003003/1.html";
    final static String GGZYJIANGXI_URL17 = "http://jxsggzy.cn/web/jyxx/002003/002003004/1.html";

    final static String GGZYJIANGXI_URL18 = "http://jxsggzy.cn/web/jyxx/002005/002005001/1.html";
    final static String GGZYJIANGXI_URL19 = "http://jxsggzy.cn/web/jyxx/002005/002005002/1.html";
    final static String GGZYJIANGXI_URL20 = "http://jxsggzy.cn/web/jyxx/002005/002003003/1.html";
    final static String GGZYJIANGXI_URL21 = "http://jxsggzy.cn/web/jyxx/002006/002003004/1.html";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        String type = page.getRequest().getExtra("type").toString();
        int pageNum = Integer.parseInt(StringUtils.substringBefore(StringUtils.substringAfterLast(url, "/"), ".html"));
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("#index");
            String totalPage = elements.text();
            int pageCount = Integer.parseInt(StringUtils.substringAfter(totalPage, "/"));
            int cycleCount = pageCount >= 10 ? 10 : pageCount;
            for (int i = 2; i <= cycleCount; i++) {
                String urlTarget = StringUtils.substringBeforeLast(url, "/") + "/" + i + ".html";
                Request request = new Request(urlTarget);
                request.putExtra("type", type);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        String type = page.getRequest().getExtra("type").toString();
        Elements elements = page.getHtml().getDocument().body().select("#gengerlist > div.ewb-infolist > ul > li");
        List<BidNewsOriginal> dataItems = parseContent(elements);
        dataItems.forEach(dataItem -> dataItem.setType(type));
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("a").attr("href");
            String title = element.select("a").text();
            String date = element.select("span").text();
            if (StringUtils.isNotBlank(href)) {
                if (!StringUtils.startsWith(href, "http:")) {
                    href = "http://jxsggzy.cn" + href;
                }
                BidNewsOriginal ggzyJiangXiDataItem = new BidNewsOriginal(href, SourceCode.GGZYJIANGXI);
                ggzyJiangXiDataItem.setUrl(href);
                ggzyJiangXiDataItem.setProvince("江西");
                ggzyJiangXiDataItem.setDate(PageProcessorUtil.dataTxt(date));
                if (PageProcessorUtil.timeCompare(ggzyJiangXiDataItem.getDate())) {
                    log.info("{} is not the same day", ggzyJiangXiDataItem.getUrl());
                } else {
                    ggzyJiangXiDataItem.setTitle(title);
                    Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    String dateParse = DateTime.parse(date, DateTimeFormat.forPattern("yyyy-MM-dd")).toString("yyyyMMdd");
                    String typeId = StringUtils.substringBetween(href, "jyxx/", "/" + dateParse);
                    String typeIdReal = StringUtils.substringAfter(typeId, "/");
                    Elements elements = null;
                    if ("002006001".equalsIgnoreCase(typeIdReal) || "002006002".equalsIgnoreCase(typeIdReal)
                            || "002006004".equalsIgnoreCase(typeIdReal) || "002006005".equalsIgnoreCase(typeIdReal)
                            || "002001001".equalsIgnoreCase(typeIdReal) || "002001003".equalsIgnoreCase(typeIdReal)
                            || "002001004".equalsIgnoreCase(typeIdReal) || "002002002".equalsIgnoreCase(typeIdReal)
                            || "002002005".equalsIgnoreCase(typeIdReal) || "002003001".equalsIgnoreCase(typeIdReal)
                            || "002003004".equalsIgnoreCase(typeIdReal) || "002005001".equalsIgnoreCase(typeIdReal)
                            || "002005004".equalsIgnoreCase(typeIdReal)) {
                        elements = page.getHtml().getDocument().body().select("body > div.container.clearfix.mt20 > div.ewb-detail-box > div.article-info > div");
                    }
                    if ("002006003".equalsIgnoreCase(typeIdReal) || "002001002".equalsIgnoreCase(typeIdReal)
                            || "002002003".equalsIgnoreCase(typeIdReal) || "002003002".equalsIgnoreCase(typeIdReal)
                            || "002003003".equalsIgnoreCase(typeIdReal) || "002005002".equalsIgnoreCase(typeIdReal)
                            || "002005003".equalsIgnoreCase(typeIdReal)) {
                        elements = page.getHtml().getDocument().body().select("body > div.container.clearfix.mt20 > div.ewb-detail-box");
                    }
                    if ("002006006".equalsIgnoreCase(typeIdReal)) {
                        elements = page.getHtml().getDocument().body().select("body > div.fui-content > div");
                    }
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        ggzyJiangXiDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyJiangXiDataItem);
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
