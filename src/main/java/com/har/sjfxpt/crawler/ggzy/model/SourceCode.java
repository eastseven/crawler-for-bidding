package com.har.sjfxpt.crawler.ggzy.model;

/**
 * @author dongqi
 */
public enum SourceCode {

    GGZY("全国公共资源交易平台"),
    CM("中国移动采购与招标网"),
    CCGP("中国政府采购网"),
    JC("金采网"),
    ZGYJ("中国冶金科工"),
    ZSY("中国石油"),
    ZGZT("中国招投标"),
    CCGPHN("海南政府采购"),
    CCGPSC("四川政府采购"),
    YIBIAO("一标网"),
    SUNING("苏宁"),
    SGCC("国家电网");


    private String value;

    SourceCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
