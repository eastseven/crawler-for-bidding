package com.har.sjfxpt.crawler.ggzyprovincial.ggzyhlj;

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

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/6.
 */
@Slf4j
@Component
public class GGZYHLJSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.GGZYHLJ.toString().toLowerCase() + "-current";

    @Autowired
    GGZYHLJPageProcessor ggzyhljPageProcessor;

    @Autowired
    GGZYHLJPipeline ggzyhljPipeline;

    String[] urls = {
            "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=1",
            "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=5",
            "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=7",
            "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=4",
            "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=3"
    };

    /**
     * 爬取当日数据
     */
    public void start() {
        cleanSpider(uuid);
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestJudgment(urls[i]);
        }
        Spider spider = Spider.create(ggzyhljPageProcessor)
                .addPipeline(ggzyhljPipeline)
                .setUUID(uuid)
                .addRequest(requests)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }

    public static Request requestJudgment(String url) {
        Request request = new Request(url);
        String typeId = StringUtils.substringAfter(url, "type=");
        Map<String, String> pageParams = Maps.newHashMap();
        pageParams.put("businessType", "工程建设信息");
        switch (typeId) {
            case "1":
                pageParams.put("type", "交易公告");
                break;
            case "5":
                pageParams.put("type", "流标/废标公示");
                break;
            case "7":
                pageParams.put("type", "项目澄清");
                break;
            case "4":
                pageParams.put("type", "中标候选人公示");
                break;
            case "3":
                pageParams.put("type", "交易证明书");
                break;
        }
        request.putExtra("pageParams", pageParams);
        return request;
    }

}
