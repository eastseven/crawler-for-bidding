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
     * yyyy-MM-dd or TIMESTAMP or yyyy-MM-dd HH:mm:ss
     * @return
     */
    String dayPattern() default "yyyy-MM-dd";

    /**
     * 分页查询数据时，需要用到的日期范围。
     *
     * 1D 一天, yyyy-MM-dd，2018-01-01 to 2018-01-02
     * 1D 一天, yyyy-MM-dd HH:mm:ss，2018-01-01 00:00:00 to 2018-01-02 23:59:59
     *
     * @return 1D,3D,10D,1M,3M
     */
    String dayScope() default "";

    String dateStartField() default "";

    String dateEndField() default "";
    /**
     * 需要替换的字段名称，必须对应 postParams 中的字段名字
     * @return
     */
    String[] needPlaceholderFields() default {};
}
