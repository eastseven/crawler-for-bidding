package com.har.sjfxpt.crawler.ccgp.ccgpsc;

import com.har.sjfxpt.crawler.core.model.DataItemDTO;
import com.har.sjfxpt.crawler.core.model.SourceCode;
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

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2017/11/8.
 */
@Slf4j
@Component
public class CCGPSiChuanPipeline implements Pipeline{

    @Autowired
    CCGPSiChuanPageDataRepository ccgpSiChuanPageDataRepository;

    @Autowired
    HBaseService HBaseService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<CCGPSiChuanDataItem> dataItems=resultItems.get(KEY_DATA_ITEMS);
        if(CollectionUtils.isEmpty(dataItems)){
            log.warn("{} save nothing", SourceCode.CCGPSC);
        }else {
            ccgpSiChuanPageDataRepository.save(dataItems);
            log.info("{} save {} to mongodb", SourceCode.CCGPSC, dataItems.size());

            List<DataItemDTO> dtoList = dataItems.stream().map(dataItem -> dataItem.dto()).collect(Collectors.toList());
            HBaseService.save2BidNewsOriginalTable(dtoList);
        }
    }
}
