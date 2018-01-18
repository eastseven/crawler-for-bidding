package com.har.sjfxpt.crawler.core.service;

import com.har.sjfxpt.crawler.core.model.GongGongZiYuanPageData;
import com.har.sjfxpt.crawler.core.repository.GongGongZiYuanPageDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PageDataService {

    @Autowired
    GongGongZiYuanPageDataRepository pageDataRepository;

    public void save(String date, int size, int page, String url) {
        GongGongZiYuanPageData pageData = new GongGongZiYuanPageData();
        pageData.setDate(date);
        pageData.setSize(size);
        pageData.setPage(page);
        pageData.setUrl(url);

        pageDataRepository.save(pageData);
    }
}
