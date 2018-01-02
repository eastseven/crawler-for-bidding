package com.har.sjfxpt.crawler.core.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * hbase bid_news_original 表实体
 *
 * @author dongqi
 */
@Setter
@Getter
@NoArgsConstructor
@ToString(exclude = {"formatContent", "rowKey", "forceUpdate"})
@Document(collection = "bid_news_original")
public class BidNewsOriginal {

    /**
     * 初始化 id, url, createTime
     *
     * @param url
     */
    public BidNewsOriginal(String url) {
        this.id = DigestUtils.md5Hex(url);
        this.url = url;
        this.createTime = DateTime.now().toString("yyyyMMddHH");
    }

    /**
     * hbase rowKey: yyyyMMdd:title
     */
    @Field("row_key")
    protected String rowKey;

    @Id
    protected String id;

    protected String url;

    @NotNull
    protected String title;

    @NotNull
    protected String province;

    @NotNull
    protected String type;

    @NotNull
    protected String source;

    @NotNull
    @Field("source_code")
    protected String sourceCode;

    /**
     * yyyy-MM-dd HH:mm
     */
    @NotNull
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")
    protected String date;

    /**
     * yyyyMMddHH
     */
    @NotNull
    @Pattern(regexp = "\\d{10}")
    @Field("create_time")
    protected String createTime;

    @NotNull
    @Field("format_content")
    protected String formatContent;

    /**
     * 采购单位名称，甲方
     * 中国政府采购网
     */
    protected String purchaser;

    /**
     * 项目名称，title 下划线前面的值
     * 中国移动
     */
    @Field("project_name")
    protected String projectName;

    /**
     * 原始的行业分类
     */
    @Field("original_industry_category")
    protected String originalIndustryCategory;

    @Field("force_update")
    protected boolean forceUpdate;

    /**
     * 预算金额 budget
     */
    protected String budget;

    /**
     * 总成交金额 total_bid_money
     */
    @Field("total_bid_money")
    protected String totalBidMoney;

    /**
     * 中标公司名称
     */
    @Field("bid_company_name")
    protected String bidCompanyName;

    @Field("fetch_time")
    private Date fetchTime = DateTime.now().toDate();

    @Field("project_code")
    private String projectCode;
}
