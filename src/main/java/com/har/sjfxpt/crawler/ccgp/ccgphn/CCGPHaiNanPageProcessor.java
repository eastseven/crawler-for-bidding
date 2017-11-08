package com.har.sjfxpt.crawler.ccgp.ccgphn;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.List;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/11/7.
 */
@Slf4j
@Component
public class CCGPHaiNanPageProcessor implements BasePageProcessor {

    private HttpClientDownloader httpClientDownloader;

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().toString();
        int num = Integer.parseInt(StringUtils.substringBetween(url, "currentPage=", "&begindate"));
        log.debug("num=={}", num);
        if (num == 1) {
            Elements pager = page.getHtml().getDocument().body().select("body > div.neibox > div.neibox02 > div.box > div > div.nei02_right > div.nei02_04 > div.nei02_04_02 > form > ul > li");
            String test = StringUtils.trim(pager.text());
            int pageNum = Integer.parseInt(StringUtils.substringBetween(test, "总共", "页"));
            log.debug("pageNum=={}", pageNum);
            for (int i = 2; i <= pageNum; i++) {
                String targetUrl = StringUtils.replace(url, "currentPage=1", "currentPage=" + i);
                page.addTargetRequest(targetUrl);
            }
        }
    }

    @Override
    public void handleContent(Page page) {
        Elements elements = page.getHtml().getDocument().body().select("body > div.neibox > div.neibox02 > div.box > div > div.nei02_right > div.nei02_04 > div.nei02_04_01 >ul >li");
        if (elements.isEmpty()) {
            log.error("fetch error, elements is empty");
            return;
        }
        List<CCGPHaiNanModel> dataItems = parseContent(elements);
        if(!dataItems.isEmpty()){
            page.putField(KEY_DATA_ITEMS,dataItems);
        }else {
            log.warn("fetch {} no data", page.getUrl().get());
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<CCGPHaiNanModel> dataItems = Lists.newArrayList();
        for (Element a : items) {
            Elements element = a.select("em>a");
            Elements type = a.select("span>tt");
            String date = a.select("i").text();
            String typeTxt = type.text();
            if (typeTxt.contains("| ")) {
                typeTxt = StringUtils.replace(typeTxt, "| ", "");
            }
            String url = element.attr("href");
            String title = element.text();
            String projectName= StringUtils.substringBeforeLast(title,"-");
            CCGPHaiNanModel CCGPHaiNanModel = new CCGPHaiNanModel(url);
            CCGPHaiNanModel.setType(typeTxt);
            CCGPHaiNanModel.setUrl("http://www.ccgp-hainan.gov.cn" + url);
            CCGPHaiNanModel.setTitle(title);
            CCGPHaiNanModel.setDate(date);
            CCGPHaiNanModel.setProjectName(StringUtils.defaultString(projectName,""));

            log.debug("url=={}", url);
            Request request = new Request("http://www.ccgp-hainan.gov.cn" + url);
            Page page = httpClientDownloader.download(request, SiteUtil.get().toTask());
            String html = page.getHtml().getDocument().html();
            Element element1=page.getHtml().getDocument().body();
            Elements source=element1.select("body > div.neibox > div.neibox02 > div.box > div > div.nei03_02 > div.basic");
            String tenderer=StringUtils.substringBetween(source.text(),"信息来源："," 公告类型：");
            String dateDatail=StringUtils.substringAfter(source.text(),"发表时间：");
            if(StringUtils.isNotBlank(dateDatail)){
                CCGPHaiNanModel.setDate(dateDatail);
            }
            CCGPHaiNanModel.setPurchaser(tenderer);
            Element formatContentHtml=element1.select("body > div.neibox > div.neibox02 > div.box > div > div.nei03_02").first();
            String fromatContent= PageProcessorUtil.formatElementsByWhitelist(formatContentHtml);
            log.debug("tenderer=={}dateDatail=={}",tenderer,dateDatail);
            if(StringUtils.isNotBlank(html)){
                CCGPHaiNanModel.setHtml(html);
                CCGPHaiNanModel.setFormatContent(fromatContent);
            }
            dataItems.add(CCGPHaiNanModel);
        }
        return dataItems;
    }

    @Override
    public void process(Page page) {

        handlePaging(page);

        handleContent(page);
    }

    @Override
    public Site getSite() {
        httpClientDownloader = new HttpClientDownloader();
        return SiteUtil.get();
    }
}
