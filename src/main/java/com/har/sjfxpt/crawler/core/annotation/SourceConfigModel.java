package com.har.sjfxpt.crawler.core.annotation;

import com.har.sjfxpt.crawler.core.model.SourceCode;
import lombok.Data;
import org.assertj.core.util.Lists;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author dongqi
 */
@Data
@Document(collection = "source_config_model")
public class SourceConfigModel {

    @Id
    private String id;

    @Field("code")
    private SourceCode sourceCode;

    private String name;

    @Field("use_proxy")
    private boolean useProxy;

    private boolean disable;

    @Field("use_selenium")
    private boolean useSelenium;

    private List<SourceModel> sources = Lists.newArrayList();
}
