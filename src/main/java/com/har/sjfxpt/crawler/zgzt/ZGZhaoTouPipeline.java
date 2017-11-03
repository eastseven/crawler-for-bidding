package com.har.sjfxpt.crawler.zgzt;

import com.har.sjfxpt.crawler.ggzy.model.DataItemDTO;
import com.har.sjfxpt.crawler.ggzy.service.DataItemService;
import com.har.sjfxpt.crawler.petrochina.ZGShiYouDataItem;
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
 * Created by Administrator on 2017/11/2.
 */
@Slf4j
@Component
public class ZGZhaoTouPipeline implements Pipeline{

    @Autowired
    ZGZhaoTouDataItemRepository zgZhaoTouDataItemRepository;

    @Autowired
    DataItemService dataItemService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<ZGZhaoTouDataItem> dataItemList=resultItems.get(KEY_DATA_ITEMS);
        if(org.springframework.util.CollectionUtils.isEmpty(dataItemList)){
            log.warn("zhongguozhaotou save nothing,{}",task.getSite());
        }else {
            zgZhaoTouDataItemRepository.save(dataItemList);
            log.info("zhongguozhaotou save {} to mongodb",dataItemList.size());
//
////            List<DataItemDTO> dtoList = dataItemList.stream().map(dataItem -> dataItem.dto()).collect(Collectors.toList());
////            dataItemService.save2BidNewsOriginalTable(dtoList);
        }
    }
}
