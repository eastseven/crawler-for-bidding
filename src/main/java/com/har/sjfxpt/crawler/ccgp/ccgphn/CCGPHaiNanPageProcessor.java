package com.har.sjfxpt.crawler.ccgp.ccgphn;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/11/7.
 * @author luo fei
 */
@Slf4j
@Component
public class CCGPHaiNanPageProcessor implements BasePageProcessor {

    private HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().toString();
        int num = Integer.parseInt(StringUtils.substringBetween(url, "currentPage=", "&begindate"));
        if (num == 1) {
            Elements pager = page.getHtml().getDocument().body().select("body > div.neibox > div.neibox02 > div.box > div > div.nei02_right > div.nei02_04 > div.nei02_04_02 > form > ul > li");
            String test = StringUtils.trim(pager.text());
            int pageNum = Integer.parseInt(StringUtils.substringBetween(test, "总共", "页"));
            for (int i = 2; i <= pageNum; i++) {
                String targetUrl = StringUtils.replace(url, "currentPage=1", "currentPage=" + i);
                page.addTargetRequest(new Request(targetUrl));
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Elements elements = page.getHtml().getDocument().body().select("body > div.neibox > div.neibox02 > div.box > div > div.nei02_right > div.nei02_04 > div.nei02_04_01 >ul >li");
        if (elements.isEmpty()) {
            log.error("fetch error, elements is empty");
            return;
        }
        List<CCGPHaiNanDataItem> dataItems = parseContent(elements);
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<CCGPHaiNanDataItem> dataItems = Lists.newArrayList();
        for (Element a : items) {
            Elements element = a.select("em>a");
            Elements type = a.select("span>tt");
            String date = a.select("i").text();
            String typeTxt = type.text();
            if (typeTxt.contains("| ")) {
                typeTxt = StringUtils.replace(typeTxt, "| ", "");
            }
            String url = element.attr("href");
            if (StringUtils.startsWith(url, "http")) {
                url = "http://www.ccgp-hainan.gov.cn" + url;
            }
            String title = element.text();
            String projectName = StringUtils.substringBeforeLast(title, "-");
            CCGPHaiNanDataItem haiNanDataItem = new CCGPHaiNanDataItem(url);
            haiNanDataItem.setType(typeTxt);
            haiNanDataItem.setUrl(url);
            haiNanDataItem.setTitle(title);
            haiNanDataItem.setDate(date);
            haiNanDataItem.setProjectName(StringUtils.defaultString(projectName, ""));

            // 正文处理
            try {
                Request request = new Request(url);
                Page page = httpClientDownloader.download(request, SiteUtil.get().toTask());

                Element body = page.getHtml().getDocument().body();
                Elements source = body.select("body > div.neibox > div.neibox02 > div.box > div > div.nei03_02 > div.basic");

                String purchaserAgent = StringUtils.substringBetween(source.text(), "信息来源：", " 公告类型：");
                purchaserAgent = StringUtils.strip(purchaserAgent);
                haiNanDataItem.setPurchaserAgent(purchaserAgent);

                String dateDetail = StringUtils.substringAfter(source.text(), "发表时间：");
                dateDetail = StringUtils.trim(dateDetail);
                if (StringUtils.isNotBlank(dateDetail)) {
                    haiNanDataItem.setDate(dateDetail);
                }

                Elements html = body.select("body > div.neibox > div.neibox02 > div.box > div > div.nei03_02 div.content01");
                String format = PageProcessorUtil.formatElementsByWhitelist(html.first());
                for (Element td : Jsoup.parse(format).select("td")) {
                    String tdText = td.text();
                    if (StringUtils.contains(tdText, "项目编号")) {
                        log.debug(">>> {}, {}", tdText, td.nextElementSibling().text());
                        haiNanDataItem.setProjectCode(StringUtils.trim(td.nextElementSibling().text()));
                    }

                    if (StringUtils.contains(tdText, "预算金额")) {
                        log.debug(">>> {}, {}", tdText, td.nextElementSibling().text());
                        haiNanDataItem.setBudget(StringUtils.trim(td.nextElementSibling().text()));
                    }

                    if (StringUtils.contains(tdText, "中标金额(万元)") ||
                            StringUtils.contains(tdText, "成交金额(万元)")) {
                        log.debug(">>> {}, {}", tdText, td.nextElementSibling().text());
                        haiNanDataItem.setTotalBidMoney(StringUtils.trim(td.nextElementSibling().text()));
                    }

                    if (StringUtils.contains(tdText, "中标供应商名称") ||
                            StringUtils.contains(tdText, "成交供应商名称")) {
                        log.debug(">>> {}, {}", tdText, td.nextElementSibling().text());
                        haiNanDataItem.setBidCompanyName(StringUtils.trim(td.nextElementSibling().text()));
                    }

                    if (StringUtils.contains(tdText, "中标供应商地址") ||
                            StringUtils.contains(tdText, "成交供应商地址")) {
                        log.debug(">>> {}, {}", tdText, td.nextElementSibling().text());
                        haiNanDataItem.setBidCompanyAddress(StringUtils.trim(td.nextElementSibling().text()));
                    }

                    if (StringUtils.contains(tdText, "采购人单位名称")) {
                        log.debug(">>> {}, {}", tdText, td.nextElementSibling().text());
                        haiNanDataItem.setPurchaser(StringUtils.trim(td.nextElementSibling().text()));
                    }
                }

                String formatContent = PageProcessorUtil.formatElementsByWhitelist(html.first());
                haiNanDataItem.setFormatContent(formatContent);
            } catch (Exception e) {
                log.error("", e);
                log.error("{} formatContent fetch fail", url);
            }
            dataItems.add(haiNanDataItem);
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
        return SiteUtil.get();
    }
}
