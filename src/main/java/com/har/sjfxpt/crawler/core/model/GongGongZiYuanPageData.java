package com.har.sjfxpt.crawler.core.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 全国公共资源交易平台每日数据统计
 */
@Data
@Document(collection = "data_item_ggzy_page")
public class GongGongZiYuanPageData implements Serializable {

    /**
     * ID
     * 格式yyyy-MM-dd
     */
    @Id
    private String date;

    private int page;

    private int size;

    private String url;
}
