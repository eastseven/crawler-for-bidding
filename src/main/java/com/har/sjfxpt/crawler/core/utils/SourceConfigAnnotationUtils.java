package com.har.sjfxpt.crawler.core.utils;

import com.google.common.collect.Lists;
import com.har.sjfxpt.crawler.core.annotation.Source;
import com.har.sjfxpt.crawler.core.annotation.SourceConfig;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.List;

/**
 * @author dongqi
 */
@Slf4j
public final class SourceConfigAnnotationUtils {

    public static List<SourceModel> find(Class pageProcessorClass) {
        List<SourceModel> sourceModelList = Lists.newArrayList();
        SourceConfig config = AnnotationUtils.findAnnotation(pageProcessorClass, SourceConfig.class);

        if (config.disable()) {
            log.info(">>> {} SourceConfig is disable", pageProcessorClass);
            return sourceModelList;
        }


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

                sourceModelList.add(sourceModel);
            }
        }

        return sourceModelList;
    }
}
