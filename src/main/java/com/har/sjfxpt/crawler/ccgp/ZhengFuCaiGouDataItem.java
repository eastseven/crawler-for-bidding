package com.har.sjfxpt.crawler.ccgp;

import com.har.sjfxpt.crawler.core.model.DataItemDTO;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

import static com.har.sjfxpt.crawler.ccgp.ZhengFuCaiGouDataItem.T_NAME;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = T_NAME)@Deprecated
public class ZhengFuCaiGouDataItem {

    public static final String T_NAME = "data_item_ccgp";

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

    @Transient
    private String formatContent;

    @Transient
    private String textContent;

    /**
     * 公告概要 summary
     */
    private String summaryFormatContent;

    /**
     * 预算金额
     */
    private String budget;

    /**
     * 总成交金额
     */
    private String totalBidMoney;

    public DataItemDTO dto() {
        DataItemDTO dto = new DataItemDTO();
        BeanUtils.copyProperties(this, dto);
        dto.setOriginalIndustryCategory(StringUtils.defaultString(this.industry, ""));
        dto.setCreateTime(new DateTime(this.getCreateTime()).toString("yyyyMMddHH"));
        if (StringUtils.isNotBlank(pubDate)) {
            dto.setDate(pubDate);
        } else {
            dto.setDate(new DateTime(this.getCreateTime()).toString("yyyy-MM-dd HH:mm"));
        }

        dto.setType(StringUtils.defaultString(type, "其他"));
        dto.setProvince(StringUtils.defaultString(province, "全国"));
        dto.setSource(SourceCode.CCGP.getValue());
        dto.setSourceCode(SourceCode.CCGP.toString());

        if (StringUtils.contains(dto.getBudget(), "详见公告正文")) {
            dto.setBudget("");
        }

        if (StringUtils.contains(dto.getTotalBidMoney(), "详见公告正文")) {
            dto.setTotalBidMoney("");
        }

        return dto;
    }
}
