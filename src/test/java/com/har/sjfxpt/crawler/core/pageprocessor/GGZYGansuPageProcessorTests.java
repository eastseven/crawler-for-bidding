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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<Request> requestList = list.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        log.debug("request={}", requestList);
        Spider.create(ggzyGanSuPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(hBasePipeline)
                .thread(8)
                .run();
    }

    @Test
    public void testDownPage() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        String href = "http://www.gsggfw.cn/w/bid/bidDealAnnounce/7730/details.html";
        Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
        Elements elements = page.getHtml().getDocument().body().select("body > div.mod-content.clear > div.mod-cont-lft.clear > div.mod-arti-area > div.mod-arti-body");
        String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
        Elements elements1 = elements.select("iframe");
        if (!elements1.isEmpty()) {
            String iframeUrl = elements1.attr("src");
            log.debug("iframeUrl={}", iframeUrl);
            Page page1 = httpClientDownloader.download(new Request(iframeUrl), SiteUtil.get().setTimeOut(30000).toTask());
            Element element1 = page1.getHtml().getDocument().body();
            String formatContentAdd = PageProcessorUtil.formatElementsByWhitelist(element1.html());
            formatContent = formatContent + formatContentAdd;
        }
        if (StringUtils.isNotBlank(formatContent)) {
            log.debug("formatContent={}", formatContent);
        }
    }

    @Test
    public void testBlock() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        String href = "http://www.gsggfw.cn/w/bid/winResultAnno/60950/details.html";
        Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
        Elements elements = page.getHtml().getDocument().body().select("body > div.mod-content.clear > div.mod-cont-lft.clear > div.mod-arti-area > div.mod-arti-body");
        log.debug("first={}", elements.first());
        log.debug("{}", elements.first().isBlock());
    }
}
