package com.har.sjfxpt.crawler.yibiao;

import com.har.sjfxpt.crawler.BaseSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.concurrent.ExecutorService;

/**
 * Created by Administrator on 2017/11/10.
 */
@Slf4j
@Component
public class YiBiaoSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.YIBIAO.toString().toLowerCase() + "-current";

    @Autowired
    YiBiaoPageProcessor yiBiaoPageProcessor;

    @Autowired
    YiBiaoPipeline yiBiaoPipeline;

    @Autowired
    ExecutorService executorService;

    final int num = Runtime.getRuntime().availableProcessors();

    /**
     * 爬取当日数据
     */
    public void start() {

        String url = "http://www.1-biao.com/data/AjaxTender.aspx?0.06563536587854646&hidtypeID=&hidIndustryID=&hidProvince=&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=1&hidPape=1&keyword=";

        cleanSpider(uuid);

        Spider spider = Spider.create(yiBiaoPageProcessor)
                .addPipeline(yiBiaoPipeline)
                .addUrl(url)
                .setUUID(uuid)
                .setExecutorService(executorService)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }

    /**
     * 按省爬取历史
     */
    public void fetchHistory() {

        String[] urls = {
                "http://www.1-biao.com/data/AjaxTender.aspx?0.648357409685727&hidtypeID=&hidIndustryID=&hidProvince=23&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.07913589179468294&hidtypeID=&hidIndustryID=&hidProvince=22&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.8090257270745143&hidtypeID=&hidIndustryID=&hidProvince=25&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.9422979259891453&hidtypeID=&hidIndustryID=&hidProvince=28&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.04858760281002139&hidtypeID=&hidIndustryID=&hidProvince=29&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.6620913280843574&hidtypeID=&hidIndustryID=&hidProvince=26&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.8155706207478035&hidtypeID=&hidIndustryID=&hidProvince=24&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.3322341578936039&hidtypeID=&hidIndustryID=&hidProvince=35&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.9031533762880772&hidtypeID=&hidIndustryID=&hidProvince=9&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.25370687964038474&hidtypeID=&hidIndustryID=&hidProvince=19&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.35661413122288366&hidtypeID=&hidIndustryID=&hidProvince=2&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.11055441314443537&hidtypeID=&hidIndustryID=&hidProvince=10&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.3708828184404822&hidtypeID=&hidIndustryID=&hidProvince=15&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.7406441498897409&hidtypeID=&hidIndustryID=&hidProvince=11&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.1909567938189609&hidtypeID=&hidIndustryID=&hidProvince=16&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.1079524430393708&hidtypeID=&hidIndustryID=&hidProvince=3&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.06407083449901907&hidtypeID=&hidIndustryID=&hidProvince=6&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.20783105626311182&hidtypeID=&hidIndustryID=&hidProvince=17&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.7090292963387401&hidtypeID=&hidIndustryID=&hidProvince=18&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.27379009242373487&hidtypeID=&hidIndustryID=&hidProvince=13&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.16236643825308827&hidtypeID=&hidIndustryID=&hidProvince=12&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.07285936278323457&hidtypeID=&hidIndustryID=&hidProvince=5&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.24270651287259248&hidtypeID=&hidIndustryID=&hidProvince=27&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.03096304712743958&hidtypeID=&hidIndustryID=&hidProvince=14&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.8849605295723706&hidtypeID=&hidIndustryID=&hidProvince=20&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.028128167892413902&hidtypeID=&hidIndustryID=&hidProvince=8&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.7843719012029409&hidtypeID=&hidIndustryID=&hidProvince=4&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.9770245372123094&hidtypeID=&hidIndustryID=&hidProvince=21&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.8596489040447564&hidtypeID=&hidIndustryID=&hidProvince=7&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.1578679411680466&hidtypeID=&hidIndustryID=&hidProvince=30&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.41885172672931925&hidtypeID=&hidIndustryID=&hidProvince=31&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword="
        };

        Request[] requests = new Request[urls.length];

        for (int i = 0; i < urls.length; i++) {
            Request request = new Request(urls[i]);
            requests[i] = request;
        }

        cleanSpider(uuid);

        Spider spider = Spider.create(yiBiaoPageProcessor)
                .addPipeline(yiBiaoPipeline)
                .addRequest(requests)
                .setExecutorService(executorService)
                .setUUID(uuid)
                .thread(num);
        addSpider(spider);
        start(uuid);
    }

}
