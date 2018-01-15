package com.har.sjfxpt.crawler.core.repository;

import com.har.sjfxpt.crawler.core.model.SpiderLog;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Administrator on 2018/1/15.
 */
public interface SpiderLogRepository extends MongoRepository<SpiderLog, String> {
}
