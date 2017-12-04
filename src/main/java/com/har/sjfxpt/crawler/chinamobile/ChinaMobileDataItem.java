package com.har.sjfxpt.crawler.chinamobile;

import com.har.sjfxpt.crawler.ggzy.model.DataItemDTO;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import lombok.Getter;
import lombok.Setter;
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
 * @author dongqi
 */
@Setter
@Getter
@ToString
@Document(collection = "data_item_china_mobile")
public class ChinaMobileDataItem {

    public ChinaMobileDataItem(String url) {
        this.id = DigestUtils.md5Hex(url);
        this.url = url;
    }

    @Id
    private String id;

    private String url;

    private Date createTime = new Date();

    private String province = "全国";

    private String type;

    private String purchaser;

    private boolean download = false;

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

    private String projectName;

    public DataItemDTO dto() {
        DataItemDTO dto = new DataItemDTO();
        BeanUtils.copyProperties(this, dto);
        dto.setSource(SourceCode.CM.getValue());
        dto.setSourceCode(SourceCode.CM.toString());
        DateTime dt = new DateTime(this.getCreateTime());
        dto.setCreateTime(dt.toString("yyyyMMddHH"));
        if (StringUtils.isBlank(date)) {
            dto.setDate(dt.toString("yyyy-MM-dd HH:mm"));
        } else if (date.length() == 10) {
            //len == 10 既只有年月日
            dto.setDate(dto.getDate().concat(dt.toString(" HH:mm")));
        }
        return dto;
    }
}
