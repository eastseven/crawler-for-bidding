package com.har.sjfxpt.crawler.other;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.service.ProxyService;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.ProvinceUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;

/**
 * @author dongqi
 * 三维天地
 * <p>
 * 中国冶金科工，中国铝业 都是 三维天地 开发的
 */
@Slf4j
public class SunWayWorldPageProcessor implements BasePageProcessor {

    final static String PAGE_PARAMS = "pageParams";

    private SourceCode sourceCode;

    private String domain;

    private HttpClientDownloader httpClientDownloader;

    private ProxyService proxyService;

    public void setSourceCode(SourceCode sourceCode) {
        this.sourceCode = sourceCode;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setHttpClientDownloader(HttpClientDownloader httpClientDownloader) {
        this.httpClientDownloader = httpClientDownloader;
    }

    public void setProxyService(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @Override
    public void handlePaging(Page page) {
        String type = (String) page.getRequest().getExtra("type");
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
                    request.putExtra("type", type);
                    log.debug("request=={}", request);
                    page.addTargetRequest(request);

                    if ("采购信息".equalsIgnoreCase(type) && i >= 10) {
                        break;
                    }
                }
            }

        }

    }

    @Override
    public void handleContent(Page page) {
        Elements elements = page.getHtml().getDocument().body().select("body > div.main_1 > div.rightbx > div.datalb > table > tbody >tr");

        if (elements.isEmpty()) {
            log.error("fetch error, elements is empty");
            return;
        }

        List<BidNewsOriginal> dataItems = parseContent(elements);
        String type = (String) page.getRequest().getExtra("type");
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
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        for (Element a : items) {
            String title = a.select("td.txtLeft > a").text();
            String date = a.select("td:nth-child(2)").text();
            String url = urlParser(a.select("a").attr("onclick"));
            if (StringUtils.isNotBlank(url)) {
                BidNewsOriginal dataItem = new BidNewsOriginal(url, sourceCode);
                dataItem.setTitle(title);

                if (StringUtils.contains(title, "资格预审：")) {
                    dataItem.setProjectName(StringUtils.substringAfter(title, "资格预审："));
                }
                if (StringUtils.contains(title, "招标公告：")) {
                    dataItem.setProjectName(StringUtils.substringAfter(title, "招标公告："));
                }
                dataItem.setDate(PageProcessorUtil.dataTxt(date));
                dataItem.setUrl(url);
                dataItem.setProvince(ProvinceUtil.get(title));

                log.debug("dataItem=={}", dataItem);

                log.debug(">>> download {}", url);
                httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(proxyService.getAliyunProxies()));
                try {
                    Element root = httpClientDownloader.download(new Request(url), getSite().toTask())
                            .getHtml().getDocument().body().select("body > div.main-news").first();
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(root);
                    dataItem.setFormatContent(formatContent);
                } catch (Exception e) {
                    log.error("", e);
                    log.error(">>> {} fetch content fail", url);
                }

                dataItems.add(dataItem);
            }
        }
        return dataItems;
    }

    @Override
    public void process(Page page) {
        log.debug(">>> url {}", page.getUrl().get());
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
                url = domain + "/b2b/web/two/indexinfoAction.do?actionType=showCgxjDetail&xjbm=" + parameter;
                break;
            case "showZbsMessage":
                url = domain + "/b2b/web/two/indexinfoAction.do?actionType=showZbsDetail&inviteid=" + parameter;
                break;
            case "showCgwzMessage":
                url = domain + "/b2b/web/two/indexinfoAction.do?actionType=showUrgentDetail&xxbh=" + parameter;
                break;
            case "showWxwzMessage":
                url = domain + "/b2b/web/two/indexinfoAction.do?actionType=showSaleDetail&xxbh=" + parameter;
                break;
            case "showOldMaterialMessage":
                url = domain + "/b2b/web/two/indexinfoAction.do?actionType=showOldMaterialDetail&xxbh=" + parameter;
                break;
            case "showInvalidMaterialMessage":
                url = domain + "/b2b/web/two/indexinfoAction.do?actionType=showInvalidMaterialDetail&xxbh=" + parameter;
                break;
            case "showCqggMessage":
                url = domain + "/b2b/web/two/indexinfoAction.do?actionType=showCqggDetail&xxbh=" + parameter;
                break;
            case "showPxjgmessage":
                url = domain + "/b2b/web/two/indexinfoAction.do?actionType=showPxjgDetail&xxbh=" + parameter;
                break;
            case "showMessage":
                url = domain + "/b2b/web/two/indexinfoAction.do?actionType=showMessage&xxbh=" + parameter;
                break;
            case "showZgysDetail":
                url = domain + "/b2b/web/two/indexinfoAction.do?actionType=showZgysDetail&zgyswjbm=" + parameter;
                break;
            case "showZhongbggMessage":
                url = domain + "/b2b/web/two/indexinfoAction.do?actionType=showZhongbggDetail&xxbh=" + parameter;
                break;
            case "showXcpMessage":
                url = domain + "/b2b/web/two/indexinfoAction.do?actionType=showNewProductDetail&dwbm=" + parameter;
                break;
            case "showYzbgsMessage":
                url = domain + "/b2b/web/two/indexinfoAction.do?actionType=showYzbgsDetail&xxbh=" + parameter;
                break;
            default:
        }

        return url;
    }
}
