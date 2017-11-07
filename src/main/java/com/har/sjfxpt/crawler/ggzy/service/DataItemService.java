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
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.har.sjfxpt.crawler.ggzy.model.DataItemDTO.ROW_KEY_LENGTH;

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
            if (StringUtils.isBlank(dataItem.getFormatContent())) {
                log.error("{} {} save to hbase fail, formatContent is empty", dataItem.getSourceCode(), dataItem.getId());
                redisTemplate.boundListOps("fetch_fail_url_" + dataItem.getSourceCode().toLowerCase()).leftPush(dataItem.getUrl());
                continue;
            }

            try {
                String rowKey = getRowKey(dataItem);
                if (rowKey.length() != ROW_KEY_LENGTH) {
                    throw new Exception(rowKey + " length not equal " + ROW_KEY_LENGTH + ", data item id is " + dataItem.getId() + ", source " + dataItem.getSourceCode());
                }

                String sourceCode = dataItem.getSourceCode();
                Put row = assemble(rowKey, dataItem);

                String date = StringUtils.substringBefore(rowKey, ":");
                if (current.equalsIgnoreCase(date)) {
                    boolean exists = originalTable.exists(new Get(rowKey.getBytes()));
                    if (!exists) {
                        originalTable.put(row);
                        sourceCodeByDateCounter(date, dataItem);
                        log.debug("save {} {}[mongo={}] to {}", sourceCode, rowKey, dataItem.getId(), DataItem.T_NAME_HTML);
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

        if (counter > 0) {
            log.info("{} {} hbase save {}", dataItemList.iterator().next().getSourceCode(), current, counter);
        }
    }

    private String getRowKey(DataItemDTO dataItem) {
        String date = StringUtils.substring(dataItem.getDate(), 0, 10).replace("-", "");
        String rowKey = date;
        rowKey += ':' + DigestUtils.md5Hex(StringUtils.trim(dataItem.getTitle()));

        return rowKey;
    }

    private Put assemble(String rowKey, DataItemDTO dataItem) throws UnsupportedEncodingException {
        Put put = new Put(rowKey.getBytes());
        put.addColumn(family, "url".getBytes(),           StringUtils.defaultString(dataItem.getUrl(), "").getBytes(charsetName));
        put.addColumn(family, "title".getBytes(),         StringUtils.defaultString(dataItem.getTitle(), "").getBytes(charsetName));
        put.addColumn(family, "province".getBytes(),      StringUtils.defaultString(dataItem.getProvince(), "全国").getBytes(charsetName));
        put.addColumn(family, "type".getBytes(),          StringUtils.defaultString(dataItem.getType(), "").getBytes(charsetName));
        put.addColumn(family, "source".getBytes(),        StringUtils.defaultString(dataItem.getSource(), "其他").getBytes(charsetName));
        put.addColumn(family, "sourceCode".getBytes(),    StringUtils.defaultString(dataItem.getSourceCode(), "UNKNOWN").getBytes(charsetName));
        put.addColumn(family, "date".getBytes(),          StringUtils.defaultString(dataItem.getDate(), "").getBytes(charsetName));
        put.addColumn(family, "create_time".getBytes(),   StringUtils.defaultString(dataItem.getCreateTime(), "").getBytes(charsetName));
        put.addColumn(family, "formatContent".getBytes(), StringUtils.defaultString(dataItem.getFormatContent(), "").getBytes(charsetName));
        put.addColumn(family, "purchaser".getBytes(),     StringUtils.defaultString(dataItem.getPurchaser(), "").getBytes(charsetName));
        put.addColumn(family, "project_name".getBytes(),  StringUtils.defaultString(dataItem.getProjectName(), "").getBytes(charsetName));

        //textContent 废弃
        put.addColumn(family, "textContent".getBytes(), StringUtils.defaultString("", "").getBytes(charsetName));

        return put;
    }

    private void sourceCodeByDateCounter(String date, DataItemDTO dto) {
        try {
            redisTemplate.boundValueOps(date + ':' + dto.getSourceCode().toLowerCase()).increment(1L);
        } catch (Exception e) {
            log.error("", e);
            log.error("sourceCodeByDateCounter count fail, {} mongo id {} ", dto.getSourceCode(), dto.getId());
        }

        hourlyCounter(dto);
        hourlyCounterBySourceCode(dto);
    }

    private void hourlyCounter(DataItemDTO dto) {
        try {
            if (!DateTime.now().toString("yyyyMMddHH").equalsIgnoreCase(dto.getCreateTime())) return;
            redisTemplate.boundValueOps(dto.getCreateTime()).increment(1);
        } catch (Exception e) {
            log.error("", e);
            log.error("hourlyCounter count fail, {} mongo id {}", dto.getSourceCode(), dto.getId());
        }
    }

    private void hourlyCounterBySourceCode(DataItemDTO dto) {
        try {
            if (!DateTime.now().toString("yyyyMMddHH").equalsIgnoreCase(dto.getCreateTime())) return;
            redisTemplate.boundValueOps(dto.getCreateTime() + ':' + dto.getSourceCode().toLowerCase()).increment(1);
        } catch (Exception e) {
            log.error("", e);
            log.error("hourlyCounter count fail, {} mongo id {}", dto.getSourceCode(), dto.getId());
        }
    }
}
