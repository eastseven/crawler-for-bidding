package com.har.sjfxpt.crawler.chinamobile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Setter@Getter@ToString
@Document(collection = "data_item_china_mobile")
public class ChinaMobileDataItem {

    public ChinaMobileDataItem(String url) {
        this.id = DigestUtils.md5Hex(url);
    }

    @Id
    private String id;

    private String url;

    private Date createTime = new Date();

    private String province = "全国";

    private String type;

    private String purchaser;

    @Indexed
    private String date;

    @Indexed
    private String title;

    @Transient
    private String html;

    @Transient
    private String formatContent;

    @Transient
    private String textContent;
}
