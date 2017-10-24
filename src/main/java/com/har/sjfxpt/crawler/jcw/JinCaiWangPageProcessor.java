package com.har.sjfxpt.crawler.jcw;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by Administrator on 2017/10/24.
 */
@Slf4j
@Component
public class JinCaiWangPageProcessor implements PageProcessor{

    @Override
    public void process(Page page) {
        if(page.getUrl().toString().startsWith("http://www.cfcpn.com/plist")){
            Element html=page.getHtml().getDocument().body();
            Elements elements=html.select("body > div.container-fluid.cfcpn_container_list-bg > div > div > div.col-lg-9.cfcpn_padding_LR0.cfcpn_list_border-right > div > p > a");
            for (Element a:elements){
                log.debug("a=={}",a.attr("href"));
                page.addTargetRequest(a.attr("href"));
            }
        }else {
            String pageContent=page.getHtml().getDocument().body().toString();
            log.debug("pageContent=={}",pageContent);
        }
    }

    @Override
    public Site getSite() {
        return Site.me();
    }
}
