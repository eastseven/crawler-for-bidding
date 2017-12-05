package com.har.sjfxpt.crawler.ggzyprovincial.ggzyxz;

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
 * Created by Administrator on 2017/12/5.
 */
@Slf4j
@Component
public class GGZYXZSpiderLauncher extends BaseSpiderLauncher {


    private final String uuid = SourceCode.GGZYXZ.toString().toLowerCase() + "-current";

    @Autowired
    GGZYXZPageProcessor ggzyxzPageProcessor;

    @Autowired
    GGZYXZPipeline ggzyxzPipeline;

    final int num = Runtime.getRuntime().availableProcessors();

    String[] urls = {
            "http://www.xzggzy.gov.cn:9090/zbzsgg/index_1.jhtml",
            "http://www.xzggzy.gov.cn:9090/jyjggg/index_1.jhtml",
            "http://www.xzggzy.gov.cn:9090/zbwjcq/index_1.jhtml",
            "http://www.xzggzy.gov.cn:9090/zgysjg/index_1.jhtml",

            "http://www.xzggzy.gov.cn:9090/cggg/index_1.jhtml",
            "http://www.xzggzy.gov.cn:9090/zbgg/index_1.jhtml",
            "http://www.xzggzy.gov.cn:9090/cght/index_1.jhtml",
            "http://www.xzggzy.gov.cn:9090/gzsx/index_1.jhtml"
    };

    /**
     * 爬取当日
     */
    public void start() {
        cleanSpider(uuid);
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestGenerator(urls[i]);
        }
        Spider spider = Spider.create(ggzyxzPageProcessor)
                .addRequest(requests)
                .addPipeline(ggzyxzPipeline)
                .setUUID(uuid)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }

    public static Request requestGenerator(String url) {
        Request request = new Request(url);
        String typeField = StringUtils.substringBetween(url, "9090/", "/index_");
        Map<String, String> pageParams = Maps.newHashMap();
        switch (typeField) {
            case "zbzsgg":
                pageParams.put("type", "招标/资审公告");
                pageParams.put("businessType", "建设工程");
                break;
            case "jyjggg":
                pageParams.put("type", "交易结果公告");
                pageParams.put("businessType", "建设工程");
                break;
            case "zbwjcq":
                pageParams.put("type", "招标/招标文件澄清");
                pageParams.put("businessType", "建设工程");
                break;
            case "zgysjg":
                pageParams.put("type", "资格预审结果");
                pageParams.put("businessType", "建设工程");
                break;
            case "cggg":
                pageParams.put("type", "采购/资审公告");
                pageParams.put("businessType", "政府采购");
                break;
            case "zbgg":
                pageParams.put("type", "中标公告");
                pageParams.put("businessType", "政府采购");
                break;
            case "cght":
                pageParams.put("type", "采购合同");
                pageParams.put("businessType", "政府采购");
                break;
            case "gzsx":
                pageParams.put("type", "更正事项");
                pageParams.put("businessType", "政府采购");
                break;
        }
        request.putExtra("pageParams", pageParams);
        return request;
    }
}
