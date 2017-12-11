package com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebei;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/12/8.
 */
@Slf4j
@Component
public class GGZYHeBeiPageProcessor implements BasePageProcessor {

    HttpClientDownloader httpClientDownloader;

    final static int ARTICLE_NUM = 10;

    final static String PAGE_PARAMS = "pageParams";

    @Override
    public void handlePaging(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get(PAGE_PARAMS);
        String url = page.getUrl().get();
        int pageNum = Integer.parseInt(StringUtils.substringBetween(page.getUrl().get(), "pn=", "&rn="));
        if (pageNum == 0) {
            GGZYHeBeiAnnouncement ggzyHeBeiAnnouncement = JSONObject.parseObject(page.getRawText(), GGZYHeBeiAnnouncement.class);
            int totalcount = Integer.parseInt(ggzyHeBeiAnnouncement.getResult().getTotalcount());
            int pageCount = totalcount % ARTICLE_NUM == 0 ? totalcount / ARTICLE_NUM : totalcount / ARTICLE_NUM + 1;
            log.info("pageCount=={}", pageCount);
            if (pageCount >= 20) {
                for (int i = 1; i <= 20; i++) {
                    String urlTarget = url.replace("pn=0", "pn=" + i);
                    Request request = new Request(urlTarget);
                    request.putExtra(PAGE_PARAMS, pageParams);
                    page.addTargetRequest(request);
                }
            } else {
                for (int i = 1; i <= pageCount; i++) {
                    String urlTarget = url.replace("pn=0", "pn=" + i);
                    Request request = new Request(urlTarget);
                    request.putExtra(PAGE_PARAMS, pageParams);
                    page.addTargetRequest(request);
                }
            }

        }
    }

    @Override
    public void handleContent(Page page) {
        Map<String, String> pageParams = (Map<String, String>) page.getRequest().getExtras().get(PAGE_PARAMS);
        GGZYHeBeiAnnouncement ggzyHeBeiAnnouncement = JSONObject.parseObject(page.getRawText(), GGZYHeBeiAnnouncement.class);
        List<GGZYHeBeiDataItem> dataItems = Lists.newArrayList();
        List<GGZYHeBeiAnnouncement.ResultBean.RecordsBean> recordsBeanList = ggzyHeBeiAnnouncement.getResult().getRecords();
        for (int i = 0; i < recordsBeanList.size(); i++) {
            String href = recordsBeanList.get(i).getLink();
            String date = recordsBeanList.get(i).getDate();
            if (!StringUtils.startsWith(href, "http:")) {
                href = "http://www.hebpr.cn" + href;
            }
            GGZYHeBeiDataItem ggzyHeBeiDataItem = new GGZYHeBeiDataItem(href);
            ggzyHeBeiDataItem.setUrl(href);
            ggzyHeBeiDataItem.setDate(PageProcessorUtil.dataTxt(date));

            if (PageProcessorUtil.timeCompare(ggzyHeBeiDataItem.getDate())) {
                log.warn("{} is not the same day", ggzyHeBeiDataItem.getUrl());
            } else {
                Page page1 = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                Elements elements = page1.getHtml().getDocument().body().select("#mainContent");
                Elements elements1 = page1.getHtml().getDocument().body().select("body > div.ewb-container.ewb-pb30 > div.ewb-main > div > div.ewb-poll-hd > h1");
                String title = elements1.text();
                if (StringUtils.containsIgnoreCase(title, "<font color='#CC0000'>") || StringUtils.containsIgnoreCase(title, "</font>")) {
                    title = StringUtils.remove(title, "<font color='#CC0000'>");
                    title = StringUtils.remove(title, "</font>");
                }
                ggzyHeBeiDataItem.setTitle(title);
                String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                if (StringUtils.isNotBlank(formatContent)) {
                    ggzyHeBeiDataItem.setFormatContent(formatContent);
                    dataItems.add(ggzyHeBeiDataItem);
                }
            }
        }

        String type = pageParams.get("type");
        String businessType = pageParams.get("businessType");
        dataItems.forEach(dataItem -> {
            dataItem.setType(type);
            dataItem.setBusinessType(businessType);
            dataItem.setForceUpdate(true);
        });
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }

    }

    @Override
    public List parseContent(Elements items) {
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
