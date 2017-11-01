package com.har.sjfxpt.crawler.ggzy.scheduler;

import com.har.sjfxpt.crawler.ccgp.ZhengFuCaiGouSpiderLauncher;
import com.har.sjfxpt.crawler.chinamobile.ChinaMobileSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.GongGongZiYuanSpiderLauncher;
import com.har.sjfxpt.crawler.jcw.JinCaiWangSpiderLauncher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author dongqi
 */
@Slf4j
@Component
@Profile({"prod"})
public class SpiderTaskScheduler {

    @Value("${app.fetch.current.day:false}")
    boolean flag;

    @Autowired
    ApplicationContext context;

    /**
     * 启动后10秒执行，5分钟一次
     */
    @Scheduled(initialDelay = 10000, fixedRate = 5 * 60 * 1000)
    public void fetchCurrentDay() {
        if (flag) {
            log.info(">>> start fetch gong gong zi yuan");
            context.getBean(GongGongZiYuanSpiderLauncher.class).start();
        }
    }

    /**
     * 启动后20秒执行，10分钟一次
     */
    @Scheduled(initialDelay = 20000, fixedRate = 10 * 60 * 1000)
    public void fetchCurrentDay4CM() {
        if (flag) {
            log.info(">>> start fetch china mobile");
            context.getBean(ChinaMobileSpiderLauncher.class).start();
        }
    }

    /**
     * 由于金采网每日更新数据很少，所以抓取频率按小时规划，分别在9点，12点，17点执行一次
     */
    @Scheduled(cron = "0 0 9,12,17 * * *")
    public void fetchJinCaiWang() {
        if (flag) {
            log.info(">>> start fetch jin cai wang");
            context.getBean(JinCaiWangSpiderLauncher.class).start();
        }
    }

    @Scheduled(initialDelay = 20000, fixedRate = 15 * 60 * 1000)
    public void fetchCCGP() {
        if (flag) {
            log.info(">>> start fetch ccgp");
            context.getBean(ZhengFuCaiGouSpiderLauncher.class).start();
        }
    }


}
