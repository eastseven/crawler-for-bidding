package com.har.sjfxpt.crawler.dongfeng;

import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;

import java.util.List;

/**
 * Created by Administrator on 2017/11/13.
 */
@Slf4j
@Component
public class DongFengPageProcessor implements BasePageProcessor{
    @Override
    public void handlePaging(Page page) {

    }

    @Override
    public void handleContent(Page page) {

    }

    @Override
    public List parseContent(Elements items) {
        return null;
    }

    @Override
    public void process(Page page) {

    }

    @Override
    public Site getSite() {
        return null;
    }
}
