package com.har.sjfxpt.crawler.zgyj;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.service.ProxyService;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.ProvinceUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/10/27.
 *
 * @author luofei
 */
@Slf4j
@Component
public class ZGYeJinPageProcessor implements BasePageProcessor {

    final static String PAGE_PARAMS = "pageParams";

    @Autowired
    HttpClientDownloader downloader;

    @Autowired
    ProxyService proxyService;

    @Override
    public void handlePaging(Page page) {

        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int currentPage = (int) pageParams.get("currpage");
        if (currentPage == 1) {
            Elements pager = page.getHtml().getDocument().body().select("body > div.main_1 > div.rightbx > div.page > table > tbody > tr > td:nth-child(4)");
            String pageNum = pager.text();
            int num = Integer.parseInt(StringUtils.substringBefore(StringUtils.substringAfter(pageNum, "/ "), " 页"));
            log.debug("num=={}", num);
            if (num > 1) {
                for (int i = 2; i <= num; i++) {
                    Map<String, Object> params = Maps.newHashMap(pageParams);
                    params.put("currpage", i);
                    Request request = new Request(page.getUrl().get());
                    request.setMethod(HttpConstant.Method.POST);
                    request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
                    request.putExtra(PAGE_PARAMS, params);
                    log.debug("request=={}", request);
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
        String type = (String) pageParams.get("type");
        log.debug("type=={}", type);
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

                if (StringUtils.contains(title, "资格预审：")) {
                    zgYeJinDataItem.setProjectName(StringUtils.substringAfter(title, "资格预审："));
                }
                if (StringUtils.contains(title, "招标公告：")) {
                    zgYeJinDataItem.setProjectName(StringUtils.substringAfter(title, "招标公告："));
                }
                zgYeJinDataItem.setDate(date);
                zgYeJinDataItem.setUrl(url);
                zgYeJinDataItem.setProvince(ProvinceUtil.get(title));

                log.debug("zgYeJinDataItem=={}", zgYeJinDataItem);

                log.debug(">>> download {}", url);
                downloader.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxies()));
                Element root =downloader.download(url).getDocument().body().select("body > div.main-news").first();
                String formatContent = PageProcessorUtil.formatElementsByWhitelist(root);

                zgYeJinDataItem.setFormatContent(formatContent);
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


    /**
     * 解析网页地址
     *
     * @param onclick
     * @return
     */
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
