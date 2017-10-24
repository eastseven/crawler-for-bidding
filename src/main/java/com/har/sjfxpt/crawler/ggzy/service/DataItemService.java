package com.har.sjfxpt.crawler.ggzy.service;

import com.har.sjfxpt.crawler.ggzy.config.HBaseConfig;
import com.har.sjfxpt.crawler.ggzy.model.DataItem;
import com.har.sjfxpt.crawler.ggzy.model.GongGongZiYuanModel;
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
    HBaseConfig config;

    final byte[] family = "f".getBytes();
    final String charsetName = "utf-8";

    private Connection conn;

    private Table table;
    private Table html;

    @PostConstruct
    public void init() {
        try {
            this.conn = ConnectionFactory.createConnection(config.get());
            this.table = conn.getTable(TableName.valueOf(config.getNamespace(), DataItem.T_NAME));
            this.html = conn.getTable(TableName.valueOf(config.getNamespace(), DataItem.T_NAME_HTML));
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @PreDestroy
    public void destroy() {
        if (table != null) {
            try {
                table.close();
                log.info(">>> hbase table {} close", DataItem.T_NAME);
            } catch (IOException e) {
                log.error("", e);
            }
        }

        if (html != null) {
            try {
                html.close();
                log.info(">>> hbase table {} close", DataItem.T_NAME_HTML);
            } catch (IOException e) {
                log.error("", e);
            }
        }

        if (conn != null) {
            try {
                conn.close();
                log.info(">>> hbase connection close");
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }

    public void save(List<DataItem> dataItemList) {
        if (CollectionUtils.isEmpty(dataItemList)) {
            return;
        }

        try {
            List<Put> putList = assemble(dataItemList);
            table.put(putList);
            log.info("hbase save size {}", putList.size());
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public void save2BidNewsOriginalTable(List<DataItem> dataItemList) {
        if (CollectionUtils.isEmpty(dataItemList)) {
            return;
        }

        try {
            List<Put> putList = assembleWithGGZY(dataItemList);
            html.put(putList);
            log.info("hbase table {} save size {}", DataItem.T_NAME_HTML, putList.size());
        } catch (Exception e) {
            log.error("", e);
        }
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

    private List<Put> assembleWithGGZY(List<DataItem> dataItemList) throws UnsupportedEncodingException {
        List<Put> putList = Lists.newArrayList();
        for (DataItem dataItem : dataItemList) {
            if (StringUtils.isBlank(dataItem.getFormatContent())) {
                log.warn(">>> {} formatContent is blank", dataItem.getId());
                continue;
            }

            GongGongZiYuanModel model = new GongGongZiYuanModel(dataItem);
            String date = dataItem.getDate().replace("-", "") + ':';
            String rowKey = date + dataItem.getId();

            Put put = new Put(rowKey.getBytes());
            put.addColumn(family, "url".getBytes(),           StringUtils.defaultString(model.getUrl(), "").getBytes(charsetName));
            put.addColumn(family, "title".getBytes(),         StringUtils.defaultString(model.getTitle(), "").getBytes(charsetName));
            put.addColumn(family, "province".getBytes(),      StringUtils.defaultString(model.getProvince(), "全国").getBytes(charsetName));
            put.addColumn(family, "type".getBytes(),          StringUtils.defaultString(model.getType(), "").getBytes(charsetName));
            put.addColumn(family, "source".getBytes(),        StringUtils.defaultString(model.getSource(), "全国公共资源交易平台").getBytes(charsetName));
            put.addColumn(family, "sourceCode".getBytes(),    StringUtils.defaultString(model.getSourceCode(), "GGZY").getBytes(charsetName));
            put.addColumn(family, "date".getBytes(),          StringUtils.defaultString(model.getDate(), "").getBytes(charsetName));
            put.addColumn(family, "create_time".getBytes(),   StringUtils.defaultString(model.getCreateTime(), "").getBytes(charsetName));
            put.addColumn(family, "formatContent".getBytes(), StringUtils.defaultString(model.getFormatContent(), "").getBytes(charsetName));
            put.addColumn(family, "textContent".getBytes(),   StringUtils.defaultString(model.getTextContent(), "").getBytes(charsetName));

            putList.add(put);
        }

        return putList;
    }
}
