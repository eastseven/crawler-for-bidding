package com.har.sjfxpt.crawler.ggzy.provincial;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.provincial.ShanXiPageProcessor.*;

/**
 * Created by Administrator on 2017/12/12.
 * <p>
 * 山西公共资源 页面解析
 *
 * @author luofei
 * @author dongqi
 */
@Slf4j
@Component
@SourceConfig(code = SourceCode.GGZYSHANXI, useProxy = true, sources = {
        @Source(url = GGZYSHANXI_URL, post = true, postParams = GGZYSHANXI_POSTPARAMS1),

        @Source(url = GGZYSHANXI_URL, post = true, postParams = GGZYSHANXI_POSTPARAMS2),

        @Source(url = GGZYSHANXI_URL, post = true, postParams = GGZYSHANXI_POSTPARAMS3),

        @Source(url = GGZYSHANXI_URL, post = true, postParams = GGZYSHANXI_POSTPARAMS4)
})
public class ShanXiPageProcessor implements BasePageProcessor {

    final static String GGZYSHANXI_URL = "http://prec.sxzwfw.gov.cn/TenderProjectSx/ColTableInfoOther.do";
    final static String GGZYSHANXI_POSTPARAMS1 = "{'date':'1day','huanJie':'PUBLICITY','pageIndex':1,'end_time':'','projectType':'gcjs','begin_time':'','projectName':''}";
    final static String GGZYSHANXI_POSTPARAMS2 = "{'date':'1day','huanJie':'NOTICE','pageIndex':1,'end_time':'','projectType':'zfcg','begin_time':'','projectName':''}";
    final static String GGZYSHANXI_POSTPARAMS3 = "{'date':'1day','huanJie':'PUBLICITY','pageIndex':1,'end_time':'','projectType':'zfcg','begin_time':'','projectName':''}";
    final static String GGZYSHANXI_POSTPARAMS4 = "{'date':'1day','huanJie':'NOTICE','pageIndex':1,'end_time':'','projectType':'gcjs','begin_time':'','projectName':''}";


    final static String PAGE_PARAMS = "pageParams";

    final static String PREFIX = "http://prec.sxzwfw.gov.cn";

    @Autowired
    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().get();
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int pageNum = Integer.parseInt(pageParams.get("pageIndex").toString());
        if (pageNum == 1) {
            Elements elements = page.getHtml().getDocument().body().select("#Page_TotalPage");
            int totalPage = Integer.parseInt(elements.attr("value").toString());
            for (int i = 2; i <= totalPage; i++) {
                Map<String, Object> nextPage = Maps.newHashMap(pageParams);
                nextPage.put("pageIndex", i);
                Request request = new Request(url);
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
                request.putExtra("pageParams", nextPage);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Elements elements = page.getHtml().getDocument().body().select("table tbody tr");
        if (elements.isEmpty()) {
            log.error("fetch error, elements is empty");
            return;
        }
        List<BidNewsOriginal> dataItems = parseContent(elements);
        Object extra = page.getRequest().getExtra(PAGE_PARAMS);
        if (extra != null) {
            Map<String, Object> map = (Map<String, Object>) extra;
            String huanJie = (String) map.get("huanJie");
            if (!CollectionUtils.isEmpty(dataItems)) {
                dataItems.parallelStream().forEach(data -> {
                    if ("NOTICE".equalsIgnoreCase(huanJie)) {
                        data.setType("交易公告");
                    } else {
                        data.setType("交易结果");
                    }
                });

                page.putField(KEY_DATA_ITEMS, dataItems);
            }
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        if (items.isEmpty()) {
            return dataItems;
        }

        List<String> urls = Lists.newArrayList();
        for (Element element : items) {
            Elements tds = element.select("td");
            String projectCode = tds.get(0).text();
            String title = tds.get(1).select("a").attr("title");
            String href = tds.get(1).select("a").attr("href");
            String date = tds.get(2).text();

            date = PageProcessorUtil.dataTxt(date);

            String url = PREFIX + href;
            BidNewsOriginal dataItem = new BidNewsOriginal(url, SourceCode.GGZYSHANXI);
            dataItem.setProvince("山西");
            dataItem.setDate(date);
            dataItem.setTitle(title);
            dataItem.setProjectCode(projectCode);
            try {
                download(Jsoup.connect(url).get().body(), dataItem);
            } catch (Exception e) {
                log.error(">>> {} download fail", url);
            }
            urls.add(url);

            dataItems.add(dataItem);
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
        return SiteUtil.get().setSleepTime(10000);
    }

    public void download(Element body, BidNewsOriginal dataItem) {
        try {


            if (!body.select("div.jiaoyihuanjie.ct").isEmpty()) {
                for (Element td : body.select("div.table_project_container table.table_content tr td")) {
                    String text = td.text();
                    if (text.equalsIgnoreCase("项目名称")) {
                        dataItem.setProjectName(td.nextElementSibling().text());
                    }

                    if (text.equalsIgnoreCase("招标人")) {
                        dataItem.setPurchaser(td.nextElementSibling().text());
                    }
                }
            }

            if (!body.select("div.notice_content").isEmpty()) {
                String formatContent = PageProcessorUtil.formatElementsByWhitelist(body.select("div.notice_content").first());
                dataItem.setFormatContent(formatContent);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
