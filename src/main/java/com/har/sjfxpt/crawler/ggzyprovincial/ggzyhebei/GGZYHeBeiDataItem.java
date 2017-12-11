package com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebei;

import com.har.sjfxpt.crawler.ggzy.model.DataItemDTO;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by Administrator on 2017/12/8.
 */
@Data
@ToString
@Document(collection = "data_item_ggzy_hebei")
public class GGZYHeBeiDataItem {

    public GGZYHeBeiDataItem(String url) {
        this.id = DigestUtils.md5Hex(url);
    }

    @Id
    private String id;

    private String url;

    private Date createTime = DateTime.now().toDate();

    /**
     * 地区
     */
    private String province = "河北";

    private String type;

    private String source;

    private String businessType;


    /**
     * 发布时间
     */
    @Indexed
    private String date;

    @Indexed
    private String title;


    private String formatContent;

    private boolean forceUpdate;


    public DataItemDTO dto() {
        DataItemDTO dto = new DataItemDTO();
        BeanUtils.copyProperties(this, dto);
        dto.setSource(SourceCode.GGZYHEBEI.getValue());
        dto.setSourceCode(SourceCode.GGZYHEBEI.toString());
        DateTime ct = new DateTime(this.getCreateTime());
        dto.setCreateTime(ct.toString("yyyyMMddHH"));
        if (StringUtils.isBlank(date)) {
            dto.setDate(ct.toString("yyyy-MM-dd HH:mm"));
        }
        return dto;
    }

}
