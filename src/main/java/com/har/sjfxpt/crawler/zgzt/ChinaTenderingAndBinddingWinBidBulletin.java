package com.har.sjfxpt.crawler.zgzt;

import java.util.List;

/**
 * Created by Administrator on 2017/11/6.
 */
public class ChinaTenderingAndBinddingWinBidBulletin {

    /**
     * message :
     * success : true
     * object : {"winBidBulletin":[{"transactionPlatfCode":"E5300000177","bulletinContent":"<div class=\"announce_box\">\t<h2>中标结果公告<\/h2>\t\t<div class=\"con\">\t\t\t<table>\t\t\t\t<tr>\t\t\t\t\t<td width=\"25%\">标段编号：<\/td>\t\t\t\t\t<td width=\"75%\"><span id=\"bdBH\">GC530000201700295001003<\/span><\/td>\t\t\t\t<\/tr>\t\t\t\t<tr>\t\t\t\t\t<td>标段名称：<\/td>\t\t\t\t\t<td><span id=\"bdName\">云南省2013-2017年城市棚户区改造（四期）-沾益区西河片区项目安置房建设项目三标段施工<\/span><\/td>\t\t\t\t<\/tr>\t\t\t\t<tr>\t\t\t\t\t<td>工程类型：<\/td>\t\t\t\t\t<td><span>施工<\/span><\/td>\t\t\t\t<\/tr>\t\t\t\t<tr>\t\t\t\t\t<td>发布时间：<\/td>\t\t\t\t\t<td>\t\t\t\t\t\t<span id=\"zbgsStartTime\">2017-11-06 09:00:00<\/span>\t\t\t\t\t<\/td>\t\t\t\t<\/tr>\t\t\t<tr>\t\t\t\t<td width=\"25%\">中标人：<\/td>\t\t\t\t<td width=\"75%\"><b><span id=\"tbrName\">云南建投第十三建设有限公司<\/span><\/b><\/td>\t\t\t<\/tr>\t\t\t<tr class=\"yx\">\t\t\t\t<td>中标价：<\/td>\t\t\t<td><b><span id=\"zhongBiaoJE\">10544.101347万元<\/span><\/b><\/td>\t\t\t<\/tr>\t\t\t<tr>\t\t\t\t<td>中标工期：<\/td>\t\t\t\t<td><span id=\"zhongBiaoGQ\">计划工期540日历天，开工日期以发包人书面通知为准天<\/span><\/td>\t\t\t<\/tr>\t\t\t<tr class=\"sg_jl yx\">\t\t\t\t<td>项目经理：<\/td>\t\t\t\t<td><span id=\"xiangMuJiLi\">黄鑫<\/span><\/td>\t\t\t<\/tr>\t\t\t<tr>\t\t\t\t<td>备注说明：<\/td>\t\t\t\t<td><span><\/span><\/td>\t\t\t<\/tr>\t\t<\/table>\t<\/div><\/div>","attachmentCode":"","sourceUrl":" ","tenderProjectCode":"E5300000177000447001","schemaVersion":"V60.02","bulletinssueTime":"20171106090000","bulletinName":"云南省2013-2017年城市棚户区改造（四期）-沾益区西河片区项目安置房建设项目一、二、三标段施工"},{"transactionPlatfCode":"E5300000177","bulletinContent":"<div class=\"announce_box\">\t<h2>中标结果公告<\/h2>\t\t<div class=\"con\">\t\t\t<table>\t\t\t\t<tr>\t\t\t\t\t<td width=\"25%\">标段编号：<\/td>\t\t\t\t\t<td width=\"75%\"><span id=\"bdBH\">GC530000201700295001002<\/span><\/td>\t\t\t\t<\/tr>\t\t\t\t<tr>\t\t\t\t\t<td>标段名称：<\/td>\t\t\t\t\t<td><span id=\"bdName\">云南省2013-2017年城市棚户区改造（四期）-沾益区西河片区项目安置房建设项目二标段施工<\/span><\/td>\t\t\t\t<\/tr>\t\t\t\t<tr>\t\t\t\t\t<td>工程类型：<\/td>\t\t\t\t\t<td><span>施工<\/span><\/td>\t\t\t\t<\/tr>\t\t\t\t<tr>\t\t\t\t\t<td>发布时间：<\/td>\t\t\t\t\t<td>\t\t\t\t\t\t<span id=\"zbgsStartTime\">2017-11-06 09:00:00<\/span>\t\t\t\t\t<\/td>\t\t\t\t<\/tr>\t\t\t<tr>\t\t\t\t<td width=\"25%\">中标人：<\/td>\t\t\t\t<td width=\"75%\"><b><span id=\"tbrName\">中建四局第五建筑工程有限公司<\/span><\/b><\/td>\t\t\t<\/tr>\t\t\t<tr class=\"yx\">\t\t\t\t<td>中标价：<\/td>\t\t\t<td><b><span id=\"zhongBiaoJE\">12534.956553万元<\/span><\/b><\/td>\t\t\t<\/tr>\t\t\t<tr>\t\t\t\t<td>中标工期：<\/td>\t\t\t\t<td><span id=\"zhongBiaoGQ\">540日历天天<\/span><\/td>\t\t\t<\/tr>\t\t\t<tr class=\"sg_jl yx\">\t\t\t\t<td>项目经理：<\/td>\t\t\t\t<td><span id=\"xiangMuJiLi\">黄建义<\/span><\/td>\t\t\t<\/tr>\t\t\t<tr>\t\t\t\t<td>备注说明：<\/td>\t\t\t\t<td><span><\/span><\/td>\t\t\t<\/tr>\t\t<\/table>\t<\/div><\/div>","attachmentCode":"","sourceUrl":" ","tenderProjectCode":"E5300000177000447001","schemaVersion":"V60.02","bulletinssueTime":"20171106090000","bulletinName":"云南省2013-2017年城市棚户区改造（四期）-沾益区西河片区项目安置房建设项目一、二、三标段施工"}]}
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
        private List<WinBidBulletinBean> winBidBulletin;

        public List<WinBidBulletinBean> getWinBidBulletin() {
            return winBidBulletin;
        }

        public void setWinBidBulletin(List<WinBidBulletinBean> winBidBulletin) {
            this.winBidBulletin = winBidBulletin;
        }

        public static class WinBidBulletinBean {
            /**
             * transactionPlatfCode : E5300000177
             * bulletinContent : <div class="announce_box">	<h2>中标结果公告</h2>		<div class="con">			<table>				<tr>					<td width="25%">标段编号：</td>					<td width="75%"><span id="bdBH">GC530000201700295001003</span></td>				</tr>				<tr>					<td>标段名称：</td>					<td><span id="bdName">云南省2013-2017年城市棚户区改造（四期）-沾益区西河片区项目安置房建设项目三标段施工</span></td>				</tr>				<tr>					<td>工程类型：</td>					<td><span>施工</span></td>				</tr>				<tr>					<td>发布时间：</td>					<td>						<span id="zbgsStartTime">2017-11-06 09:00:00</span>					</td>				</tr>			<tr>				<td width="25%">中标人：</td>				<td width="75%"><b><span id="tbrName">云南建投第十三建设有限公司</span></b></td>			</tr>			<tr class="yx">				<td>中标价：</td>			<td><b><span id="zhongBiaoJE">10544.101347万元</span></b></td>			</tr>			<tr>				<td>中标工期：</td>				<td><span id="zhongBiaoGQ">计划工期540日历天，开工日期以发包人书面通知为准天</span></td>			</tr>			<tr class="sg_jl yx">				<td>项目经理：</td>				<td><span id="xiangMuJiLi">黄鑫</span></td>			</tr>			<tr>				<td>备注说明：</td>				<td><span></span></td>			</tr>		</table>	</div></div>
             * attachmentCode :
             * sourceUrl :
             * tenderProjectCode : E5300000177000447001
             * schemaVersion : V60.02
             * bulletinssueTime : 20171106090000
             * bulletinName : 云南省2013-2017年城市棚户区改造（四期）-沾益区西河片区项目安置房建设项目一、二、三标段施工
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
