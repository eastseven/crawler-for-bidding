package com.har.sjfxpt.crawler.zgyj;

import com.har.sjfxpt.crawler.ggzy.model.DataItemDTO;
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
 * Created by Administrator on 2017/10/27.
 */
@Data
@ToString
@Document(collection = "data_item_zhong_guo_ye_jin")
public class ZGYeJinDataItem {


    public ZGYeJinDataItem(String url) {
        this.id = DigestUtils.md5Hex(url);
    }

    @Id
    private String id;

    private String url;

    private Date createTime=new Date();

    private String province="全国";//地区

    private String purchaseWay;//采购方式

    private String procurement;//采购人

    private String category;//品类

    private String type;

    @Indexed
    private String date;//发布时间

    @Indexed
    private String title;

    @Transient
    private String html;

    //    @Transient
    private String formatContent;

    @Transient
    private String textContent;

    public DataItemDTO dto(){
        DataItemDTO dto=new DataItemDTO();
        BeanUtils.copyProperties(this,dto);
        dto.setSource("中国冶金");
        dto.setSourceCode("ZGYJ");
        DateTime ct=new DateTime(this.getCreateTime());
        dto.setCreateTime(ct.toString("yyyyMMddHH"));
        if (StringUtils.isBlank(date)) {
            dto.setDate(date);
        }
        return dto;
    }

}
