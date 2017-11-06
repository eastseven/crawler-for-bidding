package com.har.sjfxpt.crawler.zgzt;

import java.util.List;

/**
 * Created by Administrator on 2017/11/6.
 */
public class ChinaTenderingAndBiddingOpenBidRecord {

    /**
     * message :
     * success : true
     * object : {"openBidRecord":[{"serviceOpenBidList":[{"bidSectionCode":"M3400000022003494002001","transactionPlatfCode":"M3400000022","verifyTime":"60","bidSectionName":"职工食堂食材供应商采购项目","bidderName":"安徽百大合家福连锁超市股份有限公司"},{"bidSectionCode":"M3400000022003494002001","transactionPlatfCode":"M3400000022","verifyTime":"60","bidSectionName":"职工食堂食材供应商采购项目","bidderName":"锦江麦德龙现购自运有限公司合肥包河商场"},{"bidSectionCode":"M3400000022003494002001","transactionPlatfCode":"M3400000022","verifyTime":"60","bidSectionName":"职工食堂食材供应商采购项目","bidderName":"合肥雨青商贸有限公司"},{"bidSectionCode":"M3400000022003494002001","transactionPlatfCode":"M3400000022","verifyTime":"60","bidSectionName":"职工食堂食材供应商采购项目","bidderName":"安徽优乐果蔬配送有限公司"},{"bidSectionCode":"M3400000022003494002001","transactionPlatfCode":"M3400000022","verifyTime":"60","bidSectionName":"职工食堂食材供应商采购项目","bidderName":"安徽汇慧食品配送有限公司"},{"bidSectionCode":"M3400000022003494002001","transactionPlatfCode":"M3400000022","verifyTime":"60","bidSectionName":"职工食堂食材供应商采购项目","bidderName":"合肥叶记食品有限公司"}],"openBidRecord":{"transactionPlatfCode":"M3400000022","bidSectionCodes":"M3400000022003494002001;M3400000022003494002001;M3400000022003494002001;M3400000022003494002001;M3400000022003494002001;M3400000022003494002001;","attachmentCode":"","sourceUrl":"","bidOpeningTime":"20171106090000","openBidRecordName":"安徽省国家税务局 职工食堂食材供应商采购项目开标记录"},"goodsOpenBidList":null,"projectOpenBidList":[{"bidAmount":"5173867.26","bidSectionCode":"E3702110182000293001001","transactionPlatfCode":"E3702110182","verifyTime":"120","bidSectionName":"张家楼镇苑庄村及张家楼村中心街道路修复工程","bidderName":"青岛瑞源工程集团有限公司"}]}]}
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
        private List<OpenBidRecordBeanX> openBidRecord;

        public List<OpenBidRecordBeanX> getOpenBidRecord() {
            return openBidRecord;
        }

        public void setOpenBidRecord(List<OpenBidRecordBeanX> openBidRecord) {
            this.openBidRecord = openBidRecord;
        }

        public static class OpenBidRecordBeanX {
            /**
             * serviceOpenBidList : [{"bidSectionCode":"M3400000022003494002001","transactionPlatfCode":"M3400000022","verifyTime":"60","bidSectionName":"职工食堂食材供应商采购项目","bidderName":"安徽百大合家福连锁超市股份有限公司"},{"bidSectionCode":"M3400000022003494002001","transactionPlatfCode":"M3400000022","verifyTime":"60","bidSectionName":"职工食堂食材供应商采购项目","bidderName":"锦江麦德龙现购自运有限公司合肥包河商场"},{"bidSectionCode":"M3400000022003494002001","transactionPlatfCode":"M3400000022","verifyTime":"60","bidSectionName":"职工食堂食材供应商采购项目","bidderName":"合肥雨青商贸有限公司"},{"bidSectionCode":"M3400000022003494002001","transactionPlatfCode":"M3400000022","verifyTime":"60","bidSectionName":"职工食堂食材供应商采购项目","bidderName":"安徽优乐果蔬配送有限公司"},{"bidSectionCode":"M3400000022003494002001","transactionPlatfCode":"M3400000022","verifyTime":"60","bidSectionName":"职工食堂食材供应商采购项目","bidderName":"安徽汇慧食品配送有限公司"},{"bidSectionCode":"M3400000022003494002001","transactionPlatfCode":"M3400000022","verifyTime":"60","bidSectionName":"职工食堂食材供应商采购项目","bidderName":"合肥叶记食品有限公司"}]
             * openBidRecord : {"transactionPlatfCode":"M3400000022","bidSectionCodes":"M3400000022003494002001;M3400000022003494002001;M3400000022003494002001;M3400000022003494002001;M3400000022003494002001;M3400000022003494002001;","attachmentCode":"","sourceUrl":"","bidOpeningTime":"20171106090000","openBidRecordName":"安徽省国家税务局 职工食堂食材供应商采购项目开标记录"}
             * goodsOpenBidList : null
             * projectOpenBidList : [{"bidAmount":"5173867.26","bidSectionCode":"E3702110182000293001001","transactionPlatfCode":"E3702110182","verifyTime":"120","bidSectionName":"张家楼镇苑庄村及张家楼村中心街道路修复工程","bidderName":"青岛瑞源工程集团有限公司"}]
             */

            private OpenBidRecordBean openBidRecord;
            private Object goodsOpenBidList;
            private List<ServiceOpenBidListBean> serviceOpenBidList;
            private List<ProjectOpenBidListBean> projectOpenBidList;

            public OpenBidRecordBean getOpenBidRecord() {
                return openBidRecord;
            }

            public void setOpenBidRecord(OpenBidRecordBean openBidRecord) {
                this.openBidRecord = openBidRecord;
            }

            public Object getGoodsOpenBidList() {
                return goodsOpenBidList;
            }

            public void setGoodsOpenBidList(Object goodsOpenBidList) {
                this.goodsOpenBidList = goodsOpenBidList;
            }

            public List<ServiceOpenBidListBean> getServiceOpenBidList() {
                return serviceOpenBidList;
            }

            public void setServiceOpenBidList(List<ServiceOpenBidListBean> serviceOpenBidList) {
                this.serviceOpenBidList = serviceOpenBidList;
            }

            public List<ProjectOpenBidListBean> getProjectOpenBidList() {
                return projectOpenBidList;
            }

            public void setProjectOpenBidList(List<ProjectOpenBidListBean> projectOpenBidList) {
                this.projectOpenBidList = projectOpenBidList;
            }

            public static class OpenBidRecordBean {
                /**
                 * transactionPlatfCode : M3400000022
                 * bidSectionCodes : M3400000022003494002001;M3400000022003494002001;M3400000022003494002001;M3400000022003494002001;M3400000022003494002001;M3400000022003494002001;
                 * attachmentCode :
                 * sourceUrl :
                 * bidOpeningTime : 20171106090000
                 * openBidRecordName : 安徽省国家税务局 职工食堂食材供应商采购项目开标记录
                 */

                private String transactionPlatfCode;
                private String bidSectionCodes;
                private String attachmentCode;
                private String sourceUrl;
                private String bidOpeningTime;
                private String openBidRecordName;

                public String getTransactionPlatfCode() {
                    return transactionPlatfCode;
                }

                public void setTransactionPlatfCode(String transactionPlatfCode) {
                    this.transactionPlatfCode = transactionPlatfCode;
                }

                public String getBidSectionCodes() {
                    return bidSectionCodes;
                }

                public void setBidSectionCodes(String bidSectionCodes) {
                    this.bidSectionCodes = bidSectionCodes;
                }

                public String getAttachmentCode() {
                    return attachmentCode;
                }

                public void setAttachmentCode(String attachmentCode) {
                    this.attachmentCode = attachmentCode;
                }

                public String getSourceUrl() {
                    return sourceUrl;
                }

                public void setSourceUrl(String sourceUrl) {
                    this.sourceUrl = sourceUrl;
                }

                public String getBidOpeningTime() {
                    return bidOpeningTime;
                }

                public void setBidOpeningTime(String bidOpeningTime) {
                    this.bidOpeningTime = bidOpeningTime;
                }

                public String getOpenBidRecordName() {
                    return openBidRecordName;
                }

                public void setOpenBidRecordName(String openBidRecordName) {
                    this.openBidRecordName = openBidRecordName;
                }
            }

            public static class ServiceOpenBidListBean {
                /**
                 * bidSectionCode : M3400000022003494002001
                 * transactionPlatfCode : M3400000022
                 * verifyTime : 60
                 * bidSectionName : 职工食堂食材供应商采购项目
                 * bidderName : 安徽百大合家福连锁超市股份有限公司
                 */

                private String bidSectionCode;
                private String transactionPlatfCode;
                private String verifyTime;
                private String bidSectionName;
                private String bidderName;

                public String getBidSectionCode() {
                    return bidSectionCode;
                }

                public void setBidSectionCode(String bidSectionCode) {
                    this.bidSectionCode = bidSectionCode;
                }

                public String getTransactionPlatfCode() {
                    return transactionPlatfCode;
                }

                public void setTransactionPlatfCode(String transactionPlatfCode) {
                    this.transactionPlatfCode = transactionPlatfCode;
                }

                public String getVerifyTime() {
                    return verifyTime;
                }

                public void setVerifyTime(String verifyTime) {
                    this.verifyTime = verifyTime;
                }

                public String getBidSectionName() {
                    return bidSectionName;
                }

                public void setBidSectionName(String bidSectionName) {
                    this.bidSectionName = bidSectionName;
                }

                public String getBidderName() {
                    return bidderName;
                }

                public void setBidderName(String bidderName) {
                    this.bidderName = bidderName;
                }
            }

            public static class ProjectOpenBidListBean {
                /**
                 * bidAmount : 5173867.26
                 * bidSectionCode : E3702110182000293001001
                 * transactionPlatfCode : E3702110182
                 * verifyTime : 120
                 * bidSectionName : 张家楼镇苑庄村及张家楼村中心街道路修复工程
                 * bidderName : 青岛瑞源工程集团有限公司
                 */

                private String bidAmount;
                private String bidSectionCode;
                private String transactionPlatfCode;
                private String verifyTime;
                private String bidSectionName;
                private String bidderName;

                public String getBidAmount() {
                    return bidAmount;
                }

                public void setBidAmount(String bidAmount) {
                    this.bidAmount = bidAmount;
                }

                public String getBidSectionCode() {
                    return bidSectionCode;
                }

                public void setBidSectionCode(String bidSectionCode) {
                    this.bidSectionCode = bidSectionCode;
                }

                public String getTransactionPlatfCode() {
                    return transactionPlatfCode;
                }

                public void setTransactionPlatfCode(String transactionPlatfCode) {
                    this.transactionPlatfCode = transactionPlatfCode;
                }

                public String getVerifyTime() {
                    return verifyTime;
                }

                public void setVerifyTime(String verifyTime) {
                    this.verifyTime = verifyTime;
                }

                public String getBidSectionName() {
                    return bidSectionName;
                }

                public void setBidSectionName(String bidSectionName) {
                    this.bidSectionName = bidSectionName;
                }

                public String getBidderName() {
                    return bidderName;
                }

                public void setBidderName(String bidderName) {
                    this.bidderName = bidderName;
                }
            }
        }
    }
}
