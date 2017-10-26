package com.har.sjfxpt.crawler.jcw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.RedisScheduler;

/**
 * Created by Administrator on 2017/10/26.
 */
@Service
public class JinCaiWangSpiderLauncher {

    @Autowired
    JinCaiWangPageProcessor jinCaiWangPageProcessor;

    @Autowired
    JinCaiWangPipeline jinCaiWangPipeline;

    public void start() {
        String[] urls = {"http://www.cfcpn.com/plist/caigou?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=",
                "http://www.cfcpn.com/plist/zhengji?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=",
                "http://www.cfcpn.com/plist/jieguo?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=",
                "http://www.cfcpn.com/plist/biangeng?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo="};
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            Request request = new Request(urls[i]);
            if (urls[i].contains("caigou")) {
                request.putExtra("type", "采购");
            }
            if (urls[i].contains("zhengji")) {
                request.putExtra("type", "征集");
            }
            if (urls[i].contains("jieguo")) {
                request.putExtra("type", "结果");
            }
            if (urls[i].contains("biangeng")) {
                request.putExtra("type", "变更");
            }
            requests[i] = request;
        }
        Spider.create(jinCaiWangPageProcessor)
                .addRequest(requests)
                .addPipeline(jinCaiWangPipeline)
                .thread(10)
                .run();
    }
}
