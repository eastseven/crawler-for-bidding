package com.har.sjfxpt.crawler.core.tools;

import com.har.sjfxpt.crawler.core.HBaseTests;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

@Slf4j
public class CreateTableTests extends HBaseTests {

    // mvn test -Dtest=CreateTableTests#test -Paliyun -Dspring.profiles.active=prod
    @Test
    public void test() {
        Assert.assertNotNull(this.hbaseConfig);
        Assert.assertNotNull(this.conf);

        try {
            Connection conn = ConnectionFactory.createConnection(this.conf);
            Admin admin = conn.getAdmin();
            TableName tableName = TableName.valueOf(this.hbaseConfig.getNamespace(), "original_title");
            if (!admin.tableExists(tableName)) {
                admin.createTable(new HTableDescriptor(tableName).addFamily(new HColumnDescriptor(Bytes.toBytes("f"))));
                log.info(">>> TEST create table {}", tableName.getNameWithNamespaceInclAsString());
            }

            admin.close();
            conn.close();
        } catch (IOException e) {
            log.error("", e);
        }
    }
}
