package com.har.sjfxpt.crawler.ggzyprovincial.ggzygz;

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

/**
 * Created by Administrator on 2017/11/30.
 */
@Slf4j
@Component
public class GGZYGZSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.GGZYGZ.toString().toLowerCase() + "-current";

    @Autowired
    GGZYGZPageProcessor GGZYGZPageProcessor;

    @Autowired
    GGZYGZPipeline GGZYGZPipeline;

    final int num = Runtime.getRuntime().availableProcessors();

    String[] urls = {
            "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=GP&businessType=JYGG&inDates=1&ext=&origin=ALL",
            "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=GP&businessType=ZSJGGS&inDates=1&ext=&origin=ALL",
            "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=GP&businessType=JYJGGS&inDates=1&ext=&origin=ALL",
            "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=GP&businessType=FBGG&inDates=1&ext=&origin=ALL",
            "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=CE&businessType=JYGG&inDates=1&ext=&origin=ALL",
            "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=CE&businessType=ZSJGGS&inDates=1&ext=&origin=ALL",
            "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=CE&businessType=JYJGGS&inDates=1&ext=&origin=ALL",
            "http://www.gzjyfw.gov.cn/gcms/queryContent_1.jspx?title=&businessCatalog=CE&businessType=FBGG&inDates=1&ext=&origin=ALL"
    };

    /**
     * 爬取当日数据
     */
    public void start() {
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i]);
        }
        cleanSpider(uuid);
        Spider spider = Spider.create(GGZYGZPageProcessor)
                .addPipeline(GGZYGZPipeline)
                .setUUID(uuid)
                .addRequest(requests)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }

    public static Request requestGenerator(String url) {
        Map<String, String> params = Maps.newHashMap();
        Request request = new Request(url);
        String business = StringUtils.substringBetween(url, "businessCatalog=", "&businessType");
        String type = StringUtils.substringBetween(url, "businessType=", "&inDates");
        if (business.equalsIgnoreCase("GP")) {
            params.put("businessType", "政府采购");
        }
        if (business.equalsIgnoreCase("CE")) {
            params.put("businessType", "建设工程");
        }
        switch (type) {
            case "JYGG":
                params.put("type", "交易公告");
                break;
            case "ZSJGGS":
                params.put("type", "资审结果公示");
                break;
            case "JYJGGS":
                params.put("type", "交易结果公示");
                break;
            case "FBGG":
                params.put("type", "流标公示");
                break;
        }
        request.putExtra("pageParams", params);
        return request;
    }

}
