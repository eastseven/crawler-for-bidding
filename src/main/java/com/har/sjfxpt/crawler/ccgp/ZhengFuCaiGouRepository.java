package com.har.sjfxpt.crawler.ccgp;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ZhengFuCaiGouRepository extends MongoRepository<ZhengFuCaiGouDataItem, String> {

    ZhengFuCaiGouDataItem findByUrl(String url);
}
