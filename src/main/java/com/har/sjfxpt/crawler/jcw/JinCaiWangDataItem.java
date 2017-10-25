package com.har.sjfxpt.crawler.jcw;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by Administrator on 2017/10/25.
 */
@Data
@ToString
@Document(collection = "data_item_jincaiwang")
public class JinCaiWangDataItem {

    public JinCaiWangDataItem(String url) {
        this.id = DigestUtils.md5Hex(url);
    }

    @Id
    private String id;

    private String url;

    private String createTime=new DateTime(new Date()).toString("yyyy-MM-dd-HH");

    private String province;//地区

    private String purchaseWay;//采购方式

    private String procurement;//采购人

    private String category;//品类

    @Indexed
    private String pubDate;//发布时间

    @Indexed
    private String title;

    @Transient
    private String html;

    @Transient
    private String formatContent;

    @Transient
    private String textContent;

}
