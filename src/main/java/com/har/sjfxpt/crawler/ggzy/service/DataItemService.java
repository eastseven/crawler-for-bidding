package com.har.sjfxpt.crawler.ggzy.service;

import com.har.sjfxpt.crawler.ggzy.config.HBaseConfig;
import com.har.sjfxpt.crawler.ggzy.model.DataItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author dongqi
 */
@Slf4j
@Service
public class DataItemService {

    @Autowired
    HBaseConfig config;

    public void save(List<DataItem> dataItemList) {
        if (CollectionUtils.isEmpty(dataItemList)) {
            return;
        }

        final String prefix = DateTime.now().toString("yyyyMMdd") + ':';
        Connection conn = null;
        Table table = null;
        try {
            conn = ConnectionFactory.createConnection(config.get());
            table = conn.getTable(TableName.valueOf(config.getNamespace(), DataItem.T_NAME));
            byte[] family = "f".getBytes();
            final String charsetName = "utf-8";
            List<Put> putList = Lists.newArrayList();
            for (DataItem dataItem : dataItemList) {
                String date = dataItem.getDate().replace("-", "");
                String rowKey = prefix + date;

                Put put = new Put(rowKey.getBytes());
                put.addColumn(family, "url".getBytes(), StringUtils.defaultString(dataItem.getUrl(), "").getBytes(charsetName));
                put.addColumn(family, "createTime".getBytes(), StringUtils.defaultString(new DateTime(dataItem.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "").getBytes(charsetName));
                put.addColumn(family, "province".getBytes(), StringUtils.defaultString(dataItem.getProvince(), "").getBytes(charsetName));
                put.addColumn(family, "source".getBytes(), StringUtils.defaultString(dataItem.getSource(), "全国公共资源交易平台").getBytes(charsetName));
                put.addColumn(family, "businessType".getBytes(), StringUtils.defaultString(dataItem.getBusinessType(), "").getBytes(charsetName));
                put.addColumn(family, "infoType".getBytes(), StringUtils.defaultString(dataItem.getInfoType(), "").getBytes(charsetName));
                put.addColumn(family, "industry".getBytes(), StringUtils.defaultString(dataItem.getIndustry(), "").getBytes(charsetName));
                put.addColumn(family, "date".getBytes(), StringUtils.defaultString(dataItem.getDate(), "").getBytes(charsetName));
                put.addColumn(family, "title".getBytes(), StringUtils.defaultString(dataItem.getTitle(), "").getBytes(charsetName));
                put.addColumn(family, "html".getBytes(), StringUtils.defaultString(dataItem.getHtml(), "").getBytes(charsetName));
                put.addColumn(family, "formatContent".getBytes(), StringUtils.defaultString(dataItem.getFormatContent(), "").getBytes(charsetName));
                put.addColumn(family, "textContent".getBytes(), StringUtils.defaultString(dataItem.getTextContent(), "").getBytes(charsetName));

                putList.add(put);
            }

            table.put(putList);
            log.info("hbase save size {}", putList.size());
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (table != null) {
                try {
                    table.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
    }
}
