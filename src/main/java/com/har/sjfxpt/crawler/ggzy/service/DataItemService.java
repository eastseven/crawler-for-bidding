package com.har.sjfxpt.crawler.ggzy.service;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.ggzy.config.HBaseConfig;
import com.har.sjfxpt.crawler.ggzy.config.KeyWordsProperties;
import com.har.sjfxpt.crawler.ggzy.model.DataItem;
import com.har.sjfxpt.crawler.ggzy.model.DataItemDTO;
import com.huaban.analysis.jieba.JiebaSegmenter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.har.sjfxpt.crawler.ggzy.model.DataItemDTO.ROW_KEY_LENGTH;

/**
 * @author dongqi
 */
@Slf4j
@Service
public class DataItemService {

    @Autowired
    KeyWordsProperties keyWordsProperties;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    HBaseConfig config;

    final byte[] family = "f".getBytes();
    final String charsetName = "utf-8";

    private Connection conn;

    private Table originalTable;
    private Table historyTable;

    private Map<String, String[]> keyWordsMap = Maps.newHashMap();
    private JiebaSegmenter segmenter;

    @PostConstruct
    public void init() {
        try {
            this.conn = ConnectionFactory.createConnection(config.get());
            this.originalTable = conn.getTable(TableName.valueOf(config.getNamespace(), DataItem.T_NAME_HTML));
            this.historyTable = conn.getTable(TableName.valueOf(config.getNamespace(), DataItem.T_NAME_HTML_HISTORY));
        } catch (Exception e) {
            log.error("", e);
        }

        segmenter = new JiebaSegmenter();

        keyWordsMap = keyWordsProperties.getCategories().stream()
                .collect(Collectors.toMap(line -> line.split(",")[0], line -> line.split(",")));
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

            //计算 行业分类
            //setIndustryCategory(dataItem);

            try {
                String rowKey = getRowKey(dataItem);
                if (rowKey.length() != ROW_KEY_LENGTH) {
                    throw new Exception(rowKey + " length not equal " + ROW_KEY_LENGTH + ", data item id is " + dataItem.getId() + ", source " + dataItem.getSourceCode());
                }

                // 数据写入策略：
                // 记录所有数据到original表，如果是历史数据，则再保存一份在history表中

                String sourceCode = dataItem.getSourceCode();
                Put row = assemble(rowKey, dataItem);
                String date = StringUtils.substringBefore(rowKey, ":");
                boolean exists = originalTable.exists(new Get(rowKey.getBytes()));
                if (!exists) {
                    originalTable.put(row);
                    log.info("save {} {}[mongo={}] to {}", sourceCode, rowKey, dataItem.getId(), DataItem.T_NAME_HTML);
                    counter++;
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
     * 将title及textContent
     * 先分词，再匹配行业关键字，找出对应的行业分类
     * @param dataItem
     * @return dataItem.industryCategory 以逗号结尾的行业分类
     */
    public DataItemDTO setIndustryCategory(DataItemDTO dataItem) {
        dataItem.setTextContent(Jsoup.clean(dataItem.getFormatContent(), Whitelist.none()));
        String text = dataItem.getTitle();
        String words = segmenter.sentenceProcess(text).stream()
                .filter(word -> StringUtils.isNotBlank(word) && word.length() > 1)
                .map(word -> StringUtils.trim(word))
                .collect(Collectors.joining(","));
        String industryCategory = mark(words);
        dataItem.setIndustryCategory(industryCategory);

        return dataItem;
    }

    /**
     * 生成hbase row key
     * @param dataItem
     * @return rowKey 格式 yyyyMMdd:md5(title)
     */
    private String getRowKey(DataItemDTO dataItem) {
        String date = StringUtils.substring(dataItem.getDate(), 0, 10).replace("-", "");
        return getRowKey(date, dataItem.getTitle());
    }

    private String getRowKey(String date, String title) {
        return String.join(":", date, DigestUtils.md5Hex(StringUtils.trim(title)));
    }

    private Put assemble(String rowKey, DataItemDTO dataItem) throws UnsupportedEncodingException {
        Put put = new Put(rowKey.getBytes());
        put.addColumn(family, "url".getBytes(),                StringUtils.defaultString(dataItem.getUrl(), "").getBytes(charsetName));
        put.addColumn(family, "title".getBytes(),              StringUtils.defaultString(dataItem.getTitle(), "").getBytes(charsetName));
        put.addColumn(family, "province".getBytes(),           StringUtils.defaultString(dataItem.getProvince(), "全国").getBytes(charsetName));
        put.addColumn(family, "type".getBytes(),               StringUtils.defaultString(dataItem.getType(), "").getBytes(charsetName));
        put.addColumn(family, "source".getBytes(),             StringUtils.defaultString(dataItem.getSource(), "其他").getBytes(charsetName));
        put.addColumn(family, "sourceCode".getBytes(),         StringUtils.defaultString(dataItem.getSourceCode(), "UNKNOWN").getBytes(charsetName));
        put.addColumn(family, "date".getBytes(),               StringUtils.defaultString(dataItem.getDate(), "").getBytes(charsetName));
        put.addColumn(family, "create_time".getBytes(),        StringUtils.defaultString(dataItem.getCreateTime(), "").getBytes(charsetName));
        put.addColumn(family, "formatContent".getBytes(),      StringUtils.defaultString(dataItem.getFormatContent(), "").getBytes(charsetName));
        put.addColumn(family, "purchaser".getBytes(),          StringUtils.defaultString(dataItem.getPurchaser(), "").getBytes(charsetName));
        put.addColumn(family, "project_name".getBytes(),       StringUtils.defaultString(dataItem.getProjectName(), "").getBytes(charsetName));

        put.addColumn(family, "original_industry_category".getBytes(),  StringUtils.defaultString(dataItem.getOriginalIndustryCategory(), "").getBytes(charsetName));

        return put;
    }

    @Deprecated
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

    /**
     * 打分
     * @param sources
     * @return
     */
    public String mark(String... sources) {
        try {
            Map<Integer, String> map = Maps.newHashMap();
            for (String industryCategoryLabel : keyWordsMap.keySet()) {
                String[] industryCategoryKeyWords = keyWordsMap.get(industryCategoryLabel);
                Integer score = 0;
                for (String keyWords : industryCategoryKeyWords) {
                    for (String source : sources) {
                        if (StringUtils.isBlank(source)) continue;
                        if (source.contains(StringUtils.trim(keyWords))) score++;
                    }
                }

                if (!map.containsKey(score)) {
                    map.put(score, industryCategoryLabel);
                } else {
                    String value = String.join(",", map.get(score), industryCategoryLabel);
                    map.put(score, value);
                }
            }

            Integer key = map.keySet().stream().max((a, b) -> Integer.compare(a, b)).get();
            if (key > 0) return map.get(key);

        } catch (Exception e) {
            log.error("", e);
            log.error("");
        }

        return "其他";
    }
}
