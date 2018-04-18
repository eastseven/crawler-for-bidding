package com.har.sjfxpt.crawler.ccgp.provincial;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;

import java.util.List;

import static com.har.sjfxpt.crawler.ccgp.provincial.HuBeiPageProcessor.*;

/**
 * Created by Administrator on 2018/1/22.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.CCGPHUBEI,
        sources = {
                @Source(url = URL1, type = ""),
                @Source(url = URL2, type = ""),
                @Source(url = URL3, type = ""),
                @Source(url = URL4, type = ""),
                @Source(url = URL5, type = ""),
                @Source(url = URL6, type = ""),
                @Source(url = URL7, type = ""),
                @Source(url = URL8, type = ""),
                @Source(url = URL9, type = ""),
                @Source(url = URL10, type = ""),
                @Source(url = URL11, type = ""),
                @Source(url = URL12, type = ""),
        }
)
public class HuBeiPageProcessor implements BasePageProcessor {

    final static String URL1 = "http://www.ccgp-hubei.gov.cn/notice/cggg/pzbgg/index_1.html";
    final static String URL2 = "http://www.ccgp-hubei.gov.cn/notice/cggg/pzhbgg/index_1.html";
    final static String URL3 = "http://www.ccgp-hubei.gov.cn/notice/cggg/pgzgg/index_1.html";
    final static String URL4 = "http://www.ccgp-hubei.gov.cn/notice/cggg/pfbgg/index_1.html";
    final static String URL5 = "http://www.ccgp-hubei.gov.cn/notice/cggg/pdylygg/index_1.html";
    final static String URL6 = "http://www.ccgp-hubei.gov.cn/notice/cggg/pqtgg/index_1.html";

    final static String URL7 = "http://www.ccgp-hubei.gov.cn/notice/cggg/czbgg/index_1.html";
    final static String URL8 = "http://www.ccgp-hubei.gov.cn/notice/cggg/czhbgg/index_1.html";
    final static String URL9 = "http://www.ccgp-hubei.gov.cn/notice/cggg/cgzgg/index_1.html";
    final static String URL10 = "http://www.ccgp-hubei.gov.cn/notice/cggg/cfbgg/index_1.html";
    final static String URL11 = "http://www.ccgp-hubei.gov.cn/notice/cggg/cdylygg/index_1.html";
    final static String URL12 = "http://www.ccgp-hubei.gov.cn/notice/cggg/cqtgg/index_1.html";

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        int pageCount = Integer.parseInt(StringUtils.substringBetween(url, "index_", ".html"));
        if (pageCount == 1) {
            String pageContent = page.getHtml().getDocument().body().select("#main > div > div > div.col-lg-9.col-sm-9.col-md-9.col-xs-9.no-padding-right.left-area.list-2 > div.col-lg-12.col-sm-12.col-md-12.col-xs-12.text-center > ul > li").text();
            int pageNum = Integer.parseInt(StringUtils.substringBetween(pageContent, "共1/", "页"));
            String type = page.getRequest().getExtra("type").toString();
            if (pageNum >= 2) {
                for (int i = 2; i <= pageNum; i++) {
                    String urlTarget = url.replace("index_1", "index_" + i);
                    Request requests = new Request(urlTarget);
                    requests.putExtra("announcementType", type);
                    page.addTargetRequest(requests);
                }
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Elements elements = page.getHtml().getDocument().body().select("#main > div > div > div.col-lg-9.col-sm-9.col-md-9.col-xs-9.no-padding-right.left-area.list-2 > div.col-lg-12.col-sm-12.col-md-12.col-xs-12.no-padding > div > ul > li");
        List<BidNewsOriginal> dataItems = parseContent(elements);
    }

    @Override
    public List parseContent(Elements items) {
        List<BidNewsOriginal> bidNewsOriginalList = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                if (!StringUtils.startsWith(href, "http://")) {
                    href = "http://www.ccgp-hubei.gov.cn" + href;
                    String title = element.select("a").text();
                    String date = element.select("span").text();
                    log.debug("url={}\ttitle={}\tdate={}", href, title, date);

                    BidNewsOriginal bidNewsOriginal = new BidNewsOriginal(href, SourceCode.CCGPHUBEI);
                    bidNewsOriginal.setProvince("湖北");
                    bidNewsOriginal.setTitle(title);
                    bidNewsOriginal.setDate(PageProcessorUtil.dataTxt(date));
                    
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
