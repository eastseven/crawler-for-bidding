package com.har.sjfxpt.crawler.ccgp.provincial;

import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;

import java.util.List;

import static com.har.sjfxpt.crawler.ccgp.provincial.ShangHaiPageProcessor.*;

/**
 * Created by Administrator on 2018/1/16.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.CCGPSHANGHAI,
        useSelenium = true,
        sources = {
                @Source(url = CCGPSHANGHAI_URL1, post = true, postParams = CCGPSHANGHAI_POSTPARAMS1,needPlaceholderFields ={"query_begindayes,query_begindayes"})
        }
)
public class ShangHaiPageProcessor implements BasePageProcessor {

    final static String CCGPSHANGHAI_URL1 = "http://www.ccgp-shanghai.gov.cn/news.do?method=purchasePracticeMore";

    final static String CCGPSHANGHAI_POSTPARAMS1 = "{'findAjaxZoneAtClient':'false','flag':'cggg','method':'purchasePracticeMore','bulletininfotable_totalpages':'1','bulletininfotable_p':'1','ec_i':'bulletininfotable','bFlag':'00','bulletininfotable_pg':'1','bulletininfotable_crd':'10','bulletininfotable_rd':'10','treenum':'05','query_begindaybs':'2018-01-16','query_begindayes':'2018-01-16','t_query_flag':'1'}";
    final static String CCGPSHANGHAI_POSTPARAMS2 = "{'findAjaxZoneAtClient':'false','flag':'cggg','method':'purchasePracticeMore','bulletininfotable_totalpages':'1','bulletininfotable_p':'1','ec_i':'bulletininfotable','bFlag':'00','bulletininfotable_pg':'1','bulletininfotable_crd':'10','bulletininfotable_rd':'10','treenum':'05','query_begindaybs':'2018-01-16','query_begindayes':'2018-01-16','t_query_flag':'1'}";
    final static String CCGPSHANGHAI_POSTPARAMS3 = "{'findAjaxZoneAtClient':'false','flag':'cggg','method':'purchasePracticeMore','bulletininfotable_totalpages':'1','bulletininfotable_p':'1','ec_i':'bulletininfotable','bFlag':'00','bulletininfotable_pg':'1','bulletininfotable_crd':'10','bulletininfotable_rd':'10','treenum':'05','query_begindaybs':'2018-01-16','query_begindayes':'2018-01-16','t_query_flag':'1'}";
    final static String CCGPSHANGHAI_POSTPARAMS4 = "{'findAjaxZoneAtClient':'false','flag':'cggg','method':'purchasePracticeMore','bulletininfotable_totalpages':'1','bulletininfotable_p':'1','ec_i':'bulletininfotable','bFlag':'00','bulletininfotable_pg':'1','bulletininfotable_crd':'10','bulletininfotable_rd':'10','treenum':'05','query_begindaybs':'2018-01-16','query_begindayes':'2018-01-16','t_query_flag':'1'}";
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
        handlePaging(page);
        handleContent(page);
    }

    @Override
    public Site getSite() {
        return SiteUtil.get().setSleepTime(100000);
    }
}
