package com.har.sjfxpt.crawler.core.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Administrator on 2018/1/15.
 */
@Data
@Document(collection = "spider_log")
public class SpiderLog {

    @Id
    private String id;

    private String uuid;

    private String status;

    private String currentTime;

    private String fetchDate;

    private String startTime;

    private long pageCount;

    private String site;

    private int threadAlive;

    private String scheduler;
}
