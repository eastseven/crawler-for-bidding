package com.har.sjfxpt.crawler.zgly;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
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

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/12/21.
 */
@Slf4j
@Component
public class ZGLvYePageProcessor implements BasePageProcessor {

    final static String PAGE_PARAMS = "pageParams";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int currPage = Integer.parseInt(pageParams.get("currpage").toString());
        if (currPage == 1) {
            String text = page.getHtml().getDocument().body().select("body > div.main_1 > div.rightbx > div.page > table > tbody > tr > td:nth-child(4)").text();
            int pageCount = Integer.parseInt(StringUtils.substringBetween(text, "/ ", " 页"));
            log.info("pageCount=={}", pageCount);
            if (pageCount >= 2) {
                for (int i = 2; i <= pageCount; i++) {
                    pageParams.put("currpage", i);
                    Request request = new Request(url);
                    request.putExtra(PAGE_PARAMS, pageParams);
                    request.setMethod(HttpConstant.Method.POST);
                    request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
                    page.addTargetRequest(request);
                }
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        Elements elements = page.getHtml().getDocument().body().select("body > div.main_1 > div.rightbx > div.datalb > table > tbody > tr");
        List<ZGLvYeDataItem> dataItems = parseContent(elements);
        String type = pageParams.get("type").toString();
        dataItems.forEach(dataItem -> dataItem.setType(type));
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<ZGLvYeDataItem> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String onclick = element.select("a").attr("onclick");
            String href = urlParser(onclick);

            if (StringUtils.isNotBlank(href)) {
                String title = element.select("a").attr("title");
                String date = element.select("td:nth-child(2)").text();

                ZGLvYeDataItem zgLvYeDataItem = new ZGLvYeDataItem(href);
                zgLvYeDataItem.setUrl(href);
                zgLvYeDataItem.setTitle(title);
                zgLvYeDataItem.setDate(PageProcessorUtil.dataTxt(date));

                Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                Elements elements = page.getHtml().getDocument().body().select("body > div.main-news");
                String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                if (StringUtils.isNotBlank(formatContent)) {
                    zgLvYeDataItem.setFormatContent(formatContent);
                    dataItems.add(zgLvYeDataItem);
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

    /**
     * 解析网页地址
     *
     * @param onclick
     * @return
     */
    public String urlParser(String onclick) {
        String function = StringUtils.substringBefore(onclick, "(");
        String parameter = StringUtils.substringBetween(onclick, "('", "')");

        String url = "";
        switch (function) {
            case "showCgxjMessage":
                url = "http://ec.chalieco.com/b2b/web/two/indexinfoAction.do?actionType=showCgxjDetail&xjbm=" + parameter;
                break;
            case "showZbsMessage":
                url = "http://ec.chalieco.com.cn/b2b/web/two/indexinfoAction.do?actionType=showZbsDetail&inviteid=" + parameter;
                break;
            case "showCgwzMessage":
                url = "http://ec.chalieco.com.cn/b2b/web/two/indexinfoAction.do?actionType=showUrgentDetail&xxbh=" + parameter;
                break;
            case "showWxwzMessage":
                url = "http://ec.chalieco.com.cn/b2b/web/two/indexinfoAction.do?actionType=showSaleDetail&xxbh=" + parameter;
                break;
            case "showOldMaterialMessage":
                url = "http://ec.chalieco.com.cn/b2b/web/two/indexinfoAction.do?actionType=showOldMaterialDetail&xxbh=" + parameter;
                break;
            case "showInvalidMaterialMessage":
                url = "http://ec.chalieco.com.cn/b2b/web/two/indexinfoAction.do?actionType=showInvalidMaterialDetail&xxbh=" + parameter;
                break;
            case "showCqggMessage":
                url = "http://ec.chalieco.com.cn/b2b/web/two/indexinfoAction.do?actionType=showCqggDetail&xxbh=" + parameter;
                break;
            case "showPxjgmessage":
                url = "http://ec.chalieco.com.cn/b2b/web/two/indexinfoAction.do?actionType=showPxjgDetail&xxbh=" + parameter;
                break;
            case "showMessage":
                url = "http://ec.chalieco.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMessage&xxbh=" + parameter;
                break;
            case "showZgysDetail":
                url = "http://ec.chalieco.com.cn/b2b/web/two/indexinfoAction.do?actionType=showZgysDetail&zgyswjbm=" + parameter;
                break;
            case "showZhongbggMessage":
                url = "http://ec.chalieco.com.cn/b2b/web/two/indexinfoAction.do?actionType=showZhongbggDetail&xxbh=" + parameter;
                break;
            case "showXcpMessage":
                url = "http://ec.chalieco.com.cn/b2b/web/two/indexinfoAction.do?actionType=showNewProductDetail&dwbm=" + parameter;
                break;
            case "showYzbgsMessage":
                url = "http://ec.chalieco.com.cn/b2b/web/two/indexinfoAction.do?actionType=showYzbgsDetail&xxbh=" + parameter;
                break;
        }
        return url;
    }
}
