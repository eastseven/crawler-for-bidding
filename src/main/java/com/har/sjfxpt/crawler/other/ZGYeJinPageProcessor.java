package com.har.sjfxpt.crawler.other;

import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import static com.har.sjfxpt.crawler.other.ZGYeJinPageProcessor.*;

/**
 * Created by Administrator on 2017/10/27.
 *
 * @author luofei
 * @author dongqi
 */
@Slf4j
@SourceConfig(code = SourceCode.ZGYJ, useProxy = true, sources = {
        @Source(type = "采购公告", url = URL_01, post = true, postParams = POST_PARAMS_01, needPlaceholderFields = {"sbsj1", "sbsj2"}),
        @Source(type = "采购信息", url = URL_02, post = true, postParams = POST_PARAMS_02),
        @Source(type = "变更公告", url = URL_03, post = true, postParams = POST_PARAMS_03, needPlaceholderFields = {"audittime", "audittime2"}),
        @Source(type = "结果公告", url = URL_04, post = true, postParams = POST_PARAMS_04, needPlaceholderFields = {"releasedate1", "releasedate2"}),
})
@Component
public class ZGYeJinPageProcessor extends SunWayWorldPageProcessor {

    public static final String URL_01 = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMoreZbs&xxposition=zbgg";
    public static final String URL_02 = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMoreCgxx&xxposition=cgxx";
    public static final String URL_03 = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMoreClarifypub&xxposition=cqgg";
    public static final String URL_04 = "http://ec.mcc.com.cn/b2b/web/two/indexinfoAction.do?actionType=showMorePub&xxposition=zhongbgg";

    public static final String POST_PARAMS_01 = "{'currpage':1,'sbsj1':'','xxposition':'zbgg','sbsj2':''}";
    public static final String POST_PARAMS_02 = "{'currpage':1,'fbrq1':'','xxposition':'cgxx','fbrq2':''}";
    public static final String POST_PARAMS_03 = "{'currpage':1,'audittime':'','xxposition':'cqgg','audittime2':''}";
    public static final String POST_PARAMS_04 = "{'currpage':1,'releasedate1':'','xxposition':'zhongbgg','releasedate2':''}";

    public ZGYeJinPageProcessor(HttpClientDownloader httpClientDownloader, ProxyService proxyService) {
        Assert.notNull(httpClientDownloader, "httpClientDownloader must not null");
        Assert.notNull(proxyService, "proxyService must not null");
        setSourceCode(SourceCode.ZGYJ);
        setDomain("http://ec.mcc.com.cn");
        setProxyService(proxyService);
        setHttpClientDownloader(httpClientDownloader);
    }
}
