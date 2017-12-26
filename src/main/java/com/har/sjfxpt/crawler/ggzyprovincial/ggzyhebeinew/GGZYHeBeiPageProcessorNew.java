package com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebeinew;

import com.alibaba.fastjson.JSONObject;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.select.Elements;
import org.mortbay.util.ajax.JSON;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/12/26.
 */
@Slf4j
@Component
public class GGZYHeBeiPageProcessorNew implements BasePageProcessor {

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
                    log.info("{} is not the same day", ggzyHeBeiDataItem.getUrl());
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

    public static Request requestGenerator(String url, String typeId, int pageCount) {
        GGZYHeBeiPageParameter ggzyHeBeiPageParameter = new GGZYHeBeiPageParameter();
        ggzyHeBeiPageParameter.setToken("");
        ggzyHeBeiPageParameter.setPn(pageCount);
        ggzyHeBeiPageParameter.setRn(10);
        ggzyHeBeiPageParameter.setSdt("2017-12-06 00:00:00");
        ggzyHeBeiPageParameter.setEdt("2017-12-26 23:59:59");
        ggzyHeBeiPageParameter.setFields("title");
        ggzyHeBeiPageParameter.setSort("{\"showdate\":\"0\"}");
        ggzyHeBeiPageParameter.setSsort("title");
        ggzyHeBeiPageParameter.setCl(200);
        List<GGZYHeBeiPageParameter.ConditionBean> conditionBeanList = Lists.newArrayList();
        GGZYHeBeiPageParameter.ConditionBean conditionBean = new GGZYHeBeiPageParameter.ConditionBean();
        conditionBean.setFieldName("categorynum");
        conditionBean.setIsLike(true);
        conditionBean.setLikeType(2);
        conditionBean.setEqual(typeId);
        conditionBeanList.add(conditionBean);
        ggzyHeBeiPageParameter.setCondition(conditionBeanList);
        ggzyHeBeiPageParameter.setTime(null);
        ggzyHeBeiPageParameter.setHighlights("title");
        ggzyHeBeiPageParameter.setStatistics(null);
        ggzyHeBeiPageParameter.setUnionCondition(null);
        ggzyHeBeiPageParameter.setAccuracy("");
        ggzyHeBeiPageParameter.setNoParticiple("0");
        ggzyHeBeiPageParameter.setSearchRange(null);
        ggzyHeBeiPageParameter.setIsBusiness(1);
        String json = JSONObject.toJSONString(ggzyHeBeiPageParameter);

        Request request = new Request(url);
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.json(json, "UTF-8"));
        request.putExtra("pageParams", ggzyHeBeiPageParameter);
        return request;
    }
}
