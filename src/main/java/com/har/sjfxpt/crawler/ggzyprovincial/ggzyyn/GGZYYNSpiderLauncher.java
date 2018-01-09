package com.har.sjfxpt.crawler.ggzyprovincial.ggzyyn;

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
 * Created by Administrator on 2017/12/4.
 */
@Slf4j
@Component@Deprecated
public class GGZYYNSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.GGZYYN.toString().toLowerCase() + "-current";

    @Autowired
    GGZYYNPageProcessor GGZYYNPageProcessor;

    @Autowired
    GGZYYNPipeline GGZYYNPipeline;

    final int num = Runtime.getRuntime().availableProcessors();

    String[] urls = {
            "https://www.ynggzyxx.gov.cn/jyxx/jsgcZbgg?currentPage=1&area=000&industriesTypeCode=0&scrollValue=777",
            "https://www.ynggzyxx.gov.cn/jyxx/jsgcGzsx?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0",
            "https://www.ynggzyxx.gov.cn/jyxx/jsgcpbjggs?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0",
            "https://www.ynggzyxx.gov.cn/jyxx/jsgcZbjggs?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0",
            "https://www.ynggzyxx.gov.cn/jyxx/jsgcZbyc?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0",

            "https://www.ynggzyxx.gov.cn/jyxx/zfcg/cggg?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0",
            "https://www.ynggzyxx.gov.cn/jyxx/zfcg/gzsx?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0",
            "https://www.ynggzyxx.gov.cn/jyxx/zfcg/kbjl?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0",
            "https://www.ynggzyxx.gov.cn/jyxx/zfcg/zbjggs?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0",
            "https://www.ynggzyxx.gov.cn/jyxx/zfcg/zfcgYcgg?currentPage=1&area=000&industriesTypeCode=0&scrollValue=0"
    };

    /**
     * 爬去当日数据
     */
    public void start() {
        cleanSpider(uuid);

        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            Request request = requestGenerator(urls[i]);
            requests[i] = request;
        }
        Spider spider = Spider.create(GGZYYNPageProcessor)
                .addPipeline(GGZYYNPipeline)
                .setUUID(uuid)
                .addRequest(requests)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }


    public static Request requestGenerator(String url) {
        Request request = new Request(url);
        String typeField = StringUtils.substringBetween(url, "/jyxx/", "?currentPage=");
        Map<String, String> pageParams = Maps.newHashMap();
        switch (typeField) {
            case "jsgcZbgg":
                pageParams.put("type", "招标公告");
                pageParams.put("businessType", "工程建设");
                break;
            case "jsgcGzsx":
                pageParams.put("type", "更正事项");
                pageParams.put("businessType", "工程建设");
                break;
            case "jsgcpbjggs":
                pageParams.put("type", "评标报告");
                pageParams.put("businessType", "工程建设");
                break;
            case "jsgcZbjggs":
                pageParams.put("type", "中标结果公告");
                pageParams.put("businessType", "工程建设");
                break;
            case "jsgcZbyc":
                pageParams.put("type", "招标异常");
                pageParams.put("businessType", "工程建设");
                break;


            case "zfcg/cggg":
                pageParams.put("type", "采购公告");
                pageParams.put("businessType", "政府采购");
                break;
            case "zfcg/gzsx":
                pageParams.put("type", "更正事项");
                pageParams.put("businessType", "政府采购");
                break;
            case "zfcg/kbjl":
                pageParams.put("type", "开标记录");
                pageParams.put("businessType", "政府采购");
                break;
            case "zfcg/zbjggs":
                pageParams.put("type", "中标结果");
                pageParams.put("businessType", "政府采购");
                break;
            case "zfcg/zfcgYcgg":
                pageParams.put("type", "异常公告");
                pageParams.put("businessType", "政府采购");
                break;
        }
        request.putExtra("pageParams", pageParams);
        return request;
    }
}
