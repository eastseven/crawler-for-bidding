package com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebei;

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
 * Created by Administrator on 2017/12/11.
 */
@Slf4j
@Component
public class GGZYHeBeiSpiderLauncher extends BaseSpiderLauncher {

    private final String uuid = SourceCode.GGZYHEBEI.toString().toLowerCase() + "-current";

    @Autowired
    GGZYHeBeiPageProcessor ggzyHeBeiPageProcessor;

    @Autowired
    GGZYHeBeiPipeline ggzyHeBeiPipeline;

    String[] urls = {
            "http://www.hebpr.cn/fulltextsearch/rest/getfulltextdata?format=json&wd=%E6%B2%B3%E5%8C%97&sdt=&edt=&idx_cgy=jsgc&sort=0&rmk4=003005002001&pn=0&rn=10&cl=150",
            "http://www.hebpr.cn/fulltextsearch/rest/getfulltextdata?format=json&wd=%E6%B2%B3%E5%8C%97&sdt=&edt=&idx_cgy=jsgc&sort=0&rmk4=003005002004&pn=0&rn=10&cl=150",
            "http://www.hebpr.cn/fulltextsearch/rest/getfulltextdata?format=json&wd=%E6%B2%B3%E5%8C%97&sdt=&edt=&idx_cgy=jsgc&sort=0&rmk4=003005002003&pn=0&rn=10&cl=150",
            "http://www.hebpr.cn/fulltextsearch/rest/getfulltextdata?format=json&wd=%E6%B2%B3%E5%8C%97&sdt=&edt=&idx_cgy=jsgc&sort=0&rmk4=003005002002&pn=0&rn=10&cl=150",

            "http://www.hebpr.cn/fulltextsearch/rest/getfulltextdata?format=json&wd=%E6%B2%B3%E5%8C%97&sdt=&edt=&idx_cgy=zfcg&sort=0&rmk4=003005001001&pn=0&rn=10&cl=150",
            "http://www.hebpr.cn/fulltextsearch/rest/getfulltextdata?format=json&wd=%E6%B2%B3%E5%8C%97&sdt=&edt=&idx_cgy=zfcg&sort=0&rmk4=003005001004&pn=0&rn=10&cl=150",
            "http://www.hebpr.cn/fulltextsearch/rest/getfulltextdata?format=json&wd=%E6%B2%B3%E5%8C%97&sdt=&edt=&idx_cgy=zfcg&sort=0&rmk4=003005001003&pn=0&rn=10&cl=150",
            "http://www.hebpr.cn/fulltextsearch/rest/getfulltextdata?format=json&wd=%E6%B2%B3%E5%8C%97&sdt=&edt=&idx_cgy=zfcg&sort=0&rmk4=003005001002&pn=0&rn=10&cl=150",
    };

    /**
     * 爬取当日数据
     */
    public void start() {
        cleanSpider(uuid);
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            Request request = requestGenerators(urls[i]);
            requests[i] = request;
        }
        Spider spider = Spider.create(ggzyHeBeiPageProcessor)
                .addPipeline(ggzyHeBeiPipeline)
                .setUUID(uuid)
                .addRequest(requests)
                .thread(THREAD_NUM);
        addSpider(spider);
        start(uuid);
    }


    public static Request requestGenerators(String url) {
        String typeField = StringUtils.substringBetween(url, "idx_cgy=", "&sort=");
        String typeId = StringUtils.substringBetween(url, "rmk4=", "&pn=");
        Request request = new Request(url);
        Map<String, String> pageParams = Maps.newHashMap();
        if (typeField.equalsIgnoreCase("jsgc")) {
            pageParams.put("businessType", "工程建设");
        }
        if (typeField.equalsIgnoreCase("zfcg")) {
            pageParams.put("businessType", "政府采购");
        }
        switch (typeId) {
            case "003005002001":
                pageParams.put("type", "招标/资审公告");
                break;
            case "003005002004":
                pageParams.put("type", "招标结果公示");
                break;
            case "003005002003":
                pageParams.put("type", "中标候选人公示");
                break;
            case "003005002002":
                pageParams.put("type", "澄清/变更公告");
                break;
            case "003005001001":
                pageParams.put("type", "采购/资审公告");
                break;
            case "003005001004":
                pageParams.put("type", "结果公告");
                break;
            case "003005001003":
                pageParams.put("type", "中标候选人公示");
                break;
            case "003005001002":
                pageParams.put("type", "更正公告");
                break;
            default:
        }
        request.putExtra("pageParams", pageParams);
        return request;
    }


}
