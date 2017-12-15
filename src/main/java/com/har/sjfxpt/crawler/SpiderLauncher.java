package com.har.sjfxpt.crawler;

import com.har.sjfxpt.crawler.ccgp.ZhengFuCaiGouSpiderLauncher;
import com.har.sjfxpt.crawler.ccgp.ccgpcq.CCGPCQSpiderLauncher;
import com.har.sjfxpt.crawler.ccgp.ccgphn.CCGPHaiNanSpiderLauncher;
import com.har.sjfxpt.crawler.ccgp.ccgpsc.CCGPSiChuanSpiderLauncher;
import com.har.sjfxpt.crawler.chengduconstruction.ChengDuConstructionSpiderLauncher;
import com.har.sjfxpt.crawler.chinamobile.ChinaMobileSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.GongGongZiYuanSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzycq.GGZYCQSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyfujian.GGZYFuJianSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzygz.GGZYGZSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebei.GGZYHeBeiSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyjiangxi.GGZYJiangXiSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyshaanxi.GGZYShaanXiSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyshandong.GGZYShanDongSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.hbebtp.HBEBTPSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhlj.GGZYHLJSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhn.GGZYHNSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzysc.GGZYSCSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyxz.GGZYXZSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyyn.GGZYYNSpiderLauncher;
import com.har.sjfxpt.crawler.jcw.JinCaiWangSpiderLauncher;
import com.har.sjfxpt.crawler.petrochina.ZGShiYouSpiderLauncher;
import com.har.sjfxpt.crawler.sgcc.StateGridSpiderLauncher;
import com.har.sjfxpt.crawler.suning.SuNingSpiderLauncher;
import com.har.sjfxpt.crawler.yibiao.YiBiaoSpiderLauncher;
import com.har.sjfxpt.crawler.zgyj.ZGYeJinSpiderLauncher;
import com.har.sjfxpt.crawler.zgzt.ChinaTenderingAndBiddingLauncher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author dongqi
 */
@Slf4j
@Service
@Order
public class SpiderLauncher implements CommandLineRunner {

    @Autowired
    ApplicationContext context;

    @Autowired
    GongGongZiYuanSpiderLauncher gongGongZiYuanSpiderLauncher;

    @Autowired
    ChinaMobileSpiderLauncher chinaMobileSpiderLauncher;

    @Autowired
    ZhengFuCaiGouSpiderLauncher zhengFuCaiGouSpiderLauncher;

    @Autowired
    JinCaiWangSpiderLauncher jinCaiWangSpiderLauncher;

    @Autowired
    ZGShiYouSpiderLauncher zhongGuoShiYouSpiderLauncher;

    @Autowired
    ZGYeJinSpiderLauncher zhongGuoYeJinSpiderLauncher;

    @Autowired
    ChinaTenderingAndBiddingLauncher chinaTenderingAndBiddingLauncher;

    @Autowired
    CCGPHaiNanSpiderLauncher CCGPHaiNanSpiderLauncher;

    @Autowired
    CCGPSiChuanSpiderLauncher ccgpSiChuanSpiderLauncher;

    @Autowired
    YiBiaoSpiderLauncher yiBiaoSpiderLauncher;

    @Autowired
    SuNingSpiderLauncher suNingSpiderLauncher;

    @Autowired
    GGZYSCSpiderLauncher ggzyscSpiderLauncher;

    @Autowired
    GGZYCQSpiderLauncher ggzycqSpiderLauncher;

    @Autowired
    GGZYHNSpiderLauncher ggzyhnSpiderLauncher;

    @Autowired
    GGZYGZSpiderLauncher ggzygzSpiderLauncher;

    @Autowired
    CCGPCQSpiderLauncher ccgpcqSpiderLauncher;

    @Autowired
    GGZYYNSpiderLauncher ggzyynSpiderLauncher;

    @Autowired
    StateGridSpiderLauncher stateGridSpiderLauncher;

    @Autowired
    ChengDuConstructionSpiderLauncher chengDuConstructionSpiderLauncher;

    @Autowired
    GGZYXZSpiderLauncher ggzyxzSpiderLauncher;

    @Autowired
    GGZYHLJSpiderLauncher ggzyhljSpiderLauncher;

    @Autowired
    HBEBTPSpiderLauncher hbebtpSpiderLauncher;

    @Autowired
    GGZYHeBeiSpiderLauncher ggzyHeBeiSpiderLauncher;

    @Autowired
    GGZYFuJianSpiderLauncher ggzyFuJianSpiderLauncher;

    @Autowired
    GGZYJiangXiSpiderLauncher ggzyJiangXiSpiderLauncher;

    @Autowired
    GGZYShanDongSpiderLauncher ggzyShanDongSpiderLauncher;

    @Autowired
    GGZYShaanXiSpiderLauncher ggzyShaanXiSpiderLauncher;

    public void info() {
        gongGongZiYuanSpiderLauncher.printInfo();
        chinaMobileSpiderLauncher.printInfo();
        zhengFuCaiGouSpiderLauncher.printInfo();
        jinCaiWangSpiderLauncher.printInfo();
        zhongGuoShiYouSpiderLauncher.printInfo();
        zhongGuoYeJinSpiderLauncher.printInfo();
        chinaTenderingAndBiddingLauncher.printInfo();
        CCGPHaiNanSpiderLauncher.printInfo();
        ccgpSiChuanSpiderLauncher.printInfo();
        yiBiaoSpiderLauncher.printInfo();
        suNingSpiderLauncher.printInfo();
        ggzyscSpiderLauncher.printInfo();
        ggzycqSpiderLauncher.printInfo();
        ggzyhnSpiderLauncher.printInfo();
        ggzygzSpiderLauncher.printInfo();
        stateGridSpiderLauncher.printInfo();
        ccgpcqSpiderLauncher.printInfo();
        ggzyynSpiderLauncher.printInfo();
        chengDuConstructionSpiderLauncher.printInfo();
        ggzyxzSpiderLauncher.printInfo();
        ggzyhljSpiderLauncher.printInfo();
        hbebtpSpiderLauncher.printInfo();
        ggzyHeBeiSpiderLauncher.printInfo();
        ggzyFuJianSpiderLauncher.printInfo();
        ggzyJiangXiSpiderLauncher.printInfo();
        ggzyShanDongSpiderLauncher.printInfo();
        ggzyShaanXiSpiderLauncher.printInfo();
    }

    @Override
    public void run(String... args) {
        log.info("{}", Arrays.toString(args));
        for (String arg : args) {
            switch (arg) {
                case "start-ggzy":
                    gongGongZiYuanSpiderLauncher.start();
                    break;
                case "history-ggzy":
                    gongGongZiYuanSpiderLauncher.fetchHistory();
                    break;
                case "start-cm":
                    chinaMobileSpiderLauncher.start();
                    break;
                case "history-cm":
                    chinaMobileSpiderLauncher.fetchHistoryStartWith2013().start();
                    break;
                case "start-ccgp":
                    zhengFuCaiGouSpiderLauncher.start();
                    break;
                case "history-ccgp":
                    zhengFuCaiGouSpiderLauncher.history().start();
                    break;
                case "history-url":
                    zhengFuCaiGouSpiderLauncher.getRedisUrl();
                    break;
                case "page-ccgp":
                    zhengFuCaiGouSpiderLauncher.countPageData().start();
                    break;
                case "start-jcw":
                    jinCaiWangSpiderLauncher.start();
                    break;
                case "start-zgyj":
                    zhongGuoYeJinSpiderLauncher.start();
                    break;
                case "history-zgyj":
                    zhongGuoYeJinSpiderLauncher.fetchHistory();
                    break;
                case "start-zsy":
                    zhongGuoShiYouSpiderLauncher.start();
                    break;
                case "history-zsy":
                    zhongGuoShiYouSpiderLauncher.fetchHistory();
                    break;
                case "start-zgzt":
                    chinaTenderingAndBiddingLauncher.start();
                    break;
                case "history-zgzt":
                    chinaTenderingAndBiddingLauncher.fetchHistory();
                    break;
                case "start-ccgphn":
                    CCGPHaiNanSpiderLauncher.start();
                    break;
                case "history-ccgphn":
                    CCGPHaiNanSpiderLauncher.fetchHistory();
                    break;
                case "start-ccgpsc":
                    ccgpSiChuanSpiderLauncher.start();
                    break;
                case "start-yibiao":
                    yiBiaoSpiderLauncher.start();
                    break;
                case "history-yibiao":
                    yiBiaoSpiderLauncher.fetchHistory();
                    break;
                case "history-parseUrl":
                    yiBiaoSpiderLauncher.beginScheduled();
                    break;
                case "start-suning":
                    suNingSpiderLauncher.start();
                    break;
                case "history-suning":
                    suNingSpiderLauncher.fetchHistory();
                    break;
                case "history-sgcc":
                    stateGridSpiderLauncher.fetchAll();
                    break;
                case "start-ggzysc":
                    ggzyscSpiderLauncher.start();
                    break;
                case "start-ggzycq":
                    ggzycqSpiderLauncher.start();
                    break;
                case "start-ggzyhn":
                    ggzyhnSpiderLauncher.start();
                    break;
                case "start-ggzygz":
                    ggzygzSpiderLauncher.start();
                    break;
                case "start-ccgpcq":
                    ccgpcqSpiderLauncher.start();
                    break;
                case "start-ggzyyn":
                    ggzyynSpiderLauncher.start();
                    break;
                case "start-cdjs":
                    chengDuConstructionSpiderLauncher.start();
                    break;
                case "start-ggzyxz":
                    ggzyxzSpiderLauncher.start();
                    break;
                case "start-ggzyhlj":
                    ggzyhljSpiderLauncher.start();
                    break;
                case "start-hbebtp":
                    hbebtpSpiderLauncher.start();
                    break;
                case "start-ggzyhebei":
                    ggzyHeBeiSpiderLauncher.start();
                    break;
                case "start-ggzyfujian":
                    ggzyFuJianSpiderLauncher.start();
                    break;
                case "start-ggzyjiangxi":
                    ggzyJiangXiSpiderLauncher.start();
                    break;
                case "start-ggzyshandong":
                    ggzyShanDongSpiderLauncher.start();
                    break;
                case "start-ggzyshaanxi":
                    ggzyShaanXiSpiderLauncher.start();
                    break;
                default:
                    break;
            }
        }
    }

}
