package com.har.sjfxpt.crawler.ggzyprovincial.hbebtp;

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
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;

import static com.har.sjfxpt.crawler.ggzyprovincial.hbebtp.HBEBTPPageProcessor.*;

/**
 * Created by Administrator on 2017/12/6.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.HBEBPT,
        sources = {
                @Source(url = HBEBTP_URL1),
                @Source(url = HBEBTP_URL2),
                @Source(url = HBEBTP_URL3),
                @Source(url = HBEBTP_URL4),
                @Source(url = HBEBTP_URL5),
                @Source(url = HBEBTP_URL6),
                @Source(url = HBEBTP_URL7),
                @Source(url = HBEBTP_URL8),
                @Source(url = HBEBTP_URL9),
                @Source(url = HBEBTP_URL10),
                @Source(url = HBEBTP_URL11),
                @Source(url = HBEBTP_URL12),
                @Source(url = HBEBTP_URL13),
                @Source(url = HBEBTP_URL14),
                @Source(url = HBEBTP_URL15),
                @Source(url = HBEBTP_URL16),
                @Source(url = HBEBTP_URL17),
                @Source(url = HBEBTP_URL18),
                @Source(url = HBEBTP_URL19),
                @Source(url = HBEBTP_URL20),
                @Source(url = HBEBTP_URL21),
                @Source(url = HBEBTP_URL22),
                @Source(url = HBEBTP_URL23),
                @Source(url = HBEBTP_URL24),
                @Source(url = HBEBTP_URL25),
                @Source(url = HBEBTP_URL26),
                @Source(url = HBEBTP_URL27),
                @Source(url = HBEBTP_URL28),
                @Source(url = HBEBTP_URL29),
                @Source(url = HBEBTP_URL30),
                @Source(url = HBEBTP_URL31),
                @Source(url = HBEBTP_URL32),
                @Source(url = HBEBTP_URL33),
                @Source(url = HBEBTP_URL34),
                @Source(url = HBEBTP_URL35)
        }
)
public class HBEBTPPageProcessor implements BasePageProcessor {

    final static String HBEBTP_URL1 = "http://www.hbbidcloud.com/hbcloud/jyxx/002001/002001001/?Paging=1";
    final static String HBEBTP_URL2 = "http://www.hbbidcloud.com/hbcloud/jyxx/002002/002002001/?Paging=1";
    final static String HBEBTP_URL3 = "http://www.hbbidcloud.com/hbcloud/jyxx/002003/002003001/?Paging=1";
    final static String HBEBTP_URL4 = "http://www.hbbidcloud.com/hbcloud/jyxx/002004/002004001/?Paging=1";
    final static String HBEBTP_URL5 = "http://www.hbbidcloud.com/hbcloud/jyxx/002005/002005001/?Paging=1";
    final static String HBEBTP_URL6 = "http://www.hbbidcloud.com/hbcloud/jyxx/002001/002001002/?Paging=1";
    final static String HBEBTP_URL7 = "http://www.hbbidcloud.com/hbcloud/jyxx/002002/002002002/?Paging=1";
    final static String HBEBTP_URL8 = "http://www.hbbidcloud.com/hbcloud/jyxx/002003/002003002/?Paging=1";
    final static String HBEBTP_URL9 = "http://www.hbbidcloud.com/hbcloud/jyxx/002004/002004002/?Paging=1";
    final static String HBEBTP_URL10 = "http://www.hbbidcloud.com/hbcloud/jyxx/002005/002005002/?Paging=1";
    final static String HBEBTP_URL11 = "http://www.hbbidcloud.com/hbcloud/jyxx/002001/002001003/?Paging=1";
    final static String HBEBTP_URL12 = "http://www.hbbidcloud.com/hbcloud/jyxx/002002/002002003/?Paging=1";
    final static String HBEBTP_URL13 = "http://www.hbbidcloud.com/hbcloud/jyxx/002003/002003003/?Paging=1";
    final static String HBEBTP_URL14 = "http://www.hbbidcloud.com/hbcloud/jyxx/002004/002004003/?Paging=1";
    final static String HBEBTP_URL15 = "http://www.hbbidcloud.com/hbcloud/jyxx/002005/002005003/?Paging=1";
    final static String HBEBTP_URL16 = "http://www.hbbidcloud.com/hbcloud/jyxx/002001/002001004/?Paging=1";
    final static String HBEBTP_URL17 = "http://www.hbbidcloud.com/hbcloud/jyxx/002002/002002004/?Paging=1";
    final static String HBEBTP_URL18 = "http://www.hbbidcloud.com/hbcloud/jyxx/002003/002003004/?Paging=1";
    final static String HBEBTP_URL19 = "http://www.hbbidcloud.com/hbcloud/jyxx/002004/002004004/?Paging=1";
    final static String HBEBTP_URL20 = "http://www.hbbidcloud.com/hbcloud/jyxx/002005/002005004/?Paging=1";
    final static String HBEBTP_URL21 = "http://www.hbbidcloud.com/hbcloud/jyxx/002001/002001005/?Paging=1";
    final static String HBEBTP_URL22 = "http://www.hbbidcloud.com/hbcloud/jyxx/002002/002002005/?Paging=1";
    final static String HBEBTP_URL23 = "http://www.hbbidcloud.com/hbcloud/jyxx/002003/002003005/?Paging=1";
    final static String HBEBTP_URL24 = "http://www.hbbidcloud.com/hbcloud/jyxx/002004/002004005/?Paging=1";
    final static String HBEBTP_URL25 = "http://www.hbbidcloud.com/hbcloud/jyxx/002005/002005005/?Paging=1";
    final static String HBEBTP_URL26 = "http://www.hbbidcloud.com/hbcloud/jyxx/002001/002001006/?Paging=1";
    final static String HBEBTP_URL27 = "http://www.hbbidcloud.com/hbcloud/jyxx/002002/002002006/?Paging=1";
    final static String HBEBTP_URL28 = "http://www.hbbidcloud.com/hbcloud/jyxx/002003/002003006/?Paging=1";
    final static String HBEBTP_URL29 = "http://www.hbbidcloud.com/hbcloud/jyxx/002004/002004006/?Paging=1";
    final static String HBEBTP_URL30 = "http://www.hbbidcloud.com/hbcloud/jyxx/002005/002005006/?Paging=1";
    final static String HBEBTP_URL31 = "http://www.hbbidcloud.com/hbcloud/jyxx/002001/002001007/?Paging=1";
    final static String HBEBTP_URL32 = "http://www.hbbidcloud.com/hbcloud/jyxx/002002/002002007/?Paging=1";
    final static String HBEBTP_URL33 = "http://www.hbbidcloud.com/hbcloud/jyxx/002003/002003007/?Paging=1";
    final static String HBEBTP_URL34 = "http://www.hbbidcloud.com/hbcloud/jyxx/002004/002004007/?Paging=1";
    final static String HBEBTP_URL35 = "http://www.hbbidcloud.com/hbcloud/jyxx/002005/002005007/?Paging=1";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        if (StringUtils.containsIgnoreCase(url, "?Paging=1")) {
            Elements elements = page.getHtml().getDocument().body().select("#Paging > div > div > table > tbody > tr");
            String pageCount = StringUtils.substringBetween(elements.text(), "/", " ");
            if (StringUtils.isNotBlank(pageCount)) {
                int pageNum = Integer.parseInt(pageCount);
                if (pageNum >= 2) {
                    int cycleNum = pageNum >= 5 ? 5 : pageNum;
                    for (int i = 2; i <= cycleNum; i++) {
                        String urlTarget = url + "?Paging=" + i;
//                        page.addTargetRequest(urlTarget);
                    }
                }
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        String url = page.getUrl().get();
        Elements elements = page.getHtml().getDocument().body().select("#right > ul >li");
        List<BidNewsOriginal> dataItems = parseContent(elements);
        String type = typeJudgment(url);
        dataItems.forEach(dataItem -> dataItem.setType(type));
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    public String typeJudgment(String url) {
        String typeFiled = StringUtils.substringAfter(url, "jyxx/");
        String typeFiledReal = StringUtils.substringBetween(typeFiled, "/", "/");
        String type = null;
        if (typeFiledReal.startsWith("002001")) {
            type = "招标项目";
        }
        if (typeFiledReal.startsWith("002002")) {
            type = "招标公告";
        }
        if (typeFiledReal.startsWith("002003")) {
            type = "澄清修改文件";
        }
        if (typeFiledReal.startsWith("002004")) {
            type = "中标候选人公示";
        }
        if (typeFiledReal.startsWith("002005")) {
            type = "中标结果公告";
        }
        return type;
    }

    @Override
    public List parseContent(Elements items) {
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        for (Element element : items) {
            String href = element.select("a").attr("href");
            if (StringUtils.isNotBlank(href)) {
                String title = element.select("a").attr("title");
                String date = element.select("span").text();
                if (!StringUtils.startsWith(href, "http:")) {
                    href = "http://www.hbbidcloud.com" + href;
                }
                BidNewsOriginal hbebtpDataItem = new BidNewsOriginal(href, SourceCode.HBEBPT);
                hbebtpDataItem.setUrl(href);
                hbebtpDataItem.setTitle(title);
                hbebtpDataItem.setDate(PageProcessorUtil.dataTxt(date));
                hbebtpDataItem.setProvince("湖北");
                if (PageProcessorUtil.timeCompare(hbebtpDataItem.getDate())) {
                    log.warn("{} is not the same day", hbebtpDataItem.getUrl());
                } else {
                    Page page = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    try {
                        Elements elements = page.getHtml().getDocument().body().select("#tblInfo");
                        String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                        if (StringUtils.contains(formatContent, "阅读次数：")) {
                            formatContent = StringUtils.remove(formatContent, StringUtils.substringBetween(formatContent, "<h4>", "</h4>"));
                        }
                        if (StringUtils.isNotBlank(formatContent)) {
                            hbebtpDataItem.setFormatContent(formatContent);
                            dataItems.add(hbebtpDataItem);
                        }
                    } catch (Exception e) {
                        log.info("href=={}", href);
                    }
                }
            }
        }
        return dataItems;
    }

    @Override
    public void process(Page page) {
        handlePaging(page);
        handleContent(page);
    }

    @Override
    public Site getSite() {
        httpClientDownloader = new HttpClientDownloader();
        return SiteUtil.get().setSleepTime(10000);
    }
}
