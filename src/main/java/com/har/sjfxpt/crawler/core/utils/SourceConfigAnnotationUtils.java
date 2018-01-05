package com.har.sjfxpt.crawler.core.utils;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.annotation.SourceConfigModel;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.annotation.AnnotationUtils;
import us.codecraft.webmagic.Request;

import java.util.List;

/**
 * @author dongqi
 */
@Slf4j
public final class SourceConfigAnnotationUtils {

    public static SourceConfigModel get(Class pageProcessorClass) {
        List<SourceModel> sourceModelList = Lists.newArrayList();
        SourceConfig config = AnnotationUtils.findAnnotation(pageProcessorClass, SourceConfig.class);

        SourceConfigModel configModel = new SourceConfigModel();
        configModel.setDisable(config.disable());
        configModel.setUseProxy(config.useProxy());
        configModel.setSourceCode(config.code());
        configModel.setId(config.code().name());
        configModel.setName(config.code().getValue());
        configModel.setUseSelenium(config.useSelenium());

        Source[] sources = config.sources();
        if (ArrayUtils.isNotEmpty(sources)) {
            for (Source source : sources) {
                String url = source.url();

                SourceModel sourceModel = new SourceModel();

                sourceModel.setUrl(url);
                sourceModel.setType(source.type());
                sourceModel.setPost(source.post());
                sourceModel.setJsonPostParams(source.postParams());
                sourceModel.setDayPattern(source.dayPattern());
                sourceModel.setNeedPlaceholderFields(source.needPlaceholderFields());
                sourceModel.setDayScope(source.dayScope());
                sourceModel.setDateStartField(source.dateStartField());
                sourceModel.setDateEndField(source.dateEndField());

                sourceModelList.add(sourceModel);
            }
            configModel.setSources(sourceModelList);
        }

        return configModel;
    }

    public static List<SourceModel> find(Class pageProcessorClass) {
        SourceConfigModel configModel = get(pageProcessorClass);

        if (configModel.isDisable()) {
            log.info(">>> {} SourceConfig is disable", pageProcessorClass);
            return Lists.newArrayList();
        }
        return configModel.getSources();
    }

    public static Request[] toRequests(Class pageProcessorClass) {
        return  find(pageProcessorClass).stream().map(SourceModel::createRequest).toArray(Request[]::new);
    }
}
