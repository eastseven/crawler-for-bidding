package com.har.sjfxpt.crawler.ggzy.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 将爬虫数据转换为hbase需要的格式
 *
 * @author dongqi
 */
@Setter@Getter@ToString
public class DataItemDTO {

    public static final int ROW_KEY_LENGTH = 41;

    /**
     * hbase rowKey: yyyyMMdd:title
     */
    private String rowKey;

    private String id;

    private String url;

    private String title;

    private String province;

    private String type;

    private String source;

    private String sourceCode;

    /**
     * yyyy-MM-dd HH:mm
     */
    private String date;

    /**
     * yyyyMMddHH
     */
    private String createTime;

    private String formatContent;

    private String textContent;

    /**
     * 采购单位名称，甲方
     */
    private String purchaser;
}
