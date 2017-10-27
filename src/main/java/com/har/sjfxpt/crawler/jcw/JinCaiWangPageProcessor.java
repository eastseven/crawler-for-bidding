package com.har.sjfxpt.crawler.jcw;

import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.ProvinceUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
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
 */
@Slf4j
@Component
public class JinCaiWangPageProcessor implements BasePageProcessor {

    final String KEY_URLS = "jin_cai_wang";

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public void process(Page page) {

        //处理分页
        handlePaging(page);

        //获取列表内容
        Element html = page.getHtml().getDocument().body();
        Elements items = html.select("body > div.container-fluid.cfcpn_container_list-bg > div > div > div.col-lg-9.cfcpn_padding_LR0.cfcpn_list_border-right > div.cfcpn_list_content.text-left");
        List<JinCaiWangDataItem> dataItemList = parseContent(items);
        String type = (String) page.getRequest().getExtras().get("type");
        dataItemList.forEach(dataItem -> dataItem.setType(type));
        if (dataItemList != null) {
            page.putField("dataItemList", dataItemList);
        }
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
                    log.debug("urlTarget=={}", urlTarget);
                    String type = (String) page.getRequest().getExtras().get("type");
                    Request request = new Request(urlTarget);
                    request.putExtra("type", type);
                    request.putExtra("ignore", true);
                    page.addTargetRequest(request);
                }
            }

        }
    }

    @Override
    public void handleContent(Page page) {

    }

    @Override
    public List parseContent(Elements items) {
        List<JinCaiWangDataItem> dataItemList = Lists.newArrayList();

        for (Element target : items) {
            String href = target.select("p.cfcpn_list_title > a").attr("href");
            href = "http://www.cfcpn.com" + href;
            long value = stringRedisTemplate.boundSetOps(KEY_URLS).add(href);
            if (value == 0L) {
                // 重复数据
                log.info("重复数据=={}",href);
                continue;
            } else {
                log.debug("href=={}", href);
                String titleTxt = target.select("p.cfcpn_list_title > a").text();
                log.debug("txt=={}", titleTxt);
                String date = target.select("p.cfcpn_list_date.text-right").text();
                log.debug("time=={}", date);

                JinCaiWangDataItem jinCaiWangDataItem = new JinCaiWangDataItem(DigestUtils.md5Hex(href));

                jinCaiWangDataItem.setUrl(href);

                jinCaiWangDataItem.setTitle(titleTxt);

                jinCaiWangDataItem.setDate(PageProcessorUtil.dataTxt(date));

                Elements elements = target.select("div.media-body");
                String content = elements.text();
                String[] text = StringUtils.split(content, "    ");
                for (int i = 0; i < text.length; i++) {
                    if (text[i].contains("采购人")) {
                        jinCaiWangDataItem.setProcurement(StringUtils.substringAfter(text[i], ":"));
                    }
                    if (text[i].contains("采购方式")) {
                        jinCaiWangDataItem.setPurchaseWay(StringUtils.substringAfter(text[i], ":"));
                    }
                    if (text[i].contains("地区")) {
                        jinCaiWangDataItem.setProvince(ProvinceUtil.get(StringUtils.substringAfter(text[i], ":")));
                    }
                    if (text[i].contains("品类")) {
                        jinCaiWangDataItem.setCategory(StringUtils.substringAfter(text[i], ":"));
                    }
                }

                try {
                    Document document = Jsoup.connect(href).timeout(60000).userAgent(SiteUtil.get().getUserAgent()).get();
                    String html = document.html();
                    Element root = document.body().select("body > div.container-fluid.cfcpn_container_list-bg > div > div.row > div.col-lg-9.cfcpn_news_mian").first();
                    Elements title=root.select("#news_head > p.cfcpn_news_title");
                    String titleT=title.text();
                    if(jinCaiWangDataItem.getTitle().contains("...")&&StringUtils.isNotBlank(titleT)){
                        jinCaiWangDataItem.setTitle(titleT);
                    }
                    String formatContent = PageProcessorUtil.formatElementsByWhitelist(root);
                    String textContent = PageProcessorUtil.extractTextByWhitelist(root);
                    jinCaiWangDataItem.setHtml(html);
                    jinCaiWangDataItem.setFormatContent(formatContent);
                    jinCaiWangDataItem.setTextContent(textContent);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("",e);
                }
                dataItemList.add(jinCaiWangDataItem);
            }

        }
        return dataItemList;
    }
}
