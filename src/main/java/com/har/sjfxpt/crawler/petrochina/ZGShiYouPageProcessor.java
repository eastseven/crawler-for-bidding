package com.har.sjfxpt.crawler.petrochina;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.ProvinceUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
 * Created by Administrator on 2017/10/30.
 *
 * @author luofei
 */
@Slf4j
@Component
public class ZGShiYouPageProcessor implements BasePageProcessor {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private HttpClientDownloader httpClientDownloader;

    final static String PAGE_PARAMS = "pageParams";

    final static String KEY_URLS = "petrochina";

    public final static String formUrl = "http://eportal.energyahead.com/wps/portal/ebid/!ut/p/c5/hY3LdoIwEEC_xQ_wJGgCdRkRgjwKCGJg4wnWglBelofy9cWebrUzyztzL4jAtCXvLwlvL1XJvwADkXgkKywTX9QNjF0Mt9pm_-aKsmDYeOLhU44C9M_34dF7fgHhS_7rf3D4ZAgE71pVnEEIImmyLP8s5mqy-ObOsX20pBoCPmAQHb0M1pb5XVrjynZ8JW-902hB92zUtlt-zXmB6wDVXarRNI-LtAupnZWH2r2SRPbaYO2-7gjSo8PCQZIrMiiEOShRcwUeoMrvbUyN5c3Za_eQoyz-ZI3OhiIt6PpuW5wG2WYeJpnG1cWwOzX0NASumW4qRlTlyrogwk1kfxiU7HeCRHQdZZ0wL8um39pCHo35uhfVfOGNC_FmoLqXWLSOw1xOrBmoCwYvTnrFFZn9AIuHl4s!/dl3/d3/L0lDU0dabVppbW1BIS9JTFNBQ0l3a0FnUWlRQ0NLSkFJRVlrQWdUaVFDQ0JKQUlJbFNRQ0FKMkZEUS80QzFiOFVBZy83X0E5M0NBVDZKSzVMOTUwSVRMUlBPVDQzRzE3L2RldGFpbA!!/";

    @Override
    public void process(Page page) {
        handlePaging(page);

        handleContent(page);
    }

    @Override
    public Site getSite() {
        httpClientDownloader = new HttpClientDownloader();
        return SiteUtil.get();
    }

    @Override
    public void handlePaging(Page page) {
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int currentPage = (int) pageParams.get("pageNo");
        if (currentPage == 1) {
            Elements pager = page.getHtml().getDocument().body().select("#mainContent > table > tbody > tr > td > table > tbody > tr > td > div > table > tbody > tr > td > table > tbody > tr:nth-child(2) > td > div > span:nth-child(5)");
            String pageNum = pager.text();
            int num = Integer.parseInt(StringUtils.substringBefore(StringUtils.substringAfter(pageNum, "共"), "页").replaceAll(" ", ""));
            log.debug("num=={}", num);
            if (num > 1) {
                for (int i = 2; i <= num; i++) {
                    pageParams.put("pageNo", i);
                    Request request = new Request(page.getUrl().get());
                    request.setMethod(HttpConstant.Method.POST);
                    request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
                    request.putExtra(PAGE_PARAMS, pageParams);
                    page.addTargetRequest(request);
                }
            }
        }

    }

    @Override
    public void handleContent(Page page) {
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        Elements elements = page.getHtml().getDocument().body().select("#list_content > div > div");

        if (elements.isEmpty()) {
            log.error("fetch error, elements is empty");
            return;
        }

        List<ZGShiYouDataItem> dataItems = parseContent(elements);
        String type = (String) pageParams.get("type");
        dataItems.forEach(dataItem -> dataItem.setType(type));
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<ZGShiYouDataItem> dataItems = Lists.newArrayList();
        for (Element a : items) {
            String hrefId = a.select("div.f-left > a").attr("href");
            if (hrefId.contains("#")) {
                continue;
            } else {
                String id = StringUtils.substringBetween(hrefId, "(", ")");
                long value = stringRedisTemplate.boundSetOps(KEY_URLS).add(id);
                if (value == 0L) {
                    continue;
                } else {
                    String title = a.select("div.f-left > a").text();
                    String information = a.select("div.f-right").text();
                    String date = StringUtils.substringAfter(information, "         ");
                    String tenderer = a.select("div.f-right").attr("title");

                    ZGShiYouDataItem zgShiYouDataItem = new ZGShiYouDataItem(id);
                    zgShiYouDataItem.setTitle(title);
                    zgShiYouDataItem.setDate(date);
                    zgShiYouDataItem.setTenderer(tenderer);
                    zgShiYouDataItem.setProvince(ProvinceUtil.get(title));

                    Request request = new Request(formUrl);
                    Map<String, Object> param = Maps.newHashMap();

                    param.put("documentId", id);
                    log.info(">>> download id {}", id);
                    request.setMethod(HttpConstant.Method.POST);
                    request.setRequestBody(HttpRequestBody.form(param, "UTF-8"));
                    Page page = httpClientDownloader.download(request, SiteUtil.get().toTask());
                    String html = page.getHtml().getDocument().html();
                    Element element = page.getHtml().getDocument().body();
                    Element formatContentHtml = element.select("#wptheme_pageArea").first();
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(formatContentHtml);
                    String textContent = PageProcessorUtil.extractTextByWhitelist(formatContentHtml);
                    if (StringUtils.isNotBlank(html)) {
                        zgShiYouDataItem.setHtml(html);
                        zgShiYouDataItem.setFormatContent(formatContent);
                        zgShiYouDataItem.setTextContent(formatText(textContent));
                    }
                    dataItems.add(zgShiYouDataItem);
                }
            }

        }
        return dataItems;
    }

    public String formatText(String textContent) {
        String[] removeText = {
                "/wps/contenthandler/ebid/!ut/p/digest!mNxE8R0tr4bpN964Me4g1g/pm/oid:--portletwindowid--@oid:6_A95CAT6JK55Q50IHDU8Q6C1KO5",
                "/wps/contenthandler/ebid/!ut/p/digest!mNxE8R0tr4bpN964Me4g1g/um/secure/currentuser/profile?expandRefs=true",
                "首页 &gt;&gt; 公开招标公告 &gt;&gt; 详细信息",
                "                                 "
        };
        for (int i = 0; i < removeText.length; i++) {
            if (textContent.contains(removeText[i])) {
                textContent = textContent.replace(removeText[i], "");
            }
        }
        return textContent;
    }

}
