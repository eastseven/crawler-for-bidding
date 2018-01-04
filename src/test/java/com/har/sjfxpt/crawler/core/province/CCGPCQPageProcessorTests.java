package com.har.sjfxpt.crawler.core.province;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ccgp.ccgpcq.CCGPCQDetailAnnouncement;
import com.har.sjfxpt.crawler.ccgp.ccgpcq.CCGPCQPageProcessor;
import com.har.sjfxpt.crawler.ccgp.ccgpcq.CCGPCQPipeline;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static com.har.sjfxpt.crawler.ccgp.ccgpcq.CCGPCQSpiderLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/11/30.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CCGPCQPageProcessorTests {


    @Autowired
    CCGPCQPageProcessor ccgpcqPageProcessor;

    @Autowired
    CCGPCQPipeline ccgpcqPipeline;

    @Autowired
    HBasePipeline hBasePipeline;

    String[] urls = {
            "https://www.cqgp.gov.cn/gwebsite/api/v1/notices/stable?pi=1&ps=20&startDate=&timestamp=" + DateTime.now().getMillis() + "&type=100,200,201,202,203,204,205,206,207,309,400,401,402,3091,4001",
            "https://www.cqgp.gov.cn/gwebsite/api/v1/notices/stable?pi=1&ps=20&startDate=&timestamp=" + DateTime.now().getMillis() + "&type=301,303",
            "https://www.cqgp.gov.cn/gwebsite/api/v1/notices/stable?pi=1&ps=20&startDate=&timestamp=" + DateTime.now().getMillis() + "&type=300,302,304,3041,305,306,307,308"
    };

    @Test
    public void testCQPageProcessor() {
        Request[] requests = new Request[urls.length];
        String date = DateTime.now().toString("yyyy-MM-dd");
        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i], date);
        }
        Spider.create(ccgpcqPageProcessor)
                .addRequest(requests)
                .addPipeline(ccgpcqPipeline)
                .thread(4)
                .run();
    }

    @Test
    public void testCCGPCQAnnotation() {
        List<SourceModel> list = SourceConfigAnnotationUtils.find(ccgpcqPageProcessor.getClass());
        List<Request> requests = Lists.newArrayList();
        for (SourceModel sourceModel : list) {
            Request request = sourceModel.createRequest();
            requests.add(request);
        }
        if (!requests.isEmpty()) {
            Spider.create(ccgpcqPageProcessor)
                    .addRequest(requests.toArray(new Request[requests.size()]))
                    .addPipeline(hBasePipeline)
                    .thread(8)
                    .run();
        } else {
            log.warn("request is empty!");
        }
    }


    @Test
    public void testHref() throws UnsupportedEncodingException {
        String title = "重庆市永川区疾病预防控制中心电感耦合等离子体质谱仪采购(17A0749)预公示";
        String code = URLEncoder.encode(title.getBytes().toString(), "utf-8");
        String href = "https://www.cqgp.gov.cn/notices/detail/512574234195881984?title=" + code;
        log.debug("href=={}", href);
    }

    @Test
    public void testPageDownload() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Page page = httpClientDownloader.download(new Request("https://www.cqgp.gov.cn/gwebsite/api/v1/notices/stable/524551196971855872"), SiteUtil.get().setTimeOut(30000).toTask());
        CCGPCQDetailAnnouncement ccgpcqDetailAnnouncement = JSONObject.parseObject(page.getRawText(), CCGPCQDetailAnnouncement.class);
        log.info("rawText={}", page.getRawText());
        String html = ccgpcqDetailAnnouncement.getNotice().getHtml();
        Whitelist whitelist = Whitelist.relaxed();
        whitelist.removeTags("style");
        whitelist.removeTags("script");
        whitelist.removeAttributes("table", "style", "width", "height");
        whitelist.removeAttributes("td", "style", "width", "height");
        String formatContent = Jsoup.clean(html, whitelist);
        formatContent = StringUtils.removeAll(formatContent, "<!-{2,}.*?-{2,}>|(&nbsp;)|<o:p>|</o:p>");
        log.info("formatContent=={}", formatContent);
    }


}
