package com.har.sjfxpt.crawler.ggzy.pageprocessor;

import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebei.GGZYHeBeiPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebei.GGZYHeBeiPipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.THREAD_NUM;
import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebei.GGZYHeBeiSpiderLauncher.requestGenerators;

/**
 * Created by Administrator on 2017/12/8.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYHeBeiPageProcessorTests {

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

    @Test
    public void testHeBeiProcessor() {
        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            Request request = requestGenerators(urls[i]);
            requests[i] = request;
        }
        Spider.create(ggzyHeBeiPageProcessor)
                .addRequest(requests)
                .thread(THREAD_NUM)
                .addPipeline(ggzyHeBeiPipeline)
                .run();
    }

    @Test
    public void testFormcatent() {
        String text = "Z1300001715731001<font color='#CC0000'>河北</font>省地震局<font color='#CC0000'>河北</font>省区域烈度速报台网系统建设项目通信信道租赁公开招标异常结果公示";
        if (StringUtils.containsIgnoreCase(text, "<font color='#CC0000'>") || StringUtils.containsIgnoreCase(text, "</font>")) {
            text = StringUtils.remove(text, "<font color='#CC0000'>");
            text = StringUtils.remove(text, "</font>");
        }
        log.info("text=={}", text);
    }


}
