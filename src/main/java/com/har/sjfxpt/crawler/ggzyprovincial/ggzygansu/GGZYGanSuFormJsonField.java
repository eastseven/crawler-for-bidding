package com.har.sjfxpt.crawler.ggzyprovincial.ggzygansu;

/**
 * Created by Administrator on 2017/12/17.
 */
public class GGZYGanSuFormJsonField {
    /**
     * areaCode : 620000
     * workNotice : {"noticeNature":"1","bulletinType":"2"}
     * assortmentindex : 0
     */

    private String areaCode;
    private WorkNoticeBean workNotice;
    private String assortmentindex;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public WorkNoticeBean getWorkNotice() {
        return workNotice;
    }

    public void setWorkNotice(WorkNoticeBean workNotice) {
        this.workNotice = workNotice;
    }

    public String getAssortmentindex() {
        return assortmentindex;
    }

    public void setAssortmentindex(String assortmentindex) {
        this.assortmentindex = assortmentindex;
    }

    public static class WorkNoticeBean {
        /**
         * noticeNature : 1
         * bulletinType : 2
         */

        private String noticeNature;
        private String bulletinType;

        public String getNoticeNature() {
            return noticeNature;
        }

        public void setNoticeNature(String noticeNature) {
            this.noticeNature = noticeNature;
        }

        public String getBulletinType() {
            return bulletinType;
        }

        public void setBulletinType(String bulletinType) {
            this.bulletinType = bulletinType;
        }
    }
}
