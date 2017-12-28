package com.har.sjfxpt.crawler.ggzyprovincial.ggzyhn;

import com.har.sjfxpt.crawler.core.model.DataItemDTO;
import com.har.sjfxpt.crawler.core.service.DataItemService;
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
 * Created by Administrator on 2017/11/29.
 */
@Slf4j
@Component
public class GGZYHNPipeline implements Pipeline{

    @Autowired
    GGZYHNDataItemRepository GGZYHNDataItemRepository;

    @Autowired
    DataItemService dataItemService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<GGZYHNDataItem> dataItemList=resultItems.get("dataItemList");
        if(CollectionUtils.isEmpty(dataItemList)){
            log.warn("ggzyHN save nothing,{}",task.getSite());
        }else {
            GGZYHNDataItemRepository.save(dataItemList);
            log.info("ggzyHN save {} to mongodb",dataItemList.size());

            List<DataItemDTO> dtoList = dataItemList.stream().map(dataItem -> dataItem.dto()).collect(Collectors.toList());
            dataItemService.save2BidNewsOriginalTable(dtoList);
        }
    }
}
