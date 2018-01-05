package com.har.sjfxpt.crawler.petrochina;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.ProvinceUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;

import java.util.List;

/**
 * @author dongqi
 * <p>
 * 中国石油物资采购网
 * http://eportal.energyahead.com/wps/portal/eportal
 */
@Slf4j
@Component
@SourceConfig(code = SourceCode.ZSY, sources = {
        @Source(type = "公开招标", url = "http://eportal.energyahead.com/wps/portal/ebid/wcm_search/bidnotice_publish"),
        @Source(type = "资格预审", url = "http://eportal.energyahead.com/wps/portal/ebid/wcm_search/bidnotice_zgys"),
        @Source(type = "中标公示", url = "http://eportal.energyahead.com/wps/portal/ebid/wcm_search/bidresult_publish"),
        @Source(type = "中标结果", url = "http://eportal.energyahead.com/wps/portal/ebid/wcm_search/bidresult_publish_1")
})
public class PetroChinaPageProcessor implements BasePageProcessor {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    final static String KEY_URLS = "petrochina";

    @Override
    public void handlePaging(Page page) {

    }

    @Override
    public void handleContent(Page page) {
        List<BidNewsOriginal> dataItems = Lists.newArrayList();
        Elements items = page.getHtml().getDocument().body().select("div#list_content div.c1-bline");
        String formAction = page.getHtml().getDocument().select("form[name='detailForm']").attr("action");
        formAction = "http://eportal.energyahead.com" + formAction;
        log.debug(">>> form action {}", formAction);
        for (Element element : items) {
            String href = element.select("div.f-left a").attr("href");
            if (StringUtils.containsOnly(href, "#")) continue;

            String documentId = StringUtils.substringBetween(href, "(", ")");

            if (stringRedisTemplate.boundSetOps(KEY_URLS).add(documentId) == 0L) continue;

            String title = element.select("div.f-left a").attr("title");
            String purchaser = element.select("div.f-right").attr("title");
            if (purchaser.contains("-->")) {
                purchaser = StringUtils.split(purchaser, "-->")[0];
            }
            purchaser = StringUtils.strip(purchaser);

            String text = element.select("div.f-right").text();
            String date = text.split(" ")[1];
            date = PageProcessorUtil.dataTxt(date);

            log.debug(">>> {}, {}, {}, {}, {}, {}", title, href, documentId, purchaser, text, text.split(" "));

            BidNewsOriginal dataItem = new BidNewsOriginal(documentId);
            dataItem.setSource(SourceCode.ZSY.getValue());
            dataItem.setSourceCode(SourceCode.ZSY.name());
            dataItem.setDate(date);
            dataItem.setTitle(title);
            dataItem.setProvince(ProvinceUtil.get(purchaser));
            dataItem.setPurchaser(purchaser);

            try {
                Document document = Jsoup.connect(formAction).data("documentId", documentId).get();
                Elements mainContent = document.body().select("div#mainContent");
                String formatContent = PageProcessorUtil.formatElementsByWhitelist(mainContent.first());
                dataItem.setFormatContent(formatContent);
            } catch (Exception e) {
                log.error("", e);
            }

            dataItems.add(dataItem);
        }

        if (page.getRequest().getExtras().containsKey("type")) {
            String type = (String) page.getRequest().getExtra("type");
            dataItems.forEach(bidNewsOriginal -> bidNewsOriginal.setType(type));
        }

        if (CollectionUtils.isNotEmpty(dataItems)) {
            page.putField(KEY_DATA_ITEMS, dataItems);
        }
    }

    @Override
    public List parseContent(Elements items) {
        return null;
    }

    @Override
    public void process(Page page) {
        log.debug(">>> url {}", page.getUrl().get());

        handlePaging(page);
        handleContent(page);
    }

    @Override
    public Site getSite() {
        return SiteUtil.get();
    }
}
