package com.har.sjfxpt.crawler.core.repository;

import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author dongqi
 */
public interface BidNewsOriginalRepository extends MongoRepository<BidNewsOriginal, String> {
}
