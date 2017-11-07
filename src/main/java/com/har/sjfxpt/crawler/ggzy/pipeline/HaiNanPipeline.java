package com.har.sjfxpt.crawler.ggzy.pipeline;

import com.har.sjfxpt.crawler.ggzy.model.DataItemDTO;
import com.har.sjfxpt.crawler.ggzy.model.HaiNanModel;
import com.har.sjfxpt.crawler.ggzy.model.SourceCode;
import com.har.sjfxpt.crawler.ggzy.repository.HaiNanPageDataRepository;
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
 * Created by Administrator on 2017/11/7.
 */
@Slf4j
@Component
public class HaiNanPipeline implements Pipeline {

    @Autowired
    HaiNanPageDataRepository haiNanPageDataRepository;

    @Autowired
    DataItemService dataItemService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<HaiNanModel> dataItemList = resultItems.get(KEY_DATA_ITEMS);
        if (CollectionUtils.isEmpty(dataItemList)) {
            log.warn("{} save nothing", SourceCode.CCGPHN);
        }else {
            haiNanPageDataRepository.save(dataItemList);
            log.info("{} save {} to mongodb", SourceCode.CCGPHN, dataItemList.size());

            List<DataItemDTO> dtoList = dataItemList.stream().map(dataItem -> dataItem.dto()).collect(Collectors.toList());
            dataItemService.save2BidNewsOriginalTable(dtoList);
        }
    }
}
