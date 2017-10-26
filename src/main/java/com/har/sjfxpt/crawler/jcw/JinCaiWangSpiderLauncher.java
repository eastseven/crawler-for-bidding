package com.har.sjfxpt.crawler.jcw;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 2017/10/26.
 */
public class JinCaiWangSpiderLauncher {

    @Autowired
    JinCaiWangPageProcessor jinCaiWangPageProcessor;

    @Autowired
    JinCaiWangPipeline jinCaiWangPipeline;

    public void start(){

    }
}
