package com.har.sjfxpt.crawler.ggzy.downloader;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

@Slf4j
public class HttpClientDownloaderExt extends HttpClientDownloader {

    @Override
    public Page download(Request request, Task task) {
        Page page = super.download(request, task);
        log.debug("{}", page.getHtml().toString());
        return page;
    }

    @Override
    protected void onSuccess(Request request) {
        log.info("onSuccess {}", request);
        super.onSuccess(request);
    }

    @Override
    protected void onError(Request request) {
        log.error("{}", request);
        super.onError(request);
    }
}
