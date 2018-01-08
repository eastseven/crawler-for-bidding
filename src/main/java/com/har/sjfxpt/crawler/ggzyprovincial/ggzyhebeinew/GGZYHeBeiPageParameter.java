package com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebeinew;

import java.util.List;

/**
 * Created by Administrator on 2017/12/26.
 */
@Deprecated
public class GGZYHeBeiPageParameter {

    /**
     * token :
     * pn : 0
     * rn : null
     * sdt : 2017-12-06 00:00:00
     * edt : 2017-12-26 23:59:59
     * wd :
     * inc_wd :
     * exc_wd :
     * fields : title
     * cnum : 001;002
     * sort : {"showdate":"0"}
     * ssort : title
     * cl : 200
     * terminal :
     * condition : [{"fieldName":"categorynum","isLike":true,"likeType":2,"equal":"003005002001"}]
     * time : null
     * highlights : title
     * statistics : null
     * unionCondition : null
     * accuracy :
     * noParticiple : 0
     * searchRange : null
     * isBusiness : 1
     */

    private String token;
    private int pn;
    private Object rn;
    private String sdt;
    private String edt;
    private String wd;
    private String inc_wd;
    private String exc_wd;
    private String fields;
    private String cnum;
    private String sort;
    private String ssort;
    private int cl;
    private String terminal;
    private Object time;
    private String highlights;
    private Object statistics;
    private Object unionCondition;
    private String accuracy;
    private String noParticiple;
    private Object searchRange;
    private int isBusiness;
    private List<ConditionBean> condition;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPn() {
        return pn;
    }

    public void setPn(int pn) {
        this.pn = pn;
    }

    public Object getRn() {
        return rn;
    }

    public void setRn(Object rn) {
        this.rn = rn;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getEdt() {
        return edt;
    }

    public void setEdt(String edt) {
        this.edt = edt;
    }

    public String getWd() {
        return wd;
    }

    public void setWd(String wd) {
        this.wd = wd;
    }

    public String getInc_wd() {
        return inc_wd;
    }

    public void setInc_wd(String inc_wd) {
        this.inc_wd = inc_wd;
    }

    public String getExc_wd() {
        return exc_wd;
    }

    public void setExc_wd(String exc_wd) {
        this.exc_wd = exc_wd;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getCnum() {
        return cnum;
    }

    public void setCnum(String cnum) {
        this.cnum = cnum;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSsort() {
        return ssort;
    }

    public void setSsort(String ssort) {
        this.ssort = ssort;
    }

    public int getCl() {
        return cl;
    }

    public void setCl(int cl) {
        this.cl = cl;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public Object getTime() {
        return time;
    }

    public void setTime(Object time) {
        this.time = time;
    }

    public String getHighlights() {
        return highlights;
    }

    public void setHighlights(String highlights) {
        this.highlights = highlights;
    }

    public Object getStatistics() {
        return statistics;
    }

    public void setStatistics(Object statistics) {
        this.statistics = statistics;
    }

    public Object getUnionCondition() {
        return unionCondition;
    }

    public void setUnionCondition(Object unionCondition) {
        this.unionCondition = unionCondition;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getNoParticiple() {
        return noParticiple;
    }

    public void setNoParticiple(String noParticiple) {
        this.noParticiple = noParticiple;
    }

    public Object getSearchRange() {
        return searchRange;
    }

    public void setSearchRange(Object searchRange) {
        this.searchRange = searchRange;
    }

    public int getIsBusiness() {
        return isBusiness;
    }

    public void setIsBusiness(int isBusiness) {
        this.isBusiness = isBusiness;
    }

    public List<ConditionBean> getCondition() {
        return condition;
    }

    public void setCondition(List<ConditionBean> condition) {
        this.condition = condition;
    }

    public static class ConditionBean {
        /**
         * fieldName : categorynum
         * isLike : true
         * likeType : 2
         * equal : 003005002001
         */

        private String fieldName;
        private boolean isLike;
        private int likeType;
        private String equal;

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public boolean isIsLike() {
            return isLike;
        }

        public void setIsLike(boolean isLike) {
            this.isLike = isLike;
        }

        public int getLikeType() {
            return likeType;
        }

        public void setLikeType(int likeType) {
            this.likeType = likeType;
        }

        public String getEqual() {
            return equal;
        }

        public void setEqual(String equal) {
            this.equal = equal;
        }
    }
}
