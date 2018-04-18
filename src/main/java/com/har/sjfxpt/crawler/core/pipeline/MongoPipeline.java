package com.har.sjfxpt.crawler.core.pipeline;

import com.har.sjfxpt.crawler.core.model.BidNewsOriginal;
import com.har.sjfxpt.crawler.core.repository.BidNewsOriginalRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;
import java.util.stream.Collectors;

import static com.har.sjfxpt.crawler.core.processor.BasePageProcessor.KEY_DATA_ITEMS;

/**
 * Created by Administrator on 2018/1/18.
 * @author luofei
 * @author dongqi
 */
@Slf4j
@Component
public class MongoPipeline implements Pipeline {

    @Autowired
    BidNewsOriginalRepository repository;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<BidNewsOriginal> dataItemList = resultItems.get(KEY_DATA_ITEMS);
        if (CollectionUtils.isEmpty(dataItemList)) {
            return;
        }

        List<BidNewsOriginal> list = dataItemList.stream()
                .filter(bidNewsOriginal -> StringUtils.isNotBlank(bidNewsOriginal.getFormatContent()))
                .collect(Collectors.toList());

        // 只是为了快速查看数据，实际数据还是以HBase中的为准
        repository.save(list);
    }
}
