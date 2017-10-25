package com.har.sjfxpt.crawler.jcw;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

/**
 * Created by Administrator on 2017/10/25.
 */
@Slf4j
@Component
public class JinCaiWangPipeline implements Pipeline{

    @Autowired
    JinCaiWangDataItemRepository jinCaiWangDataItemRepository;
    @Override
    public void process(ResultItems resultItems, Task task) {
        List<JinCaiWangDataItem> dataItemList=resultItems.get("dataItemList");
        if(CollectionUtils.isEmpty(dataItemList)){
            log.warn("jincaiwang save nothing,{}",task.getSite());
        }else {
            jinCaiWangDataItemRepository.save(dataItemList);
            log.info("jincaiwang save {} to mongodb",dataItemList.size());
        }
    }
}
