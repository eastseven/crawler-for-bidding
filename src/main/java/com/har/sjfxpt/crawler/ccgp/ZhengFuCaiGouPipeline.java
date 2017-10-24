package com.har.sjfxpt.crawler.ccgp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

@Slf4j
@Component
public class ZhengFuCaiGouPipeline implements Pipeline {

    @Autowired ZhengFuCaiGouRepository repository;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<ZhengFuCaiGouDataItem> dataItemList = resultItems.get("dataItemList");
        repository.save(dataItemList);
    }
}
