package com.har.sjfxpt.crawler.zgyj;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.ProvinceUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/10/27.
 */
@Slf4j
@Component
public class ZGYeJinPageProcessor implements BasePageProcessor {

    final static String PAGE_PARAMS = "pageParams";

    @Override
    public void handlePaging(Page page) {

        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int currentPage = (int) pageParams.get("currpage");
        if (currentPage == 1) {
            Elements pager = page.getHtml().getDocument().body().select("body > div.main_1 > div.rightbx > div.page > table > tbody > tr > td:nth-child(4)");
            String pageNum = pager.text();
            int num = Integer.parseInt(StringUtils.substringBefore(StringUtils.substringAfter(pageNum, "/ "), " 页"));
            log.info("num=={}", num);
            if (num > 1) {
                for (int i = 2; i <= num; i++) {
                    Map<String, Object> params = Maps.newHashMap(pageParams);
                    params.put("currpage", i);
                    Request request = new Request(page.getUrl().get());
                    request.setMethod(HttpConstant.Method.POST);
                    request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
                    request.putExtra(PAGE_PARAMS, params);
                    log.info("request=={}", request);
                    page.addTargetRequest(request);
                }
            }

        }

    }

    @Override
    public void handleContent(Page page) {
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        Elements elements = page.getHtml().getDocument().body().select("body > div.main_1 > div.rightbx > div.datalb > table > tbody >tr");

        if (elements.isEmpty()) {
            log.error("fetch error, elements is empty");
            return;
        }

        List<ZGYeJinDataItem> dataItems = parseContent(elements);
        String type= (String) pageParams.get("type");
        log.debug("type=={}",type);
        dataItems.forEach(dataItem -> dataItem.setType(type));
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }

    }

    @Override
    public List parseContent(Elements items) {
        List<ZGYeJinDataItem> dataItems = Lists.newArrayList();
        for (Element a : items) {
            String title = a.select("td.txtLeft > a").text();
            String date = a.select("td:nth-child(2)").text();
            String url = urlParser(a.select("a").attr("onclick"));
            if (StringUtils.isNotBlank(url)) {
                ZGYeJinDataItem zgYeJinDataItem = new ZGYeJinDataItem(url);
                zgYeJinDataItem.setTitle(title);
                zgYeJinDataItem.setDate(date);
                zgYeJinDataItem.setUrl(url);
                zgYeJinDataItem.setProvince(ProvinceUtil.get(title));

                log.info("zgYeJinDataItem=={}", zgYeJinDataItem);


                try {
                    log.info(">>> download {}", url);
                    Document document = Jsoup.connect(url).timeout(60000).userAgent(SiteUtil.get().getUserAgent()).get();
                    String html = document.html();
                    Element root = document.body().select("body > div.main-news").first();
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(root);
                    String textContent = PageProcessorUtil.extractTextByWhitelist(root);

                    zgYeJinDataItem.setHtml(html);
                    zgYeJinDataItem.setFormatContent(formatContent);
                    zgYeJinDataItem.setTextContent(textContent);
                } catch (IOException e) {
                    log.error("page download failed!", e);
                }
                dataItems.add(zgYeJinDataItem);
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
        return SiteUtil.get();
    }


    //解析网页地址
    public String urlParser(String onclick) {
        String function = StringUtils.substringBefore(onclick, "(");
        String parameter = StringUtils.substringBefore(StringUtils.substringAfter(onclick, "('"), "')");

        String url = "";
        switch (function) {
            case "showCgxjMessage":
                url = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showCgxjDetail&xjbm=" + parameter;
                break;
            case "showZbsMessage":
                url = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showZbsDetail&inviteid=" + parameter;
                break;
            case "showCgwzMessage":
                url = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showUrgentDetail&xxbh=" + parameter;
                break;
            case "showWxwzMessage":
                url = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showSaleDetail&xxbh=" + parameter;
                break;
            case "showOldMaterialMessage":
                url = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showOldMaterialDetail&xxbh=" + parameter;
                break;
            case "showInvalidMaterialMessage":
                url = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showInvalidMaterialDetail&xxbh=" + parameter;
                break;
            case "showCqggMessage":
                url = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showCqggDetail&xxbh=" + parameter;
                break;
            case "showPxjgmessage":
                url = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showPxjgDetail&xxbh=" + parameter;
                break;
            case "showMessage":
                url = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMessage&xxbh=" + parameter;
                break;
            case "showZgysDetail":
                url = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showZgysDetail&zgyswjbm=" + parameter;
                break;
            case "showZhongbggMessage":
                url = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showZhongbggDetail&xxbh=" + parameter;
                break;
            case "showXcpMessage":
                url = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showNewProductDetail&dwbm=" + parameter;
                break;
            case "showYzbgsMessage":
                url = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showYzbgsDetail&xxbh=" + parameter;
                break;
        }

        return url;
    }
}
