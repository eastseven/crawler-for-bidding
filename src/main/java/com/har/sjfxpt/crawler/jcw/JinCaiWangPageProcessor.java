package com.har.sjfxpt.crawler.jcw;

import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.core.utils.ProvinceUtil;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/10/24.
 *
 * @author luofei
 * @author dongqi
 */
@Slf4j
@Component
@SourceConfig(
        code = SourceCode.JC,
        sources = {
                @Source(url = "http://www.cfcpn.com/plist/caigou?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=", type = "采购"),
                @Source(url = "http://www.cfcpn.com/plist/zhengji?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=", type = "征集"),
                @Source(url = "http://www.cfcpn.com/plist/jieguo?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=", type = "结果"),
                @Source(url = "http://www.cfcpn.com/plist/biangeng?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=", type = "变更"),
        }
)
public class JinCaiWangPageProcessor implements BasePageProcessor {

    final String KEY_URLS = "jin_cai_wang";

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public void process(Page page) {
        //处理分页
        handlePaging(page);
        //获取列表内容
        handleContent(page);
    }

    @Override
    public Site getSite() {
        return SiteUtil.get();
    }

    @Override
    public void handlePaging(Page page) {
        String url = page.getUrl().toString();
        String pageNum = StringUtils.substringBefore(StringUtils.substringAfter(url, "pageNo="), "&kflag=");
        int num = Integer.parseInt(pageNum);
        if (num == 1) {
            Element html = page.getHtml().getDocument().body();
            Elements totalPage = html.select("body > div.container-fluid.cfcpn_container_list-bg > div > div > div.col-lg-9.cfcpn_padding_LR0.cfcpn_list_border-right > nav > ul > li:nth-child(11) > a");
            String size = null;
            for (Element a : totalPage) {
                size = a.text();
            }
            if (size != null) {
                int sizeNum = Integer.parseInt(size);
                for (int i = 2; i <= sizeNum; i++) {
                    String urlTarget = url.replace("1", "" + i);
                    Request request = new Request(urlTarget);
                    page.addTargetRequest(request);
                }
            }

        }
    }

    @Override
    public void handleContent(Page page) {
        Element html = page.getHtml().getDocument().body();
        Elements items = html.select("body > div.container-fluid.cfcpn_container_list-bg > div > div > div.col-lg-9.cfcpn_padding_LR0.cfcpn_list_border-right > div.cfcpn_list_content.text-left");
        List<BidNewsOriginal> dataItemList = parseContent(items);
        for (BidNewsOriginal dataItem : dataItemList) {
            if (StringUtils.isBlank(dataItem.getType())) {
                dataItem.setType(page.getRequest().getExtra("type").toString());
            }
        }
        if (dataItemList != null) {
            page.putField(KEY_DATA_ITEMS, dataItemList);
        }
    }

    @Override
    public List parseContent(Elements items) {
        List<BidNewsOriginal> dataItemList = Lists.newArrayList();

        for (Element target : items) {
            String href = target.select("p.cfcpn_list_title > a").attr("href");
            href = "http://www.cfcpn.com" + href;
            long value = stringRedisTemplate.boundSetOps(KEY_URLS).add(href);
            if (value == 0L) {
                // 重复数据
                log.warn("{} is duplication", href);
                continue;
            } else {
                log.debug("href=={}", href);
                String titleTxt = target.select("p.cfcpn_list_title > a").text();
                String date = target.select("p.cfcpn_list_date.text-right").text();
                date = StringUtils.substringAfter(date, "时间：");
                log.debug("time=={}", date);

                BidNewsOriginal jinCaiWangDataItem = new BidNewsOriginal(href, SourceCode.JC);
                jinCaiWangDataItem.setTitle(titleTxt);
                jinCaiWangDataItem.setDate(PageProcessorUtil.dataTxt(date));

                Elements elements = target.select("div.media-body");
                String content = elements.text();
                String[] text = StringUtils.split(content, "    ");
                for (int i = 0; i < text.length; i++) {
                    if (text[i].contains("采购人")) {
                        jinCaiWangDataItem.setPurchaser(StringUtils.substringAfter(text[i], ":"));
                    }
                    if (text[i].contains("采购方式")) {
                        jinCaiWangDataItem.setType(StringUtils.substringAfter(text[i], ":"));
                    }
                    if (text[i].contains("地区")) {
                        jinCaiWangDataItem.setProvince(ProvinceUtil.get(StringUtils.substringAfter(text[i], ":")));
                    }
                    if (text[i].contains("品类")) {
                        jinCaiWangDataItem.setOriginalIndustryCategory(StringUtils.substringAfter(text[i], ":"));
                    }
                }

                try {
                    Document document = Jsoup.connect(href).timeout(60000).userAgent(SiteUtil.get().getUserAgent()).get();
                    Element root = document.body().select("body > div.container-fluid.cfcpn_container_list-bg > div > div.row > div.col-lg-9.cfcpn_news_mian").first();
                    Elements title = root.select("#news_head > p.cfcpn_news_title");
                    String titleT = title.text();
                    log.debug("titleT=={}", titleT);
                    if (titleT != null) {
                        jinCaiWangDataItem.setTitle(titleT);
                    }
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(root);
                    jinCaiWangDataItem.setFormatContent(formatContent);
                } catch (IOException e) {
                    log.error("", e);
                }
                dataItemList.add(jinCaiWangDataItem);
            }

        }
        return dataItemList;
    }
}
