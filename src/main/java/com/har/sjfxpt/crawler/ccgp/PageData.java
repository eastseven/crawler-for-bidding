package com.har.sjfxpt.crawler.ccgp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data@Builder@NoArgsConstructor@AllArgsConstructor
@Document(collection = "data_item_ccgp_page")
public class PageData implements Serializable {

    @Id
    private String date;

    @Field("date_long")
    private long dateLong;

    private int page;

    private int size;

    private String url;
}
