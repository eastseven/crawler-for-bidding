package com.har.sjfxpt.crawler.ggzy.pipeline;

import com.har.sjfxpt.crawler.ggzy.model.DataItem;
import com.har.sjfxpt.crawler.ggzy.repository.DataItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

/**
 * @author dongqi
 * 全国公共资源 mongodb 存储
 */
@Slf4j
@Component
public class GongGongZiYuanMongoPipeline implements Pipeline {

    @Autowired
    DataItemRepository repository;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<DataItem> dataItemList = resultItems.get("dataItemList");
        if (CollectionUtils.isNotEmpty(dataItemList)) {
            List<DataItem> list = Lists.newArrayList();
            for (DataItem dataItem : dataItemList) {
                if (!repository.exists(dataItem.getId())) {
                    list.add(dataItem);
                }
            }

            if (list.isEmpty()) {
                return;
            }
            repository.save(list);
            log.info("{} save {} data", task.getUUID(), list.size());
        }
    }
}
