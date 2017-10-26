package com.har.sjfxpt.crawler.ccgp;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

import static com.har.sjfxpt.crawler.ccgp.ZhengFuCaiGouDataItem.T_NAME;

@Getter
@Setter
@Builder
@ToString
@Document(collection = T_NAME)
public class ZhengFuCaiGouDataItem {

    public static final String T_NAME      = "data_item_ccgp";
    public static final String T_NAME_HTML = "data_item_ccgp_html";

    @Id
    private String id;

    private String url;

    private Date createTime = new Date();

    private String province;

    private String type;

    private String purchaser;

    private String purchaserAgent;

    private String industry;

    @Indexed
    private String pubDate;

    @Indexed
    private String date;

    @Indexed
    private String title;

    @Transient
    private String html;

    private String formatContent;

    @Transient
    private String textContent;
}
