package com.har.sjfxpt.crawler.ggzy.ggzypageprocessortests;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
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
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;
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
    public void testDownloadPage() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Page page = httpClientDownloader.download(new Request("http://www.gsggfw.cn/w/bid/tenderAnnQuaInqueryAnn/17113/details.html"), SiteUtil.get().setTimeOut(30000).toTask());
        Elements elements = page.getHtml().getDocument().body().select("body > div.mod-content.clear > div.mod-cont-lft.clear > div.mod-arti-area > div.mod-arti-body");
        String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
        Elements elements1 = elements.select("iframe");
        if (!elements1.isEmpty()) {
            String iframeUrl = elements1.attr("src");
            Page page1 = httpClientDownloader.download(new Request(iframeUrl), SiteUtil.get().setTimeOut(30000).toTask());
            Element element = page1.getHtml().getDocument().body();
            String formatContentAdd = PageProcessorUtil.formatElementsByWhitelist(element);
            formatContent = formatContent + formatContentAdd;
        }
        log.info("elements=={}", formatContent);
    }

}
