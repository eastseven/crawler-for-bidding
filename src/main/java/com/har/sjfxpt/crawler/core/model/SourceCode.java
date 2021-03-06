package com.har.sjfxpt.crawler.core.model;

/**
 * @author dongqi
 */
public enum SourceCode {

    DEFAULT("default"),

    GGZY("全国公共资源交易平台"),
    GGZYSC("四川公共资源"),
    GGZYCQ("重庆公共资源"),
    GGZYHN("海南公共资源"),
    GGZYGZ("贵州公共资源"),
    GGZYYN("云南公共资源"),
    GGZYXZ("西藏公共资源"),
    GGZYHLJ("黑龙江公共资源"),
    HBEBPT("湖北电子招投标"),
    GGZYHEBEI("河北公共资源"),
    GGZYSHANXI("山西公共资源"),
    GGZYFUJIAN("福建公共资源"),
    GGZYJIANGXI("江西公共资源"),
    GGZYSHANDONG("山东公共资源"),
    GGZYSHAANXI("陕西公共资源"),
    GGZYGANSU("甘肃公共资源"),
    GGZYNINGXIA("宁夏公共资源"),
    GGZYXJBT("新疆兵团公共资源"),

    CCGP("中国政府采购网"),
    CCGPHN("海南政府采购"),
    CCGPSC("四川政府采购"),
    CCGPCQ("重庆政府采购"),
    CCGPBEIJING("北京政府采购"),
    CCGPSHANGHAI("上海政府采购"),
    CCGPTIANJIN("天津政府采购"),
    CCGPHEBEI("河北政府采购"),
    CCGPHENAN("河南政府采购"),
    CCGPHUBEI("湖北政府采购"),

    CM("中国移动采购与招标网"),
    JC("金采网"),
    ZGYJ("中国冶金科工"),
    ZSY("中国石油"),
    ZGZT("中国招投标"),
    SUNING("苏宁"),
    SGCC("国家电网"),
    DONGFENG("东风招投"),
    ZGLVYE("中国铝业"),
    SHENHUA("中国神华"),
    BAOWU("中国宝武"),
    CU("中国联通"),
    ZGJIAOJIAN("中国交建"),
    ;


    private String value;

    SourceCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
