package com.har.sjfxpt.crawler.ggzy;

import com.har.sjfxpt.crawler.jcw.JinCaiWangPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Request;

/**
 * Created by Administrator on 2017/10/24.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class JinCaiWangPageProcessorTests {

    @Autowired
    JinCaiWangPageProcessor jinCaiWangPageProcessor;

    @Test
    public void testJinCaiWangProcessor(){
        String[] urls={"http://www.cfcpn.com/plist/caigou?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=",
                "http://www.cfcpn.com/plist/zhengji?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=",
                "http://www.cfcpn.com/plist/jieguo?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo=",
                "http://www.cfcpn.com/plist/biangeng?pageNo=1&kflag=0&keyword=&keywordType=&province=&city=&typeOne=&ptpTwo="};
        Request[] requests=new Request[urls.length];
        for (int i=0;i<urls.length;i++){
            Request request=new Request(urls[i]);
            requests[i]=request;
        }
        Spider.create(jinCaiWangPageProcessor)
                .addRequest(requests)
                .thread(10)
                .run();
    }

    @Test
    public void testStringUtil(){
        String content="采购人:国家开发银行浙江分行    采购方式:公开招标    地区:    品类: 招标编号：浙江分行2017年集采04号关于国家开发银行浙江省分行办公楼入室保洁服务采购项目，现对服务期限进行变更如下：原公告入室保洁服务采购项目服务期限3年，现变更为服务......";
        String[] text= StringUtils.split(content,"    ");
        for (int i=0;i<text.length;i++){
            log.debug("text=={}",text[i]);
        }
    }

}
