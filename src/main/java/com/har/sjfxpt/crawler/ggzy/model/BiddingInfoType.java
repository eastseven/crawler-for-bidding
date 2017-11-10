package com.har.sjfxpt.crawler.ggzy.model;

/**
 * @author dongqi
 * 信息类型
 * 不限
 * 1.招标 [邀标,询价,竞谈,单一,竞价]
 * 2.变更
 * 3.中标
 * 4.废标
 */
public enum BiddingInfoType {

    ZHAO_BIAO("招标"),
    YAO_BIAO("邀标"),
    XUN_JIA("询价"),
    JING_TAN("竞谈"),
    DAN_YI("单一"),
    JING_JIA("竞价"),
    BIAN_GENG("变更"),
    ZHONG_BIAO("中标"),
    FEI_BIAO("废标"),;

    private String value;

    BiddingInfoType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
