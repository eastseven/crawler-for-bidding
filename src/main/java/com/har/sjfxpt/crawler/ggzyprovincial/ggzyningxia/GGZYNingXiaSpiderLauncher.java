package com.har.sjfxpt.crawler.ggzyprovincial.ggzyningxia;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/18.
 */
@Slf4j
@Component
public class GGZYNingXiaSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.GGZYNINGXIA.toString().toLowerCase() + "-current";

    @Autowired
    GGZYNingXiaPageProcessor ggzyNingXiaPageProcessor;

    @Autowired
    GGZYNingXiaPipeline ggzyNingXiaPipeline;

    String[] urls = {
            "http://www.nxggzyjy.org/ningxiaweb/002/002001/002001001/1.html",
            "http://www.nxggzyjy.org/ningxiaweb/002/002001/002001002/1.html",
            "http://www.nxggzyjy.org/ningxiaweb/002/002001/002001003/1.html",

            "http://www.nxggzyjy.org/ningxiaweb/002/002002/002002001/1.html",
            "http://www.nxggzyjy.org/ningxiaweb/002/002002/002002002/1.html",
            "http://www.nxggzyjy.org/ningxiaweb/002/002002/002002003/1.html",
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
        Spider spider = Spider.create(ggzyNingXiaPageProcessor)
                .addPipeline(ggzyNingXiaPipeline)
                .setUUID(uuid)
                .addRequest(requests)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }

    public static Request requestGenerator(String url) {
        Request request = new Request(url);
        Map<String, String> pageParams = Maps.newHashMap();
        String typeId = StringUtils.substringBetween(StringUtils.substringAfter(url, "002/"), "/", "/");
        switch (typeId) {
            case "002001001":
                pageParams.put("type", "招标/资审公告");
                pageParams.put("businessType", "工程建设");
                break;
            case "002001002":
                pageParams.put("type", "澄清/变更公告");
                pageParams.put("businessType", "工程建设");
                break;
            case "002001003":
                pageParams.put("type", "中标公示/公告");
                pageParams.put("businessType", "工程建设");
                break;

            case "002002001":
                pageParams.put("type", "采购公告");
                pageParams.put("businessType", "政府采购");
                break;
            case "002002002":
                pageParams.put("type", "澄清/变更公告");
                pageParams.put("businessType", "政府采购");
                break;
            case "002002003":
                pageParams.put("type", "中标/成交公示");
                pageParams.put("businessType", "政府采购");
                break;
        }
        request.putExtra("pageParams", pageParams);
        return request;
    }
}
