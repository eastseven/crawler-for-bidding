package com.har.sjfxpt.crawler.ggzy.config;

import com.har.sjfxpt.crawler.ggzy.model.DataItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.IOException;

/**
 * @author dongqi
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "hbase.table")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HBaseConfig implements CommandLineRunner {

    @Value("${hbase.zookeeper.quorum}")
    String quorum;

    private boolean clean;

    private String namespace;

    public boolean isClean() {
        return clean;
    }

    public void setClean(boolean clean) {
        this.clean = clean;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public org.apache.hadoop.conf.Configuration get() {
        return configuration();
    }

    @Bean
    org.apache.hadoop.conf.Configuration configuration() {
        org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", quorum);
        configuration.setInt("mapreduce.task.timeout", 1200000);
        configuration.setInt("hbase.client.scanner.timeout.period", 600000);
        configuration.setInt("hbase.rpc.timeout", 600000);
        return configuration;
    }

    void init() throws IOException {
        Connection conn = null;
        Admin admin = null;

        try {
            conn = ConnectionFactory.createConnection(configuration());
            admin = conn.getAdmin();
            boolean namespaceExists = false;
            for (NamespaceDescriptor namespaceDescriptor : admin.listNamespaceDescriptors()) {
                if (namespace.equalsIgnoreCase(namespaceDescriptor.getName())) {
                    namespaceExists = true;
                    break;
                }
            }

            if (!namespaceExists) {
                NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create(namespace).build();
                admin.createNamespace(namespaceDescriptor);
                log.info("create hbase namespace {}", namespace);
            }

            String[] tables = {DataItem.T_NAME_HTML, DataItem.T_NAME_HTML_HISTORY};
            for (String table : tables) {
                TableName tableName = TableName.valueOf(namespace, table);
                boolean exists = admin.tableExists(tableName);
                if (!exists) {
                    admin.createTable(new HTableDescriptor(tableName).addFamily(new HColumnDescriptor(Bytes.toBytes("f"))));
                    log.info("create hbase table {}", table);
                }
            }

        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (admin != null) admin.close();
            if (conn != null) conn.close();
        }
    }

    @Override
    public void run(String... strings) throws Exception {
        // 启动检查
        init();

    }
}
