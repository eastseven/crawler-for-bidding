package com.har.sjfxpt.crawler.ggzy;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.ggzy.model.DataItemDTO;
import com.har.sjfxpt.crawler.ggzy.service.DataItemService;
import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import com.har.sjfxpt.crawler.yibiao.YiBiaoDataItemUrlRepository;
import com.har.sjfxpt.crawler.yibiao.YiBiaoDataItemUrlTarget;
import com.har.sjfxpt.crawler.yibiao.YiBiaoPageProcessor;
import com.har.sjfxpt.crawler.yibiao.YiBiaoPipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/11/9.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class YiBiaoPageProcessorTests {

    @Autowired
    YiBiaoPageProcessor yiBiaoPageProcessor;

    @Autowired
    YiBiaoPipeline yiBiaoPipeline;


    @Test
    public void testYiBiaoPageProcessor() {

        String[] urls = {
                "http://www.1-biao.com/data/AjaxTender.aspx?0.648357409685727&hidtypeID=&hidIndustryID=&hidProvince=23&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.07913589179468294&hidtypeID=&hidIndustryID=&hidProvince=22&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.8090257270745143&hidtypeID=&hidIndustryID=&hidProvince=25&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.9422979259891453&hidtypeID=&hidIndustryID=&hidProvince=28&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.04858760281002139&hidtypeID=&hidIndustryID=&hidProvince=29&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.6620913280843574&hidtypeID=&hidIndustryID=&hidProvince=26&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.8155706207478035&hidtypeID=&hidIndustryID=&hidProvince=24&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.3322341578936039&hidtypeID=&hidIndustryID=&hidProvince=35&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.9031533762880772&hidtypeID=&hidIndustryID=&hidProvince=9&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.25370687964038474&hidtypeID=&hidIndustryID=&hidProvince=19&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.35661413122288366&hidtypeID=&hidIndustryID=&hidProvince=2&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.11055441314443537&hidtypeID=&hidIndustryID=&hidProvince=10&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.3708828184404822&hidtypeID=&hidIndustryID=&hidProvince=15&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.7406441498897409&hidtypeID=&hidIndustryID=&hidProvince=11&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.1909567938189609&hidtypeID=&hidIndustryID=&hidProvince=16&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.1079524430393708&hidtypeID=&hidIndustryID=&hidProvince=3&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.06407083449901907&hidtypeID=&hidIndustryID=&hidProvince=6&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.20783105626311182&hidtypeID=&hidIndustryID=&hidProvince=17&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.7090292963387401&hidtypeID=&hidIndustryID=&hidProvince=18&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.27379009242373487&hidtypeID=&hidIndustryID=&hidProvince=13&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.16236643825308827&hidtypeID=&hidIndustryID=&hidProvince=12&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.07285936278323457&hidtypeID=&hidIndustryID=&hidProvince=5&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.24270651287259248&hidtypeID=&hidIndustryID=&hidProvince=27&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.03096304712743958&hidtypeID=&hidIndustryID=&hidProvince=14&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.8849605295723706&hidtypeID=&hidIndustryID=&hidProvince=20&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.028128167892413902&hidtypeID=&hidIndustryID=&hidProvince=8&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.7843719012029409&hidtypeID=&hidIndustryID=&hidProvince=4&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.9770245372123094&hidtypeID=&hidIndustryID=&hidProvince=21&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.8596489040447564&hidtypeID=&hidIndustryID=&hidProvince=7&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.1578679411680466&hidtypeID=&hidIndustryID=&hidProvince=30&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword=",
                "http://www.1-biao.com/data/AjaxTender.aspx?0.41885172672931925&hidtypeID=&hidIndustryID=&hidProvince=31&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=&hidPape=1&keyword="
        };

        Request[] requests = new Request[urls.length];

        for (int i = 0; i < urls.length; i++) {
            Request request = new Request(urls[i]);
            requests[i] = request;
        }

        Spider.create(yiBiaoPageProcessor)
                .addPipeline(yiBiaoPipeline)
                .addRequest(requests)
                .thread(4)
                .run();

    }

    final static Pattern yyyymmddPattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");

    @Test
    public void testDate() {

        String date = "2017-11-09 17:54";

        Matcher matcher = yyyymmddPattern.matcher(date);
        String dataStr = null;
        if (matcher.find()) {
            dataStr = matcher.group();
        }

        log.debug("dataStr=={}", dataStr);

        DateTime dateTime = new DateTime("2017-11-09");

        log.debug("date=={}", new DateTime(new Date()).toString("yyyy-MM-dd"));

        DateTime dateTime1 = new DateTime(new DateTime(new Date()).toString("yyyy-MM-dd"));

        log.debug("result=={}", dateTime.isBefore(dateTime1));
    }


    @Test
    public void testFormatContent() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Request request = new Request("http://www.1-biao.com/data/T2169754.html");
        Element html = httpClientDownloader.download(request, SiteUtil.get().setTimeOut(30000).toTask()).getHtml().getDocument().body();
        Elements elements = null;
        elements = html.select("body > div.g-doc > div.g-bd > div.g-lit-mn.f-fl");
        while (StringUtils.isBlank(elements.html())) {
            Page page = httpClientDownloader.download(request, SiteUtil.get().setTimeOut(30000).toTask());
            elements = page.getHtml().getDocument().body().select("body > div.g-doc > div.g-bd > div.g-lit-mn.f-fl");
        }
        String formtContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
        log.debug("fromatContent=={}", formtContent);
    }

    @Autowired
    YiBiaoDataItemUrlRepository yiBiaoDataItemUrlRepository;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    final String KEY_URLS = "fetch_fail_url_yibiao";

    @Autowired
    DataItemService dataItemService;

    @Autowired
    HttpClientDownloader httpClientDownloader;

    @Test
    public void testMongoTest() {
        org.springframework.data.domain.Page<YiBiaoDataItemUrlTarget> lists = yiBiaoDataItemUrlRepository.findAll(new PageRequest(0, 20));
        log.debug("size=={}", lists.getTotalPages());
        int pageSize = lists.getTotalPages();
        for (int i = 0; i <= pageSize; i++) {
            org.springframework.data.domain.Page<YiBiaoDataItemUrlTarget> currentPage = yiBiaoDataItemUrlRepository.findAll(new PageRequest(i, 20));
            List<YiBiaoDataItemUrlTarget> dataItems = Lists.newArrayList();
            log.info("pageNum=={}", i);
            for (YiBiaoDataItemUrlTarget yiBiaoDataItemUrlTarget : currentPage) {
                String titleReal = yiBiaoDataItemUrlTarget.getTitle().replace(StringUtils.substringBetween(yiBiaoDataItemUrlTarget.getTitle(), "[", "]"), "");
                Matcher m = Pattern.compile("[\\u4e00-\\u9fa5]").matcher(titleReal);
                if (yiBiaoDataItemUrlTarget.getFormatContent() == null && m.find()) {
                    Request request = new Request(yiBiaoDataItemUrlTarget.getUrl());
                    try {
                        Element html = httpClientDownloader.download(request, SiteUtil.get().setTimeOut(30000).toTask()).getHtml().getDocument().body();
                        Elements elements = null;
                        elements = html.select("body > div.g-doc > div.g-bd > div.g-lit-mn.f-fl");
                        int counter = 0;
                        while (StringUtils.isBlank(elements.html()) && counter <= 10) {
                            counter++;
                            Page page = httpClientDownloader.download(request, SiteUtil.get().setTimeOut(30000).toTask());
                            elements = page.getHtml().getDocument().body().select("body > div.g-doc > div.g-bd > div.g-lit-mn.f-fl");
                            if (counter == 10 && StringUtils.isBlank(elements.html())) {
                                stringRedisTemplate.boundSetOps(KEY_URLS).add(yiBiaoDataItemUrlTarget.getUrl());
                            }
                        }
                        String formtContent = PageProcessorUtil.formatElementsByWhitelist(elements.first());
                        if (StringUtils.isNotBlank(formtContent)) {
                            yiBiaoDataItemUrlTarget.setFormatContent(formtContent);
                            dataItems.add(yiBiaoDataItemUrlTarget);
                        }
                    } catch (Exception e) {
                        log.warn("{} is wrong page", yiBiaoDataItemUrlTarget.getUrl());
                    }
                } else {
                    log.debug("titleReal=={}", titleReal);
                }
            }
            yiBiaoDataItemUrlRepository.save(dataItems);
            List<DataItemDTO> dtoList = dataItems.stream().map(dataItem -> dataItem.dto()).collect(Collectors.toList());
            dataItemService.save2BidNewsOriginalTable(dtoList);
        }
    }

    @Test
    public void testRegex() {
        String title = "[中标] 2016-02-24 15:03:13";
        String titleReal = title.replace(StringUtils.substringBetween(title, "[", "]"), "");
        log.debug("titleReal=={}", titleReal);
        String regex = "[\\u4e00-\\u9fa5]";
        Matcher m = Pattern.compile(regex).matcher(titleReal);
        log.debug("find=={}", m.find());
    }

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void testRedis() throws UnsupportedEncodingException {
        final String key = "test_redis_set";

//        for (int index = 0; index < 10; index++) {
//            long result = redisTemplate.boundSetOps(key).add("hello redis " + index);
//            Assert.assertTrue(result > 0L);
//        }
        long total = redisTemplate.boundSetOps(key).size();

        Object popValue = redisTemplate.boundSetOps(key).pop();
        long last = redisTemplate.boundSetOps(key).size();
        log.info(">>> total={} pop [{}], last={}", total, popValue, last);
    }


}
