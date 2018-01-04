package com.har.sjfxpt.crawler.ccgp;

import org.springframework.data.mongodb.repository.MongoRepository;
@Deprecated
public interface PageDataRepository extends MongoRepository<PageData, String> {

    PageData findFirstByDate();
}
