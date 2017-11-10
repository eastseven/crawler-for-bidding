package com.har.sjfxpt.crawler.ggzy;

import com.har.sjfxpt.crawler.ggzy.utils.PageProcessorUtil;
import com.har.sjfxpt.crawler.yibiao.YiBiaoPageProcessor;
import com.har.sjfxpt.crawler.yibiao.YiBiaoPipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Spider;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        String url = "http://www.1-biao.com/data/AjaxTender.aspx?0.06563536587854646&hidtypeID=&hidIndustryID=&hidProvince=&hidCity=&hidPrice=&txtPrice1=&txtPrice2=&hidDate=1&hidPape=1&keyword=";

        Spider.create(yiBiaoPageProcessor)
                .addPipeline(yiBiaoPipeline)
                .addUrl(url)
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
    public void test(){

        String test="&lt;font&gt;&lt;a&gt;&lt;a&gt; &lt;table border=\"1\" bordercolor=\"#d0d0d0\"&gt;&lt;tr&gt;&lt;td&gt; &lt;table id=\"tblInfo\" cellspacing=\"1\" cellpadding=\"1\" align=\"center\" border=\"0\" runat=\"server\"&gt; &lt;tr&gt; &lt;td id=\"tdTitle\" align=\"center\" runat=\"server\"&gt; &lt;font color=\"\" &gt; &lt;b&gt; 小蓝经开区汽车南路和振铃东路明渠工程 &lt;/b&gt;&lt;/font&gt; &lt;br /&gt; &lt;br /&gt; &lt;font color=\"#000000\" &gt;【信息时间：2017/11/10&amp;nbsp;&amp;nbsp;阅读次数：&lt;script src=\"/ncxztb/Upclicktimes.aspx?InfoID=ec54ea6f-9908-4861-a489-b4202e6fa7a8\"&gt;&lt;/script&gt;】&lt;a href=\"javascript:void(0)\" onClick=\"window.print();\"&gt;&lt;font color=\"#000000\"&gt;【我要打印】&lt;/font&gt;&lt;/a&gt;&lt;a href=\"javascript:window.close()\"&gt;&lt;font color=\"#000000\" &gt;【关闭】&lt;/font&gt;&lt;/a&gt;&lt;/font&gt;&lt;font color=\"#000000\"&gt;&lt;/font&gt; &lt;/td&gt; &lt;/tr&gt; &lt;tr&gt; &lt;td height=\"10\"&gt; &lt;/td&gt; &lt;/tr&gt; &lt;tr&gt; &lt;td height=\"250\" valign=\"top\" class=\"infodetail\" id=\"TDContent\"&gt; &lt;p&gt;&amp;#160;&lt;/p&gt; &lt;div class=\"Section1\" &gt; &lt;div class=\"MsoNormal\" &gt;&lt;span &gt;南昌市使用建筑市场施工企业信用综合评价名录施工招标公告&lt;span&gt;&lt;o:p&gt;&lt;/o:p&gt;&lt;/span&gt;&lt;/span&gt;&lt;/div&gt; &lt;div class=\"MsoNormal\" &gt;&lt;span &gt;项目编号:&lt;/span&gt;&lt;span &gt;&lt;span class=\"Apple-converted-";

        int i= StringUtils.split(test,"&lt;").length;

        int b=StringUtils.countMatches(test,"&lt;");

        log.debug("i=={}",i);
        log.debug("b=={}",b);

    }

}
