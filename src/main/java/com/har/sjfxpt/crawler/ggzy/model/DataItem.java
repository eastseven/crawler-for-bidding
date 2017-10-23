package com.har.sjfxpt.crawler.ggzy.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

import static com.har.sjfxpt.crawler.ggzy.model.DataItem.T_NAME;

/**
 * @author dongqi
 */
@Getter
@Setter
@Builder@ToString
@Document(collection = T_NAME)
public class DataItem {

    public static final String T_NAME = "data_item_ggzy";

    @Id
    private String id;

    private String url;

    private Date createTime = new Date();

    private String province;

    private String source;

    private String businessType;

    private String infoType;

    private String industry;

    @Indexed
    private String pubDate;

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

