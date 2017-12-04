package com.har.sjfxpt.crawler.ggzyprovincial.ggzyhn;

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

/**
 * Created by Administrator on 2017/11/29.
 */
@Slf4j
@Component
public class GGZYHNSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.CCGPHN.toString().toLowerCase() + "-current";

    @Autowired
    GGZYHNPageProcessor GGZYHNPageProcessor;

    @Autowired
    GGZYHNPipeline GGZYHNPipeline;

    final int num = Runtime.getRuntime().availableProcessors();

    String[] urls = {
            "http://www.ggzy.hi.gov.cn/ggzy/jgzbgg/index_1.jhtml",
            "http://www.ggzy.hi.gov.cn/ggzy/jgzbgs/index_1.jhtml",
            "http://www.ggzy.hi.gov.cn/ggzy/cggg/index_1.jhtml",
            "http://www.ggzy.hi.gov.cn/ggzy/cgzbgg/index_1.jhtml"
    };

    /**
     * 爬取当日数据
     */
    public void start() {
        Request[] requests = new Request[urls.length];

        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerators(urls[i]);
        }
        cleanSpider(uuid);
        Spider spider = Spider.create(GGZYHNPageProcessor)
                .addPipeline(GGZYHNPipeline)
                .setUUID(uuid)
                .addRequest(requests)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }


    public static Request requestGenerators(String url) {
        String type = StringUtils.substringBetween(url, "ggzy/", "/index");
        Request request = new Request(url);
        Map<String, String> params = Maps.newHashMap();
        if (type.equalsIgnoreCase("jgzbgg")) {
            params.put("type", "招标公告");
            params.put("businessType", "建设工程");
            request.putExtra("pageParams", params);
        }
        if (type.equalsIgnoreCase("jgzbgs")) {
            params.put("type", "中标公示");
            params.put("businessType", "建设工程");
            request.putExtra("pageParams", params);
        }
        if (type.equalsIgnoreCase("cggg")) {
            params.put("type", "采购公告");
            params.put("businessType", "政府采购");
            request.putExtra("pageParams", params);
        }
        if (type.equalsIgnoreCase("cgzbgg")) {
            params.put("type", "中标公告");
            params.put("businessType", "政府采购");
            request.putExtra("pageParams", params);
        }
        return request;
    }

}
