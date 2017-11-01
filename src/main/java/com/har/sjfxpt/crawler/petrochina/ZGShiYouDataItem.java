package com.har.sjfxpt.crawler.petrochina;

import com.har.sjfxpt.crawler.ggzy.model.DataItemDTO;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by Administrator on 2017/10/30.
 */
@Data
@ToString
@Document(collection = "data_item_petrochina")
public class ZGShiYouDataItem {

    public ZGShiYouDataItem(String url) {
        this.id = DigestUtils.md5Hex(url);
    }

    @Id
    private String id;

    private String url;

    private Date createTime = DateTime.now().toDate();

    /**
     * 地区
     */
    private String province = "全国";

    private String type;

    /**
     * 投标人
     */
    private String tenderer;

    /**
     * 发布时间
     */
    @Indexed
    private String date;

    @Indexed
    private String title;

    @Transient
    private String html;

    private String formatContent;

    @Transient
    private String textContent;

    public DataItemDTO dto() {
        DataItemDTO dto = new DataItemDTO();
        BeanUtils.copyProperties(this, dto);
        dto.setSource(SourceCode.ZSY.getValue());
        dto.setSourceCode(SourceCode.ZSY.toString());
        DateTime ct = new DateTime(this.getCreateTime());
        dto.setCreateTime(ct.toString("yyyyMMddHH"));
        if (StringUtils.isNotBlank(date)) {
            dto.setDate(date);
        }
        return dto;
    }


}
