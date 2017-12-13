package com.har.sjfxpt.crawler.ggzyprovincial.ggzyshanxi;

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

/**
 * Created by Administrator on 2017/12/12.
 */
@Slf4j
@Component
public class GGZYShanXiPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    final static String PAGE_PARAMS = "pageParams";

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int pageNum = Integer.parseInt(pageParams.get("pageIndex").toString());
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("#Page_TotalPage");
            int totalPage = Integer.parseInt(elements.attr("value").toString());
            for (int i = 2; i <= totalPage; i++) {
                pageParams.put("pageIndex", i);
                Request request = new Request(url);
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
                request.putExtra("pageParams", pageParams);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        Elements elements = page.getHtml().getDocument().body().select("body > table > tbody > tr");
        if (elements.isEmpty()) {
            log.error("fetch error, elements is empty");
            return;
        }
        List<GGZYShanXiDataItem> dataItems = parseContent(elements);
    }

    @Override
    public List parseContent(Elements items) {
        for (Element element : items) {
            String href = element.select("a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                if (StringUtils.startsWith(href, "../")) {
                    href = StringUtils.replace(href, "../", "http://prec.sxzwfw.gov.cn/ThemeSX/");
                }
                String title = element.select("a").attr("title");
                String date = element.select("td:nth-child(3)").text();
                GGZYShanXiDataItem ggzyShanXiDataItem = new GGZYShanXiDataItem(href);
                ggzyShanXiDataItem.setTitle(title);
                ggzyShanXiDataItem.setUrl(href);
                ggzyShanXiDataItem.setDate(PageProcessorUtil.dataTxt(date));

                Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                Elements elements = page.getHtml().getDocument().body().select("");
            }

        }
        return null;
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
