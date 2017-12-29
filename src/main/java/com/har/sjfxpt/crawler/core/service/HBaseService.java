package com.har.sjfxpt.crawler.core.service;

import com.har.sjfxpt.crawler.core.config.HBaseConfig;
import com.har.sjfxpt.crawler.core.model.BidNewOriginal;
import com.har.sjfxpt.crawler.core.model.DataItem;
import com.har.sjfxpt.crawler.core.model.DataItemDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import static com.har.sjfxpt.crawler.core.model.DataItemDTO.ROW_KEY_LENGTH;

/**
 * @author dongqi
 */
@Slf4j
@Service
public class HBaseService {

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

    /**
     * 只是为了兼容以前的代码，后面逐步替换调用该方法的类
     * @param dataItemList
     */
    @Deprecated
    public void save2BidNewsOriginalTable(List<DataItemDTO> dataItemList) {
        if (CollectionUtils.isEmpty(dataItemList)) {
            return;
        }
        List<BidNewOriginal> list = dataItemList.parallelStream().map(dataItem -> (BidNewOriginal) dataItem).collect(Collectors.toList());
        saveBidNewsOriginals(list);
    }

    public void saveBidNewsOriginals(List<BidNewOriginal> dataItemList) {
        if (CollectionUtils.isEmpty(dataItemList)) {
            return;
        }

        String current = DateTime.now().toString("yyyyMMdd");
        int counter = 0;
        for (BidNewOriginal original : dataItemList) {
            if (StringUtils.isBlank(original.getFormatContent())) {
                log.error("{} {} save to hbase fail, formatContent is empty, {}", original.getSourceCode(), original.getId(), original.getUrl());
                redisTemplate.boundListOps("fetch_fail_url_" + original.getSourceCode().toLowerCase()).leftPush(original.getUrl());
                continue;
            }
            if (StringUtils.isNotBlank(original.getUrl()) && !StringUtils.startsWith(original.getUrl(), "http")) {
                log.error("{} {} save to hbase fail, formatContent is empty, {}", original.getSourceCode(), original.getId(), original.getUrl());
                redisTemplate.boundListOps("fetch_fail_url_" + original.getSourceCode().toLowerCase()).leftPush(original.getUrl());
                continue;
            }

            // 处理超前时间，大于当前时间，按当前时间记录
            try {
                DateTime dt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(original.getDate());
                String pattern = DateTime.now().toString("yyyy-MM-dd HH:mm");
                DateTime now = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(pattern);
                if (dt.compareTo(now) > 0) {
                    original.setDate(now.toString("yyyy-MM-dd HH:mm"));
                }
            } catch (Exception e) {
                log.error("", e);
                log.error(">>> date {}", original.getDate());
            }

            try {
                String rowKey = getRowKey(original);
                if (rowKey.length() != ROW_KEY_LENGTH) {
                    throw new Exception(rowKey + " length not equal " + ROW_KEY_LENGTH + ", data item id is " + original.getId() + ", source " + original.getSourceCode());
                }

                // 数据写入策略：
                // 记录所有数据到original表，如果是历史数据，则再保存一份在history表中

                String sourceCode = original.getSourceCode();
                Put row = assemble(rowKey, original);
                String date = StringUtils.substringBefore(rowKey, ":");
                boolean exists = originalTable.exists(new Get(rowKey.getBytes()));
                if (!exists) {
                    originalTable.put(row);
                    log.info("save {} {}[mongo={}] to {}", sourceCode, rowKey, original.getId(), DataItem.T_NAME_HTML);
                    counter++;
                }

                if (original.isForceUpdate()) {
                    originalTable.put(row);
                    log.debug("force update, save {} {}[mongo={}] to {}", sourceCode, rowKey, original.getId(), DataItem.T_NAME_HTML);
                }

                if (!current.equalsIgnoreCase(date)) {
                    historyTable.put(row);
                    redisTemplate.boundValueOps(date + ':' + sourceCode.toLowerCase()).increment(1L);
                    log.info("save {} {} to {}", sourceCode, rowKey, DataItem.T_NAME_HTML_HISTORY);
                }

            } catch (Exception e) {
                log.error("", e);
            }
        }

        if (counter > 0) {
            log.info("{} {} hbase save {}", dataItemList.iterator().next().getSourceCode(), current, counter);
        }
    }

    /**
     * 生成hbase row key
     *
     * @param dataItem
     * @return rowKey 格式 yyyyMMdd:md5(title)
     */
    private String getRowKey(BidNewOriginal dataItem) {
        String date = StringUtils.substring(dataItem.getDate(), 0, 10).replace("-", "");
        return getRowKey(date, dataItem.getTitle());
    }

    private String getRowKey(String date, String title) {
        return String.join(":", date, DigestUtils.md5Hex(StringUtils.trim(title)));
    }

    private Put assemble(String rowKey, BidNewOriginal dataItem) throws UnsupportedEncodingException {
        Put put = new Put(rowKey.getBytes());
        put.addColumn(family, "url".getBytes(), StringUtils.defaultString(dataItem.getUrl(), "").getBytes(charsetName));
        put.addColumn(family, "title".getBytes(), StringUtils.defaultString(dataItem.getTitle(), "").getBytes(charsetName));
        put.addColumn(family, "province".getBytes(), StringUtils.defaultString(dataItem.getProvince(), "全国").getBytes(charsetName));
        put.addColumn(family, "type".getBytes(), StringUtils.defaultString(dataItem.getType(), "").getBytes(charsetName));
        put.addColumn(family, "source".getBytes(), StringUtils.defaultString(dataItem.getSource(), "其他").getBytes(charsetName));
        put.addColumn(family, Bytes.toBytes("source_code"), StringUtils.defaultString(dataItem.getSourceCode(), "UNKNOWN").getBytes(charsetName));
        put.addColumn(family, Bytes.toBytes("sourceCode"), StringUtils.defaultString(dataItem.getSourceCode(), "UNKNOWN").getBytes(charsetName));
        put.addColumn(family, "date".getBytes(), StringUtils.defaultString(dataItem.getDate(), "").getBytes(charsetName));
        put.addColumn(family, "create_time".getBytes(), StringUtils.defaultString(dataItem.getCreateTime(), "").getBytes(charsetName));
        put.addColumn(family, "formatContent".getBytes(), StringUtils.defaultString(dataItem.getFormatContent(), "").getBytes(charsetName));
        put.addColumn(family, "purchaser".getBytes(), StringUtils.defaultString(dataItem.getPurchaser(), "").getBytes(charsetName));
        put.addColumn(family, "project_name".getBytes(), StringUtils.defaultString(dataItem.getProjectName(), "").getBytes(charsetName));

        put.addColumn(family, "original_industry_category".getBytes(), StringUtils.defaultString(dataItem.getOriginalIndustryCategory(), "").getBytes(charsetName));

        put.addColumn(family, Bytes.toBytes("budget"), StringUtils.defaultString(dataItem.getBudget(), "").getBytes(charsetName));
        put.addColumn(family, Bytes.toBytes("total_bid_money"), StringUtils.defaultString(dataItem.getTotalBidMoney(), "").getBytes(charsetName));
        put.addColumn(family, Bytes.toBytes("bid_company_name"), StringUtils.defaultString(dataItem.getBidCompanyName(), "").getBytes(charsetName));
        return put;
    }

}
