package com.har.sjfxpt.crawler.baowu;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.ProvinceUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
 * Created by Administrator on 2017/12/22.
 */
@Slf4j
@Component
public class BaoWuPageProcessor implements BasePageProcessor {

    final static String PAGE_PARAMS = "pageParams";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int pageNow = Integer.parseInt(pageParams.get("pageNow").toString());
        if (pageNow == 1) {
            BaoWuAnnouncement baoWuAnnouncement = JSONObject.parseObject(page.getRawText(), BaoWuAnnouncement.class);
            int pages = baoWuAnnouncement.getObj().getPages();
            for (int i = 2; i <= pages; i++) {
                pageParams.put("pageNow", i);
                Request request = new Request(url);
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
                request.putExtra(PAGE_PARAMS, pageParams);
                page.addTargetRequest(request);
            }
        }

    }

    @Override
    public void handleContent(Page page) {
        List<BaoWuDataItem> dataItems = parseContent(page);
    }

    public List parseContent(Page page) {
        List<BaoWuDataItem> dataItems = Lists.newArrayList();
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        String typeField = pageParams.get("type").toString();
        if ("purchase".equalsIgnoreCase(typeField)) {
            BaoWuAnnouncement baoWuAnnouncement = JSONObject.parseObject(page.getRawText(), BaoWuAnnouncement.class);
            List<BaoWuAnnouncement.ObjBean.NewsPageBean> newsPageBeanList = baoWuAnnouncement.getObj().getNewsPage();
            for (BaoWuAnnouncement.ObjBean.NewsPageBean newsPageBean : newsPageBeanList) {
                String id = newsPageBean.getId();
                if (StringUtils.isNotBlank(id)) {
                    String href = "http://rfq.ouyeelbuy.com/rfqNotice/bidListInfo?id=" + id;
                    String title = newsPageBean.getTitle();
                    String purchaser = newsPageBean.getOuName();
                    String date = newsPageBean.getIssueDate();
                    BaoWuDataItem baoWuDataItem = new BaoWuDataItem(href);
                    baoWuDataItem.setUrl(href);
                    baoWuDataItem.setTitle(title);
                    baoWuDataItem.setPurchaser(purchaser);
                    baoWuDataItem.setDate(PageProcessorUtil.dataTxt(date));
                    baoWuDataItem.setProvince(ProvinceUtil.get(title));
                    baoWuDataItem.setType("采购公告");

                    Page page1 = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    Elements elements = page1.getHtml().getDocument().body().select("body > div.xj_content > div > div.left");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        baoWuDataItem.setFormatContent(formatContent);
                        dataItems.add(baoWuDataItem);
                    }
                }
            }
        } else {
            BaoWuAnnouncementOther baoWuAnnouncementOther = JSONObject.parseObject(page.getRawText(), BaoWuAnnouncementOther.class);
            List<BaoWuAnnouncementOther.ObjBean.NewsPageBean> newsPageBeanList = baoWuAnnouncementOther.getObj().getNewsPage();
            for (BaoWuAnnouncementOther.ObjBean.NewsPageBean newsPageBean : newsPageBeanList) {
                String href = newsPageBean.getNoticeUrl();
                if (StringUtils.isNotBlank(href)) {
                    String title = newsPageBean.getNoticeName();
                    String purchaser = newsPageBean.getOuName();
                    String date = newsPageBean.getIssueDate();
                    BaoWuDataItem baoWuDataItem = new BaoWuDataItem(href);
                    baoWuDataItem.setUrl(href);
                    baoWuDataItem.setTitle(title);
                    baoWuDataItem.setPurchaser(purchaser);
                    baoWuDataItem.setDate(PageProcessorUtil.dataTxt(date));
                    baoWuDataItem.setProvince(ProvinceUtil.get(title));
                    if ("0".equalsIgnoreCase(typeField)) {
                        baoWuDataItem.setType("招标公告");
                        Page page1 = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                        
                    }
                    if ("1".equalsIgnoreCase(typeField)) {
                        baoWuDataItem.setType("中标结果");
                        Page page1 = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                        Elements elements = page1.getHtml().getDocument().body().select("body > div > div > table:nth-child(1) > tbody > tr > th > div > table > tbody > tr > td > div");
                        String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                        if (StringUtils.isNotBlank(formatContent)) {
                            baoWuDataItem.setFormatContent(formatContent);
                            dataItems.add(baoWuDataItem);
                        }
                    }
                }
            }
        }
        return dataItems;
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
