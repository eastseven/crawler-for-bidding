package com.har.sjfxpt.crawler.chengduconstruction;

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
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/12/5.
 */
@Slf4j
@Component
public class ChengDuConstructionPageProcessor implements BasePageProcessor {


    final static String PAGE_PARAMS = "pageParams";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int count = (int) pageParams.get("pageIndex");
        if (count == 1) {
            ChengDuConstructionAnnouncement chengDuConstructionAnnouncement = JSONObject.parseObject(page.getRawText(), ChengDuConstructionAnnouncement.class);
            int recordCount = chengDuConstructionAnnouncement.getRecordCount();
            int pageSize = (int) pageParams.get("pageSize");
            int pageNum = recordCount % pageSize == 0 ? recordCount / pageSize : recordCount / pageSize + 1;
            log.debug("pageNum=={}", pageNum);
            for (int i = 2; i <= 5; i++) {
                pageParams.put("pageIndex", i);
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
        ChengDuConstructionAnnouncement chengDuConstructionAnnouncement = JSONObject.parseObject(page.getRawText(), ChengDuConstructionAnnouncement.class);
        List<ChengDuConstructionAnnouncement.TablesBean.XmztbxxTableBean> xmztbxxTable = chengDuConstructionAnnouncement.getTables().getXmztbxxTable();
        List<ChengDuConstructionDataItem> dataItems = Lists.newArrayList();
        for (int i = 0; i < xmztbxxTable.size(); i++) {
            String urlId = xmztbxxTable.get(i).getFKXMSPID();
            if (StringUtils.isNotBlank(urlId)) {
                String href = "http://tz.xmchengdu.gov.cn/Zftz/NewWeb/XMXXDetail.aspx?itemID=" + urlId;
                String title = xmztbxxTable.get(i).getXMMC();
                String projectName = xmztbxxTable.get(i).getBDMC();
                String label = xmztbxxTable.get(i).getZBHZH();
                String date = xmztbxxTable.get(i).getFBSJ();
                String type = xmztbxxTable.get(i).getZBFS();

                ChengDuConstructionDataItem chengDuConstructionDataItem = new ChengDuConstructionDataItem(href);
                chengDuConstructionDataItem.setUrl(href);
                chengDuConstructionDataItem.setTitle(title);
                chengDuConstructionDataItem.setProjectName(projectName);
                chengDuConstructionDataItem.setLabel(label);
                chengDuConstructionDataItem.setDate(PageProcessorUtil.dataTxt(date));
                chengDuConstructionDataItem.setType(type);

                if (PageProcessorUtil.timeCompare(date)) {
                    log.warn("href {} is not the sameday", href);
                } else {
                    Page page1 = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    Elements elements = page1.getHtml().getDocument().body().select("#ctl00_MainBody_divXMXXExist");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        chengDuConstructionDataItem.setFormatContent(formatContent);
                        dataItems.add(chengDuConstructionDataItem);
                    }
                }

            }
        }

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
