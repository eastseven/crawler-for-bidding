package com.har.sjfxpt.crawler.core.annotation;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author dongqi
 */
public interface SourceConfigModelRepository extends MongoRepository<SourceConfigModel, String> {
}
