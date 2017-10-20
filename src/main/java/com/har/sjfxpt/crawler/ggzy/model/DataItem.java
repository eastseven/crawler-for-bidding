package com.har.sjfxpt.crawler.ggzy.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author dongqi
 */
@Getter
@Setter
@Builder@ToString
@Document(collection = "data_item_ggzy")
public class DataItem {

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
    private String date;

    private String title;

    private String html;

    private String formatContent;

    @Indexed
    private String textContent;
}

