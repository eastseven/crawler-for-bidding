package com.har.sjfxpt.crawler.ggzy.provincial;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.provincial.HeBeiPageProcessor.*;

/**
 * Created by Administrator on 2017/12/26.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZYHEBEI,
        sources = {
                @Source(url = GGZYHEBEI_URL, post = true, postParams = POST_PARAMS_1, dateStartField = "sdt", dateEndField = "edt", dayScope = "1D"),
                @Source(url = GGZYHEBEI_URL, post = true, postParams = POST_PARAMS_2, dateStartField = "sdt", dateEndField = "edt", dayScope = "1D"),
                @Source(url = GGZYHEBEI_URL, post = true, postParams = POST_PARAMS_3, dateStartField = "sdt", dateEndField = "edt", dayScope = "1D"),
                @Source(url = GGZYHEBEI_URL, post = true, postParams = POST_PARAMS_4, dateStartField = "sdt", dateEndField = "edt", dayScope = "1D"),
                @Source(url = GGZYHEBEI_URL, post = true, postParams = POST_PARAMS_5, dateStartField = "sdt", dateEndField = "edt", dayScope = "1D"),
                @Source(url = GGZYHEBEI_URL, post = true, postParams = POST_PARAMS_6, dateStartField = "sdt", dateEndField = "edt", dayScope = "1D"),
                @Source(url = GGZYHEBEI_URL, post = true, postParams = POST_PARAMS_7, dateStartField = "sdt", dateEndField = "edt", dayScope = "1D"),
                @Source(url = GGZYHEBEI_URL, post = true, postParams = POST_PARAMS_8, dateStartField = "sdt", dateEndField = "edt", dayScope = "1D")
        }
)
public class HeBeiPageProcessor implements BasePageProcessor {

    final static String GGZYHEBEI_URL = "http://www.hebpr.cn/inteligentsearch/rest/inteligentSearch/getFullTextDataNew";

    final static String POST_PARAMS_1 = "{'accuracy':'','cl':200,'condition':[{'equal':'003005002001','fieldName':'categorynum','isLike':true,'likeType':2}],'edt':'','fields':'title','highlights':'title','isBusiness':1,'noParticiple':'0','pn':0,'rn':10,'sdt':'','sort':'{\"showdate\":\"0\"}','ssort':'title','token':''}";
    final static String POST_PARAMS_2 = "{'accuracy':'','cl':200,'condition':[{'equal':'003005002002','fieldName':'categorynum','isLike':true,'likeType':2}],'edt':'','fields':'title','highlights':'title','isBusiness':1,'noParticiple':'0','pn':0,'rn':10,'sdt':'','sort':'{\"showdate\":\"0\"}','ssort':'title','token':''}";
    final static String POST_PARAMS_3 = "{'accuracy':'','cl':200,'condition':[{'equal':'003005002003','fieldName':'categorynum','isLike':true,'likeType':2}],'edt':'','fields':'title','highlights':'title','isBusiness':1,'noParticiple':'0','pn':0,'rn':10,'sdt':'','sort':'{\"showdate\":\"0\"}','ssort':'title','token':''}";
    final static String POST_PARAMS_4 = "{'accuracy':'','cl':200,'condition':[{'equal':'003005002004','fieldName':'categorynum','isLike':true,'likeType':2}],'edt':'','fields':'title','highlights':'title','isBusiness':1,'noParticiple':'0','pn':0,'rn':10,'sdt':'','sort':'{\"showdate\":\"0\"}','ssort':'title','token':''}";
    final static String POST_PARAMS_5 = "{'accuracy':'','cl':200,'condition':[{'equal':'003005001001','fieldName':'categorynum','isLike':true,'likeType':2}],'edt':'','fields':'title','highlights':'title','isBusiness':1,'noParticiple':'0','pn':0,'rn':10,'sdt':'','sort':'{\"showdate\":\"0\"}','ssort':'title','token':''}";
    final static String POST_PARAMS_6 = "{'accuracy':'','cl':200,'condition':[{'equal':'003005001002','fieldName':'categorynum','isLike':true,'likeType':2}],'edt':'','fields':'title','highlights':'title','isBusiness':1,'noParticiple':'0','pn':0,'rn':10,'sdt':'','sort':'{\"showdate\":\"0\"}','ssort':'title','token':''}";
    final static String POST_PARAMS_7 = "{'accuracy':'','cl':200,'condition':[{'equal':'003005001003','fieldName':'categorynum','isLike':true,'likeType':2}],'edt':'','fields':'title','highlights':'title','isBusiness':1,'noParticiple':'0','pn':0,'rn':10,'sdt':'','sort':'{\"showdate\":\"0\"}','ssort':'title','token':''}";
    final static String POST_PARAMS_8 = "{'accuracy':'','cl':200,'condition':[{'equal':'003005001004','fieldName':'categorynum','isLike':true,'likeType':2}],'edt':'','fields':'title','highlights':'title','isBusiness':1,'noParticiple':'0','pn':0,'rn':10,'sdt':'','sort':'{\"showdate\":\"0\"}','ssort':'title','token':''}";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        Map<String, Object> pageParams = (Map<String, Object>) page.getRequest().getExtras().get("pageParams");
        int pageCount = (int) pageParams.get("pn");
        if (pageCount == 0) {
            JSONObject jsonObject = (JSONObject) JSONObject.parse(page.getRawText());
            int totalCount = Integer.parseInt(JSONPath.eval(jsonObject, "$.result.totalcount").toString());
            if (totalCount >= 1) {
                for (int i = 1; i <= totalCount; i++) {
                    Request request = new Request(page.getUrl().get());
                    Map<String, Object> nextPageParams = Maps.newHashMap(pageParams);
                    nextPageParams.put("pn", i);
                    request.setMethod(HttpConstant.Method.POST);
                    request.setRequestBody(HttpRequestBody.json(JSONObject.toJSONString(pageParams), "UTF-8"));
                    request.putExtra("pageParams", nextPageParams);
                    page.addTargetRequest(request);
                }
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        Selectable recoredList = page.getJson().jsonPath("$.result.records");
        List<String> stringList = recoredList.all();
        for (String field : stringList) {
            JSONObject jsonObject = (JSONObject) JSONObject.parse(field);
            String href = JSONPath.eval(jsonObject, "$.linkurl").toString();
            if (StringUtils.isNotBlank(href)) {
                if (!StringUtils.startsWith(href, "http:")) {
                    href = "http://www.hebpr.cn" + href;
                }
                String type = JSONPath.eval(jsonObject, "$.categoryname").toString();
                String date = JSONPath.eval(jsonObject, "$.showdate").toString();
                String title = JSONPath.eval(jsonObject, "$.title").toString();
                BidNewsOriginal ggzyHeBeiDataItem = new BidNewsOriginal(href, SourceCode.GGZYHEBEI);
                ggzyHeBeiDataItem.setType(type);
                ggzyHeBeiDataItem.setDate(PageProcessorUtil.dataTxt(date));
                ggzyHeBeiDataItem.setTitle(title);
                ggzyHeBeiDataItem.setProvince("河北");

                if (PageProcessorUtil.timeCompare(ggzyHeBeiDataItem.getDate())) {
                    log.info("{} {} is not the same day", ggzyHeBeiDataItem.getUrl(), ggzyHeBeiDataItem.getDate());
                } else {
                    Page page1 = httpClientDownloader.download(new Request(href), SiteUtil.get().setTimeOut(30000).toTask());
                    Elements elements = page1.getHtml().getDocument().body().select("#hideDeil");
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                    if (StringUtils.isNotBlank(formatContent)) {
                        ggzyHeBeiDataItem.setFormatContent(formatContent);
                        dataItems.add(ggzyHeBeiDataItem);
                    }
                }
            }
        }
        if (!dataItems.isEmpty()) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        } else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
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
        httpClientDownloader = new HttpClientDownloader();
        return SiteUtil.get().setSleepTime(10000);
    }
}
