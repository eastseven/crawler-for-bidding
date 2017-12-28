package com.har.sjfxpt.crawler.ggzyprovincial.ggzyshaanxi;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.Map;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/15.
 */
@Slf4j
@Component
public class GGZYShaanXiSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.GGZYSHAANXI.toString().toLowerCase() + "-current";

    @Autowired
    GGZYShaanXiPageProcessor ggzyShaanXiPageProcessor;

    @Autowired
    GGZYShaanXiPipeline ggzyShaanXiPipeline;

    String[] urls = {
            "http://www.sxggzyjy.cn/jydt/001001/001001001/001001001001/1.html",
            "http://www.sxggzyjy.cn/jydt/001001/001001001/001001001002/1.html",
            "http://www.sxggzyjy.cn/jydt/001001/001001001/001001001005/1.html",
            "http://www.sxggzyjy.cn/jydt/001001/001001001/001001001003/1.html",

            "http://www.sxggzyjy.cn/jydt/001001/001001004/001001004001/1.html",
            "http://www.sxggzyjy.cn/jydt/001001/001001004/001001004002/1.html",
            "http://www.sxggzyjy.cn/jydt/001001/001001004/001001004003/1.html"
    };

    /**
     * 爬取当日数据
     */
    public void start() {
        cleanSpider(uuid);
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i]);
        }
        Spider spider = Spider.create(ggzyShaanXiPageProcessor)
                .addPipeline(ggzyShaanXiPipeline)
                .setUUID(uuid)
                .addRequest(requests)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }

    public static Request requestGenerator(String url) {
        Request request = new Request(url);
        String typeField = StringUtils.substringBetween(StringUtils.substringAfter(url, "001001/"), "/", "/1.html");
        Map<String, String> pageParams = Maps.newHashMap();
        switch (typeField) {
            case "001001001001":
                pageParams.put("type", "招标/资审公告");
                pageParams.put("businessType", "工程建设项目招标投标");
                break;
            case "001001001002":
                pageParams.put("type", "澄清/变更公告");
                pageParams.put("businessType", "工程建设项目招标投标");
                break;
            case "001001001005":
                pageParams.put("type", "中标候选人公示");
                pageParams.put("businessType", "工程建设项目招标投标");
                break;
            case "001001001003":
                pageParams.put("type", "中标/成交公示");
                pageParams.put("businessType", "工程建设项目招标投标");
                break;

            case "001001004001":
                pageParams.put("type", "采购公告");
                pageParams.put("businessType", "政府采购");
                break;
            case "001001004002":
                pageParams.put("type", "澄清/变更公告");
                pageParams.put("businessType", "政府采购");
                break;
            case "001001004003":
                pageParams.put("type", "中标/成交公示");
                pageParams.put("businessType", "政府采购");
                break;
        }
        request.putExtra("pageParams", pageParams);
        return request;
    }
}
