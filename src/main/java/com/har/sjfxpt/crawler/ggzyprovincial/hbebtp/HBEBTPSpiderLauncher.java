package com.har.sjfxpt.crawler.ggzyprovincial.hbebtp;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.List;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.THREAD_NUM;

/**
 * Created by Administrator on 2017/12/8.
 */
@Slf4j
@Component
public class HBEBTPSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.HBEBPT.toString().toLowerCase() + "-current";

    @Autowired
    HBEBTPPageProcessor hbebtpPageProcessor;

    @Autowired
    HBEBTPPipeline hbebtpPipeline;

    /**
     * 爬取当日数据
     */
    public void start() {
        cleanSpider(uuid);
        String url = "http://www.hbbidcloud.com/hbcloud/jyxx/00200";
        List<String> lists = Lists.newArrayList();
        List<String> listsDetail = Lists.newArrayList();
        for (int i = 1; i <= 5; i++) {
            lists.add(url + i);
        }
        for (int i = 1; i <= 7; i++) {
            for (int j = 0; j < lists.size(); j++) {
                String urlTarget = lists.get(j);
                String filed = StringUtils.substringAfter(urlTarget, "jyxx/");
                listsDetail.add(urlTarget + "/" + filed + "00" + i + "/");
            }
        }
        Request[] requests = new Request[listsDetail.size()];
        for (int i = 0; i < listsDetail.size(); i++) {
            Request request = new Request(listsDetail.get(i));
            requests[i] = request;
        }
        Spider spider = Spider.create(hbebtpPageProcessor)
                .addPipeline(hbebtpPipeline)
                .setUUID(uuid)
                .addRequest(requests)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }
}
