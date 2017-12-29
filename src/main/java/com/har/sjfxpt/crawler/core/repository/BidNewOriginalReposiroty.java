package com.har.sjfxpt.crawler.core.repository;

import com.har.sjfxpt.crawler.core.model.DataItemDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author dongqi
 */
public interface BidNewOriginalReposiroty extends MongoRepository<DataItemDTO, String> {
}
