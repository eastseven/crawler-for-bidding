package com.har.sjfxpt.crawler.zgzt;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/1.
 */
@Slf4j
@Component
public class ZGZhaoTouPageProcessor implements BasePageProcessor {

    final static String PAGE_PARAMS="pageParams";


    @Override
    public void process(Page page) {
        handlePaging(page);
    }

    @Override
    public Site getSite() {
        return SiteUtil.get();
    }

    @Override
    public void handlePaging(Page page) {
        Map<String,Object> PageParams= (Map<String, Object>) page.getRequest().getExtras().get(PAGE_PARAMS);
        int currentPage= (int) PageParams.get("pageNo");
        if(currentPage==1){
            Elements pager=page.getHtml().getDocument().body().select("#tenderProjectTotalNumShow");
            log.debug("pageNum=={}",pager.text());
//            int pageNum=Integer.parseInt(pager.text());
//            log.debug("pageNum=={}",pageNum);
        }
    }

    @Override
    public void handleContent(Page page) {

    }

    @Override
    public List parseContent(Elements items) {
        return null;
    }


}
