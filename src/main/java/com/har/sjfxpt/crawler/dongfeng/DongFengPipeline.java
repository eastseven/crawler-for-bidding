package com.har.sjfxpt.crawler.dongfeng;

import com.har.sjfxpt.crawler.ggzy.model.DataItemDTO;
import com.har.sjfxpt.crawler.ggzy.service.DataItemService;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzycq.GGZYCQDataItem;
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
 * Created by Administrator on 2017/12/21.
 */
@Slf4j
@Component
public class DongFengPipeline implements Pipeline {

    @Autowired
    DongFengDataItemRepository dongFengDataItemRepository;

    @Autowired
    DataItemService dataItemService;


    @Override
    public void process(ResultItems resultItems, Task task) {
        List<DongFengDataItem> dataItemList = resultItems.get("dataItemList");
        if (CollectionUtils.isEmpty(dataItemList)) {
            log.warn("DongFeng save nothing,{}", task.getSite());
        } else {
            dongFengDataItemRepository.save(dataItemList);
            log.info("DongFeng save {} to mongodb", dataItemList.size());

            List<DataItemDTO> dtoList = dataItemList.stream().map(dataItem -> dataItem.dto()).collect(Collectors.toList());
            dataItemService.save2BidNewsOriginalTable(dtoList);
        }
    }
}