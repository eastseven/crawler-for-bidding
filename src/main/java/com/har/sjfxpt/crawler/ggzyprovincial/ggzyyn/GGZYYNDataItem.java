package com.har.sjfxpt.crawler.ggzyprovincial.ggzyyn;

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
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * Created by Administrator on 2017/12/4.
 * @author luo fei
 */
@Data
@ToString(exclude = {"formatContent"})
@Document(collection = "data_item_ggzy_yn")@Deprecated
public class GGZYYNDataItem {

    public GGZYYNDataItem(String url) {
        this.id = DigestUtils.md5Hex(url);
    }

    @Id
    private String id;

    private String url;

    private Date createTime = DateTime.now().toDate();

    /**
     * 地区
     */
    private String province = "云南";

    private String type;

    private String source;

    private String businessType;

    private String announcementId;

    private String closeTime;

    private String status;

    /**
     * 发布时间
     */
    @Indexed
    private String date;

    @Indexed
    private String title;

    private String formatContent;

    /**
     * 预算金额 budget
     */
    private String budget;

    /**
     * 总成交金额 total_bid_money
     */
    @Field("total_bid_money")
    private String totalBidMoney;

    @Field("bid_company_name")
    private String bidCompanyName;

    /**
     * 招标方，采购方，甲方
     */
    private String purchaser;

    private String purchaserAgent;

    public DataItemDTO dto() {
        DataItemDTO dto = new DataItemDTO();
        BeanUtils.copyProperties(this, dto);
        dto.setSource(SourceCode.GGZYYN.getValue());
        dto.setSourceCode(SourceCode.GGZYYN.toString());
        DateTime ct = new DateTime(this.getCreateTime());
        dto.setCreateTime(ct.toString("yyyyMMddHH"));
        if (StringUtils.isBlank(date)) {
            dto.setDate(ct.toString("yyyy-MM-dd HH:mm"));
        }
        return dto;
    }
}
