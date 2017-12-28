package com.har.sjfxpt.crawler.zgyj;

import com.har.sjfxpt.crawler.core.model.DataItemDTO;
import com.har.sjfxpt.crawler.core.model.SourceCode;
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
 * Created by Administrator on 2017/10/27.
 * @author luofei
 */
@Data
@ToString
@Document(collection = "data_item_mcc")
public class ZGYeJinDataItem {


    public ZGYeJinDataItem(String url) {
        this.id = DigestUtils.md5Hex(url);
    }

    @Id
    private String id;

    private String url;

    private Date createTime = new Date();

    /**
     * 地区
     */
    private String province = "全国";

    private String type;

    /**
     * 发布时间
     */
    @Indexed
    private String date;

    @Indexed
    private String title;

    /**
     * 项目名称，title 下划线前面的值
     */
    private String projectName;


    private String formatContent;

    public DataItemDTO dto() {
        DataItemDTO dto = new DataItemDTO();
        BeanUtils.copyProperties(this, dto);
        dto.setSource(SourceCode.ZGYJ.getValue());
        dto.setSourceCode(SourceCode.ZGYJ.toString());
        DateTime ct = new DateTime(this.getCreateTime());
        dto.setCreateTime(ct.toString("yyyyMMddHH"));
        if (StringUtils.isBlank(date)) {
            dto.setDate(ct.toString("yyyy-MM-dd HH:mm"));
        }
        return dto;
    }

}
