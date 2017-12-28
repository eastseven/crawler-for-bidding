package com.har.sjfxpt.crawler.zgjiaojian;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/28.
 */
@Slf4j
@Component
public class ZGJiaoJianSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.ZGJIAOJIAN.toString().toLowerCase() + "-current";

    @Autowired
    ZGJiaoJianPageProcessor zgJiaoJianPageProcessor;

    @Autowired
    ZGJiaoJianPipeline zgJiaoJianPipeline;

    /**
     * 爬取当日数据
     */
    public void start() {
        cleanSpider(uuid);
        Request[] requests = {
                requestGenerator("http://empm.ccccltd.cn/PMS/downpage.shtml?id=J8Gis7a8zFp0/0+cu62h4ufCdjRQ/t9M5buuVsVwZbmhKSxRhbdvSgqcr+4yWYEPz0JTUNvOCTs="),
                requestGenerator("http://empm.ccccltd.cn/PMS/downpage.shtml?id=J8Gis7a8zFp0/0+cu62h4ufCdjRQ/t9M5buuVsVwZbmhKSxRhbdvSgqcr+4yWYEPeVgXu6xroO0=")
        };
        Spider spider = Spider.create(zgJiaoJianPageProcessor)
                .addPipeline(zgJiaoJianPipeline)
                .setUUID(uuid)
                .addRequest(requests)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }

    public static Request requestGenerator(String url) {
        Request request = new Request(url);
        String typeField = StringUtils.substringBetween(url, "/t9M5buuVsVwZbmhKSxRhbdvSgqcr+", "=");
        Map<String, Object> pageParams = Maps.newHashMap();
        if ("4yWYEPz0JTUNvOCTs".equalsIgnoreCase(typeField)) {
            pageParams.put("VENUS_PAGE_NO_KEY", "1");
            pageParams.put("VENUS_PAGE_SIZE_KEY", "20");
            pageParams.put("channelId", "2013300100000000035");
        }
        if ("4yWYEPeVgXu6xroO0".equalsIgnoreCase(typeField)) {
            pageParams.put("VENUS_PAGE_NO_KEY", "1");
            pageParams.put("VENUS_PAGE_SIZE_KEY", "20");
            pageParams.put("channelId", "2013300100000000034");
        }
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
        request.putExtra("pageParams", pageParams);
        return request;
    }

}
