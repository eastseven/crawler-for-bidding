package com.har.sjfxpt.crawler.ccgp.ccgpcq;

import com.har.sjfxpt.crawler.ccgp.ccgpsc.CCGPSiChuanDataItem;
import com.har.sjfxpt.crawler.ccgp.ccgpsc.CCGPSiChuanPageDataRepository;
import com.har.sjfxpt.crawler.ggzy.model.DataItemDTO;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import com.har.sjfxpt.crawler.ggzy.service.DataItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;
import java.util.stream.Collectors;

import static com.har.sjfxpt.crawler.ggzy.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/12/1.
 */
@Slf4j
@Component
public class CCGPCQPipeline implements Pipeline {
    @Autowired
    CCGPCQDataItemRepository ccgpcqDataItemRepository;

    @Autowired
    DataItemService dataItemService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<CCGPCQDataItem> dataItems=resultItems.get(KEY_DATA_ITEMS);
        if(CollectionUtils.isEmpty(dataItems)){
            log.warn("{} save nothing", SourceCode.CCGPCQ);
        }else {
            ccgpcqDataItemRepository.save(dataItems);
            log.info("{} save {} to mongodb", SourceCode.CCGPCQ, dataItems.size());

            List<DataItemDTO> dtoList = dataItems.stream().map(dataItem -> dataItem.dto()).collect(Collectors.toList());
            dataItemService.save2BidNewsOriginalTable(dtoList);
        }
    }
}
