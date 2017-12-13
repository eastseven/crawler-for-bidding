package com.har.sjfxpt.crawler.ggzyprovincial.ggzyfujian;

import java.util.List;

/**
 * Created by Administrator on 2017/12/12.
 */
public class GGZYFuJianAnnouncement {

    /**
     * data : [{"KIND":"GCJS","TITLE":"招标公告","GGTYPE":"1","NAME":"南平航天体验馆分布式光伏发电项目设计采购施工（EPC）总承包","M_ID":62286,"PLATFORM_CODE":"123507006919064000","PLATFORM_NAME":"南平市公共资源交易中心","TM":"2017-12-12T16:02:53","PROCODE":null,"AREACODE":"350700","AREANAME":"南平市","PROTYPE_TEXT":"能源","PROTYPE":"A08","M_DATA_SOURCE":"9999","M_TM":null,"M_PROJECT_TYPE":"1","RN":1},{"KIND":"GCJS","TITLE":"招标公告","GGTYPE":"1","NAME":"尤溪城东水厂进场便道工程A标段","M_ID":62307,"PLATFORM_CODE":"12350400669253747H","PLATFORM_NAME":"三明市公共资源交易中心","TM":"2017-12-12T16:01:14","PROCODE":null,"AREACODE":"350400","AREANAME":"三明市","PROTYPE_TEXT":"公路","PROTYPE":"A03","M_DATA_SOURCE":"9999","M_TM":null,"M_PROJECT_TYPE":"1","RN":2},{"KIND":"GCJS","TITLE":"招标公告","GGTYPE":"1","NAME":"尤溪县尤墩村路面改造及人行道景观提升工程.","M_ID":62308,"PLATFORM_CODE":"12350400669253747H","PLATFORM_NAME":"三明市公共资源交易中心","TM":"2017-12-12T15:55:40","PROCODE":null,"AREACODE":"350400","AREANAME":"三明市","PROTYPE_TEXT":"市政","PROTYPE":"A02","M_DATA_SOURCE":"9999","M_TM":null,"M_PROJECT_TYPE":"2","RN":3},{"KIND":"GCJS","TITLE":"招标公告","GGTYPE":"1","NAME":"岳峰中学（施工、预制构件一体化）","M_ID":62306,"PLATFORM_CODE":"12350100MB02709751","PLATFORM_NAME":"福州市公共资源交易服务中心","TM":"2017-12-12T15:43:11","PROCODE":null,"AREACODE":"350100","AREANAME":"福州市","PROTYPE_TEXT":"房屋建设","PROTYPE":"A01","M_DATA_SOURCE":"9999","M_TM":null,"M_PROJECT_TYPE":"1","RN":4},{"KIND":"GCJS","TITLE":"招标公告","GGTYPE":"1","NAME":"2017年全市行业IT设备耗材定点供应商采购项目","M_ID":62305,"PLATFORM_CODE":"111111122222229998","PLATFORM_NAME":"非交易中心","TM":"2017-12-12T15:40:50","PROCODE":null,"AREACODE":"350600","AREANAME":"漳州市","PROTYPE_TEXT":"其他","PROTYPE":"Z99","M_DATA_SOURCE":"9999","M_TM":null,"M_PROJECT_TYPE":"2","RN":5},{"KIND":"GCJS","TITLE":"招标公告","GGTYPE":"1","NAME":"国家海洋局三沙海洋环境监测站业务楼迁建工程--综合业务楼(监理）","M_ID":62265,"PLATFORM_CODE":"12352200MB0461560C","PLATFORM_NAME":"宁德市公共资源交易中心","TM":"2017-12-12T15:27:47","PROCODE":null,"AREACODE":"350900","AREANAME":"宁德市","PROTYPE_TEXT":"房屋建设","PROTYPE":"A01","M_DATA_SOURCE":"9999","M_TM":null,"M_PROJECT_TYPE":"2","RN":6},{"KIND":"GCJS","TITLE":"招标公告","GGTYPE":"1","NAME":"国家海洋局三沙海洋环境监测站业务楼迁建工程--综合业务楼招标公告","M_ID":62263,"PLATFORM_CODE":"12352200MB0461560C","PLATFORM_NAME":"宁德市公共资源交易中心","TM":"2017-12-12T15:27:30","PROCODE":null,"AREACODE":"350900","AREANAME":"宁德市","PROTYPE_TEXT":"房屋建设","PROTYPE":"A01","M_DATA_SOURCE":"9999","M_TM":null,"M_PROJECT_TYPE":"2","RN":7},{"KIND":"GCJS","TITLE":"招标公告","GGTYPE":"1","NAME":"福建省智信招标有限公司关于福建省储备粮管理有限公司武平直属库项目变配电设备采购的公开招标公告","M_ID":62302,"PLATFORM_CODE":"111111122222229998","PLATFORM_NAME":"非交易中心","TM":"2017-12-12T15:18:06","PROCODE":null,"AREACODE":"350100","AREANAME":"福州市","PROTYPE_TEXT":"其他","PROTYPE":"Z99","M_DATA_SOURCE":"9999","M_TM":null,"M_PROJECT_TYPE":"1","RN":8},{"KIND":"GCJS","TITLE":"招标公告","GGTYPE":"1","NAME":"闽西职业技术学院2017年小型基建及零星维修工程施工队伍招标项目","M_ID":62301,"PLATFORM_CODE":"111111122222229998","PLATFORM_NAME":"非交易中心","TM":"2017-12-12T15:17:54","PROCODE":null,"AREACODE":"350800","AREANAME":"龙岩市","PROTYPE_TEXT":"房屋建设","PROTYPE":"A01","M_DATA_SOURCE":"9999","M_TM":null,"M_PROJECT_TYPE":"2","RN":9},{"KIND":"GCJS","TITLE":"招标公告","GGTYPE":"1","NAME":"云霄厂房变配电设备施工及装材项目监理","M_ID":62259,"PLATFORM_CODE":"111111122222229998","PLATFORM_NAME":"非交易中心","TM":"2017-12-12T15:07:11","PROCODE":null,"AREACODE":"350800","AREANAME":"龙岩市","PROTYPE_TEXT":"其他","PROTYPE":"A99","M_DATA_SOURCE":"9999","M_TM":null,"M_PROJECT_TYPE":"2","RN":10}]
     * total : 37
     * pageNo : 1
     */

    private int total;
    private int pageNo;
    private List<DataBean> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * KIND : GCJS
         * TITLE : 招标公告
         * GGTYPE : 1
         * NAME : 南平航天体验馆分布式光伏发电项目设计采购施工（EPC）总承包
         * M_ID : 62286
         * PLATFORM_CODE : 123507006919064000
         * PLATFORM_NAME : 南平市公共资源交易中心
         * TM : 2017-12-12T16:02:53
         * PROCODE : null
         * AREACODE : 350700
         * AREANAME : 南平市
         * PROTYPE_TEXT : 能源
         * PROTYPE : A08
         * M_DATA_SOURCE : 9999
         * M_TM : null
         * M_PROJECT_TYPE : 1
         * RN : 1
         */

        private String KIND;
        private String TITLE;
        private String GGTYPE;
        private String NAME;
        private int M_ID;
        private String PLATFORM_CODE;
        private String PLATFORM_NAME;
        private String TM;
        private Object PROCODE;
        private String AREACODE;
        private String AREANAME;
        private String PROTYPE_TEXT;
        private String PROTYPE;
        private String M_DATA_SOURCE;
        private Object M_TM;
        private String M_PROJECT_TYPE;
        private int RN;

        public String getKIND() {
            return KIND;
        }

        public void setKIND(String KIND) {
            this.KIND = KIND;
        }

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

        public int getM_ID() {
            return M_ID;
        }

        public void setM_ID(int M_ID) {
            this.M_ID = M_ID;
        }

        public String getPLATFORM_CODE() {
            return PLATFORM_CODE;
        }

        public void setPLATFORM_CODE(String PLATFORM_CODE) {
            this.PLATFORM_CODE = PLATFORM_CODE;
        }

        public String getPLATFORM_NAME() {
            return PLATFORM_NAME;
        }

        public void setPLATFORM_NAME(String PLATFORM_NAME) {
            this.PLATFORM_NAME = PLATFORM_NAME;
        }

        public String getTM() {
            return TM;
        }

        public void setTM(String TM) {
            this.TM = TM;
        }

        public Object getPROCODE() {
            return PROCODE;
        }

        public void setPROCODE(Object PROCODE) {
            this.PROCODE = PROCODE;
        }

        public String getAREACODE() {
            return AREACODE;
        }

        public void setAREACODE(String AREACODE) {
            this.AREACODE = AREACODE;
        }

        public String getAREANAME() {
            return AREANAME;
        }

        public void setAREANAME(String AREANAME) {
            this.AREANAME = AREANAME;
        }

        public String getPROTYPE_TEXT() {
            return PROTYPE_TEXT;
        }

        public void setPROTYPE_TEXT(String PROTYPE_TEXT) {
            this.PROTYPE_TEXT = PROTYPE_TEXT;
        }

        public String getPROTYPE() {
            return PROTYPE;
        }

        public void setPROTYPE(String PROTYPE) {
            this.PROTYPE = PROTYPE;
        }

        public String getM_DATA_SOURCE() {
            return M_DATA_SOURCE;
        }

        public void setM_DATA_SOURCE(String M_DATA_SOURCE) {
            this.M_DATA_SOURCE = M_DATA_SOURCE;
        }

        public Object getM_TM() {
            return M_TM;
        }

        public void setM_TM(Object M_TM) {
            this.M_TM = M_TM;
        }

        public String getM_PROJECT_TYPE() {
            return M_PROJECT_TYPE;
        }

        public void setM_PROJECT_TYPE(String M_PROJECT_TYPE) {
            this.M_PROJECT_TYPE = M_PROJECT_TYPE;
        }

        public int getRN() {
            return RN;
        }

        public void setRN(int RN) {
            this.RN = RN;
        }
    }
}
