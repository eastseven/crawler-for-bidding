package com.har.sjfxpt.crawler.sgcc;

import com.har.sjfxpt.crawler.core.model.DataItemDTO;
import com.har.sjfxpt.crawler.core.model.SourceCode;
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

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * @author dongqi
 */
@Slf4j
@Component
public class StateGridPipeline implements Pipeline {

    @Autowired StateGridDataItemRepository dataItemRepository;

    @Autowired DataItemService dataItemService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<StateGridDataItem> dataItemList = resultItems.get(KEY_DATA_ITEMS);
        if (CollectionUtils.isEmpty(dataItemList)) {
            log.warn("{} save nothing, {}", SourceCode.SGCC, task.getSite());
        } else {
            dataItemRepository.save(dataItemList);
            log.info("{} save {} to mongodb", SourceCode.SGCC, dataItemList.size());

            List<DataItemDTO> dtoList = dataItemList.stream().map(dataItem -> dataItem.dto()).collect(Collectors.toList());
            dataItemService.save2BidNewsOriginalTable(dtoList);
        }
    }
}
