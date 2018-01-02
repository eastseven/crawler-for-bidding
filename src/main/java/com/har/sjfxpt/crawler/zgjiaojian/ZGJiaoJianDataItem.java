package com.har.sjfxpt.crawler.zgjiaojian;

import com.har.sjfxpt.crawler.core.annotation.DataItemRepository;
import com.har.sjfxpt.crawler.core.model.DataItemDTO;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.har.sjfxpt.crawler.zgjiaojian.ZGJiaoJianDataItem.COLLECTION_NAME;

/**
 * Created by Administrator on 2017/12/28.
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@Document(collection = COLLECTION_NAME)
@DataItemRepository(repository = ZGJiaoJianDataItemRepository.class)
public class ZGJiaoJianDataItem extends DataItemDTO {

    public static final String COLLECTION_NAME = "data_item_zgjiaojian";

    public ZGJiaoJianDataItem(String url) {
        this.id = DigestUtils.md5Hex(url);
        this.url = url;
        this.province = "全国";
        this.source = SourceCode.GGZYSHANXI.getValue();
        this.sourceCode = SourceCode.GGZYSHANXI.name();
        this.createTime = DateTime.now().toString("yyyyMMddHH");
    }
}
