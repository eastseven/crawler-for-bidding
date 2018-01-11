package com.har.sjfxpt.crawler.core.other;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.SpiderApplicationTests;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.model.BidNewsSpider;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.other.ZGZhaoTouPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import us.codecraft.webmagic.Request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
        String[] types = {
                "招标项目",
                "招标公告",
                "中标公告",
                "开标记录",
                "评标公示",};

        List<SourceModel> list = SourceConfigAnnotationUtils.find(zgZhaoTouPageProcessor.getClass());
        list.forEach(sourceModel -> log.info(">>>\n{}", JSONObject.toJSONString(sourceModel, true)));

        List<Request> requestList = list.parallelStream().map(SourceModel::createRequest)
                .filter(request -> request.getExtra("type").equals("评标公示"))
                .collect(Collectors.toList());

        Assert.assertTrue(requestList.size() == 1);
        BidNewsSpider.create(zgZhaoTouPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(pipeline)
                .run();
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
    public void testFormatContent() {
        String text = "<table><tbody><tr><td align=\"center\" rowspan=\"1\" colspan=\"2\"><div><h4 class=\"fleft\">华能灵华山风电场（100MW）工程水保完善工程 </h4></div></td></tr><tr><td align=\"right\" colspan=\"2\"><div style=\"margin:0 auto 10px auto;text-align:center;font-size:14px;color:#333;font-family:微软雅黑;\">发布日期：20171205115122&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div></td></tr><tr><td class=\"even\"><p>所属行业：<span>大气污染控制工程</span></p></td><td><p>所属地区：<span>北京市</span></p></td></tr><tr><td class=\"even\"><p>招标项目建立时间：<span>20171205115122</span></p></td><td><p><span></span></p></td></tr><tr><td class=\"even\"><p>招标代理机构代码：<span title=\"\">{tenderAgencyCodevalue}</span></p></td><td><p>招标代理机构名称：<span title=\"华能招标有限公司\">华能招标有限公司</span></p></td></tr><tr><td class=\"even\"><p>招标人名称：<span title=\"华能国际电力股份有限公司江西分公司\">华能国际电力股份有限公司江西分公司</span></p></td><td><p>招标组织方式：<span>委托招标</span></p></td></tr><tr><td class=\"even\"><p>行政监督部门代码：<span>{superviseDeptCodevalue}</span></p></td><td><p>行政监督部门名称：<span>{superviseDeptNamevalue}</span></p></td></tr><tr><td class=\"even\"><p>行政审核部门代码：<span>{approveDeptCodevalue}</span></p></td><td><p>行政审核部门名称：<span>{approveDeptNamevalue}</span></p></td></tr><tr><td rowspan=\"1\" colspan=\"2\"><div ><h5>招标内容与范围及招标方案说明：</h5><span></span></td></tr></tbody></table>";
        String[] filed = StringUtils.substringsBetween(text, "{", "}");
        for (int i = 0; i < filed.length; i++) {
            text = StringUtils.remove(text, "{" + filed[i] + "}");
        }
        log.info("text=={}", text);
    }

}
