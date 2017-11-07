package com.har.sjfxpt.crawler.zgzt;

import java.util.List;

/**
 * Created by Administrator on 2017/11/2.
 */
public class ChinaTenderingAndBiddingContent {
    /**
     * message :
     * success : true
     * object : {"tenderProject":[{"createTime":"20171102000201","regionCode":"北京市","tendererName":"首都医科大学附属北京康复医院（北京工人疗养院）","approveDeptName":"null","tenderAgencyName":"中招政采招标咨询（北京）有限公司","tenderAgencyCodeType":"","superviseDeptName":"","bulletinContent":"项目名称：首都医科大学附属北京康复医院2017年帕金森医学中心建设项目\n\n项目编号：1348-174ZC17A082G/04\n\n1、呼吸机；\n2、神经影像导航定位系统；\n3、脑康复系统软件；\n4、便携式睡眠记录仪；\n5、","approveDeptCode":"null","attachmentCode":"","tenderProjectCode":"1348-174ZC17A082G004","bulletinName":"首都医科大学附属北京康复医院2017年帕金森医学中心建设项目","superviseDeptCodeType":"","tendererCode":"","tenderAgencyCode":"","tenderOrganizeForm":"其他","industriesType":"总体规划","superviseDeptCode":"","schemaVersion":"V60.02","bulletinssueTime":"20171102000201"}]}
     */

    private String message;
    private boolean success;
    private ObjectBean object;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ObjectBean getObject() {
        return object;
    }

    public void setObject(ObjectBean object) {
        this.object = object;
    }

    public static class ObjectBean {
        private List<TenderProjectBean> tenderProject;

        public List<TenderProjectBean> getTenderProject() {
            return tenderProject;
        }

        public void setTenderProject(List<TenderProjectBean> tenderProject) {
            this.tenderProject = tenderProject;
        }

        public static class TenderProjectBean {
            /**
             * createTime : 20171102000201
             * regionCode : 北京市
             * tendererName : 首都医科大学附属北京康复医院（北京工人疗养院）
             * approveDeptName : null
             * tenderAgencyName : 中招政采招标咨询（北京）有限公司
             * tenderAgencyCodeType :
             * superviseDeptName :
             * bulletinContent : 项目名称：首都医科大学附属北京康复医院2017年帕金森医学中心建设项目

             项目编号：1348-174ZC17A082G/04

             1、呼吸机；
             2、神经影像导航定位系统；
             3、脑康复系统软件；
             4、便携式睡眠记录仪；
             5、
             * approveDeptCode : null
             * attachmentCode :
             * tenderProjectCode : 1348-174ZC17A082G004
             * bulletinName : 首都医科大学附属北京康复医院2017年帕金森医学中心建设项目
             * superviseDeptCodeType :
             * tendererCode :
             * tenderAgencyCode :
             * tenderOrganizeForm : 其他
             * industriesType : 总体规划
             * superviseDeptCode :
             * schemaVersion : V60.02
             * bulletinssueTime : 20171102000201
             */

            private String createTime;
            private String regionCode;
            private String tendererName;
            private String approveDeptName;
            private String tenderAgencyName;
            private String tenderAgencyCodeType;
            private String superviseDeptName;
            private String bulletinContent;
            private String approveDeptCode;
            private String attachmentCode;
            private String tenderProjectCode;
            private String bulletinName;
            private String superviseDeptCodeType;
            private String tendererCode;
            private String tenderAgencyCode;
            private String tenderOrganizeForm;
            private String industriesType;
            private String superviseDeptCode;
            private String schemaVersion;
            private String bulletinssueTime;

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getRegionCode() {
                return regionCode;
            }

            public void setRegionCode(String regionCode) {
                this.regionCode = regionCode;
            }

            public String getTendererName() {
                return tendererName;
            }

            public void setTendererName(String tendererName) {
                this.tendererName = tendererName;
            }

            public String getApproveDeptName() {
                return approveDeptName;
            }

            public void setApproveDeptName(String approveDeptName) {
                this.approveDeptName = approveDeptName;
            }

            public String getTenderAgencyName() {
                return tenderAgencyName;
            }

            public void setTenderAgencyName(String tenderAgencyName) {
                this.tenderAgencyName = tenderAgencyName;
            }

            public String getTenderAgencyCodeType() {
                return tenderAgencyCodeType;
            }

            public void setTenderAgencyCodeType(String tenderAgencyCodeType) {
                this.tenderAgencyCodeType = tenderAgencyCodeType;
            }

            public String getSuperviseDeptName() {
                return superviseDeptName;
            }

            public void setSuperviseDeptName(String superviseDeptName) {
                this.superviseDeptName = superviseDeptName;
            }

            public String getBulletinContent() {
                return bulletinContent;
            }

            public void setBulletinContent(String bulletinContent) {
                this.bulletinContent = bulletinContent;
            }

            public String getApproveDeptCode() {
                return approveDeptCode;
            }

            public void setApproveDeptCode(String approveDeptCode) {
                this.approveDeptCode = approveDeptCode;
            }

            public String getAttachmentCode() {
                return attachmentCode;
            }

            public void setAttachmentCode(String attachmentCode) {
                this.attachmentCode = attachmentCode;
            }

            public String getTenderProjectCode() {
                return tenderProjectCode;
            }

            public void setTenderProjectCode(String tenderProjectCode) {
                this.tenderProjectCode = tenderProjectCode;
            }

            public String getBulletinName() {
                return bulletinName;
            }

            public void setBulletinName(String bulletinName) {
                this.bulletinName = bulletinName;
            }

            public String getSuperviseDeptCodeType() {
                return superviseDeptCodeType;
            }

            public void setSuperviseDeptCodeType(String superviseDeptCodeType) {
                this.superviseDeptCodeType = superviseDeptCodeType;
            }

            public String getTendererCode() {
                return tendererCode;
            }

            public void setTendererCode(String tendererCode) {
                this.tendererCode = tendererCode;
            }

            public String getTenderAgencyCode() {
                return tenderAgencyCode;
            }

            public void setTenderAgencyCode(String tenderAgencyCode) {
                this.tenderAgencyCode = tenderAgencyCode;
            }

            public String getTenderOrganizeForm() {
                return tenderOrganizeForm;
            }

            public void setTenderOrganizeForm(String tenderOrganizeForm) {
                this.tenderOrganizeForm = tenderOrganizeForm;
            }

            public String getIndustriesType() {
                return industriesType;
            }

            public void setIndustriesType(String industriesType) {
                this.industriesType = industriesType;
            }

            public String getSuperviseDeptCode() {
                return superviseDeptCode;
            }

            public void setSuperviseDeptCode(String superviseDeptCode) {
                this.superviseDeptCode = superviseDeptCode;
            }

            public String getSchemaVersion() {
                return schemaVersion;
            }

            public void setSchemaVersion(String schemaVersion) {
                this.schemaVersion = schemaVersion;
            }

            public String getBulletinssueTime() {
                return bulletinssueTime;
            }

            public void setBulletinssueTime(String bulletinssueTime) {
                this.bulletinssueTime = bulletinssueTime;
            }
        }
    }
}
