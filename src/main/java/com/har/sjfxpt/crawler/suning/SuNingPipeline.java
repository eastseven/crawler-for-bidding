package com.har.sjfxpt.crawler.suning;

import com.har.sjfxpt.crawler.ggzy.model.DataItemDTO;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import com.har.sjfxpt.crawler.ggzy.service.DataItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;
import java.util.stream.Collectors;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/11/13.
 */
@Slf4j
@Component
public class SuNingPipeline implements Pipeline{

    @Autowired
    SuNingDataItemRepository suNingDataItemRepository;

    @Autowired
    DataItemService dataItemService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<SuNingDataItem> dataItemList = resultItems.get(KEY_DATA_ITEMS);
        if (org.springframework.util.CollectionUtils.isEmpty(dataItemList)) {
            log.warn("{} save nothing", SourceCode.SUNING);
        } else {
            suNingDataItemRepository.save(dataItemList);
            log.info("{} save {} to mongodb", SourceCode.SUNING, dataItemList.size());

            List<DataItemDTO> dtoList = dataItemList.stream().map(dataItem -> dataItem.dto()).collect(Collectors.toList());
            dataItemService.save2BidNewsOriginalTable(dtoList);
        }
    }
}