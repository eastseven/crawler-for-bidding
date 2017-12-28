package com.har.sjfxpt.crawler.yibiao;

import com.har.sjfxpt.crawler.core.model.DataItemDTO;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.service.DataItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;
import java.util.stream.Collectors;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/11/9.
 */
@Slf4j
@Component
public class YiBiaoPipeline implements Pipeline {

    @Autowired
    YiBiaoDataItemRepository yiBiaoDataItemRepository;

    @Autowired
    DataItemService dataItemService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<YiBiaoDataItem> dataItemList = resultItems.get(KEY_DATA_ITEMS);
        if (org.springframework.util.CollectionUtils.isEmpty(dataItemList)) {
            log.warn("{} save nothing", SourceCode.YIBIAO);
        } else {
            yiBiaoDataItemRepository.save(dataItemList);
            log.info("{} save {} to mongodb", SourceCode.YIBIAO, dataItemList.size());

            List<DataItemDTO> dtoList = dataItemList.stream().map(dataItem -> dataItem.dto()).collect(Collectors.toList());
            dataItemService.save2BidNewsOriginalTable(dtoList);
        }
    }
}
