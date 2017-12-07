package com.har.sjfxpt.crawler.ggzy.scheduler;

import com.har.sjfxpt.crawler.ccgp.ZhengFuCaiGouSpiderLauncher;
import com.har.sjfxpt.crawler.ccgp.ccgpcq.CCGPCQSpiderLauncher;
import com.har.sjfxpt.crawler.ccgp.ccgphn.CCGPHaiNanSpiderLauncher;
import com.har.sjfxpt.crawler.ccgp.ccgpsc.CCGPSiChuanSpiderLauncher;
import com.har.sjfxpt.crawler.chengduconstruction.ChengDuConstructionSpiderLauncher;
import com.har.sjfxpt.crawler.chinamobile.ChinaMobileSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.GongGongZiYuanSpiderLauncher;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzycq.GGZYCQSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzygz.GGZYGZSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhlj.GGZYHLJSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhn.GGZYHNSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzysc.GGZYSCSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyxz.GGZYXZSpiderLauncher;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyyn.GGZYYNSpiderLauncher;
import com.har.sjfxpt.crawler.jcw.JinCaiWangSpiderLauncher;
import com.har.sjfxpt.crawler.petrochina.ZGShiYouSpiderLauncher;
import com.har.sjfxpt.crawler.sgcc.StateGridSpiderLauncher;
import com.har.sjfxpt.crawler.suning.SuNingSpiderLauncher;
import com.har.sjfxpt.crawler.zgyj.ZGYeJinSpiderLauncher;
import com.har.sjfxpt.crawler.zgzt.ChinaTenderingAndBiddingLauncher;
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
@Profile({"test", "prod"})
public class SpiderTaskScheduler {

    @Value("${app.fetch.current.day:false}")
    boolean flag;

    @Autowired
    ApplicationContext context;

    /**
     * 启动后10秒执行，5分钟一次
     * 公共资源
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

    /**
     * 中国政府采购网
     */
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

    /**
     * 中国招投标
     */
    @Scheduled(initialDelay = 22001, fixedRate = 25 * 60 * 1000)
    public void fetchZGZT() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.ZGZT);
            context.getBean(ChinaTenderingAndBiddingLauncher.class).start();
        }
    }

    /**
     * 海南政府采购网
     */
    @Scheduled(initialDelay = 23000, fixedRate = 23 * 60 * 1000)
    public void fetchCCGP4HaiNan() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.CCGPHN);
            context.getBean(CCGPHaiNanSpiderLauncher.class).start();
        }
    }

    /**
     * 四川政府采购网
     */
    @Scheduled(initialDelay = 24000, fixedRate = 60 * 60 * 1000)
    public void fetchCCGP4SiChuan() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.CCGPSC);
            context.getBean(CCGPSiChuanSpiderLauncher.class).start();
        }
    }

    /**
     * 重庆政府采购网
     */
    @Scheduled(initialDelay = 23000, fixedRate = 30 * 60 * 1000)
    public void fetchCCGPCQ() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.CCGPCQ);
            context.getBean(CCGPCQSpiderLauncher.class).start();
        }
    }

    /**
     * 苏宁招标
     */
    @Scheduled(initialDelay = 24000, fixedRate = 6 * 60 * 60 * 1000)
    public void fetchSuNing() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.SUNING);
            context.getBean(SuNingSpiderLauncher.class).start();
        }
    }

    /**
     * 四川公共资源
     */
    @Scheduled(initialDelay = 22000, fixedRate = 25 * 60 * 1000)
    public void fetchGGZYSC() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.GGZYSC);
            context.getBean(GGZYSCSpiderLauncher.class).start();
        }
    }

    /**
     * 重庆公共资源
     */
    @Scheduled(initialDelay = 22000, fixedRate = 30 * 60 * 1000)
    public void fetchGGZYCQ() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.GGZYCQ);
            context.getBean(GGZYCQSpiderLauncher.class).start();
        }
    }

    /**
     * 海南公共资源
     */
    @Scheduled(initialDelay = 22000, fixedRate = 30 * 60 * 1000)
    public void fetchGGZYHN() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.GGZYHN);
            context.getBean(GGZYHNSpiderLauncher.class).start();
        }
    }

    /**
     * 贵州公共资源
     */
    @Scheduled(initialDelay = 23000, fixedRate = 30 * 60 * 1000)
    public void fetchGGZYGZ() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.GGZYGZ);
            context.getBean(GGZYGZSpiderLauncher.class).start();
        }
    }


    /**
     * 云南公共资源
     */
    @Scheduled(initialDelay = 23000, fixedRate = 45 * 60 * 1000)
    public void fetchGGZYYN() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.GGZYYN);
            context.getBean(GGZYYNSpiderLauncher.class).start();
        }
    }


    /**
     * 国家电网
     * 每个小时抓一次
     */
    @Scheduled(initialDelay = 26000, fixedRate = 60 * 60 * 1000)
    public void fetchSGCC() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.SGCC);
            context.getBean(StateGridSpiderLauncher.class).start();
        }
    }

    /**
     * 西藏公共资源
     */
    @Scheduled(initialDelay = 25000, fixedRate = 6 * 60 * 60 * 1000)
    public void fetchGGZYXZ() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.GGZYXZ);
            context.getBean(GGZYXZSpiderLauncher.class).start();
        }
    }

    /**
     * 黑龙江公共资源
     */
    @Scheduled(initialDelay = 26000, fixedRate = 2 * 60 * 60 * 1000)
    public void fetchGGZYHLJ() {
        if (flag) {
            log.info(">>> start fetch {}", SourceCode.GGZYHLJ);
            context.getBean(GGZYHLJSpiderLauncher.class).start();
        }
    }
}
