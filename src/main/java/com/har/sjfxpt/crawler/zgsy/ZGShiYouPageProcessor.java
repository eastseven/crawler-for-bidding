package com.har.sjfxpt.crawler.zgsy;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
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

/**
 * Created by Administrator on 2017/10/30.
 */
@Slf4j
@Component
public class ZGShiYouPageProcessor implements BasePageProcessor {

    final static String PAGE_PARAMS = "pageParams";

    @Override
    public void process(Page page) {
        handlePaging(page);

        handleContent(page);
    }

    @Override
    public Site getSite() {
        return SiteUtil.get();
    }

    @Override
    public void handlePaging(Page page) {
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int currentPage = (int) pageParams.get("pageNo");
        if (currentPage == 1) {
            Elements pager = page.getHtml().getDocument().body().select("#mainContent > table > tbody > tr > td > table > tbody > tr > td > div > table > tbody > tr > td > table > tbody > tr:nth-child(2) > td > div > span:nth-child(5)");
            String pageNum = pager.text();
            int num = Integer.parseInt(StringUtils.substringBefore(StringUtils.substringAfter(pageNum, "共"), "页").replaceAll(" ", ""));
            log.debug("num=={}", num);
            if (num > 1) {
                for (int i = 2; i <= num; i++) {
                    pageParams.put("pageNo", i);
                    Request request = new Request(page.getUrl().get());
                    request.setMethod(HttpConstant.Method.POST);
                    request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
                    request.putExtra(PAGE_PARAMS, pageParams);
                    page.addTargetRequest(request);
                }
            }
        }

    }

    @Override
    public void handleContent(Page page) {
        Map<String,Object> pageParams= (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        Elements elements=page.getHtml().getDocument().body().select("#list_content > div > div");

        if(elements.isEmpty()){
            log.error("fetch error, elements is empty");
            return;
        }

        List<ZGShiYouDataItem> dataItems=parseContent(elements);
    }

    @Override
    public List parseContent(Elements items) {
        List<ZGShiYouDataItem> dataItems= Lists.newArrayList();
        for (Element a:items){
            String title=a.select("div.f-left > a").text();
            String date=a.select("div.f-right").text();
            log.debug("title=={}",title);

            log.debug("date=={}",date);
        }

        return null;
    }


}
