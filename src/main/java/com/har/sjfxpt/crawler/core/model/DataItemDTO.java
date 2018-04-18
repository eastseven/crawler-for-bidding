package com.har.sjfxpt.crawler.core.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 将爬虫数据转换为hbase需要的格式
 *
 * @author dongqi
 */
@Setter
@Getter@NoArgsConstructor
@ToString(exclude = {"formatContent"})
@Deprecated
public class DataItemDTO extends BidNewsOriginal {

    public static final int ROW_KEY_LENGTH = 41;

    public DataItemDTO(String url) {
        //super(url);
    }

}
