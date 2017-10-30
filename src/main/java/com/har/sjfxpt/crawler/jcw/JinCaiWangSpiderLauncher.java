package com.har.sjfxpt.crawler.jcw;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import javax.annotation.PostConstruct;

/**
 * Created on 2017/10/26.
 *
 * @author luofei
 */
@Service
public class JinCaiWangSpiderLauncher extends BaseSpiderLauncher {

    @Autowired
    JinCaiWangPageProcessor jinCaiWangPageProcessor;

    @Autowired
    JinCaiWangPipeline jinCaiWangPipeline;

    final int num = Runtime.getRuntime().availableProcessors();

    final String[] urls = {
            "http://www.cfcpn.com/plist/caigou?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=",
            "http://www.cfcpn.com/plist/zhengji?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=",
            "http://www.cfcpn.com/plist/jieguo?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=",
            "http://www.cfcpn.com/plist/biangeng?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo="
    };

    @PostConstruct
    public void init() {
        for (String url : urls) {
            Request request = new Request(url);
            String uuid = "jcw-";
            if (url.contains("caigou")) {
                request.putExtra("type", "采购");
                uuid += "caigou";
            }
            if (url.contains("zhengji")) {
                request.putExtra("type", "征集");
                uuid += "zhengji";
            }
            if (url.contains("jieguo")) {
                request.putExtra("type", "结果");
                uuid += "jieguo";
            }
            if (url.contains("biangeng")) {
                request.putExtra("type", "变更");
                uuid += "biangeng";
            }

            cleanSpider(uuid);

            Spider spider = Spider.create(jinCaiWangPageProcessor)
                    .addPipeline(jinCaiWangPipeline)
                    .addRequest(request)
                    .setUUID(uuid)
                    .thread(num);

            addSpider(spider);
        }

    }

    public void start() {
        for (String url : urls) {
            String uuid = "jcw-";
            if (url.contains("caigou")) {
                uuid += "caigou";
            }
            if (url.contains("zhengji")) {
                uuid += "zhengji";
            }
            if (url.contains("jieguo")) {
                uuid += "jieguo";
            }
            if (url.contains("biangeng")) {
                uuid += "biangeng";
            }

            start(uuid);
        }
    }
}
