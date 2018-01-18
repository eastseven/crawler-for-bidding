package com.har.sjfxpt.crawler.ccgp.provincial;

import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ccgp.provincial.HBPageProcessor.*;

/**
 * Created by Administrator on 2018/1/17.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.CCGPHEBEI,
        sources = {
                @Source(url = URL1, post = true, postParams = POST_PARAMS_1, type = "招标公告"),
                @Source(url = URL1, post = true, postParams = POST_PARAMS_2, type = "招标公告"),
                @Source(url = URL2, post = true, postParams = POST_PARAMS_3, type = "中标公告"),
                @Source(url = URL2, post = true, postParams = POST_PARAMS_4, type = "中标公告"),
                @Source(url = URL3, post = true, postParams = POST_PARAMS_5, type = "更正公告"),
                @Source(url = URL3, post = true, postParams = POST_PARAMS_6, type = "更正公告"),
                @Source(url = URL4, post = true, postParams = POST_PARAMS_7, type = "废标公告"),
                @Source(url = URL4, post = true, postParams = POST_PARAMS_8, type = "废标公告"),
                @Source(url = URL5, post = true, postParams = POST_PARAMS_9, type = "单一来源")
        }
)
public class HBPageProcessor implements BasePageProcessor {

    final static String URL1 = "http://www.ccgp-hebei.gov.cn/zfcg/web/getBidingList_1.html";
    final static String URL2 = "http://www.ccgp-hebei.gov.cn/zfcg/web/getBidWinAnncList_1.html";
    final static String URL3 = "http://www.ccgp-hebei.gov.cn/zfcg/web/getCorrectionAnncList_1.html";
    final static String URL4 = "http://www.ccgp-hebei.gov.cn/zfcg/web/getCancelBidAnncList_1.html";
    final static String URL5 = "http://www.ccgp-hebei.gov.cn/zfcg/web/getSingleSourceList_1.html";

    final static String POST_PARAMS_1 = "{'levelFlag':'first','citycode':'130000000','cityname':'省本级'}";
    final static String POST_PARAMS_2 = "{'levelFlag':'second','citycode':'130000000','cityname':'省本级'}";
    final static String POST_PARAMS_3 = "{'levelFlag2':'first','citycode':'130000000','cityname':'省本级'}";
    final static String POST_PARAMS_4 = "{'levelFlag2':'second','citycode':'130000000','cityname':'省本级'}";
    final static String POST_PARAMS_5 = "{'recordFlag':'first','citycode':'130000000','cityname':'省本级'}";
    final static String POST_PARAMS_6 = "{'recordFlag':'second','citycode':'130000000','cityname':'省本级'}";
    final static String POST_PARAMS_7 = "{'cancellFlag':'first','citycode':'130000000','cityname':'省本级'}";
    final static String POST_PARAMS_8 = "{'cancellFlag':'second','citycode':'130000000','cityname':'省本级'}";
    final static String POST_PARAMS_9 = "{'citycode':'130000000','cityname':'省本级'}";

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        int pageCount = Integer.parseInt(StringUtils.substringBetween(url, "_", ".html"));
        if (pageCount == 1) {
            String type = page.getRequest().getExtra("type").toString();
            Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtra("pageParams");
            for (int i = 2; i <= 3; i++) {
                String urlTarget = StringUtils.replace(url, String.valueOf(pageCount), String.valueOf(i));
                Request request = new Request(urlTarget);
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
                request.putExtra("pageParams", pageParams);
                request.putExtra("type", type);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Elements elements = page.getHtml().getDocument().body().select("#moredingannctable > tbody > tr");
        List<BidNewsOriginal> dataItems = parseContent(elements);
    }

    @Override
    public List parseContent(Elements items) {
        for (Element element : items) {
            String onclick = element.attr("onclick");
            if (StringUtils.isNotBlank(onclick)) {
                if(StringUtils.startsWith(onclick,"watchContent")){

                }
            }
        }
        return null;
    }

    @Override
    public void process(Page page) {
        handlePaging(page);
        handleContent(page);
    }

    @Override
    public Site getSite() {
        return SiteUtil.get().setSleepTime(10000);
    }
}
