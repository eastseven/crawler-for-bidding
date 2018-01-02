package com.har.sjfxpt.crawler.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dongqi
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Source {

    /**
     * 需要抓取的url地址
     * @return
     */
    String url() default "";

    /**
     * 公告栏目对应的类型，如中标，招标等字样
     * @return
     */
    String type() default "";

    /**
     * 是否 是post方式提交
     * @return
     */
    boolean post() default false;

    /**
     * 分页查询参数 or 表单参数
     * @return json 字符串
     */
    String postParams() default "";

    /**
     * 分页查询数据时，需要用到的日期格式。
     * @return
     */
    String dayPattern() default "yyyy-MM-dd";

    /**
     * 需要替换的字段名称，必须对应 postParams 中的字段名字
     * @return
     */
    String[] needPlaceholderFields() default {};
}
