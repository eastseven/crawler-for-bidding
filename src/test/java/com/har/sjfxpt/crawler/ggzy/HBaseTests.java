package com.har.sjfxpt.crawler.ggzy;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class HBaseTests extends SpiderApplicationTests {

    @Autowired
    Configuration conf;

    TableName tableName = TableName.valueOf("test_ggzy");

    @Before
    public void teardown() {
        Assert.assertNotNull(conf);
        Connection conn = null;
        Admin admin = null;
        try {
            conn = ConnectionFactory.createConnection(conf);
            admin = conn.getAdmin();

            Arrays.stream(admin.listTableNames()).forEach(System.out::println);

            if (!admin.tableExists(tableName)) {
                admin.createTable(new HTableDescriptor(tableName).addFamily(new HColumnDescriptor(Bytes.toBytes("f"))));
            }

            Arrays.stream(admin.listTableNames()).forEach(System.out::println);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (admin != null) {
                try {
                    admin.close();
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

    @Test
    public void test() {
        Connection conn = null;
        Admin admin = null;
        Table table = null;
        try {
            conn = ConnectionFactory.createConnection(conf);

            String rowKey = DateTime.now().toString("yyyyMMdd");

            Put row = new Put(rowKey.getBytes());
            //row.addColumn("f".getBytes(), "t_column".getBytes(), "Hello HBase 233".getBytes());
            row.addColumn("f".getBytes(), "t_column_ext".getBytes(), "Hello HBase 1023 1352".getBytes());
            table = conn.getTable(tableName);
            table.put(row);
            Assert.assertNotNull(row);

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

            if (admin != null) {
                try {
                    admin.close();
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

    @Test
    public void testNamespace() {
        Assert.assertNotNull(conf);

        Connection conn = null;
        Admin admin = null;
        try {
            conn = ConnectionFactory.createConnection(conf);
            admin = conn.getAdmin();
            NamespaceDescriptor[] namespaceDescriptors = admin.listNamespaceDescriptors();
            for (NamespaceDescriptor namespaceDescriptor : namespaceDescriptors) {
                log.debug(">>> {}", namespaceDescriptor.getName());
            }

            admin.close();
            conn.close();
        } catch (Exception e) {
            log.error("", e);
        } finally {

        }
    }

    @Test
    public void testCountBySourceCode() {
        Assert.assertNotNull(conf);

        Connection conn = null;
        Table table = null;
        try {
            conn = ConnectionFactory.createConnection(conf);
            table = conn.getTable(TableName.valueOf("", ""));

            Scan scan = new Scan();
            table.getScanner(scan);

            table.close();
            conn.close();
        } catch (Exception e) {
            log.error("", e);
        } finally {

        }
    }
}
