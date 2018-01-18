package com.har.sjfxpt.crawler.ccgp.provincial;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
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
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ccgp.provincial.ShangHaiPageProcessor.*;

/**
 * Created by Administrator on 2018/1/16.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.CCGPSHANGHAI,
        sources = {
                @Source(url = CCGPSHANGHAI_URL1, post = true, postParams = CCGPSHANGHAI_POSTPARAMS1),
        }
)
public class ShangHaiPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    final static String CCGPSHANGHAI_URL1 = "http://www.ccgp-shanghai.gov.cn/bulletininfo.do?method=bdetailnew";

    final static String CCGPSHANGHAI_POSTPARAMS1 = "{'findAjaxZoneAtClient':'false','treenum':'00','method':'bdetailnew','bulletininfotable_p':'1','ec_i':'bulletininfotable','bulletininfotable_pg':'1','bulletininfotable_crd':'10','bulletininfotable_rd':'10'}";

    @Override
    public void handlePaging(Page page) {
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtra("pageParams");
        String url = page.getUrl().get();
        int pageNum = Integer.parseInt(pageParams.get("bulletininfotable_p").toString());
        if (pageNum == 1) {
            String pageHtml = page.getHtml().getDocument().select("#bulletininfotable_toolbar").text();
            int pageCount = Integer.parseInt(StringUtils.substringBetween(pageHtml, "/", "页 每页"));
            if (pageCount >= 2) {
                for (int i = 2; i <= pageCount; i++) {
                    Map<String, Object> nextPage = Maps.newHashMap(pageParams);
                    nextPage.put("bulletininfotable_p", i);
                    Request request = new Request(url);
                    request.setMethod(HttpConstant.Method.POST);
                    request.setRequestBody(HttpRequestBody.form(nextPage, "UTF-8"));
                    request.putExtra("pageParams", nextPage);
                    log.debug("userAgent={}", getSite().getUserAgent());
                    page.addTargetRequest(request);
                }
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Elements elements = page.getHtml().getDocument().body().select("#bulletininfotable_table_body>tr");
        if (elements.isEmpty()) {
            log.warn("{} announcement is empty", page.getUrl().get());
            return;
        }
        List<BidNewsOriginal> dataItems = parseContent(elements);
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
            try {
                String hrefId = element.select("a").attr("value");
                if (StringUtils.isNotBlank(hrefId)) {
                    String href = "http://www.ccgp-shanghai.gov.cn/emeb_bulletin.do?method=showbulletin&bulletin_id=" + hrefId;
                    String date = element.select("td:nth-child(3)").text();
                    String title = element.select("a").text();
                    String dateDetail = StringUtils.substringBetween(date, "(", " /");

                    BidNewsOriginal bidNewsOriginal = new BidNewsOriginal(href, SourceCode.CCGPSHANGHAI);
                    bidNewsOriginal.setDate(PageProcessorUtil.dataTxt(dateDetail));
                    bidNewsOriginal.setTitle(title);
                    bidNewsOriginal.setProvince("上海");
                    String type = StringUtils.substringBefore(title, "：");
                    bidNewsOriginal.setType(type);

                    if (PageProcessorUtil.timeCompare(bidNewsOriginal.getDate())) {
                        log.warn("{} is not the same day", bidNewsOriginal.getUrl());
                    } else {
                        Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                        Element element1 = page.getHtml().getDocument().body();
                        String formatContent = PageProcessorUtil.formatElementsByWhitelist(element1);
                        if (StringUtils.isNotBlank(formatContent)) {
                            bidNewsOriginal.setFormatContent(formatContent);
                            dataItems.add(bidNewsOriginal);
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("e{}", e);
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
        return SiteUtil.get().setSleepTime(100000);
    }
}
