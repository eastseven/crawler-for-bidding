package com.har.sjfxpt.crawler;

import com.har.sjfxpt.crawler.chinamobile.ChinaMobileSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.GongGongZiYuanSpiderLauncher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

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

    @Override
    public void run(String... args) throws Exception {
        for (String arg : args) {
            switch (arg) {
                case "start":
                    gongGongZiYuanSpiderLauncher.start();
                    break;
                case "history":
                    gongGongZiYuanSpiderLauncher.fetchHistory();
                    break;
                case "start-cm":
                    chinaMobileSpiderLauncher.start();
                    break;
                case "history-cm":
                    chinaMobileSpiderLauncher.fetchHistory();
                    break;
                default:
                    break;
            }
        }
    }
}
