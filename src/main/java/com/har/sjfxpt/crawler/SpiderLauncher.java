package com.har.sjfxpt.crawler;

import com.har.sjfxpt.crawler.chinamobile.ChinaMobileSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.GongGongZiYuanSpiderLauncher;
import com.har.sjfxpt.crawler.jcw.JinCaiWangSpiderLauncher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Arrays;

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
    JinCaiWangSpiderLauncher jinCaiWangSpiderLauncher;

    @Override
    public void run(String... args) throws Exception {
        log.info("{}", Arrays.toString(args));
        for (String arg : args) {
            switch (arg) {
                case "start-all":
                    gongGongZiYuanSpiderLauncher.start();
                    chinaMobileSpiderLauncher.start();
                    break;
                case "start-ggzy":
                    gongGongZiYuanSpiderLauncher.start();
                    break;
                case "history-all":
                    chinaMobileSpiderLauncher.fetchHistory();
                    gongGongZiYuanSpiderLauncher.fetchHistory();
                    break;
                case "history-ggzy":
                    log.info(">>> start {}", arg);
                    gongGongZiYuanSpiderLauncher.fetchHistory();
                    break;
                case "start-cm":
                    chinaMobileSpiderLauncher.start();
                    break;
                case "history-cm":
                    chinaMobileSpiderLauncher.fetchHistory();
                    break;
                case "start-jcw":
                    jinCaiWangSpiderLauncher.start();
                    break;
                default:
                    break;
            }
        }
    }
}
