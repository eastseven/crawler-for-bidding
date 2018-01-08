package com.har.sjfxpt.crawler.core.pageprocessor;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.model.BidNewsSpider;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzygansu.GGZYGanSuFormJsonField;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzygansu.GGZYGanSuPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzygansu.GGZYGanSuPipeline;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.THREAD_NUM;
import static com.har.sjfxpt.crawler.ggzyprovincial.ggzygansu.GGZYGanSuSpiderLauncher.jsonGenerator;
import static com.har.sjfxpt.crawler.ggzyprovincial.ggzygansu.GGZYGanSuSpiderLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/12/17.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYGansuPageProcessorTests {

    @Autowired
    GGZYGanSuPageProcessor ggzyGanSuPageProcessor;

    @Autowired
    GGZYGanSuPipeline ggzyGanSuPipeline;

    @Autowired
    HBasePipeline hBasePipeline;

    @Test
    public void testPostParams() {
        String areaCode = "620000";
        String noticeNature = "2";
        String bulletinType = "";
        String assortmentIndex = "3";

        GGZYGanSuFormJsonField ggzyGanSuFormJsonField = new GGZYGanSuFormJsonField();
        ggzyGanSuFormJsonField.setAreaCode(areaCode);
        GGZYGanSuFormJsonField.WorkNoticeBean workNoticeBean = new GGZYGanSuFormJsonField.WorkNoticeBean();
        workNoticeBean.setNoticeNature(noticeNature);
        workNoticeBean.setBulletinType(bulletinType);
        ggzyGanSuFormJsonField.setWorkNotice(workNoticeBean);
        ggzyGanSuFormJsonField.setAssortmentindex(assortmentIndex);

        String json = JSONObject.toJSONString(ggzyGanSuFormJsonField, SerializerFeature.UseSingleQuotes);
        log.debug(">>> {}", json);

        Map<String, Object> params = Maps.newHashMap();
        params.put("filterparam", json);

        log.debug(">>> {}", params);

        String finalJson = JSONObject.toJSONString(params, SerializerFeature.UseSingleQuotes);
        log.debug(">>> final post params {}", finalJson);
    }

    @Test
    public void test() {
        BidNewsSpider.create(ggzyGanSuPageProcessor)
                .addRequest(SourceConfigAnnotationUtils.toRequests(ggzyGanSuPageProcessor.getClass()))
                .addPipeline(hBasePipeline)
                .run();
    }

    @Test
    public void testggzyGansuPageProcessorTests() {

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

        Spider.create(ggzyGanSuPageProcessor)
                .addRequest(requests)
                .thread(THREAD_NUM)
                .addPipeline(ggzyGanSuPipeline)
                .run();
    }

    @Test
    public void testggzyGanSuAnnotation() {
        List<SourceModel> list = SourceConfigAnnotationUtils.find(ggzyGanSuPageProcessor.getClass());
    }

}
