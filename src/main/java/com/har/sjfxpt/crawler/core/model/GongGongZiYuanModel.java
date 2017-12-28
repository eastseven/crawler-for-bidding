package com.har.sjfxpt.crawler.core.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@ToString@Deprecated
public class GongGongZiYuanModel {

    public GongGongZiYuanModel(DataItem dataItem) {
        BeanUtils.copyProperties(dataItem, this, "date");
        this.createTime = new DateTime(dataItem.getCreateTime()).toString("yyyyMMddHH");
        if (StringUtils.isNotBlank(dataItem.getPubDate())) {
            this.date = dataItem.getPubDate();
        } else {
            this.date = new DateTime(dataItem.getCreateTime()).toString("yyyy-MM-dd HH:mm");
        }

        this.type = StringUtils.defaultString(dataItem.getInfoType(), "其他");
        this.province = StringUtils.defaultString(dataItem.getProvince(), "全国");
    }

    private String id;
    private String url;
    private String title;
    private String province;
    private String type;
    private String source;
    private String sourceCode = "GGZY";
    private String date;
    private String createTime;

    private String formatContent = "";
    private String textContent = "";

}
