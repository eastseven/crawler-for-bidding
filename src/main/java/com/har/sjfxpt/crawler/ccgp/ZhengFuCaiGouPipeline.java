package com.har.sjfxpt.crawler.ccgp;

import com.har.sjfxpt.crawler.ggzy.model.DataItemDTO;
import com.har.sjfxpt.crawler.ggzy.service.DataItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;
import java.util.stream.Collectors;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

@Slf4j
@Component
public class ZhengFuCaiGouPipeline implements Pipeline {

    @Autowired
    ZhengFuCaiGouRepository repository;

    @Autowired
    DataItemService dataItemService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<ZhengFuCaiGouDataItem> dataItemList = resultItems.get(KEY_DATA_ITEMS);
        if (CollectionUtils.isEmpty(dataItemList)) {
            log.warn("ccgp save nothing, {}", task.getSite());
        } else {
            repository.save(dataItemList);
            log.info("ccgp save {} to mongodb", dataItemList.size());

            List<DataItemDTO> dtoList = dataItemList.stream().map(dataItem -> dataItem.dto()).collect(Collectors.toList());
            dataItemService.save2BidNewsOriginalTable(dtoList);
        }
    }
}
