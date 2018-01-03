package com.har.sjfxpt.crawler.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import com.har.sjfxpt.crawler.zgzt.ChinaTenderingAndBiddingAnnouncement;
import com.har.sjfxpt.crawler.zgzt.ChinaTenderingAndBiddingContent;
import com.har.sjfxpt.crawler.zgzt.ZGZhaoTouPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.zgzt.ChinaTenderingAndBiddingLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/11/1.
 */
@Slf4j
public class ZGZhaoTouPageProcessorTests extends SpiderApplicationTests {

    @Autowired
    ZGZhaoTouPageProcessor zgZhaoTouPageProcessor;

    @Autowired
    HBasePipeline pipeline;

    @Test
    public void testPostParams() {
        Map<String, Object> params = Maps.newHashMap();
        String[][] data = new String[][]{
                {"招标项目", "今日"},
                {"招标公告", "今日"},
                {"中标公告", "今日"},
                {"开标记录", "今日"},
                {"评标公示", "今日"},
        };

        for (String[] value : data) {
            String type = value[0];
            String date = value[1];
            params.put("searchName", "");
            params.put("searchArea", "");
            params.put("searchIndustry", "");
            params.put("centerPlat", "");
            params.put("businessType", type);
            params.put("searchTimeStart", "");
            params.put("searchTimeStop", "");
            params.put("timeTypeParam", "");
            params.put("bulletinIssnTime", "");
            params.put("bulletinIssnTimeStart", "");
            params.put("bulletinIssnTimeStop", "");
            params.put("pageNo", 1);
            params.put("row", 15);

            if ("招标公告".equalsIgnoreCase(type)) {
                params.put("bulletinIssnTime", date);
            } else {
                params.put("timeTypeParam", date);
            }

            String json = JSONObject.toJSONString(params, SerializerFeature.UseSingleQuotes);
            log.info(">>> {}, {}, {}", type, date, json);
        }

        String json = "{\"message\":\"\",\"success\":true,\"object\":{\"tenderProject\":[{\"tendererName\":\"深圳供电局有限公司\",\"tenderAgencyName\":\"广东律诚工程咨询有限公司\",\"tenderAgencyCodeType\":\"98\",\"bulletinContent\":\"深圳供电局有限公司2017年年中调整配网基建项目（第二批）（福田1-6标及宝安1-4标）施工招标，分布于福田区的21项、宝安区的65项配网基建工程，本招标项目分为2组，共10个标段,施工图纸范围内的配电网建筑、安装及调试工程，安健环制作安装，完成营配一体化信息采集和数据测量，并配合完成营配一体化信息资料录入，负责办理相关施工许可手续，配合招标人完成青苗赔偿洽谈工作。全面开展样板点的标准建设工作。\",\"bulletinName\":\"深圳供电局有限公司2017年年中调整配网基建项目（第二批）（福田1-6标及宝安1-4标）施工招标(第4标段)\",\"transactionPlatfCode\":\"E4401000002\",\"superviseDeptCodeType\":\"96\",\"tendererCode\":\"100015\",\"tenderAgencyCode\":\"914420007287418645\",\"tenderOrganizeForm\":\"委托招标\",\"industriesType\":\"建筑工程\",\"superviseDeptCode\":\"ZL3383\",\"schemaVersion\":\"V0.0\",\"bulletinssueTime\":1514517139000},{\"tendererName\":\"深圳供电局有限公司\",\"tenderAgencyName\":\"广东律诚工程咨询有限公司\",\"tenderAgencyCodeType\":\"98\",\"bulletinContent\":\"深圳供电局有限公司2017年年中调整配网基建项目（第二批）（福田1-6标及宝安1-4标）施工招标，分布于福田区的21项、宝安区的65项配网基建工程，本招标项目分为2组，共10个标段,施工图纸范围内的配电网建筑、安装及调试工程，安健环制作安装，完成营配一体化信息采集和数据测量，并配合完成营配一体化信息资料录入，负责办理相关施工许可手续，配合招标人完成青苗赔偿洽谈工作。全面开展样板点的标准建设工作。\",\"bulletinName\":\"深圳供电局有限公司2017年年中调整配网基建项目（第二批）（福田1-6标及宝安1-4标）施工招标(第4标段)\",\"transactionPlatfCode\":\"E4401000002\",\"superviseDeptCodeType\":\"96\",\"tendererCode\":\"100015\",\"tenderAgencyCode\":\"914420007287418645\",\"tenderOrganizeForm\":\"委托招标\",\"industriesType\":\"建筑工程\",\"superviseDeptCode\":\"ZL3383\",\"schemaVersion\":\"V0.0\",\"bulletinssueTime\":1514858463000}]}}";

        JSONObject root = (JSONObject) JSONObject.parse(json);
        Object tenderProject = JSONPath.eval(root, "$.object.tenderProject[0]");
        Object bulletinContent = JSONPath.eval(root, "$.object.tenderProject[0].bulletinContent");

        log.info(">>> tender {}", tenderProject);
        log.info(">>> content {}", bulletinContent);

    }

    @Test
    public void testZGZhaoTouPageProcessor() {
        String url = "http://www.cebpubservice.com/ctpsp_iiss/searchbusinesstypebeforedooraction/getStringMethod.do";

        Request[] requests = {
//                requestGenerator(url, "招标项目", "今日"),
                requestGenerator(url, "招标公告", "今日"),
//                requestGenerator(url, "中标公告", "今日"),
//                requestGenerator(url, "开标记录", "今日"),
//                requestGenerator(url, "评标公示", "今日")
        };

        Spider.create(zgZhaoTouPageProcessor)
                .addRequest(requests)
                .addPipeline(pipeline)
                .thread(requests.length)
                .run();
    }

    @Test
    public void downPage() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Request request = new Request("http://www.cebpubservice.com/ctpsp_iiss/SecondaryAction/findDetails.do");
        Map<String, Object> param = Maps.newHashMap();
        param.put("schemaVersion", "V60.02");
        param.put("businessKeyWord", "tenderProject");
        param.put("tenderProjectCode", "0773-1740SHHW7235000");
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(param, "UTF-8"));
        Page page = httpClientDownloader.download(request, SiteUtil.get().toTask());
        ChinaTenderingAndBiddingContent data = JSONObject.parseObject(page.getRawText(), ChinaTenderingAndBiddingContent.class);
        List<ChinaTenderingAndBiddingContent.ObjectBean.TenderProjectBean> tenderProjectBeanList = data.getObject().getTenderProject();

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

        String test = "<div class=\"openCloseWrap\" style=\"heigth:40px; font-size:16px;\"><div class=\"title fix open\"><h4 class=\"fleft\">{bulletinName}</h4><div class=\"ocBtn fright\"></div></div><div class=\"inner\"><div style=\"margin:0 auto 10px auto;text-align:center;font-size:14px;color:#333;font-family:微软雅黑;\">发布日期：{bulletinssueTime}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div><table class=\"table_tenderProject\" border=\"0px\" bordercolor=\"#999\"><tbody><tr><td class=\"even\"><p>所属行业：<span>{industriesType}</span></p></td><td><p>所属地区：<span>{regionCode}</span></p></td></tr><tr><td class=\"even\"><p>招标项目建立时间：<span>{createTime}</span></p></td><td><p><span></span></p></td></tr><tr><td class=\"even\"><p>招标代理机构代码：<span title=\"\"></span></p></td><td><p>招标代理机构名称：<span title=\"{tenderAgencyName}\">{tenderAgencyName}</span></p></td></tr><tr><td class=\"even\"><p>招标人名称：<span title=\"{tendererName}\">{tendererName}</span></p></td><td><p>招标组织方式：<span>{tenderOrganizeForm}</span></p></td></tr><tr><td class=\"even\"><p>行政监督部门代码：<span></span></p></td><td><p>行政监督部门名称：<span></span></p></td></tr><tr><td class=\"even\"><p>行政审核部门代码：<span>null</span></p></td><td><p>行政审核部门名称：<span>null</span></p></td></tr></tbody></table><div class=\"div_tenderProject\"><h5>招标内容与范围及招标方案说明：</h5><span>{bulletinContent}</span></div></div></div>";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            log.debug("key=={}value=={}", entry.getKey(), entry.getValue());
            if (StringUtils.isNotBlank(entry.getValue())) {
                test = test.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        log.debug("test=={}", test);
    }


    @Value("${app.html.template.table}")
    String formTemplate;

    @Test
    public void testApplication() {

        for (int i = 0; i < 10; i++) {
            String test = StringUtils.replace(formTemplate, "{bulletinName}", i + "");
            log.debug("i=={}test=={}", i, test);
        }
    }

    @Test
    public void testAnnouncement() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Request request = new Request("http://www.cebpubservice.com/ctpsp_iiss/SecondaryAction/findDetails.do");
        Map<String, Object> param = Maps.newHashMap();
        param.put("schemaVersion", "V0.0");
        param.put("businessKeyWord", "tenderBulletin");
        param.put("tenderProjectCode", "E4403000004002927006");
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(param, "UTF-8"));
        Page page1 = httpClientDownloader.download(request, SiteUtil.get().toTask());
        ChinaTenderingAndBiddingAnnouncement data1 = JSONObject.parseObject(page1.getRawText(), ChinaTenderingAndBiddingAnnouncement.class);
        List<ChinaTenderingAndBiddingAnnouncement.ObjectBean.TenderBulletinBean> tenderBulletin = data1.getObject().getTenderBulletin();
    }

    @Test
    public void testFormatContent() {
        String text = "<table><tbody><tr><td align=\"center\" rowspan=\"1\" colspan=\"2\"><div><h4 class=\"fleft\">华能灵华山风电场（100MW）工程水保完善工程 </h4></div></td></tr><tr><td align=\"right\" colspan=\"2\"><div style=\"margin:0 auto 10px auto;text-align:center;font-size:14px;color:#333;font-family:微软雅黑;\">发布日期：20171205115122&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div></td></tr><tr><td class=\"even\"><p>所属行业：<span>大气污染控制工程</span></p></td><td><p>所属地区：<span>北京市</span></p></td></tr><tr><td class=\"even\"><p>招标项目建立时间：<span>20171205115122</span></p></td><td><p><span></span></p></td></tr><tr><td class=\"even\"><p>招标代理机构代码：<span title=\"\">{tenderAgencyCodevalue}</span></p></td><td><p>招标代理机构名称：<span title=\"华能招标有限公司\">华能招标有限公司</span></p></td></tr><tr><td class=\"even\"><p>招标人名称：<span title=\"华能国际电力股份有限公司江西分公司\">华能国际电力股份有限公司江西分公司</span></p></td><td><p>招标组织方式：<span>委托招标</span></p></td></tr><tr><td class=\"even\"><p>行政监督部门代码：<span>{superviseDeptCodevalue}</span></p></td><td><p>行政监督部门名称：<span>{superviseDeptNamevalue}</span></p></td></tr><tr><td class=\"even\"><p>行政审核部门代码：<span>{approveDeptCodevalue}</span></p></td><td><p>行政审核部门名称：<span>{approveDeptNamevalue}</span></p></td></tr><tr><td rowspan=\"1\" colspan=\"2\"><div ><h5>招标内容与范围及招标方案说明：</h5><span></span></td></tr></tbody></table>";
        String[] filed = StringUtils.substringsBetween(text, "{", "}");
        for (int i = 0; i < filed.length; i++) {
            text = StringUtils.remove(text, "{" + filed[i] + "}");
        }
        log.info("text=={}", text);
    }

}
