package com.har.sjfxpt.crawler.ccgp.ccgpsc;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.Map;

/**
 * Created by Administrator on 2017/11/8.
 */
@Slf4j
@Component
public class CCGPSiChuanSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.CCGPSC.toString().toLowerCase() + "-current";

    @Autowired
    CCGPSiChuanPageProcessor ccgpSiChuanPageProcessor;

    @Autowired
    CCGPSiChuanPipeline ccgpSiChuanPipeline;

    final int num = Runtime.getRuntime().availableProcessors();

    /**
     * 爬取全部数据
     */
    public void start() {

        String[] urls = {
                "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=cggg&rp=25&page=1",
                "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=jggg&rp=25&page=1",
                "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=gzgg&rp=25&page=1"
        };
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < requests.length; i++) {
            Map<String, Object> params = Maps.newHashMap();
            Request request = new Request(urls[i]);
            if (urls[i].contains("cggg")) {
                params.put("type", "采购公告");
            }
            if (urls[i].contains("jggg")) {
                params.put("type", "结果公告");
            }
            if (urls[i].contains("gzgg")) {
                params.put("type", "更正公告");
            }
            request.putExtra("pageParams", params);
            requests[i] = request;
        }

        Spider spider = Spider.create(ccgpSiChuanPageProcessor)
                .addPipeline(ccgpSiChuanPipeline)
                .addRequest(requests)
                .setUUID(uuid)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }

}
