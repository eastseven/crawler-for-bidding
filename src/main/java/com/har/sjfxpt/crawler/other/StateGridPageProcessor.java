package com.har.sjfxpt.crawler.other;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.ProvinceUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.har.sjfxpt.crawler.other.StateGridPageProcessor.URL_01;
import static com.har.sjfxpt.crawler.other.StateGridPageProcessor.URL_02;

/**
 * @author dongqi
 */
@Slf4j
@Component
@SourceConfig(code = SourceCode.SGCC, sources = {
        @Source(type = "招标", url = URL_01),
        @Source(type = "中标", url = URL_02)
})
public class StateGridPageProcessor implements BasePageProcessor {

    public static final String URL_01 = "http://ecp.sgcc.com.cn/topic_project_list.jsp?columnName=topic10";
    public static final String URL_02 = "http://ecp.sgcc.com.cn/topic_news_list.jsp?columnName=topic23&column_code1=014001007&column_code2=014002003";

    @Override
    public void process(Page page) {

        Object fetchAll = page.getRequest().getExtra("fetchAll");
        if (fetchAll != null && (boolean) fetchAll) {
            handlePaging(page);
        }

        handleContent(page);
    }

    @Override
    public Site getSite() {
        return SiteUtil.get();
    }

    @Override
    public void handlePaging(Page page) {
        Elements elements = page.getHtml().getDocument().body().select("div.contentRight div.page a");
        if (elements.isEmpty()) {
            log.warn("{}, css [div.contentRight div.page a] is empty", page.getUrl().get());
            return;
        }
        Element pager = elements.last().previousElementSibling();
        String text = pager.text();
        String link = pager.attr("href");
        log.info(">>> text {}, link {}", text, link);

        Object typeValue = page.getRequest().getExtra("type");
        int totalPages = Integer.parseInt(text);
        for (int index = 2; index <= totalPages; index++) {
            String url = "http://ecp.sgcc.com.cn/" + StringUtils.substringBeforeLast(link, "=") + "=" + index;
            Request request = new Request(url);
            request.putExtra("type", typeValue);
            request.putExtra("fetchAll", false);
            log.info(">>> add page url {}", url);

            page.addTargetRequest(request);
        }

    }

    @Override
    public void handleContent(Page page) {
        Object type = page.getRequest().getExtra("type");
        if (type == null) {
            log.error("{}, request type is null", page.getUrl().get());
            return;
        }

        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        String css = "";
        if ("招标".equalsIgnoreCase(type.toString())) {
            css = "div.contentRight table tr";
            dataItems = parseContent(page.getHtml().getDocument().select(css));

        } else if ("中标".equalsIgnoreCase(type.toString())) {
            css = "div.contentRight div.titleList li.titleList_bj";

            Elements items = page.getHtml().getDocument().select(css);
            for (Element element : items) {
                String title = element.select("a").attr("title");
                String date = element.select("div.titleList_02").text();

                String onclick = element.select("a").attr("onclick");
                String url = StringUtils.substringBetween(onclick, "(", ")").replaceAll("'", "");
                String id1 = StringUtils.split(url, ",")[0];
                String id2 = StringUtils.split(url, ",")[1];
                url = "http://ecp.sgcc.com.cn/html/news/" + StringUtils.strip(id1) + '/' + StringUtils.strip(id2) + ".html";
                log.debug(">>> 4.{}", url);

                String html = null;
                try {
                    Elements text = Jsoup.parse(new URL(url), 60 * 1000).body().select("div.article div.bot_list");
                    html = PageProcessorUtil.formatElementsByWhitelist(text.first());
                } catch (IOException e) {
                    log.error("{} fetch fail", url);
                    log.error("", e);
                }

                BidNewsOriginal dataItem = new BidNewsOriginal(url, SourceCode.SGCC);

                dataItem.setTitle(title);
                dataItem.setDate(PageProcessorUtil.dataTxt(date));
                dataItem.setFormatContent(html);
                dataItem.setProvince(ProvinceUtil.get(title));
                dataItem.setType("中标");

                dataItems.add(dataItem);
            }
        }

        if (!CollectionUtils.isEmpty(dataItems)) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<BidNewsOriginal> dataItems = Lists.newArrayList();

        for (Element tr : items) {
            Elements row = tr.select("td.black40");
            if (row.isEmpty()) continue;

            String status = row.get(0).text();
            String code = row.get(1).text();
            String date = row.get(3).text();
            Element link = row.get(2).select("a").first();
            String name = link.attr("title");
            String onclick = link.attr("onclick");

            String url = StringUtils.substringBetween(onclick, "(", ")").replaceAll("'", "").replace(",", "/");
            url = "http://ecp.sgcc.com.cn/html/project/" + url + ".html";

            log.debug("\n{},{},{},{},{}\n", status, code, name, date, url);

            String html = null, purchaser = null;
            try {
                Element body = Jsoup.parse(new URL(url), 60 * 1000).body();
                html = PageProcessorUtil.formatElementsByWhitelist(body.select("div.article").first());
                html = Jsoup.parse(html).select("table").toString();

                Elements text = body.select("div.article table");
                for (Element element : text.select("tr")) {
                    Elements rightTd = element.select("td[align='right']");
                    if (StringUtils.contains(rightTd.text(), "招标人")) {
                        purchaser = rightTd.first().nextElementSibling().text();
                    }
                }
            } catch (IOException e) {
                log.error("", e);
                log.error("{} fetch fail", url);
            }

            BidNewsOriginal dataItem = new BidNewsOriginal(url, SourceCode.SGCC);

            dataItem.setProjectCode(code);
            dataItem.setTitle(name);
            dataItem.setDate(PageProcessorUtil.dataTxt(date));
            dataItem.setFormatContent(html);
            dataItem.setProvince(ProvinceUtil.get(name));
            dataItem.setType("招标");
            dataItem.setPurchaser(purchaser);

            dataItems.add(dataItem);
        }

        return dataItems;
    }
}
