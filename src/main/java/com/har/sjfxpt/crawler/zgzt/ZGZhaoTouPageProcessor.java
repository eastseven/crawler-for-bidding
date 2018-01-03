package com.har.sjfxpt.crawler.zgzt;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;
import static com.har.sjfxpt.crawler.zgzt.ZGZhaoTouPageProcessor.*;

/**
 * Created by Administrator on 2017/11/1.
 *
 * @author luofei
 * @author dongqi
 */
@Slf4j
@Component
@SourceConfig(code = SourceCode.ZGZT,
        sources = {
                @Source(url = SEED_URL, type = "招标项目", post = true, postParams = POST_PARAMS_01),
                @Source(url = SEED_URL, type = "招标公告", post = true, postParams = POST_PARAMS_02),
                @Source(url = SEED_URL, type = "中标公告", post = true, postParams = POST_PARAMS_03),
                @Source(url = SEED_URL, type = "开标记录", post = true, postParams = POST_PARAMS_04),
                @Source(url = SEED_URL, type = "评标公示", post = true, postParams = POST_PARAMS_05),
        }
)
public class ZGZhaoTouPageProcessor implements BasePageProcessor {

    public static final String SEED_URL = "http://www.cebpubservice.com/ctpsp_iiss/searchbusinesstypebeforedooraction/getStringMethod.do";

    public static final String POST_PARAMS_01 = "{'searchTimeStop':'','centerPlat':'','timeTypeParam':'今日','searchArea':'','bulletinIssnTimeStop':'','bulletinIssnTimeStart':'','bulletinIssnTime':'','pageNo':1,'searchName':'','searchIndustry':'','row':15,'businessType':'招标项目','searchTimeStart':''}";
    public static final String POST_PARAMS_02 = "{'searchTimeStop':'','centerPlat':'','timeTypeParam':'','searchArea':'','bulletinIssnTimeStop':'','bulletinIssnTimeStart':'','bulletinIssnTime':'今日','pageNo':1,'searchName':'','searchIndustry':'','row':15,'businessType':'招标公告','searchTimeStart':''}";
    public static final String POST_PARAMS_03 = "{'searchTimeStop':'','centerPlat':'','timeTypeParam':'今日','searchArea':'','bulletinIssnTimeStop':'','bulletinIssnTimeStart':'','bulletinIssnTime':'','pageNo':1,'searchName':'','searchIndustry':'','row':15,'businessType':'中标公告','searchTimeStart':''}";
    public static final String POST_PARAMS_04 = "{'searchTimeStop':'','centerPlat':'','timeTypeParam':'今日','searchArea':'','bulletinIssnTimeStop':'','bulletinIssnTimeStart':'','bulletinIssnTime':'','pageNo':1,'searchName':'','searchIndustry':'','row':15,'businessType':'开标记录','searchTimeStart':''}";
    public static final String POST_PARAMS_05 = "{'searchTimeStop':'','centerPlat':'','timeTypeParam':'今日','searchArea':'','bulletinIssnTimeStop':'','bulletinIssnTimeStart':'','bulletinIssnTime':'','pageNo':1,'searchName':'','searchIndustry':'','row':15,'businessType':'评标公示','searchTimeStart':''}";

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
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int currentPage = (int) pageParams.get("pageNo");
        if (currentPage == 1) {
            ChinaTenderingAndBidding data = JSONObject.parseObject(page.getRawText(), ChinaTenderingAndBidding.class);
            int size = data.getObject().getPage().getTotalPage();
            log.debug("size=={}", size);
            for (int i = 2; i <= size; i++) {
                Map<String, Object> nextPage = Maps.newHashMap(pageParams);
                nextPage.put("pageNo", i);
                Request request = new Request(URL);
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(nextPage, "UTF-8"));
                request.putExtra(PAGE_PARAMS, nextPage);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        List<BidNewsOriginal> dataItems = parseContent(page);
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
            default:
        }
        param.put("tenderProjectCode", row.getTenderProjectCode());
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(param, "UTF-8"));
        Page page = httpClientDownloader.download(request, SiteUtil.get().setTimeOut(30000).toTask());
        return page;
    }
    
    public String businessKeyWord(String type) {
        String businessKeyWord = null;
        switch (type) {
            case "招标项目":
                businessKeyWord = "tenderProject";
                break;
            case "招标公告":
                businessKeyWord = "tenderBulletin";
                break;
            case "中标公告":
                businessKeyWord = "winBidBulletin";
                break;
            case "开标记录":
                businessKeyWord = "openBidRecord";
                break;
            case "评标公示":
                businessKeyWord = "winCandidateBulletin";
                break;
            default:
        }
        return businessKeyWord;
    }

    public List parseContent(Page page) {
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);

        ChinaTenderingAndBidding data = JSONObject.parseObject(page.getRawText(), ChinaTenderingAndBidding.class);
        List<ChinaTenderingAndBidding.ObjectBean.ReturnlistBean> lists = data.getObject().getReturnlist();
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        for (ChinaTenderingAndBidding.ObjectBean.ReturnlistBean row : lists) {
            String type = (String) pageParams.get("businessType");
            String id = row.getSchemaVersion() + businessKeyWord(type) + row.getTenderProjectCode();
            long value = stringRedisTemplate.boundSetOps(KEY_URLS).add(id);
            if (value == 0L) {
                log.warn("{} is duplication", id);
                continue;
            } else {
                String title = row.getBusinessObjectName();
                String province = row.getRegionName() + "";
                String date = row.getReceiveTime();

                BidNewsOriginal dataItem = new BidNewsOriginal(id);
                dataItem.setSource(SourceCode.ZGZT.getValue());
                dataItem.setSourceCode(SourceCode.ZGZT.name());
                dataItem.setTitle(title);
                dataItem.setProvince(ProvinceUtil.get(province));
                dataItem.setDate(PageProcessorUtil.dataTxt(date));
                dataItem.setType(type);

                log.debug("ZGZhaoTouDataItem=={}", dataItem);

                switch (type) {
                    case "招标项目":
                        type01(dataItems, row, type, dataItem);
                        break;
                    case "招标公告":
                        type02(dataItems, row, type, dataItem);
                        break;
                    case "中标公告":
                        type03(dataItems, row, type, dataItem);
                        break;
                    case "开标记录":
                        type04(dataItems, row, type, dataItem);
                        break;
                    case "评标公示":
                        type05(dataItems, row, type, dataItem);
                        break;
                    default:
                }

            }

        }
        return dataItems;
    }

    /**
     * 评标公示
     *
     * @param dataItems
     * @param row
     * @param type
     * @param dataItem
     */
    private void type05(List<BidNewsOriginal> dataItems, ChinaTenderingAndBidding.ObjectBean.ReturnlistBean row, String type, BidNewsOriginal dataItem) {
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
                String test = new String(formTemplateAnnouncement);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    log.debug("key=={}value=={}", entry.getKey(), entry.getValue());
                    if (StringUtils.isNotBlank(entry.getValue())) {
                        test = StringUtils.replace(test, "{" + entry.getKey() + "}", entry.getValue());
                    }
                }
                log.debug("test=={}", test);
                if (StringUtils.isNotBlank(test)) {
                    dataItem.setFormatContent(PageProcessorUtil.removeField(test));
                    dataItems.add(dataItem);
                }
            }

        } catch (Exception e) {
            log.debug("The json data is empty");
        }
    }

    private void type04(List<BidNewsOriginal> dataItems, ChinaTenderingAndBidding.ObjectBean.ReturnlistBean row, String type, BidNewsOriginal dataItem) {
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
                String test = new String(formTemplateBidOpen);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    log.debug("key=={}value=={}", entry.getKey(), entry.getValue());
                    if (StringUtils.isNotBlank(entry.getValue())) {
                        test = StringUtils.replace(test, "{" + entry.getKey() + "}", entry.getValue());
                    }
                }
                log.debug("test=={}", test);
                if (StringUtils.isNotBlank(test)) {
                    dataItem.setFormatContent(PageProcessorUtil.removeField(test));
                    dataItems.add(dataItem);
                }

            }

        } catch (Exception e) {
            log.debug("The json data is empty");
        }
    }

    private void type03(List<BidNewsOriginal> dataItems, ChinaTenderingAndBidding.ObjectBean.ReturnlistBean row, String type, BidNewsOriginal dataItem) {
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
                String test = new String(formTemplateAnnouncement);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    log.debug("key=={}value=={}", entry.getKey(), entry.getValue());
                    if (StringUtils.isNotBlank(entry.getValue())) {
                        test = StringUtils.replace(test, "{" + entry.getKey() + "}", entry.getValue());
                    }
                }
                log.debug("test=={}", test);
                if (StringUtils.isNotBlank(test)) {
                    dataItem.setFormatContent(PageProcessorUtil.removeField(test));
                    dataItems.add(dataItem);
                }

            }

        } catch (Exception e) {
            log.debug("The json data is empty, {}", page1);
        }
    }

    private void type02(List<BidNewsOriginal> dataItems, ChinaTenderingAndBidding.ObjectBean.ReturnlistBean row, String type, BidNewsOriginal dataItem) {
        Page page = pageGenerator(row, type);
        ChinaTenderingAndBiddingAnnouncement data = JSONObject.parseObject(page.getRawText(), ChinaTenderingAndBiddingAnnouncement.class);
        List<ChinaTenderingAndBiddingAnnouncement.ObjectBean.TenderBulletinBean> tenderBulletin = data.getObject().getTenderBulletin();

        String formatContent = "";
        if (tenderBulletin.size() > 0) {
            ChinaTenderingAndBiddingAnnouncement.ObjectBean.TenderBulletinBean bean = tenderBulletin.get(0);
            Map<String, String> map = Maps.newHashMap();
            map.put("attachmentCode", bean.getAttachmentCode());
            map.put("bulletinName", bean.getBulletinName());
            map.put("bulletinssueTime", bean.getBulletinssueTime());
            map.put("schemaVersion", bean.getSchemaVersion());
            map.put("sourceUrl", bean.getSourceUrl());
            map.put("tenderProjectCode", bean.getTenderProjectCode());
            map.put("transactionPlatfCode", bean.getTransactionPlatfCode());

            formatContent = bean.getBulletinContent();
            if (StringUtils.isNotBlank(formatContent)) {
                map.put("formatContent", formatContent);
            }

        } else if (data.getObject().getQualifyBulletin().size() > 0) {
            ChinaTenderingAndBiddingAnnouncement.ObjectBean.TenderBulletinBean bean = data.getObject().getQualifyBulletin().get(0);
            formatContent = bean.getBulletinContent();
        }

        if (StringUtils.isNotBlank(formatContent) && !"F".equalsIgnoreCase(formatContent)) {
            if (!formatContent.contains("<html")) {
                formatContent = "<html>" + formatContent + "</html>";
            }
            formatContent = PageProcessorUtil.formatElementsByWhitelist(Jsoup.parse(formatContent));
            dataItem.setFormatContent(formatContent);
            dataItems.add(dataItem);
        }

    }

    private void type01(List<BidNewsOriginal> dataItems, ChinaTenderingAndBidding.ObjectBean.ReturnlistBean row, String type, BidNewsOriginal dataItem) {
        Page page = pageGenerator(row, type);
        log.debug(">>> {}\n", page.getRawText());

        String json = page.getRawText();
        JSONObject root = (JSONObject) JSONObject.parse(json);
        boolean success = (boolean) JSONPath.eval(root, "$.success");
        if (!success) return;

        JSONObject tenderProject = (JSONObject) JSONPath.eval(root, "$.object.tenderProject[0]");
        Map<String, Object> map = JSONPath.paths(tenderProject);

        String content = new String(formTemplate);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey().replace("/", "");
            String value = "" + entry.getValue();
            content = StringUtils.replace(content, key, value);

            switch (key) {
                case "tendererName":
                    dataItem.setPurchaser(value);
                    break;
                case "tenderProjectCode":
                    dataItem.setProjectCode(value);
                    break;
                default:
            }
        }

        log.debug(">>> {}\n{}\n", map, content);
        if (StringUtils.isNotBlank(content)) {
            dataItem.setFormatContent(content);
        } else {
            log.error(">>> {}\n{}\n", map, content);
        }

        dataItems.add(dataItem);
    }

}
