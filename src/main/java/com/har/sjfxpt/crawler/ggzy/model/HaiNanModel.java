package com.har.sjfxpt.crawler.ggzy.model;

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
 * Created by Administrator on 2017/11/7.
 */
@Data
@ToString
@Document(collection = "data_item_ccgp_hn")
public class HaiNanModel {



    public HaiNanModel(String url) {
        this.id = DigestUtils.md5Hex(url);
    }

    @Id
    private String id;

    private String url;

    private Date createTime= DateTime.now().toDate();

    /**
     * 地区
     */
    private String province="海南";

    private String type;

    /**
     * 投标人
     */
    private String purchaser;

    /**
     * 发布时间
     */
    @Indexed
    private String date;

    @Indexed
    private String title;

    @Transient
    private String html;

//    @Transient
    private String formatContent;


    public DataItemDTO dto(){
        DataItemDTO dto=new DataItemDTO();
        BeanUtils.copyProperties(this,dto);
        dto.setSource(SourceCode.CCGPHN.getValue());
        dto.setSourceCode(SourceCode.CCGPHN.toString());
        DateTime ct=new DateTime(this.getCreateTime());
        dto.setCreateTime(ct.toString("yyyyMMddHH"));
        if (StringUtils.isBlank(date)) {
            dto.setDate(ct.toString("yyyy-MM-dd HH:mm"));
        }
        return dto;
    }


}
