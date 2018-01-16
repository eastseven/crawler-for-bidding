package com.har.sjfxpt.crawler.core.province;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ccgp.provincial.ShangHaiPageProcessor;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2018/1/16.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShangHaiPageProcessorTests {

    @Autowired
    ShangHaiPageProcessor shangHaiPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    @Test
    public void testShangHaiPageProcessor() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(shangHaiPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(shangHaiPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(hBasePipeline)
                .thread(8)
                .run();
    }

    @Test
    public void postParamsGenerator() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("ec_i", "bulletininfotable");
        params.put("bulletininfotable_crd", "10");
        params.put("bulletininfotable_p", "1");
        params.put("findAjaxZoneAtClient", "false");
        params.put("flag", "cggg");
        params.put("t_query_flag", "1");
        params.put("bFlag", "00");
        params.put("treenum", "05");
        params.put("query_begindaybs", "2018-01-16");
        params.put("query_begindayes", "2018-01-16");
        params.put("method", "purchasePracticeMore");
        params.put("bulletininfotable_totalpages", "1");
        params.put("bulletininfotable_pg", "1");
        params.put("bulletininfotable_rd", "10");
        String json = JSONObject.toJSONString(params, SerializerFeature.UseSingleQuotes);
        log.debug("json={}", json);
    }


    @Test
    public void testPhantomjs() {
        SeleniumDownloader seleniumDownloader = new SeleniumDownloader("C:/Users/Administrator/Desktop/libs/phantomjs/windows/phantomjs.exe");
        String url = "http://www.ccgp-shanghai.gov.cn/login.do?method=beginloginnew#title";
        Page page = seleniumDownloader.download(new Request(url), SiteUtil.get().setTimeOut(300000).toTask());
    }

}
