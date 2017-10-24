package com.har.sjfxpt.crawler.ccgp;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document(collection = "data_item_ccgp_page")
public class PageData implements Serializable {

    @Id
    private String date;

    private int page;

    private int size;

    private String url;
}
