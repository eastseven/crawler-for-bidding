package com.har.sjfxpt.crawler.ggzyprovincial.ggzyjiangxi;

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
 * Created by Administrator on 2017/12/14.
 */
@Slf4j
@Component
public class GGZYJiangXiPipeline implements Pipeline {

    @Autowired
    GGZYJiangXiDataItemRepository ggzyJiangXiDataItemRepository;

    @Autowired
    HBaseService HBaseService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<GGZYJiangXiDataItem> dataItemList = resultItems.get("dataItemList");
        if (CollectionUtils.isEmpty(dataItemList)) {
            log.warn("ggzyJiangXi save nothing,{}", task.getSite());
        } else {
            ggzyJiangXiDataItemRepository.save(dataItemList);
            log.info("ggzyJiangXi save {} to mongodb", dataItemList.size());

            List<DataItemDTO> dtoList = dataItemList.stream().map(dataItem -> dataItem.dto()).collect(Collectors.toList());
            HBaseService.save2BidNewsOriginalTable(dtoList);
        }
    }
}
