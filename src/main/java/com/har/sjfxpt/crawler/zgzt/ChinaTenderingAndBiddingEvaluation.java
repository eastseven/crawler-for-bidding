package com.har.sjfxpt.crawler.zgzt;

import java.util.List;

/**
 * Created by Administrator on 2017/11/6.
 */@Deprecated
public class ChinaTenderingAndBiddingEvaluation {

    /**
     * message :
     * success : true
     * object : {"winCandidateBulletin":[{"transactionPlatfCode":"E1100000084","bulletinContent":"项目名称：S201项目自动化柔性焊接系统<br/>招标项目编号：0611-17400050968A<br/>招标范围：S201项目自动化柔性焊接系统   数量：1批<br/>招标机构：重庆招标采购（集团）有限责任公司<br/>招标人：重庆长安汽车股份有限公司<br/>开标时间：2017-11-02 10:00<br/>公示开始时间：2017-11-06 09:39<br/>评标公示截止时间：2017-11-09 23:59<br/>中标候选人名单：<table width=\"400px\" style=\"border-collapse: collapse;\" border=\"1\"><tr><th width=\"30px\">候选人排名<\/th><th width=\"130px\">投标商名称<\/th><th width=\"120px\">制造商<\/th><th width=\"120px\">制造商国别及地区<\/th><\/tr><tr><td align=\"center\">1<\/td><td align=\"center\" style=\"word-wrap:break-word;word-break:break-all;\">长安徕斯（重庆）机器人智能装备有限公司<\/td><td align=\"center\" style=\"word-wrap:break-word;word-break:break-all;\">长安徕斯（重庆）机器人智能装备有限公司<\/td><td align=\"center\" style=\"word-wrap:break-word;word-break:break-all;\">中国<\/td><\/tr><tr><td align=\"center\">2<\/td><td align=\"center\" style=\"word-wrap:break-word;word-break:break-all;\">四川成焊宝玛焊接装备工程有限公司<\/td><td align=\"center\" style=\"word-wrap:break-word;word-break:break-all;\">四川成焊宝玛焊接装备工程有限公司<\/td><td align=\"center\" style=\"word-wrap:break-word;word-break:break-all;\">中国<\/td><\/tr><tr><td align=\"center\">3<\/td><td align=\"center\" style=\"word-wrap:break-word;word-break:break-all;\">上海德梅柯汽车装备制造有限公司<\/td><td align=\"center\" style=\"word-wrap:break-word;word-break:break-all;\">上海德梅柯汽车装备制造有限公司<\/td><td align=\"center\" style=\"word-wrap:break-word;word-break:break-all;\">中国<\/td><\/tr><\/table><br/>","attachmentCode":"","sourceUrl":"www.chinabidding.com","tenderProjectCode":"0611-17400050968A000","schemaVersion":"V60.02","bulletinssueTime":"20171106093900","bulletinName":"S201项目自动化柔性焊接系统"}]}
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
        private List<WinCandidateBulletinBean> winCandidateBulletin;

        public List<WinCandidateBulletinBean> getWinCandidateBulletin() {
            return winCandidateBulletin;
        }

        public void setWinCandidateBulletin(List<WinCandidateBulletinBean> winCandidateBulletin) {
            this.winCandidateBulletin = winCandidateBulletin;
        }

        public static class WinCandidateBulletinBean {
            /**
             * transactionPlatfCode : E1100000084
             * bulletinContent : 项目名称：S201项目自动化柔性焊接系统<br/>招标项目编号：0611-17400050968A<br/>招标范围：S201项目自动化柔性焊接系统   数量：1批<br/>招标机构：重庆招标采购（集团）有限责任公司<br/>招标人：重庆长安汽车股份有限公司<br/>开标时间：2017-11-02 10:00<br/>公示开始时间：2017-11-06 09:39<br/>评标公示截止时间：2017-11-09 23:59<br/>中标候选人名单：<table width="400px" style="border-collapse: collapse;" border="1"><tr><th width="30px">候选人排名</th><th width="130px">投标商名称</th><th width="120px">制造商</th><th width="120px">制造商国别及地区</th></tr><tr><td align="center">1</td><td align="center" style="word-wrap:break-word;word-break:break-all;">长安徕斯（重庆）机器人智能装备有限公司</td><td align="center" style="word-wrap:break-word;word-break:break-all;">长安徕斯（重庆）机器人智能装备有限公司</td><td align="center" style="word-wrap:break-word;word-break:break-all;">中国</td></tr><tr><td align="center">2</td><td align="center" style="word-wrap:break-word;word-break:break-all;">四川成焊宝玛焊接装备工程有限公司</td><td align="center" style="word-wrap:break-word;word-break:break-all;">四川成焊宝玛焊接装备工程有限公司</td><td align="center" style="word-wrap:break-word;word-break:break-all;">中国</td></tr><tr><td align="center">3</td><td align="center" style="word-wrap:break-word;word-break:break-all;">上海德梅柯汽车装备制造有限公司</td><td align="center" style="word-wrap:break-word;word-break:break-all;">上海德梅柯汽车装备制造有限公司</td><td align="center" style="word-wrap:break-word;word-break:break-all;">中国</td></tr></table><br/>
             * attachmentCode :
             * sourceUrl : www.chinabidding.com
             * tenderProjectCode : 0611-17400050968A000
             * schemaVersion : V60.02
             * bulletinssueTime : 20171106093900
             * bulletinName : S201项目自动化柔性焊接系统
             */

            private String transactionPlatfCode;
            private String bulletinContent;
            private String attachmentCode;
            private String sourceUrl;
            private String tenderProjectCode;
            private String schemaVersion;
            private String bulletinssueTime;
            private String bulletinName;

            public String getTransactionPlatfCode() {
                return transactionPlatfCode;
            }

            public void setTransactionPlatfCode(String transactionPlatfCode) {
                this.transactionPlatfCode = transactionPlatfCode;
            }

            public String getBulletinContent() {
                return bulletinContent;
            }

            public void setBulletinContent(String bulletinContent) {
                this.bulletinContent = bulletinContent;
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

            public String getTenderProjectCode() {
                return tenderProjectCode;
            }

            public void setTenderProjectCode(String tenderProjectCode) {
                this.tenderProjectCode = tenderProjectCode;
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

            public String getBulletinName() {
                return bulletinName;
            }

            public void setBulletinName(String bulletinName) {
                this.bulletinName = bulletinName;
            }
        }
    }
}
