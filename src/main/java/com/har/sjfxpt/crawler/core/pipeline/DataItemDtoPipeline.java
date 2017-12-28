package com.har.sjfxpt.crawler.core.pipeline;

import com.har.sjfxpt.crawler.core.service.DataItemService;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyshanxi.GGZYShanXiDataItem;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyshanxi.GGZYShanXiDataItemRepository;
import com.har.sjfxpt.crawler.zgjiaojian.ZGJiaoJianDataItem;
import com.har.sjfxpt.crawler.zgjiaojian.ZGJiaoJianDataItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

import static com.har.sjfxpt.crawler.core.utils.GongGongZiYuanConstant.KEY_DATA_ITEMS;

/**
 * @author dongqi
 * 通用存储
 */
@Slf4j
@Component
public class DataItemDtoPipeline implements Pipeline {

    @Autowired
    DataItemService dataItemService;

    @Autowired
    ApplicationContext ctx;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List dataItemList = resultItems.get(KEY_DATA_ITEMS);
        if (CollectionUtils.isEmpty(dataItemList)) {
            log.warn(">>> save nothing, {}", task.getSite());
        } else {
            Object object = dataItemList.stream().findFirst().get();
            if (object instanceof GGZYShanXiDataItem) {
                ctx.getBean(GGZYShanXiDataItemRepository.class).save(dataItemList);
            }
            if (object instanceof ZGJiaoJianDataItem) {
                ctx.getBean(ZGJiaoJianDataItemRepository.class).save(dataItemList);
            }

//            dataItemService.save2BidNewsOriginalTable(dataItemList);
        }
    }
}
