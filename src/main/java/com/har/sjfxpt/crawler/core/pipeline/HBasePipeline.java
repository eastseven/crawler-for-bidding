package com.har.sjfxpt.crawler.core.pipeline;

import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.service.HBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;
import java.util.stream.Collectors;

import static com.har.sjfxpt.crawler.core.processor.BasePageProcessor.KEY_DATA_ITEMS;

/**
 * @author dongqi
 *         通用存储
 */
@Slf4j
@Component
public class HBasePipeline implements Pipeline {

    @Autowired
    HBaseService HBaseService;

    @Value("${app.hbase.save:true}")
    private boolean save;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<BidNewsOriginal> dataItemList = resultItems.get(KEY_DATA_ITEMS);
        if (CollectionUtils.isEmpty(dataItemList)) {
            return;
        }

        List<BidNewsOriginal> list = dataItemList.stream()
                .filter(bidNewsOriginal -> StringUtils.isNotBlank(bidNewsOriginal.getFormatContent()))
                .collect(Collectors.toList());

        if (save) {
            HBaseService.saveBidNewsOriginals(list);
        }
    }

}
