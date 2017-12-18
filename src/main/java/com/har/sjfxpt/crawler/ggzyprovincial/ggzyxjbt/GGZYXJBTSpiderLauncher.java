package com.har.sjfxpt.crawler.ggzyprovincial.ggzyxjbt;

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
public class GGZYXJBTSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.GGZYXJBT.toString().toLowerCase() + "-current";

    @Autowired
    GGZYXJBTPageProcessor ggzyxjbtPageProcessor;

    @Autowired
    GGZYXJBTPipeline ggzyxjbtPipeline;

    String[] urls = {
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001002/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001003/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001004/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001005/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001006/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004001/004001007/?Paging=1",

            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002006/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002002/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002003/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002004/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002005/?Paging=1",
            "http://ggzy.xjbt.gov.cn/TPFront/jyxx/004002/004002007/?Paging=1",
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
        Spider spider = Spider.create(ggzyxjbtPageProcessor)
                .addPipeline(ggzyxjbtPipeline)
                .setUUID(uuid)
                .addRequest(requests)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }


    public static Request requestGenerator(String url) {
        Request request = new Request(url);
        Map<String, String> pageParams = Maps.newHashMap();
        String bussinessTypeId = StringUtils.substringBefore(StringUtils.substringAfter(url, "jyxx/"), "/");
        if ("004001".equalsIgnoreCase(bussinessTypeId)) {
            pageParams.put("businessType", "工程建设");
        }
        if ("004002".equalsIgnoreCase(bussinessTypeId)) {
            pageParams.put("businessType", "政府采购");
        }
        String typeId = StringUtils.substringBetween(StringUtils.substringAfter(url, "jyxx/"), "/", "/");
        switch (typeId) {
            case "004001002":
                pageParams.put("type", "招标公告");
                break;
            case "004001003":
                pageParams.put("type", "答疑澄清");
                break;
            case "004001004":
                pageParams.put("type", "中标候选人公示");
                break;
            case "004001005":
                pageParams.put("type", "中标结果公告");
                break;
            case "004001006":
                pageParams.put("type", "资格预审公示");
                break;
            case "004001007":
                pageParams.put("type", "变更公告");
                break;

            case "004002006":
                pageParams.put("type", "单一来源公示");
                break;
            case "004002002":
                pageParams.put("type", "采购公告");
                break;
            case "004002003":
                pageParams.put("type", "变更公告");
                break;
            case "004002004":
                pageParams.put("type", "答疑澄清");
                break;
            case "004002005":
                pageParams.put("type", "结果公示");
                break;
            case "004002007":
                pageParams.put("type", "合同公示");
                break;
        }
        request.putExtra("pageParams", pageParams);
        return request;
    }
}
