package com.har.sjfxpt.crawler.ggzy.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

import static com.har.sjfxpt.crawler.ggzy.model.DataItem.T_NAME;

/**
 * 全国公共资源交易平台 实体
 * @author dongqi
 */
@Getter
@Setter
@Builder
@ToString
@Document(collection = T_NAME)
public class DataItem {

    public static final String T_NAME = "data_item_ggzy";

    public static final String T_NAME_HTML = "bid_news_original";

    public static final String T_NAME_HTML_HISTORY = "bid_news_original_history";

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

    public DataItemDTO dto() {
        DataItemDTO dto = new DataItemDTO();
        BeanUtils.copyProperties(this, dto);
        dto.setCreateTime(new DateTime(this.getCreateTime()).toString("yyyyMMddHH"));
        if (StringUtils.isNotBlank(pubDate)) {
            dto.setDate(pubDate);
        } else {
            dto.setDate(new DateTime(this.getCreateTime()).toString("yyyy-MM-dd HH:mm"));
        }

        dto.setProvince(StringUtils.defaultString(province, "全国"));
        dto.setType(StringUtils.defaultString(infoType, "其他"));
        dto.setSource(SourceCode.GGZY.getValue());
        dto.setSourceCode(SourceCode.GGZY.toString());
        return dto;
    }
}

