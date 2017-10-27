package com.har.sjfxpt.crawler.ggzy.service;

import com.har.sjfxpt.crawler.ggzy.config.HBaseConfig;
import com.har.sjfxpt.crawler.ggzy.model.DataItem;
import com.har.sjfxpt.crawler.ggzy.model.DataItemDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author dongqi
 */
@Slf4j
@Service
public class DataItemService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    HBaseConfig config;

    final byte[] family = "f".getBytes();
    final String charsetName = "utf-8";

    private Connection conn;

    private Table originalTable;
    private Table historyTable;

    @PostConstruct
    public void init() {
        try {
            this.conn = ConnectionFactory.createConnection(config.get());
            this.originalTable = conn.getTable(TableName.valueOf(config.getNamespace(), DataItem.T_NAME_HTML));
            this.historyTable = conn.getTable(TableName.valueOf(config.getNamespace(), DataItem.T_NAME_HTML_HISTORY));
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @PreDestroy
    public void destroy() {
        if (conn != null) {
            try {
                conn.close();
                log.info(">>> hbase connection close");
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }

    public void save2BidNewsOriginalTable(List<DataItemDTO> dataItemList) {
        if (CollectionUtils.isEmpty(dataItemList)) {
            return;
        }

        String current = DateTime.now().toString("yyyyMMdd");
        int counter = 0;
        for (DataItemDTO dataItem : dataItemList) {
            try {
                String sourceCode = dataItem.getSourceCode();
                String rowKey = getRowKey(dataItem);
                Put row = assemble(rowKey, dataItem);

                String date = StringUtils.substringBefore(rowKey, ":");
                if (current.equalsIgnoreCase(date)) {
                    boolean exists = originalTable.exists(new Get(rowKey.getBytes()));
                    if (!exists) {
                        originalTable.put(row);
                        redisTemplate.boundValueOps(date + ':' + sourceCode.toLowerCase()).increment(1L);
                        log.info("save {} {} to {}", sourceCode, rowKey, DataItem.T_NAME_HTML);
                        counter++;
                    }
                } else {
                    boolean exists = historyTable.exists(new Get(rowKey.getBytes()));
                    if (!exists) {
                        historyTable.put(row);
                        redisTemplate.boundValueOps(date + ':' + sourceCode.toLowerCase()).increment(1L);
                        log.info("save {} {} to {}", sourceCode, rowKey, DataItem.T_NAME_HTML_HISTORY);
                        counter++;
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }

        log.info("hbase save {}", counter);
    }

    private List<Put> assemble(List<DataItem> dataItemList) throws UnsupportedEncodingException {
        List<Put> putList = Lists.newArrayList();

        for (DataItem dataItem : dataItemList) {
            String date = dataItem.getDate().replace("-", "") + ':';
            String rowKey = date + dataItem.getId();

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

        return putList;
    }

    private List<Put> assembleWithGGZY(List<DataItemDTO> dataItemList) throws UnsupportedEncodingException {
        List<Put> putList = Lists.newArrayList();
        int counter = 1;
        for (DataItemDTO dataItem : dataItemList) {

            if (StringUtils.isBlank(dataItem.getFormatContent())) {
                log.warn(">>> {} {} formatContent is blank", dataItem.getSourceCode(), dataItem.getId());
                continue;
            }

            if (StringUtils.isBlank(dataItem.getTitle())) {
                log.warn(">>> {} {} title is blank", dataItem.getSourceCode(), dataItem.getId());
                continue;
            }

            String rowKey = getRowKey(dataItem);
            putList.add(assemble(rowKey, dataItem));

            if (counter == 1) {
                log.info("rowKey {} put to table {}", rowKey, DataItem.T_NAME_HTML);
            }
            counter++;
        }

        return putList;
    }

    private String getRowKey(DataItemDTO dataItem) {
        String date = StringUtils.substring(dataItem.getDate(), 0, 10).replace("-", "");
        String rowKey = date;
        rowKey += ':' + DigestUtils.md5Hex(StringUtils.trim(dataItem.getTitle()));

        return rowKey;
    }

    private Put assemble(String rowKey, DataItemDTO dataItem) throws UnsupportedEncodingException {
        Put put = new Put(rowKey.getBytes());
        put.addColumn(family, "url".getBytes(), StringUtils.defaultString(dataItem.getUrl(), "").getBytes(charsetName));
        put.addColumn(family, "title".getBytes(), StringUtils.defaultString(dataItem.getTitle(), "").getBytes(charsetName));
        put.addColumn(family, "province".getBytes(), StringUtils.defaultString(dataItem.getProvince(), "全国").getBytes(charsetName));
        put.addColumn(family, "type".getBytes(), StringUtils.defaultString(dataItem.getType(), "").getBytes(charsetName));
        put.addColumn(family, "source".getBytes(), StringUtils.defaultString(dataItem.getSource(), "其他").getBytes(charsetName));
        put.addColumn(family, "sourceCode".getBytes(), StringUtils.defaultString(dataItem.getSourceCode(), "UNKNOWN").getBytes(charsetName));
        put.addColumn(family, "date".getBytes(), StringUtils.defaultString(dataItem.getDate(), "").getBytes(charsetName));
        put.addColumn(family, "create_time".getBytes(), StringUtils.defaultString(dataItem.getCreateTime(), "").getBytes(charsetName));
        put.addColumn(family, "formatContent".getBytes(), StringUtils.defaultString(dataItem.getFormatContent(), "").getBytes(charsetName));
        put.addColumn(family, "textContent".getBytes(), StringUtils.defaultString(dataItem.getTextContent(), "").getBytes(charsetName));

        return put;
    }

    public void test() {
        Scan scan = new Scan();
        try {
            originalTable.getScanner(scan);
        } catch (IOException e) {
            log.error("", e);
        }
    }
}
