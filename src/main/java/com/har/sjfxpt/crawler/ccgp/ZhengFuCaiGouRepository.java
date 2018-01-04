package com.har.sjfxpt.crawler.ccgp;

import org.springframework.data.mongodb.repository.MongoRepository;
@Deprecated
public interface ZhengFuCaiGouRepository extends MongoRepository<ZhengFuCaiGouDataItem, String> {

    ZhengFuCaiGouDataItem findByUrl(String url);
}
