package com.har.sjfxpt.crawler.core.pipeline;

import com.har.sjfxpt.crawler.core.repository.BidNewOriginalReposiroty;
import com.har.sjfxpt.crawler.core.service.DataItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * @author dongqi
 * 通用存储
 */
@Slf4j
@Component
public class DataItemDtoPipeline implements Pipeline {

    @Autowired
    DataItemService dataItemService;

    @Autowired
    ApplicationContext ctx;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List dataItemList = resultItems.get(KEY_DATA_ITEMS);
        if (CollectionUtils.isEmpty(dataItemList)) {
            log.warn(">>> save nothing, {}", task.getSite());
        } else {
            ctx.getBean(BidNewOriginalReposiroty.class).save(dataItemList);
            dataItemService.save2BidNewsOriginalTable(dataItemList);
        }
    }
}
