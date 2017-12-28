package com.har.sjfxpt.crawler.core.processor;

import com.har.sjfxpt.crawler.core.model.SourceCode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dongqi
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SourceConfig {

    Source[] sources() default {};

    SourceCode code() default SourceCode.DEFAULT;

    boolean useProxy() default false;
}
