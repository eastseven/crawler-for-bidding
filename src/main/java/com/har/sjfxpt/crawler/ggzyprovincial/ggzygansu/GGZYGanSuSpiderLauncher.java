package com.har.sjfxpt.crawler.ggzyprovincial.ggzygansu;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/17.
 */
@Slf4j
@Component@Deprecated
public class GGZYGanSuSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.GGZYGANSU.toString().toLowerCase() + "-current";

    @Autowired
    GGZYGanSuPageProcessor ggzyGanSuPageProcessor;

    @Autowired
    GGZYGanSuPipeline ggzyGanSuPipeline;

    Request[] requests = {
            requestGenerator("http://www.gsggfw.cn/w/bid/tenderAnnQuaInqueryAnn/pageList?pageNo=1&pageSize=20", "620000", "1", "2", "0", "资格预审公告"),
            requestGenerator("http://www.gsggfw.cn/w/bid/tenderAnnQuaInqueryAnn/pageList?pageNo=1&pageSize=20", "620000", "1", "1", "1", "招标公告"),
            requestGenerator("http://www.gsggfw.cn/w/bid/tenderAnnQuaInqueryAnn/pageList?pageNo=1&pageSize=20", "620000", "2", "", "1", "更正公告"),
            requestGenerator("http://www.gsggfw.cn/w/bid/qualiInqueryResult/pageList?pageNo=1&pageSize=20", "620000", "2", "", "3", "资格预审公示"),
            requestGenerator("http://www.gsggfw.cn/w/bid/winResultAnno/pageList?pageNo=1&pageSize=20", "620000", "1", "3", "3", "中标结果公告"),
            requestGenerator("http://www.gsggfw.cn/w/bid/winResultAnno/pageList?pageNo=1&pageSize=20", "620000", "2", "3", "3", "中标结果更正公告"),

            requestGenerator("http://www.gsggfw.cn/w/bid/purchaseQualiInqueryAnn/pageList?pageNo=1&pageSize=20", "620000", "1", "1", "", "采购（资格预审）公告"),
            requestGenerator("http://www.gsggfw.cn/w/bid/correctionItem/pageList?pageNo=1&pageSize=20", "620000", "1", "1", "", "更正事项"),
            requestGenerator("http://www.gsggfw.cn/w/bid/bidDealAnnounce/pageList?pageNo=1&pageSize=20", "620000", "1", "1", "", "中标(成交)结果公告")

    };

    /**
     * 爬取当日数据
     */
    public void start() {
        cleanSpider(uuid);
        Spider spider=Spider.create(ggzyGanSuPageProcessor)
                .addPipeline(ggzyGanSuPipeline)
                .setUUID(uuid)
                .addRequest(requests)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }

    public static Request requestGenerator(String url, String areaCode, String noticeNature, String bulletinType, String assortmentIndex, String type) {
        Request request = new Request(url);
        request.setMethod(HttpConstant.Method.POST);
        Map<String, Object> pageParams = Maps.newHashMap();
        pageParams.put("filterparam", jsonGenerator(areaCode, noticeNature, bulletinType, assortmentIndex));
        request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
        if (StringUtils.isNotBlank(assortmentIndex)) {
            pageParams.put("businessType", "工程建设");
        } else {
            pageParams.put("businessType", "政府采购");
        }
        pageParams.put("type", type);
        request.putExtra("pageParams", pageParams);
        return request;
    }

    public static String jsonGenerator(String areaCode, String noticeNature, String bulletinType, String assortmentIndex) {
        GGZYGanSuFormJsonField ggzyGanSuFormJsonField = new GGZYGanSuFormJsonField();
        ggzyGanSuFormJsonField.setAreaCode(areaCode);
        GGZYGanSuFormJsonField.WorkNoticeBean workNoticeBean = new GGZYGanSuFormJsonField.WorkNoticeBean();
        workNoticeBean.setNoticeNature(noticeNature);
        workNoticeBean.setBulletinType(bulletinType);
        ggzyGanSuFormJsonField.setWorkNotice(workNoticeBean);
        ggzyGanSuFormJsonField.setAssortmentindex(assortmentIndex);
        String JsonFiled = JSONObject.toJSONString(ggzyGanSuFormJsonField);
        return JsonFiled;
    }

}
