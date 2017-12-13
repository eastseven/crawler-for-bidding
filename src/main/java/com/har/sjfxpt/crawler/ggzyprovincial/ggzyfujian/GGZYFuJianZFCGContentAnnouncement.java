package com.har.sjfxpt.crawler.ggzyprovincial.ggzyfujian;

import java.util.List;

/**
 * Created by Administrator on 2017/12/13.
 */
public class GGZYFuJianZFCGContentAnnouncement {

    /**
     * GGTYPE1 : 0
     * GGTYPE2 : 0
     * GGTYPE3 : 1
     * GGTYPE4 : 0
     * data : []
     * data3 : [{"TITLE":"采购合同","GGTYPE":"3","NAME":"移动环保厕所及安装","TM":"2017-12-12T15:40:23","PLATFORM_NAME":"泉州市公共资源交易中心","URL":"http://cz.fjzfcg.gov.cn/notice/noticeinfo/109807","PURCHASER_NAME":"晋江市市政园林局","SUPPLIER_NAME":"福建秋田环境科技有限公司","CONTRACT_AMOUNT":566800,"PRICE_UNIT_TEXT":"元","CURRENCY_CODE_TEXT":"人民币","CONTRACT_TERM":"0年"}]
     */

    private int GGTYPE1;
    private int GGTYPE2;
    private int GGTYPE3;
    private int GGTYPE4;
    private List<?> data;
    private List<Data3Bean> data3;

    public int getGGTYPE1() {
        return GGTYPE1;
    }

    public void setGGTYPE1(int GGTYPE1) {
        this.GGTYPE1 = GGTYPE1;
    }

    public int getGGTYPE2() {
        return GGTYPE2;
    }

    public void setGGTYPE2(int GGTYPE2) {
        this.GGTYPE2 = GGTYPE2;
    }

    public int getGGTYPE3() {
        return GGTYPE3;
    }

    public void setGGTYPE3(int GGTYPE3) {
        this.GGTYPE3 = GGTYPE3;
    }

    public int getGGTYPE4() {
        return GGTYPE4;
    }

    public void setGGTYPE4(int GGTYPE4) {
        this.GGTYPE4 = GGTYPE4;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    public List<Data3Bean> getData3() {
        return data3;
    }

    public void setData3(List<Data3Bean> data3) {
        this.data3 = data3;
    }

    public static class Data3Bean {
        /**
         * TITLE : 采购合同
         * GGTYPE : 3
         * NAME : 移动环保厕所及安装
         * TM : 2017-12-12T15:40:23
         * PLATFORM_NAME : 泉州市公共资源交易中心
         * URL : http://cz.fjzfcg.gov.cn/notice/noticeinfo/109807
         * PURCHASER_NAME : 晋江市市政园林局
         * SUPPLIER_NAME : 福建秋田环境科技有限公司
         * CONTRACT_AMOUNT : 566800
         * PRICE_UNIT_TEXT : 元
         * CURRENCY_CODE_TEXT : 人民币
         * CONTRACT_TERM : 0年
         */

        private String TITLE;
        private String GGTYPE;
        private String NAME;
        private String TM;
        private String PLATFORM_NAME;
        private String URL;
        private String PURCHASER_NAME;
        private String SUPPLIER_NAME;
        private int CONTRACT_AMOUNT;
        private String PRICE_UNIT_TEXT;
        private String CURRENCY_CODE_TEXT;
        private String CONTRACT_TERM;

        public String getTITLE() {
            return TITLE;
        }

        public void setTITLE(String TITLE) {
            this.TITLE = TITLE;
        }

        public String getGGTYPE() {
            return GGTYPE;
        }

        public void setGGTYPE(String GGTYPE) {
            this.GGTYPE = GGTYPE;
        }

        public String getNAME() {
            return NAME;
        }

        public void setNAME(String NAME) {
            this.NAME = NAME;
        }

        public String getTM() {
            return TM;
        }

        public void setTM(String TM) {
            this.TM = TM;
        }

        public String getPLATFORM_NAME() {
            return PLATFORM_NAME;
        }

        public void setPLATFORM_NAME(String PLATFORM_NAME) {
            this.PLATFORM_NAME = PLATFORM_NAME;
        }

        public String getURL() {
            return URL;
        }

        public void setURL(String URL) {
            this.URL = URL;
        }

        public String getPURCHASER_NAME() {
            return PURCHASER_NAME;
        }

        public void setPURCHASER_NAME(String PURCHASER_NAME) {
            this.PURCHASER_NAME = PURCHASER_NAME;
        }

        public String getSUPPLIER_NAME() {
            return SUPPLIER_NAME;
        }

        public void setSUPPLIER_NAME(String SUPPLIER_NAME) {
            this.SUPPLIER_NAME = SUPPLIER_NAME;
        }

        public int getCONTRACT_AMOUNT() {
            return CONTRACT_AMOUNT;
        }

        public void setCONTRACT_AMOUNT(int CONTRACT_AMOUNT) {
            this.CONTRACT_AMOUNT = CONTRACT_AMOUNT;
        }

        public String getPRICE_UNIT_TEXT() {
            return PRICE_UNIT_TEXT;
        }

        public void setPRICE_UNIT_TEXT(String PRICE_UNIT_TEXT) {
            this.PRICE_UNIT_TEXT = PRICE_UNIT_TEXT;
        }

        public String getCURRENCY_CODE_TEXT() {
            return CURRENCY_CODE_TEXT;
        }

        public void setCURRENCY_CODE_TEXT(String CURRENCY_CODE_TEXT) {
            this.CURRENCY_CODE_TEXT = CURRENCY_CODE_TEXT;
        }

        public String getCONTRACT_TERM() {
            return CONTRACT_TERM;
        }

        public void setCONTRACT_TERM(String CONTRACT_TERM) {
            this.CONTRACT_TERM = CONTRACT_TERM;
        }
    }
}
