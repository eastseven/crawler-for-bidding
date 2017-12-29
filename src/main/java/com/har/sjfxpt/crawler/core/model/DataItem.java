package com.har.sjfxpt.crawler.core.model;

import com.har.sjfxpt.crawler.core.annotation.DataItemRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

import static com.har.sjfxpt.crawler.core.model.DataItem.T_NAME;

/**
 * 全国公共资源交易平台 实体
 * @author dongqi
 */
@Getter
@Setter
@ToString
@Document(collection = T_NAME)
@DataItemRepository(repository = com.har.sjfxpt.crawler.core.repository.DataItemRepository.class)
public class DataItem extends DataItemDTO {

    public static final String T_NAME = "data_item_ggzy";

    public static final String T_NAME_HTML = "bid_news_original";

    public static final String T_NAME_HTML_HISTORY = "bid_news_original_history";

    public DataItem(String url) {
        super(url);
        this.source = SourceCode.GGZY.getValue();
        this.sourceCode = SourceCode.GGZY.name();
    }

    private Date fetchTime = new Date();

    private String businessType;

    @Indexed
    private String pubDate;

    @Deprecated
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
        dto.setSource(SourceCode.GGZY.getValue());
        dto.setSourceCode(SourceCode.GGZY.toString());
        return dto;
    }
}

