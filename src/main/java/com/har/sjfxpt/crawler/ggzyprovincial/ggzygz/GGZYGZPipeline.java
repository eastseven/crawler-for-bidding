package com.har.sjfxpt.crawler.ggzyprovincial.ggzygz;

import com.har.sjfxpt.crawler.core.model.DataItemDTO;
import com.har.sjfxpt.crawler.core.service.HBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/11/30.
 */
@Slf4j
@Component
public class GGZYGZPipeline implements Pipeline {

    @Autowired
    GGZYGZDataItemRepository GGZYGZDataItemRepository;

    @Autowired
    HBaseService HBaseService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<GGZYGZDataItem> dataItemList = resultItems.get("dataItemList");
        if (CollectionUtils.isEmpty(dataItemList)) {
            log.warn("ggzyGZ save nothing,{}", task.getSite());
        } else {
            GGZYGZDataItemRepository.save(dataItemList);
            log.info("ggzyGZ save {} to mongodb", dataItemList.size());

            List<DataItemDTO> dtoList = dataItemList.stream().map(dataItem -> dataItem.dto()).collect(Collectors.toList());
            HBaseService.save2BidNewsOriginalTable(dtoList);
        }
    }
}
