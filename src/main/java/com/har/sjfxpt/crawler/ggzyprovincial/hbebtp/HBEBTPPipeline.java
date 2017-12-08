package com.har.sjfxpt.crawler.ggzyprovincial.hbebtp;

import com.har.sjfxpt.crawler.ggzy.service.DataItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

/**
 * Created by Administrator on 2017/12/8.
 */
@Slf4j
@Component
public class HBEBTPPipeline implements Pipeline {

    @Autowired
    HBEBTPDataItemRepository hbebtpDataItemRepository;

    @Autowired
    DataItemService dataItemService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<HBEBTPDataItem> dataItemList = resultItems.get("dataItemList");
        if (CollectionUtils.isEmpty(dataItemList)) {
            log.warn("HBEBTP save nothing,{}", task.getSite());
        } else {
            hbebtpDataItemRepository.save(dataItemList);
            log.info("HBEBTP save {} to mongodb", dataItemList.size());

//            List<DataItemDTO> dtoList = dataItemList.stream().map(dataItem -> dataItem.dto()).collect(Collectors.toList());
//            dataItemService.save2BidNewsOriginalTable(dtoList);
        }
    }
}
