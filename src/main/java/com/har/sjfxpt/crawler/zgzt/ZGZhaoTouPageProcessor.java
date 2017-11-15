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
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * Created by Administrator on 2017/11/1.
 */
@Slf4j
@Component
public class ZGZhaoTouPageProcessor implements BasePageProcessor {

    final String KEY_URLS = "zhong_guo_zhao_tou";

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Value("${app.html.template.table}")
    String formTemplate;

    @Value("${app.html.template.announcement}")
    String formTemplateAnnouncement;

    @Value("${app.html.template.bidOpen}")
    String formTemplateBidOpen;

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

    public Page pageGenerator(ChinaTenderingAndBidding.ObjectBean.ReturnlistBean row, String type) {
        Request request = new Request(SHOW_DETAIL);
        Map<String, Object> param = Maps.newHashMap();
        param.put("schemaVersion", row.getSchemaVersion());
        switch (type) {
            case "招标项目":
                param.put("businessKeyWord", "tenderProject");
                break;
            case "招标公告":
                param.put("businessKeyWord", "tenderBulletin");
                break;
            case "中标公告":
                param.put("businessKeyWord", "winBidBulletin");
                break;
            case "开标记录":
                param.put("businessKeyWord", "openBidRecord");
                break;
            case "评标公示":
                param.put("businessKeyWord", "winCandidateBulletin");
                break;
        }
        param.put("tenderProjectCode", row.getTenderProjectCode());
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(param, "UTF-8"));
        Page page = httpClientDownloader.download(request, SiteUtil.get().toTask());
        return page;
    }

    public List parseContent(Page page) {
        Map<String, Object> PageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);

        ChinaTenderingAndBidding data = JSONObject.parseObject(page.getRawText(), ChinaTenderingAndBidding.class);
        List<ChinaTenderingAndBidding.ObjectBean.ReturnlistBean> lists = data.getObject().getReturnlist();
        List<ZGZhaoTouDataItem> dataItems = Lists.newArrayList();
        for (ChinaTenderingAndBidding.ObjectBean.ReturnlistBean row : lists) {
            String type = (String) PageParams.get("businessType");
            String id = row.getTenderProjectCode();
            long value = stringRedisTemplate.boundSetOps(KEY_URLS).add(id);
            if (value == 0L) {
                log.warn("{} is duplication", id);
                continue;
            } else {
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

                log.debug("ZGZhaoTouDataItem=={}", zgZhaoTouDataItem);

                if (type.equals("招标项目")) {
                    Page page1 = pageGenerator(row, type);
                    ChinaTenderingAndBiddingContent data1 = JSONObject.parseObject(page1.getRawText(), ChinaTenderingAndBiddingContent.class);
                    List<ChinaTenderingAndBiddingContent.ObjectBean.TenderProjectBean> tenderProjectBeanList = data1.getObject().getTenderProject();

                    Map<String, String> map = Maps.newHashMap();

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
                    if (StringUtils.isNotBlank(test)) {
                        zgZhaoTouDataItem.setFormatContent(test);
                        dataItems.add(zgZhaoTouDataItem);
                    }
                }

                if (type.equals("招标公告")) {
                    Page page1 = pageGenerator(row, type);
                    ChinaTenderingAndBiddingAnnouncement data1 = JSONObject.parseObject(page1.getRawText(), ChinaTenderingAndBiddingAnnouncement.class);
                    List<ChinaTenderingAndBiddingAnnouncement.ObjectBean.TenderBulletinBean> tenderBulletin = data1.getObject().getTenderBulletin();

                    if (tenderBulletin.size() > 0) {
                        Map<String, String> map = Maps.newHashMap();
                        String formatContent = "";
                        map.put("attachmentCode", tenderBulletin.get(0).getAttachmentCode());
                        map.put("bulletinName", tenderBulletin.get(0).getBulletinName());
                        map.put("bulletinssueTime", tenderBulletin.get(0).getBulletinssueTime());
                        map.put("schemaVersion", tenderBulletin.get(0).getSchemaVersion());
                        map.put("sourceUrl", tenderBulletin.get(0).getSourceUrl());
                        map.put("tenderProjectCode", tenderBulletin.get(0).getTenderProjectCode());
                        map.put("transactionPlatfCode", tenderBulletin.get(0).getTransactionPlatfCode());
                        for (ChinaTenderingAndBiddingAnnouncement.ObjectBean.TenderBulletinBean tenderBulletinBean : tenderBulletin) {
                            formatContent = formatContent + "<p>" + tenderBulletinBean.getBulletinContent() + "<p>";
                        }
                        if (StringUtils.isNotBlank(formatContent)) {
                            map.put("formatContent", formatContent);
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

                if (type.equals("中标公告")) {
                    Page page1 = pageGenerator(row, type);
                    ChinaTenderingAndBinddingWinBidBulletin data1 = JSONObject.parseObject(page1.getRawText(), ChinaTenderingAndBinddingWinBidBulletin.class);
                    try {
                        List<ChinaTenderingAndBinddingWinBidBulletin.ObjectBean.WinBidBulletinBean> winBidBulletinBeanList = data1.getObject().getWinBidBulletin();
                        log.debug("winBidBulletinBeanList=={}", winBidBulletinBeanList.size());
                        if (winBidBulletinBeanList.size() > 0) {
                            Map<String, String> map = Maps.newHashMap();
                            String formatContent = "";
                            map.put("transactionPlatfCode", winBidBulletinBeanList.get(0).getTransactionPlatfCode());
                            map.put("attachmentCode", winBidBulletinBeanList.get(0).getAttachmentCode());
                            map.put("sourceUrl", winBidBulletinBeanList.get(0).getSourceUrl());
                            map.put("tenderProjectCode", winBidBulletinBeanList.get(0).getTenderProjectCode());
                            map.put("bulletinssueTime", winBidBulletinBeanList.get(0).getBulletinssueTime());
                            map.put("bulletinName", winBidBulletinBeanList.get(0).getBulletinName());
                            for (ChinaTenderingAndBinddingWinBidBulletin.ObjectBean.WinBidBulletinBean winBidBulletinBean : winBidBulletinBeanList) {
                                formatContent = formatContent + "<p>" + winBidBulletinBean.getBulletinContent() + "</p>";
                            }
                            if (StringUtils.isNotBlank(formatContent)) {
                                map.put("formatContent", formatContent);
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

                    } catch (Exception e) {
                        log.debug("The json data is empty, {}", page1);
                    }

                }

                if (type.equals("开标记录")) {
                    Page page1 = pageGenerator(row, type);
                    ChinaTenderingAndBiddingOpenBidRecord data1 = JSONObject.parseObject(page1.getRawText(), ChinaTenderingAndBiddingOpenBidRecord.class);
                    try {
                        List<ChinaTenderingAndBiddingOpenBidRecord.ObjectBean.OpenBidRecordBeanX> openBidRecordBeanList = data1.getObject().getOpenBidRecord();
                        if (openBidRecordBeanList != null) {
                            Map<String, String> map = Maps.newHashMap();
                            String formatContent = "";
                            map.put("transactionPlatfCode", openBidRecordBeanList.get(0).getOpenBidRecord().getTransactionPlatfCode());
                            map.put("bidSectionCodes", openBidRecordBeanList.get(0).getOpenBidRecord().getBidSectionCodes());
                            map.put("bidOpeningTime", openBidRecordBeanList.get(0).getOpenBidRecord().getBidOpeningTime());
                            map.put("openBidRecordName", openBidRecordBeanList.get(0).getOpenBidRecord().getOpenBidRecordName());
                            if (openBidRecordBeanList.get(0).getServiceOpenBidList() != null) {
                                for (int i = 0; i < openBidRecordBeanList.get(0).getServiceOpenBidList().size(); i++) {
                                    String table = "<tr><td>" + openBidRecordBeanList.get(0).getServiceOpenBidList().get(i).getBidderName() + "</td><td>" + openBidRecordBeanList.get(0).getServiceOpenBidList().get(i).getBidSectionName() + "</td><td>" + openBidRecordBeanList.get(0).getServiceOpenBidList().get(i).getVerifyTime() + "</td></tr>";
                                    formatContent = formatContent + table;
                                }
                            }
                            if (openBidRecordBeanList.get(0).getProjectOpenBidList() != null) {
                                for (int i = 0; i < openBidRecordBeanList.get(0).getProjectOpenBidList().size(); i++) {
                                    String table = "<tr><td>" + openBidRecordBeanList.get(0).getProjectOpenBidList().get(i).getBidderName() + "</td><td>" + openBidRecordBeanList.get(0).getProjectOpenBidList().get(i).getBidAmount() + "</td><td>" + openBidRecordBeanList.get(0).getProjectOpenBidList().get(i).getVerifyTime() + "</td></tr>";
                                    formatContent = formatContent + table;
                                }
                            }
                            if (StringUtils.isNotBlank(formatContent)) {
                                map.put("formatContent", formatContent);
                            }
                            String test = formTemplateBidOpen;
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

                    } catch (Exception e) {
                        log.debug("The json data is empty");
                    }
                }

                if (type.equals("评标公示")) {
                    Page page1 = pageGenerator(row, type);
                    ChinaTenderingAndBiddingEvaluation data1 = JSONObject.parseObject(page1.getRawText(), ChinaTenderingAndBiddingEvaluation.class);
                    try {
                        List<ChinaTenderingAndBiddingEvaluation.ObjectBean.WinCandidateBulletinBean> winCandidateBulletinBeanList = data1.getObject().getWinCandidateBulletin();
                        log.debug("winBidBulletinBeanList=={}", winCandidateBulletinBeanList.size());
                        if (winCandidateBulletinBeanList.size() > 0) {
                            Map<String, String> map = Maps.newHashMap();
                            String formatContent = "";
                            map.put("transactionPlatfCode", winCandidateBulletinBeanList.get(0).getTransactionPlatfCode());
                            map.put("attachmentCode", winCandidateBulletinBeanList.get(0).getAttachmentCode());
                            map.put("sourceUrl", winCandidateBulletinBeanList.get(0).getSourceUrl());
                            map.put("tenderProjectCode", winCandidateBulletinBeanList.get(0).getTenderProjectCode());
                            map.put("bulletinssueTime", winCandidateBulletinBeanList.get(0).getBulletinssueTime());
                            map.put("bulletinName", winCandidateBulletinBeanList.get(0).getBulletinName());
                            for (ChinaTenderingAndBiddingEvaluation.ObjectBean.WinCandidateBulletinBean winBidBulletinBean : winCandidateBulletinBeanList) {
                                formatContent = formatContent + "<p>" + winBidBulletinBean.getBulletinContent() + "</p>";
                            }
                            if (StringUtils.isNotBlank(formatContent)) {
                                map.put("formatContent", formatContent);
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

                    } catch (Exception e) {
                        log.debug("The json data is empty");
                    }
                }

            }


        }
        return dataItems;
    }

}
