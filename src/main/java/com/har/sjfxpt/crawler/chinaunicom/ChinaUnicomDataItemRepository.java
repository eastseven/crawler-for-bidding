package com.har.sjfxpt.crawler.chinaunicom;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Administrator on 2017/12/27.
 */
@Deprecated
public interface ChinaUnicomDataItemRepository extends MongoRepository<ChinaUnicomDataItem, String> {
}
