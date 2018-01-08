package com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebeinew;

import com.alibaba.fastjson.JSONObject;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
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

import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebeinew.GGZYHeBeiPageProcessor.*;
import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebeinew.GGZYHeBeiSpiderLauncher.requestGenerator;

/**
 * Created by Administrator on 2017/12/26.
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.GGZYHEBEI,
        sources = {
                @Source(url = GGZYHEBEI_URL, post = true, postParams = POST_PARAMS_1),
                @Source(url = GGZYHEBEI_URL, post = true, postParams = POST_PARAMS_2),
                @Source(url = GGZYHEBEI_URL, post = true, postParams = POST_PARAMS_3),
                @Source(url = GGZYHEBEI_URL, post = true, postParams = POST_PARAMS_4),
                @Source(url = GGZYHEBEI_URL, post = true, postParams = POST_PARAMS_5),
                @Source(url = GGZYHEBEI_URL, post = true, postParams = POST_PARAMS_6),
                @Source(url = GGZYHEBEI_URL, post = true, postParams = POST_PARAMS_7),
                @Source(url = GGZYHEBEI_URL, post = true, postParams = POST_PARAMS_8)
        }
)
public class GGZYHeBeiPageProcessor implements BasePageProcessor {

    final static String GGZYHEBEI_URL = "http://www.hebpr.cn/inteligentsearch/rest/inteligentSearch/getFullTextDataNew";

    final static String POST_PARAMS_1 = "{'accuracy':'','cl':200,'condition':[{'equal':'003005002001','fieldName':'categorynum','isLike':true,'likeType':2}],'edt':'2018-01-08 23:59:59','fields':'title','highlights':'title','isBusiness':1,'noParticiple':'0','pn':0,'rn':10,'sdt':'2018-01-08 00:00:00','sort':'{\"showdate\":\"0\"}','ssort':'title','token':''}";
    final static String POST_PARAMS_2 = "{'accuracy':'','cl':200,'condition':[{'equal':'003005002002','fieldName':'categorynum','isLike':true,'likeType':2}],'edt':'2018-01-08 23:59:59','fields':'title','highlights':'title','isBusiness':1,'noParticiple':'0','pn':0,'rn':10,'sdt':'2018-01-08 00:00:00','sort':'{\"showdate\":\"0\"}','ssort':'title','token':''}";
    final static String POST_PARAMS_3 = "{'accuracy':'','cl':200,'condition':[{'equal':'003005002003','fieldName':'categorynum','isLike':true,'likeType':2}],'edt':'2018-01-08 23:59:59','fields':'title','highlights':'title','isBusiness':1,'noParticiple':'0','pn':0,'rn':10,'sdt':'2018-01-08 00:00:00','sort':'{\"showdate\":\"0\"}','ssort':'title','token':''}";
    final static String POST_PARAMS_4 = "{'accuracy':'','cl':200,'condition':[{'equal':'003005002004','fieldName':'categorynum','isLike':true,'likeType':2}],'edt':'2018-01-08 23:59:59','fields':'title','highlights':'title','isBusiness':1,'noParticiple':'0','pn':0,'rn':10,'sdt':'2018-01-08 00:00:00','sort':'{\"showdate\":\"0\"}','ssort':'title','token':''}";
    final static String POST_PARAMS_5 = "{'accuracy':'','cl':200,'condition':[{'equal':'003005001001','fieldName':'categorynum','isLike':true,'likeType':2}],'edt':'2018-01-08 23:59:59','fields':'title','highlights':'title','isBusiness':1,'noParticiple':'0','pn':0,'rn':10,'sdt':'2018-01-08 00:00:00','sort':'{\"showdate\":\"0\"}','ssort':'title','token':''}";
    final static String POST_PARAMS_6 = "{'accuracy':'','cl':200,'condition':[{'equal':'003005001002','fieldName':'categorynum','isLike':true,'likeType':2}],'edt':'2018-01-08 23:59:59','fields':'title','highlights':'title','isBusiness':1,'noParticiple':'0','pn':0,'rn':10,'sdt':'2018-01-08 00:00:00','sort':'{\"showdate\":\"0\"}','ssort':'title','token':''}";
    final static String POST_PARAMS_7 = "{'accuracy':'','cl':200,'condition':[{'equal':'003005001003','fieldName':'categorynum','isLike':true,'likeType':2}],'edt':'2018-01-08 23:59:59','fields':'title','highlights':'title','isBusiness':1,'noParticiple':'0','pn':0,'rn':10,'sdt':'2018-01-08 00:00:00','sort':'{\"showdate\":\"0\"}','ssort':'title','token':''}";
    final static String POST_PARAMS_8 = "{'accuracy':'','cl':200,'condition':[{'equal':'003005001004','fieldName':'categorynum','isLike':true,'likeType':2}],'edt':'2018-01-08 23:59:59','fields':'title','highlights':'title','isBusiness':1,'noParticiple':'0','pn':0,'rn':10,'sdt':'2018-01-08 00:00:00','sort':'{\"showdate\":\"0\"}','ssort':'title','token':''}";

    HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        Map<String, Object> pageParams = page.getRequest().getExtras();
        GGZYHeBeiPageParameter ggzyHeBeiPageParameter = (GGZYHeBeiPageParameter) pageParams.get("pageParams");
        int pageCount = ggzyHeBeiPageParameter.getPn();
        if (pageCount == 0) {
            GGZYHeBeiDirectoryParameter ggzyHeBeiDirectoryParameter = JSONObject.parseObject(page.getRawText(), GGZYHeBeiDirectoryParameter.class);
            int totalCount = ggzyHeBeiDirectoryParameter.getResult().getTotalcount();
            int cycleCount = totalCount % 10 == 0 ? totalCount / 10 : totalCount / 10 + 1;
            int cyclePage = cycleCount >= 20 ? 20 : cycleCount;
            for (int i = 1; i <= cyclePage; i++) {
                String url = page.getUrl().get();
                String typeId = ggzyHeBeiPageParameter.getCondition().get(0).getEqual();
                Request request = requestGenerator(url, typeId, i * 10);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Map<String, Object> pageParams = page.getRequest().getExtras();
        GGZYHeBeiPageParameter ggzyHeBeiPageParameter = (GGZYHeBeiPageParameter) pageParams.get("pageParams");
        GGZYHeBeiDirectoryParameter ggzyHeBeiDirectoryParameter = JSONObject.parseObject(page.getRawText(), GGZYHeBeiDirectoryParameter.class);
        List<GGZYHeBeiDirectoryParameter.ResultBean.RecordsBean> recordsBeanList = ggzyHeBeiDirectoryParameter.getResult().getRecords();
        List<GGZYHeBeiDataItem> dataItems = Lists.newArrayList();
        for (GGZYHeBeiDirectoryParameter.ResultBean.RecordsBean recordsBean : recordsBeanList) {
            String href = recordsBean.getLinkurl();
            if (StringUtils.isNotBlank(href)) {
                if (!StringUtils.startsWith(href, "http:")) {
                    href = "http://www.hebpr.cn" + href;
                }
                String type = recordsBean.getCategoryname();
                String date = recordsBean.getShowdate();
                String title = recordsBean.getTitle();
                String businessType = businessTypeJudge(ggzyHeBeiPageParameter.getCondition().get(0).getEqual());
                GGZYHeBeiDataItem ggzyHeBeiDataItem = new GGZYHeBeiDataItem(href);
                ggzyHeBeiDataItem.setUrl(href);
                ggzyHeBeiDataItem.setType(type);
                ggzyHeBeiDataItem.setDate(PageProcessorUtil.dataTxt(date));
                ggzyHeBeiDataItem.setBusinessType(businessType);
                ggzyHeBeiDataItem.setTitle(title);

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

    public static String businessTypeJudge(String typeId) {
        String businessType = null;
        if (typeId.startsWith("003005001")) {
            businessType = "政府采购";
        }
        if (typeId.startsWith("003005002")) {
            businessType = "工程建设";
        }
        return businessType;
    }
}
