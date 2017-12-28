package com.har.sjfxpt.crawler.core.repository;

import com.har.sjfxpt.crawler.core.model.DataItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author dongqi
 */
public interface DataItemRepository extends MongoRepository<DataItem, String> {

    Page<DataItem> findByTextContentIsNull(Pageable pageable);

    Page<DataItem> findByHtmlIsNull(Pageable pageable);

    DataItem findTopByHtmlIsNull();
}
