package com.har.sjfxpt.crawler.yibiao;

import com.har.sjfxpt.crawler.ggzy.model.DataItemDTO;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by Administrator on 2017/11/17.
 */
@Data
@ToString
@Document(collection = "data_item_yi_biao_wang_targetUrl")
public class YiBiaoDataItemUrlTarget {

    @Id
    private String id;

    private String url;

    private Date createTime = DateTime.now().toDate();

    /**
     * 地区
     */
    private String province;

    private String type;

    /**
     * 行业
     */
    private String originalIndustryCategory;

    /**
     * 发布时间
     */
    @Indexed
    private String date;

    @Indexed
    private String title;

    private String formatContent;

    private String filtration;

    public DataItemDTO dto() {
        DataItemDTO dto = new DataItemDTO();
        BeanUtils.copyProperties(this, dto);
        dto.setSource(SourceCode.YIBIAO.getValue());
        dto.setSourceCode(SourceCode.YIBIAO.toString());
        DateTime ct = new DateTime(this.getCreateTime());
        dto.setCreateTime(ct.toString("yyyyMMddHH"));
        if (StringUtils.isBlank(date)) {
            dto.setDate(ct.toString("yyyy-MM-dd HH:mm"));
        }
        return dto;
    }

}
