package com.har.sjfxpt.crawler.ccgp.ccgpsc;

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
        cleanSpider(uuid);
        String[] urls = {
                "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=cggg&rp=25&page=1",
                "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=jggg&rp=25&page=1",
                "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=gzgg&rp=25&page=1",

                "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=shiji_cggg1&rp=25&page=1",
                "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=shiji_cggg&rp=25&page=1",
                "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=shiji_jggg&rp=25&page=1",
                "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=shiji_cjgg&rp=25&page=1",
                "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=shiji_gzgg&rp=25&page=1",
                "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=shiji_fblbgg&rp=25&page=1",
                "http://www.sczfcg.com/CmsNewsController.do?method=recommendBulletinList&moreType=provincebuyBulletinMore&channelCode=shiji_qtgg&rp=25&page=1"

        };
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < requests.length; i++) {
            if (urls[i].contains("shiji_")) {
                String type = StringUtils.substringBetween(urls[i], "shiji_", "&rp");
                Map<String, Object> params = Maps.newHashMap();
                Request request = new Request(urls[i]);
                if (type.equalsIgnoreCase("cggg1")) {
                    params.put("type", "资格预审公告");
                }
                if (type.equalsIgnoreCase("cggg")) {
                    params.put("type", "采购公告");
                }
                if (type.equalsIgnoreCase("jggg")) {
                    params.put("type", "中标公告");
                }
                if (type.equalsIgnoreCase("cjgg")) {
                    params.put("type", "成交公告");
                }
                if (type.equalsIgnoreCase("gzgg")) {
                    params.put("type", "更正公告");
                }
                if (type.equalsIgnoreCase("fblbgg")) {
                    params.put("type", " 废标流标公告");
                }
                if (type.equalsIgnoreCase("qtgg")) {
                    params.put("type", "其他公告");
                }
                request.putExtra("pageParams", params);
                requests[i] = request;
            } else {
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
