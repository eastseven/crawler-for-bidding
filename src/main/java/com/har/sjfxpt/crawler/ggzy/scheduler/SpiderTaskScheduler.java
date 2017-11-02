package com.har.sjfxpt.crawler.ggzy.scheduler;

import com.har.sjfxpt.crawler.ccgp.ZhengFuCaiGouSpiderLauncher;
import com.har.sjfxpt.crawler.chinamobile.ChinaMobileSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.GongGongZiYuanSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import com.har.sjfxpt.crawler.jcw.JinCaiWangSpiderLauncher;
import com.har.sjfxpt.crawler.petrochina.ZGShiYouSpiderLauncher;
import com.har.sjfxpt.crawler.zgyj.ZGYeJinSpiderLauncher;
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
    public void fetchGGZY() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.GGZY);
            context.getBean(GongGongZiYuanSpiderLauncher.class).start();
        }
    }

    /**
     * 启动后20秒执行，10分钟一次
     */
    @Scheduled(initialDelay = 20000, fixedRate = 10 * 60 * 1000)
    public void fetchCM() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.CM);
            context.getBean(ChinaMobileSpiderLauncher.class).start();
        }
    }

    /**
     * 由于金采网每日更新数据很少，所以抓取频率按小时规划，分别在9点，12点，17点执行一次
     */
    @Scheduled(cron = "0 0 9,12,17 * * *")
    public void fetchJinCaiWang() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.JC);
            context.getBean(JinCaiWangSpiderLauncher.class).start();
        }
    }

    @Scheduled(initialDelay = 20000, fixedRate = 15 * 60 * 1000)
    public void fetchCCGP() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.CCGP);
            context.getBean(ZhengFuCaiGouSpiderLauncher.class).start();
        }
    }

    /**
     * 中国石油
     */
    @Scheduled(initialDelay = 21000, fixedRate = 20 * 60 * 1000)
    public void fetchZGSY() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.ZSY);
            context.getBean(ZGShiYouSpiderLauncher.class).start();
        }
    }

    /**
     * 中国冶金科工
     */
    @Scheduled(initialDelay = 22000, fixedRate = 60 * 60 * 1000)
    public void fetchZGYJ() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.ZGYJ);
            context.getBean(ZGYeJinSpiderLauncher.class).start();
        }
    }
}
