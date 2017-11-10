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
     * 中国政府采购网
     */
    private String purchaser;

    /**
     * 项目名称，title 下划线前面的值
     * 中国移动
     */
    private String projectName;

    /**
     * 行业分类
     * 通过配置文件中的关键字匹配，计算出的值
     * 汉字，以英文逗号分隔
     */
    private String industryCategory;

    private String originalIndustryCategory;
}
