package com.har.sjfxpt.crawler.core.pipeline;

import com.har.sjfxpt.crawler.core.repository.BidNewsOriginalRepository;
import com.har.sjfxpt.crawler.core.service.HBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

import static com.har.sjfxpt.crawler.core.processor.BasePageProcessor.KEY_DATA_ITEMS;

/**
 * @author dongqi
 *         通用存储
 */
@Slf4j
@Component
public class HBasePipeline implements Pipeline {

    @Autowired
    HBaseService HBaseService;

    @Autowired
    ApplicationContext ctx;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List dataItemList = resultItems.get(KEY_DATA_ITEMS);
        if (CollectionUtils.isEmpty(dataItemList)) {
            log.warn(">>> save nothing, {}", task.getSite());
        } else {
            // 只是为了快速查看数据，实际数据还是以HBase中的为准
            ctx.getBean(BidNewsOriginalRepository.class).save(dataItemList);

            HBaseService.saveBidNewsOriginals(dataItemList);
        }
    }

}
