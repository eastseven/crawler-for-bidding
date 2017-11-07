package com.har.sjfxpt.crawler;

import com.har.sjfxpt.crawler.ccgp.ZhengFuCaiGouSpiderLauncher;
import com.har.sjfxpt.crawler.chinamobile.ChinaMobileSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.GongGongZiYuanSpiderLauncher;
import com.har.sjfxpt.crawler.jcw.JinCaiWangSpiderLauncher;
import com.har.sjfxpt.crawler.petrochina.ZGShiYouSpiderLauncher;
import com.har.sjfxpt.crawler.zgyj.ZGYeJinSpiderLauncher;
import com.har.sjfxpt.crawler.zgzt.ChinaTenderingAndBiddingLanucher;
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
    ChinaTenderingAndBiddingLanucher chinaTenderingAndBiddingLanucher;

    public void info() {
        gongGongZiYuanSpiderLauncher.printInfo();
        chinaMobileSpiderLauncher.printInfo();
        zhengFuCaiGouSpiderLauncher.printInfo();
        jinCaiWangSpiderLauncher.printInfo();
        zhongGuoShiYouSpiderLauncher.printInfo();
        zhongGuoYeJinSpiderLauncher.printInfo();
        chinaTenderingAndBiddingLanucher.printInfo();
    }

    @Autowired
    ZGYeJinSpiderLauncher zgYeJinSpiderLauncher;

    @Override
    public void run(String... args) throws Exception {
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
                    chinaMobileSpiderLauncher.fetchHistory();
                    break;
                case "start-ccgp":
                    zhengFuCaiGouSpiderLauncher.start();
                    break;
                case "history-ccgp":
                    zhengFuCaiGouSpiderLauncher.history();
                    break;
                case "page-ccgp":
                    zhengFuCaiGouSpiderLauncher.countPageData();
                    break;
                case "start-jcw":
                    jinCaiWangSpiderLauncher.start();
                    break;
                case "start-zgyj":
                    zgYeJinSpiderLauncher.start();
                    break;
                case "history-zgyj":
                    zgYeJinSpiderLauncher.fetchHistory();
                    break;
                case "start-zsy":
                    zhongGuoShiYouSpiderLauncher.start();
                    break;
                case "history-zsy":
                    zhongGuoShiYouSpiderLauncher.fetchHistory();
                    break;
                case "start-zgzt":
                    chinaTenderingAndBiddingLanucher.start();
                    break;
                default:
                    break;
            }
        }
    }

}
