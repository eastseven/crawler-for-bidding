package com.har.sjfxpt.crawler.ggzy.pipeline;

import com.har.sjfxpt.crawler.ggzy.model.DataItem;
import com.har.sjfxpt.crawler.ggzy.service.DataItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * @author dongqi
 */
@Slf4j
@Component
public class HBasePipeline implements Pipeline {

    @Autowired
    DataItemService dataItemService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<DataItem> dataItemList = resultItems.get(KEY_DATA_ITEMS);
        dataItemService.save(dataItemList);

        dataItemService.save2BidNewsOriginalTable(dataItemList);
    }

}
