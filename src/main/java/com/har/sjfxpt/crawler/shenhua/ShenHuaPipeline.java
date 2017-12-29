package com.har.sjfxpt.crawler.shenhua;

import com.har.sjfxpt.crawler.core.model.DataItemDTO;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.service.HBaseService;
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
 * Created by Administrator on 2017/12/22.
 */
@Slf4j
@Component
public class ShenHuaPipeline implements Pipeline {

    @Autowired
    ShenHuaDataItemRepository shenHuaDataItemRepository;

    @Autowired
    HBaseService HBaseService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<ShenHuaDataItem> dataItemList = resultItems.get(KEY_DATA_ITEMS);
        if (org.springframework.util.CollectionUtils.isEmpty(dataItemList)) {
            log.warn("{} save nothing", SourceCode.SHENHUA);
        } else {
            shenHuaDataItemRepository.save(dataItemList);
            log.info("{} save {} to mongodb", SourceCode.SHENHUA, dataItemList.size());

            List<DataItemDTO> dtoList = dataItemList.stream().map(dataItem -> dataItem.dto()).collect(Collectors.toList());
            HBaseService.save2BidNewsOriginalTable(dtoList);
        }
    }
}
