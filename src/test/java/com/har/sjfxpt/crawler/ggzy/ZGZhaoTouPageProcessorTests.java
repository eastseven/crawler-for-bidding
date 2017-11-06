package com.har.sjfxpt.crawler.ggzy;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import com.har.sjfxpt.crawler.zgzt.ChinaTenderingAndBiddingAnnouncement;
import com.har.sjfxpt.crawler.zgzt.ChinaTenderingAndBiddingContent;
import com.har.sjfxpt.crawler.zgzt.ZGZhaoTouPageProcessor;
import com.har.sjfxpt.crawler.zgzt.ZGZhaoTouPipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/1.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ZGZhaoTouPageProcessorTests {

    @Autowired
    ZGZhaoTouPageProcessor zgZhaoTouPageProcessor;

    @Autowired
    ZGZhaoTouPipeline zgZhaoTouPipeline;

    public Request requestGenerator(String url, String type) {

        Request request = new Request(url);

        Map<String, Object> params = Maps.newHashMap();

        if(type.equals("招标项目")||type.equals("中标公告")||type.equals("开标记录")||type.equals("评标公示")){
            params.put("searchName", "");
            params.put("searchArea", "");
            params.put("searchIndustry", "");
            params.put("centerPlat", "");
            params.put("businessType", type);
            params.put("searchTimeStart", "");
            params.put("searchTimeStop", "");
            params.put("timeTypeParam", "今日");
            params.put("bulletinIssnTime", "");
            params.put("bulletinIssnTimeStart", "");
            params.put("bulletinIssnTimeStop", "");
            params.put("pageNo", 1);
            params.put("row", 15);
        }
        if(type.equals("招标公告")){
            params.put("searchName", "");
            params.put("searchArea", "");
            params.put("searchIndustry", "");
            params.put("centerPlat", "");
            params.put("businessType", type);
            params.put("searchTimeStart", "");
            params.put("searchTimeStop", "");
            params.put("timeTypeParam", "");
            params.put("bulletinIssnTime", "今日");
            params.put("bulletinIssnTimeStart", "");
            params.put("bulletinIssnTimeStop", "");
            params.put("pageNo", 1);
            params.put("row", 15);
        }
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
        request.putExtra("pageParams", params);
        return request;
    }

    @Test
    public void testZGZhaoTouPageProcessor() {

        String url = "http://www.cebpubservice.com/ctpsp_iiss/searchbusinesstypebeforedooraction/getStringMethod.do";

        Request request1 = requestGenerator(url, "招标项目");
        Request request2 = requestGenerator(url, "招标公告");
        Request request3 = requestGenerator(url, "中标公告");
        Request request4 = requestGenerator(url, "开标记录");
        Request request5 = requestGenerator(url, "评标公示");

        Request[] requests={request1,request2,request3,request4,request5};

        Spider.create(zgZhaoTouPageProcessor)
                .addRequest(requests)
                .addPipeline(zgZhaoTouPipeline)
                .thread(4)
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


    @Value("${app.proxy.pool.formTemplate}")
    String formTemplate;

    @Test
    public void testApplication() {

        for (int i = 0; i < 10; i++) {
            String test = StringUtils.replace(formTemplate, "{bulletinName}", i + "");
            log.debug("i=={}test=={}", i, test);
        }
    }

    @Test
    public void testAnnouncement(){
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Request request = new Request("http://www.cebpubservice.com/ctpsp_iiss/SecondaryAction/findDetails.do");
        Map<String, Object> param = Maps.newHashMap();
        param.put("schemaVersion", "V0.0");
        param.put("businessKeyWord", "tenderBulletin");
        param.put("tenderProjectCode", "E4403000004002927006");
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(param, "UTF-8"));
        Page page1 = httpClientDownloader.download(request, SiteUtil.get().toTask());
        ChinaTenderingAndBiddingAnnouncement data1=JSONObject.parseObject(page1.getRawText(),ChinaTenderingAndBiddingAnnouncement.class);
        List<ChinaTenderingAndBiddingAnnouncement.ObjectBean.TenderBulletinBean> tenderBulletin=data1.getObject().getTenderBulletin();

    }


}
