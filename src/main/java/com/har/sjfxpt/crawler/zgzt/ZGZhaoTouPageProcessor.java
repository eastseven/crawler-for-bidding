package com.har.sjfxpt.crawler.zgzt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
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
                Request request = new Request(SEED_URL);
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
            log.warn(">>> fetch {} no data", page.getUrl().get());
        }

    }

    @Override
    public List parseContent(Elements items) {
        return null;
    }

    public Page pageGenerator(String schemaVersion, String tenderProjectCode, String type) {
        Request request = new Request(SHOW_DETAIL);
        Map<String, Object> param = Maps.newHashMap();
        param.put("schemaVersion", schemaVersion);
        param.put("tenderProjectCode", tenderProjectCode);

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
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);

        Object root = JSONObject.parse(page.getRawText());
        log.debug(">>> \n{}", JSON.toJSONString(JSONObject.parse(page.getRawText()), true));

        boolean success = (boolean) JSONPath.eval(root, "$.success");
        if (!success) return dataItems;

        JSONArray returnList = (JSONArray) JSONPath.eval(root, "$.object.returnlist");
        for (Object object : returnList) {
            String type = (String) pageParams.get("businessType");
            Object schemaVersion = JSONPath.eval(object, "$.schemaVersion");
            Object tenderProjectCode = JSONPath.eval(object, "$.tenderProjectCode");
            String id = schemaVersion + businessKeyWord(type) + tenderProjectCode;
            long value = stringRedisTemplate.boundSetOps(KEY_URLS).add(id);
            if (value == 0L) {
                log.warn("{} is duplication", id);
                continue;
            }

            if (!JSONPath.contains(object, "$.businessObjectName")) continue;
            log.debug(">>> \n{}", JSON.toJSONString(object, true));

            BidNewsOriginal dataItem = new BidNewsOriginal(id);
            dataItem.setSource(SourceCode.ZGZT.getValue());
            dataItem.setSourceCode(SourceCode.ZGZT.name());
            dataItem.setTitle(JSONPath.eval(object, "$.businessObjectName").toString());

            if (!JSONPath.contains(object, "$.regionName")) {
                dataItem.setProvince(ProvinceUtil.get(JSONPath.eval(object, "$.regionName").toString()));
            } else if (!JSONPath.contains(object, "$.transactionPlatfName")) {
                dataItem.setProvince(ProvinceUtil.get(JSONPath.eval(object, "$.transactionPlatfName").toString()));
            }

            dataItem.setDate(PageProcessorUtil.dataTxt(JSONPath.eval(object, "$.receiveTime").toString()));
            dataItem.setType(type);
            dataItem.setProjectCode(tenderProjectCode.toString());

            Page _page = pageGenerator(schemaVersion.toString(), tenderProjectCode.toString(), type);
            switch (type) {
                case "招标项目":
                    type01(_page, dataItem);
                    break;
                case "招标公告":
                    type02(_page, dataItem);
                    break;
                case "中标公告":
                    type03(_page, dataItem);
                    break;
                case "开标记录":
                    type04(_page, dataItem);
                    break;
                case "评标公示":
                    type05(_page, dataItem);
                    break;
                default:
            }

            dataItems.add(dataItem);
        }

        return dataItems;
    }

    /**
     * 评标公示
     * @param page
     * @param dataItem
     */
    private void type05(Page page, BidNewsOriginal dataItem) {
        JSONObject root = JSONObject.parseObject(page.getRawText());
        if (!root.getBoolean("success")) return;

        log.debug(">>> type 05 评标公示\n{}", JSON.toJSONString(root, true));
        String path = "$.object.winCandidateBulletin[0].bulletinContent";
        if (JSONPath.contains(root, path)) {
            String html = JSONPath.eval(root, path).toString();
            String formatContent = PageProcessorUtil.formatElementsByWhitelist(Jsoup.parse(html));
            dataItem.setFormatContent(formatContent);
        }
    }

    /**
     * 开标记录
     * @param page
     * @param dataItem
     */
    private void type04(Page page, BidNewsOriginal dataItem) {
        JSONObject json = JSONObject.parseObject(page.getRawText());

        boolean success = (boolean) JSONPath.eval(json, "$.success");
        if (!success) return;
        log.debug(">>> \n{}", JSON.toJSONString(json, SerializerFeature.PrettyFormat));

        try {
            String path = "$.object.openBidRecord[0].goodsOpenBidList";
            if (JSONPath.contains(json, path)) {
                JSONArray goodsOpenBidList = (JSONArray) JSONPath.eval(json, "$.object.openBidRecord[0].goodsOpenBidList");

                StringBuilder table = new StringBuilder();
                table.append("<table>");
                table.append("<tr><th>投标人名称</th><th>投标价格</th></tr>");
                goodsOpenBidList.forEach(o -> {
                    String bidderName = JSONPath.eval(o, "$.bidderName").toString();
                    String bidAmount = JSONPath.eval(o, "$.bidAmount").toString();

                    table.append("<tr><td>").append(bidderName).append("</td><td>").append(bidAmount).append("</td></tr>");
                });
                table.append("</table>");

                dataItem.setFormatContent(table.toString());
            }

        } catch (Exception e) {
            log.error("", e);
            log.error(">>> ZGZT type 04, The json data is empty");
        }
    }

    /**
     * 中标公告
     * @param page
     * @param dataItem
     */
    private void type03(Page page, BidNewsOriginal dataItem) {
        JSONObject json = JSONObject.parseObject(page.getRawText());
        if (!json.getBoolean("success")) return;

        log.debug(">>> \n{}", JSON.toJSONString(json, SerializerFeature.PrettyFormat));

        try {
            JSONArray winBidBulletins = (JSONArray) JSONPath.eval(json, "$.object.winBidBulletin");
            if (winBidBulletins.isEmpty()) return;

            JSONObject winBidBulletin = (JSONObject) winBidBulletins.get(0);

            String tenderProjectCode = winBidBulletin.getString("tenderProjectCode");
            if (StringUtils.isNotBlank(tenderProjectCode)) {
                dataItem.setProjectCode(tenderProjectCode);
            }

            String html = winBidBulletin.getString("bulletinContent");
            if (StringUtils.isNotBlank(html) && !StringUtils.contains(html, "<html")) {
                html = "<html>" + html + "</html>";
            }
            String formatContent = PageProcessorUtil.formatElementsByWhitelist(Jsoup.parse(html));
            if (StringUtils.isNotBlank(formatContent)) {
                dataItem.setFormatContent(formatContent);
            }

        } catch (Exception e) {
            log.debug("The json data is empty, {}", page.getUrl().get());
        }
    }

    /**
     * 招标公告
     *
     * @param page
     * @param dataItem
     */
    private void type02(Page page, BidNewsOriginal dataItem) {
        Object root = JSON.parse(page.getRawText());
        boolean success = (boolean) JSONPath.eval(root, "$.success");
        if (!success) return;

        log.debug(">>> type 02 招标公告\n{}", JSONObject.toJSONString(root, true));

        StringBuilder html = new StringBuilder();

        //招标公告
        JSONArray tenderBulletin = (JSONArray) JSONPath.eval(root, "$.object.tenderBulletin");
        if (!tenderBulletin.isEmpty()) {
            html.append("<p>招标公告");
            int counter = 0;
            for (Object object : tenderBulletin) {
                if (counter >= 3) break;
                JSONObject tender = (JSONObject) object;
                html.append("<p>").append(tender.get("bulletinName")).append("</p>");
                html.append("<p>发布时间：")
                        .append(DateTimeFormat.forPattern("yyyyMMddHHmmss").parseDateTime(tender.get("bulletinssueTime").toString()).toString("yyyy-MM-dd HH:mm:ss"))
                        .append("</p>");
                html.append("<p>").append(tender.get("bulletinContent")).append("</p>");
                counter++;
            }
            html.append("</p>");
        }

        //变更公告
        JSONArray amendBulletin = (JSONArray) JSONPath.eval(root, "$.object.amendBulletin");
        if (!amendBulletin.isEmpty()) {
            html.append("<p>变更公告");
            int counter = 0;
            for (Object object : amendBulletin) {
                if (counter >= 3) break;
                JSONObject amend = (JSONObject) object;
                String date = DateTimeFormat.forPattern("yyyyMMddHHmmss").parseDateTime(amend.get("bulletinssueTime").toString()).toString("yyyy-MM-dd HH:mm:ss");
                html.append("<p>").append(amend.get("bulletinName")).append("</p>");
                html.append("<p>发布时间：").append(date).append("</p>");
                html.append("<p>").append(amend.get("bulletinContent")).append("</p>");
                counter++;
            }
            html.append("</p>");
        }
        //tender Bulletin
        //amend Bulletin

        dataItem.setFormatContent(html.toString());
    }

    /**
     * 招标项目
     *
     * @param page
     * @param dataItem
     */
    private void type01(Page page, BidNewsOriginal dataItem) {
        String json = page.getRawText();
        JSONObject root = (JSONObject) JSONObject.parse(json);
        boolean success = (boolean) JSONPath.eval(root, "$.success");
        if (!success) return;

        log.debug(">>> type 01 招标项目\n{}", JSONObject.toJSONString(root, true));

        JSONObject tenderProject = (JSONObject) JSONPath.eval(root, "$.object.tenderProject[0]");
        Map<String, Object> map = JSONPath.paths(tenderProject);
        StringBuilder table = new StringBuilder();
        table.append("<table><tbody>");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey().replace("/", "");
            String value = "" + entry.getValue();

            switch (key) {
                case "bulletinssueTime":
                    table.append("<tr><td>").append("招标项目建立时间").append("</td><td>").append(new DateTime(Long.valueOf(value)).toString("yyyy-MM-dd HH:mm")).append("</td></tr>");
                    break;
                case "tenderAgencyName":
                    table.append("<tr><td>").append("招标代理机构名称").append("</td><td>").append(value).append("</td></tr>");
                    break;
                case "tendererName":
                    dataItem.setPurchaser(value);
                    table.append("<tr><td>").append("招标人名称").append("</td><td>").append(value).append("</td></tr>");
                    break;
                case "superviseDeptCodeType":
                    break;
                case "tenderProjectCode":
                    dataItem.setProjectCode(value);
                    break;
                case "transactionPlatfCode":
                    break;
                case "industriesType":
                    //所属行业
                    if (StringUtils.isNotBlank(value) && !"null".equalsIgnoreCase(value)) {
                        dataItem.setOriginalIndustryCategory(value);
                    }
                    table.append("<tr><td>").append("所属行业").append("</td><td>").append(value).append("</td></tr>");
                    break;
                case "tenderOrganizeForm":
                    //招标组织方式
                    table.append("<tr><td>").append("招标组织方式").append("</td><td>").append(value).append("</td></tr>");
                    break;
                case "superviseDeptCode":
                    break;
                case "bulletinName":

                    break;
                case "tenderAgencyCode":
                    table.append("<tr><td>").append("招标代理机构代码").append("</td><td>").append(value).append("</td></tr>");
                    break;
                case "tenderAgencyCodeType":
                    //
                    break;
                case "bulletinContent":
                    table.append("<tr><td rowspan='1' colspan='2'><div>").append(value).append("</div></td></tr>");
                    break;

                default:
            }
        }
        table.append("</tbody></table>");

        String content = table.toString();
        if (StringUtils.isNotBlank(content)) {
            dataItem.setFormatContent(content);
        }

    }

}
