package com.har.sjfxpt.crawler;

import com.har.sjfxpt.crawler.baowu.BaoWuSpiderLauncher;
import com.har.sjfxpt.crawler.ccgp.ZhengFuCaiGouSpiderLauncher;
import com.har.sjfxpt.crawler.ccgp.ccgpcq.CCGPCQSpiderLauncher;
import com.har.sjfxpt.crawler.ccgp.ccgphn.CCGPHaiNanSpiderLauncher;
import com.har.sjfxpt.crawler.ccgp.ccgpsc.CCGPSiChuanSpiderLauncher;
import com.har.sjfxpt.crawler.chinamobile.ChinaMobileSpiderLauncher;
import com.har.sjfxpt.crawler.chinaunicom.ChinaUnicomSpiderLauncher;
import com.har.sjfxpt.crawler.dongfeng.DongFengSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzycq.GGZYCQSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyfujian.GGZYFuJianSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzygansu.GGZYGanSuSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzygz.GGZYGZSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebeinew.GGZYHeBeiSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhlj.GGZYHLJSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhn.GGZYHNSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyjiangxi.GGZYJiangXiSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyningxia.GGZYNingXiaSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzysc.GGZYSCSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyshaanxi.GGZYShaanXiSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyshandong.GGZYShanDongSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyxz.GGZYXZSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyyn.GGZYYNSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.hbebtp.HBEBTPSpiderLauncher;
import com.har.sjfxpt.crawler.jcw.JinCaiWangSpiderLauncher;
import com.har.sjfxpt.crawler.petrochina.ZGShiYouSpiderLauncher;
import com.har.sjfxpt.crawler.suning.SuNingSpiderLauncher;
import com.har.sjfxpt.crawler.zgzt.ChinaTenderingAndBiddingLauncher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author dongqi
 */
@Slf4j
@Service
@Profile("prod")
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@Deprecated
public class SpiderLauncher implements CommandLineRunner {

    @Autowired
    ApplicationContext context;

    @Autowired
    ChinaMobileSpiderLauncher chinaMobileSpiderLauncher;

    @Autowired
    ZhengFuCaiGouSpiderLauncher zhengFuCaiGouSpiderLauncher;

    @Autowired
    JinCaiWangSpiderLauncher jinCaiWangSpiderLauncher;

    @Autowired
    ZGShiYouSpiderLauncher zhongGuoShiYouSpiderLauncher;

    @Autowired
    ChinaTenderingAndBiddingLauncher chinaTenderingAndBiddingLauncher;

    @Autowired
    CCGPHaiNanSpiderLauncher CCGPHaiNanSpiderLauncher;

    @Autowired
    CCGPSiChuanSpiderLauncher ccgpSiChuanSpiderLauncher;

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
    GGZYXZSpiderLauncher ggzyxzSpiderLauncher;

    @Autowired
    GGZYHLJSpiderLauncher ggzyhljSpiderLauncher;

    @Autowired
    HBEBTPSpiderLauncher hbebtpSpiderLauncher;

    @Autowired
    GGZYFuJianSpiderLauncher ggzyFuJianSpiderLauncher;

    @Autowired
    GGZYJiangXiSpiderLauncher ggzyJiangXiSpiderLauncher;

    @Autowired
    GGZYShanDongSpiderLauncher ggzyShanDongSpiderLauncher;

    @Autowired
    GGZYShaanXiSpiderLauncher ggzyShaanXiSpiderLauncher;

    @Autowired
    GGZYGanSuSpiderLauncher ggzyGanSuSpiderLauncher;

    @Autowired
    GGZYNingXiaSpiderLauncher ggzyNingXiaSpiderLauncher;


    @Autowired
    DongFengSpiderLauncher dongFengSpiderLauncher;

    @Autowired
    BaoWuSpiderLauncher baoWuSpiderLauncher;

    @Autowired
    GGZYHeBeiSpiderLauncher ggzyHeBeiSpiderLauncher;

    @Autowired
    ChinaUnicomSpiderLauncher chinaUnicomSpiderLauncher;

    public void info() {
        chinaMobileSpiderLauncher.printInfo();
        zhengFuCaiGouSpiderLauncher.printInfo();
        jinCaiWangSpiderLauncher.printInfo();
        zhongGuoShiYouSpiderLauncher.printInfo();
        chinaTenderingAndBiddingLauncher.printInfo();
        CCGPHaiNanSpiderLauncher.printInfo();
        ccgpSiChuanSpiderLauncher.printInfo();
        suNingSpiderLauncher.printInfo();
        ggzyscSpiderLauncher.printInfo();
        ggzycqSpiderLauncher.printInfo();
        ggzyhnSpiderLauncher.printInfo();
        ggzygzSpiderLauncher.printInfo();
        ccgpcqSpiderLauncher.printInfo();
        ggzyynSpiderLauncher.printInfo();
        ggzyxzSpiderLauncher.printInfo();
        ggzyhljSpiderLauncher.printInfo();
        hbebtpSpiderLauncher.printInfo();
        ggzyFuJianSpiderLauncher.printInfo();
        ggzyJiangXiSpiderLauncher.printInfo();
        ggzyShanDongSpiderLauncher.printInfo();
        ggzyShaanXiSpiderLauncher.printInfo();
        ggzyGanSuSpiderLauncher.printInfo();
        ggzyNingXiaSpiderLauncher.printInfo();
        dongFengSpiderLauncher.printInfo();
        baoWuSpiderLauncher.printInfo();
        ggzyHeBeiSpiderLauncher.printInfo();
        chinaUnicomSpiderLauncher.printInfo();
    }

    @Override
    public void run(String... args) {
        log.info(">>> SpiderLauncher {}", Arrays.toString(args));
        for (String arg : args) {
            switch (arg) {
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
                case "page-ccgp":
                    zhengFuCaiGouSpiderLauncher.countPageData().start();
                    break;
                case "start-jcw":
                    jinCaiWangSpiderLauncher.start();
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
                case "start-suning":
                    suNingSpiderLauncher.start();
                    break;
                case "history-suning":
                    suNingSpiderLauncher.fetchHistory();
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
                case "start-ggzyxz":
                    ggzyxzSpiderLauncher.start();
                    break;
                case "start-ggzyhlj":
                    ggzyhljSpiderLauncher.start();
                    break;
                case "start-hbebtp":
                    hbebtpSpiderLauncher.start();
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
                case "start-ggzygansu":
                    ggzyGanSuSpiderLauncher.start();
                    break;
                case "start-ggzyningxia":
                    ggzyNingXiaSpiderLauncher.start();
                    break;
                case "start-dongfeng":
                    dongFengSpiderLauncher.start();
                    break;
                case "start-baowu":
                    baoWuSpiderLauncher.start();
                    break;
                case "start-hebei":
                    ggzyHeBeiSpiderLauncher.start();
                    break;
                case "start-chinaunicom":
                    chinaUnicomSpiderLauncher.start();
                    break;
                default:
                    break;
            }
        }
    }

}

