package com.har.sjfxpt.crawler.core.annotation;

import lombok.Data;

import java.util.Map;

/**
 * @author dongqi
 */
@Data
public class SourceModel {

    private String url;
    private boolean post;
    private Map<String, Object> postParams;
    private String dayPattern;
    private String[] needPlaceholderFields;

}
