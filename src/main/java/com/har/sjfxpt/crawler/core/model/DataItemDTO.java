package com.har.sjfxpt.crawler.core.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 将爬虫数据转换为hbase需要的格式
 *
 * @author dongqi
 */
@Setter
@Getter
@ToString(exclude = {"formatContent"})
public class DataItemDTO {

    public static final int ROW_KEY_LENGTH = 41;

    /**
     * hbase rowKey: yyyyMMdd:title
     */
    protected String rowKey;

    protected String id;

    protected String url;

    protected String title;

    protected String province;

    protected String type;

    protected String source;

    protected String sourceCode;

    /**
     * yyyy-MM-dd HH:mm
     */
    protected String date;

    /**
     * yyyyMMddHH
     */
    protected String createTime;

    protected String formatContent;

    protected String textContent;

    /**
     * 采购单位名称，甲方
     * 中国政府采购网
     */
    protected String purchaser;

    /**
     * 项目名称，title 下划线前面的值
     * 中国移动
     */
    protected String projectName;

    /**
     * 行业分类
     * 通过配置文件中的关键字匹配，计算出的值
     * 汉字，以英文逗号分隔
     */
    protected String industryCategory;

    /**
     * 原始的行业分类
     */
    protected String originalIndustryCategory;

    protected boolean forceUpdate;

    /**
     * 预算金额 budget
     */
    protected String budget;

    /**
     * 总成交金额 total_bid_money
     */
    protected String totalBidMoney;

    /**
     * 中标公司名称
     */
    protected String bidCompanyName;

}
