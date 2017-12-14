package com.har.sjfxpt.crawler.ggzyprovincial.ggzyjiangxi;

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
 * Created by Administrator on 2017/12/14.
 */
@Slf4j
@Component
public class GGZYJiangXiSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.GGZYJIANGXI.toString().toLowerCase() + "-current";

    @Autowired
    GGZYJiangXiPageProcessor ggzyJiangXiPageProcessor;

    @Autowired
    GGZYJiangXiPipeline ggzyJiangXiPipeline;


    String[] urls = {
            "http://jxsggzy.cn/web/jyxx/002006/002006001/1.html",
            "http://jxsggzy.cn/web/jyxx/002006/002006002/1.html",
            "http://jxsggzy.cn/web/jyxx/002006/002006003/1.html",
            "http://jxsggzy.cn/web/jyxx/002006/002006004/1.html",
            "http://jxsggzy.cn/web/jyxx/002006/002006005/1.html",
            "http://jxsggzy.cn/web/jyxx/002006/002006006/1.html",

            "http://jxsggzy.cn/web/jyxx/002001/002001001/1.html",
            "http://jxsggzy.cn/web/jyxx/002001/002001002/1.html",
            "http://jxsggzy.cn/web/jyxx/002001/002001003/1.html",
            "http://jxsggzy.cn/web/jyxx/002001/002001004/1.html",

            "http://jxsggzy.cn/web/jyxx/002002/002002002/1.html",
            "http://jxsggzy.cn/web/jyxx/002002/002002003/1.html",
            "http://jxsggzy.cn/web/jyxx/002002/002002005/1.html",

            "http://jxsggzy.cn/web/jyxx/002003/002003001/1.html",
            "http://jxsggzy.cn/web/jyxx/002003/002003002/1.html",
            "http://jxsggzy.cn/web/jyxx/002003/002003003/1.html",
            "http://jxsggzy.cn/web/jyxx/002003/002003004/1.html",

            "http://jxsggzy.cn/web/jyxx/002005/002005001/1.html",
            "http://jxsggzy.cn/web/jyxx/002005/002005002/1.html",
            "http://jxsggzy.cn/web/jyxx/002005/002005003/1.html",
            "http://jxsggzy.cn/web/jyxx/002005/002005004/1.html",
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
        Spider spider = Spider.create(ggzyJiangXiPageProcessor)
                .addPipeline(ggzyJiangXiPipeline)
                .addRequest(requests)
                .setUUID(uuid)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }

    public static Request requestGenerator(String url) {
        Request request = new Request(url);
        Map<String, String> pageParams = Maps.newHashMap();
        String urlType = StringUtils.substringAfter(url, "jyxx/");
        if (StringUtils.startsWith(urlType, "002006/")) {
            pageParams.put("businessType", "政府采购");
            String typeId = StringUtils.substringBetween(urlType, "002006/", "/1.html");
            if ("002006001".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "采购公告");
            }
            if ("002006002".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "变更公告");
            }
            if ("002006003".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "答疑澄清");
            }
            if ("002006004".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "结果公示");
            }
            if ("002006005".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "单一来源公示");
            }
            if ("002006006".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "合同公示");
            }
        }
        if (StringUtils.startsWith(urlType, "002001/")) {
            pageParams.put("businessType", "房建及市政工程");
            String typeId = StringUtils.substringBetween(urlType, "002001/", "/1.html");
            if ("002001001".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "招标公告");
            }
            if ("002001002".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "答疑澄清");
            }
            if ("002001003".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "文件下载");
            }
            if ("002001004".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "中标公示");
            }
        }
        if (StringUtils.startsWith(urlType, "002002/")) {
            pageParams.put("businessType", "交通工程");
            String typeId = StringUtils.substringBetween(urlType, "002002/", "/1.html");
            if ("002002002".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "招标公告");
            }
            if ("002002003".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "补遗书");
            }
            if ("002002005".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "中标公示");
            }
        }

        if (StringUtils.startsWith(urlType, "002003/")) {
            pageParams.put("businessType", "水利工程");
            String typeId = StringUtils.substringBetween(urlType, "002003/", "/1.html");
            if ("002003001".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "资格预审公告/招标公告");
            }
            if ("002003002".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "澄清补遗");
            }
            if ("002003003".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "文件下载");
            }
            if ("002003004".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "中标候选人公示");
            }
        }

        if (StringUtils.startsWith(urlType, "002005/")) {
            pageParams.put("businessType", "水利工程");
            String typeId = StringUtils.substringBetween(urlType, "002005/", "/1.html");
            if ("002005001".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "招标公告");
            }
            if ("002005002".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "答疑澄清");
            }
            if ("002005003".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "文件下载");
            }
            if ("002005004".equalsIgnoreCase(typeId)) {
                pageParams.put("type", "结果公示");
            }
        }
        request.putExtra("pageParams", pageParams);
        return request;
    }

}
