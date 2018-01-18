package com.har.sjfxpt.crawler.core.province;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ccgp.provincial.ShangHaiPageProcessor;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.pipeline.MongoPipeline;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    MongoPipeline mongoPipeline;

    @Test
    public void testShangHaiPageProcessor() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(shangHaiPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(shangHaiPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(mongoPipeline)
                .thread(8)
                .run();
    }

    @Test
    public void postParamsGenerator() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("findAjaxZoneAtClient", "false");
        params.put("ec_i", "bulletininfotable");
        params.put("method", "bdetailnew");
        params.put("bulletininfotable_p", "1");
        params.put("bulletininfotable_rd", "10");
        params.put("bulletininfotable_crd", "10");
        params.put("bulletininfotable_pg", "1");
        params.put("treenum", "00");
        String json = JSONObject.toJSONString(params, SerializerFeature.UseSingleQuotes);
        log.debug("json={}", json);
    }


    @Test
    public void testPhantomjs() {
        SeleniumDownloader seleniumDownloader = new SeleniumDownloader("C:/Users/Administrator/Desktop/libs/phantomjs/windows/phantomjs.exe");
        String url = "http://www.ccgp-shanghai.gov.cn/login.do?method=beginloginnew#title";
        Page page = seleniumDownloader.download(new Request(url), SiteUtil.get().setTimeOut(300000).toTask());
    }

    @Test
    public void testDownloadPage() {
        HttpClientDownloader httpClientDownloader=new HttpClientDownloader();
        String url="http://www.ccgp-shanghai.gov.cn/emeb_bulletin.do?method=showbulletin&bulletin_id=2018002151";
        Page page=httpClientDownloader.download(new Request(url),SiteUtil.get().setTimeOut(30000).toTask());
        Element element= page.getHtml().getDocument().body();
        log.debug("html={}",element.html());
    }

}
