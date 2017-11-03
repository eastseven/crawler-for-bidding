package com.har.sjfxpt.crawler.zgzt;

import com.alibaba.fastjson.JSONObject;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/11/1.
 */
@Slf4j
@Component
public class ZGZhaoTouPageProcessor implements BasePageProcessor {

    @Value("${app.proxy.pool.formTemplate}")
    String formTemplate;

    @Value("${app.proxy.pool.formTemplateAnnouncement}")
    String formTemplateAnnouncement;

    HttpClientDownloader httpClientDownloader;

    final static String PAGE_PARAMS = "pageParams";

    final static String URL = "http://www.cebpubservice.com/ctpsp_iiss/searchbusinesstypebeforedooraction/getStringMethod.do";

    final static String SHOW_DETAIL = "http://www.cebpubservice.com/ctpsp_iiss/SecondaryAction/findDetails.do";

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
        Map<String, Object> PageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int currentPage = (int) PageParams.get("pageNo");
        if (currentPage == 1) {
            ChinaTenderingAndBidding data = JSONObject.parseObject(page.getRawText(), ChinaTenderingAndBidding.class);
            int size = data.getObject().getPage().getTotalPage();
            log.debug("size=={}", size);
            for (int i = 2; i <= size; i++) {
                PageParams.put("pageNo", i);
                Request request = new Request(URL);
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(PageParams, "UTF-8"));
                request.putExtra(PAGE_PARAMS, PageParams);
                page.addTargetRequest(request);
            }

        }
    }

    @Override
    public void handleContent(Page page) {
        List<ZGZhaoTouDataItem> dataItems = parseContent(page);
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

    public List parseContent(Page page) {
        Map<String, Object> PageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);

        ChinaTenderingAndBidding data = JSONObject.parseObject(page.getRawText(), ChinaTenderingAndBidding.class);
        List<ChinaTenderingAndBidding.ObjectBean.ReturnlistBean> lists = data.getObject().getReturnlist();
        List<ZGZhaoTouDataItem> dataItems = Lists.newArrayList();
        for (ChinaTenderingAndBidding.ObjectBean.ReturnlistBean row : lists) {
            String type = (String) PageParams.get("businessType");
            String id = row.getTenderProjectCode();
            String title = row.getBusinessObjectName();
            String professional = row.getIndustriesType();
            String province = row.getRegionName() + "";
            String platform = row.getTransactionPlatfName();
            String date = row.getReceiveTime();
            ZGZhaoTouDataItem zgZhaoTouDataItem = new ZGZhaoTouDataItem(id);
            zgZhaoTouDataItem.setTitle(title);
            zgZhaoTouDataItem.setProfessional(professional);
            zgZhaoTouDataItem.setProvince(ProvinceUtil.get(province));
            zgZhaoTouDataItem.setPlatform(platform);
            zgZhaoTouDataItem.setDate(PageProcessorUtil.dataTxt(date));
            zgZhaoTouDataItem.setType(type);

            if (type.equals("招标项目")) {
                Request request = new Request(SHOW_DETAIL);
                Map<String, Object> param = Maps.newHashMap();
                param.put("schemaVersion", row.getSchemaVersion());
                param.put("businessKeyWord", "tenderProject");
                param.put("tenderProjectCode", row.getTenderProjectCode());
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(param, "UTF-8"));
                Page page1 = httpClientDownloader.download(request, SiteUtil.get().toTask());
                ChinaTenderingAndBiddingContent data1 = JSONObject.parseObject(page1.getRawText(), ChinaTenderingAndBiddingContent.class);
                List<ChinaTenderingAndBiddingContent.ObjectBean.TenderProjectBean> tenderProjectBeanList = data1.getObject().getTenderProject();

                Map<String, String> map = new HashMap<>();

                map.put("createTime", tenderProjectBeanList.get(0).getCreateTime());
                map.put("regionCode", tenderProjectBeanList.get(0).getRegionCode());
                map.put("tendererName", tenderProjectBeanList.get(0).getTendererName());
                map.put("approveDeptName", tenderProjectBeanList.get(0).getApproveDeptName());
                map.put("tenderAgencyName", tenderProjectBeanList.get(0).getTenderAgencyName());
                map.put("tenderAgencyCodeType", tenderProjectBeanList.get(0).getTenderAgencyCodeType());
                map.put("superviseDeptName", tenderProjectBeanList.get(0).getSuperviseDeptName());
                map.put("bulletinContent", tenderProjectBeanList.get(0).getBulletinContent());
                map.put("approveDeptCode", tenderProjectBeanList.get(0).getApproveDeptCode());
                map.put("attachmentCode", tenderProjectBeanList.get(0).getAttachmentCode());
                map.put("tenderProjectCode", tenderProjectBeanList.get(0).getTenderProjectCode());
                map.put("bulletinName", tenderProjectBeanList.get(0).getBulletinName());
                map.put("superviseDeptCodeType", tenderProjectBeanList.get(0).getSuperviseDeptCodeType());
                map.put("tendererCode", tenderProjectBeanList.get(0).getTendererCode());
                map.put("tenderAgencyCode", tenderProjectBeanList.get(0).getTenderAgencyCode());
                map.put("tenderOrganizeForm", tenderProjectBeanList.get(0).getTenderOrganizeForm());
                map.put("industriesType", tenderProjectBeanList.get(0).getIndustriesType());
                map.put("superviseDeptCode", tenderProjectBeanList.get(0).getSuperviseDeptCode());
                map.put("schemaVersion", tenderProjectBeanList.get(0).getSchemaVersion());
                map.put("bulletinssueTime", tenderProjectBeanList.get(0).getBulletinssueTime());

                String test = formTemplate;
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    log.debug("key=={}value=={}", entry.getKey(), entry.getValue());
                    if (StringUtils.isNotBlank(entry.getValue())) {
                        test = StringUtils.replace(test, "{" + entry.getKey() + "}", entry.getValue());
                    }
                }
                log.debug("test=={}", test);
                if (StringUtils.isNotBlank(test)) {
                    zgZhaoTouDataItem.setHtml(test);
                    zgZhaoTouDataItem.setFormatContent(test);
                    dataItems.add(zgZhaoTouDataItem);
                }
            }

            if (type.equals("招标公告")) {
                log.debug("list=={}", row.getTenderProjectCode());
                log.debug("list=={}", row.getSchemaVersion());
                Request request = new Request(SHOW_DETAIL);
                Map<String, Object> param = Maps.newHashMap();
                param.put("schemaVersion", row.getSchemaVersion());
                param.put("businessKeyWord", "tenderBulletin");
                param.put("tenderProjectCode", row.getTenderProjectCode());
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(param, "UTF-8"));
                Page page1 = httpClientDownloader.download(request, SiteUtil.get().toTask());
                ChinaTenderingAndBiddingAnnouncement data1 = JSONObject.parseObject(page1.getRawText(), ChinaTenderingAndBiddingAnnouncement.class);
                List<ChinaTenderingAndBiddingAnnouncement.ObjectBean.TenderBulletinBean> tenderBulletin = data1.getObject().getTenderBulletin();

                if (tenderBulletin.size() > 0) {
                    Map<String, String> map = new HashMap<>();
                    String formatContent="";
                    map.put("attachmentCode", tenderBulletin.get(0).getAttachmentCode());
                    map.put("bulletinName", tenderBulletin.get(0).getBulletinName());
                    map.put("bulletinssueTime", tenderBulletin.get(0).getBulletinssueTime());
                    map.put("schemaVersion", tenderBulletin.get(0).getSchemaVersion());
                    map.put("sourceUrl", tenderBulletin.get(0).getSourceUrl());
                    map.put("tenderProjectCode", tenderBulletin.get(0).getTenderProjectCode());
                    map.put("transactionPlatfCode", tenderBulletin.get(0).getTransactionPlatfCode());
                    for (ChinaTenderingAndBiddingAnnouncement.ObjectBean.TenderBulletinBean tenderBulletinBean:tenderBulletin){
                        formatContent=formatContent+"<p>"+ tenderBulletinBean.getBulletinContent()+"<p>";
                    }
                    if(StringUtils.isNotBlank(formatContent)){
                        map.put("formatContent",formatContent);
                    }

                    String test = formTemplateAnnouncement;
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        log.debug("key=={}value=={}", entry.getKey(), entry.getValue());
                        if (StringUtils.isNotBlank(entry.getValue())) {
                            test = StringUtils.replace(test, "{" + entry.getKey() + "}", entry.getValue());
                        }
                    }
                    log.debug("test=={}", test);
                    if (StringUtils.isNotBlank(test)) {
                        zgZhaoTouDataItem.setHtml(test);
                        zgZhaoTouDataItem.setFormatContent(test);
                        dataItems.add(zgZhaoTouDataItem);
                    }

                }

            }

        }
        return dataItems;
    }

}
