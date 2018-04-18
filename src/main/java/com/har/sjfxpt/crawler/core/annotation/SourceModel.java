package com.har.sjfxpt.crawler.core.annotation;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author dongqi
 * @author luofei
 */
@Data
@Slf4j
public class SourceModel {

    private String url;

    private String type;

    private boolean post;

    @Field("post_params")
    private String jsonPostParams;

    @Transient
    private Map<String, Object> postParams;

    @Field("day_pattern")
    private String dayPattern = "yyyy-MM-dd";

    @Field("day_scope")
    private String dayScope;

    @Field("day_start_field")
    private String dateStartField;

    @Field("day_end_field")
    private String dateEndField;

    @Field("post_params_replace_fields")
    private String[] needPlaceholderFields;

    public Request createRequest() {
        Request request = new Request(this.getUrl());
        if (StringUtils.isNotBlank(this.jsonPostParams)) {
            //TODO 分页 带日期范围的查询，需要重构

            Map<String, Object> pageParams = JSONObject.parseObject(this.jsonPostParams, Map.class);
            this.setPostParams(pageParams);

            if (ArrayUtils.isNotEmpty(this.getNeedPlaceholderFields())) {
                for (String field : this.getNeedPlaceholderFields()) {
                    String value = DateTime.now().toString(this.getDayPattern());
                    this.getPostParams().put(field, value);
                }
            }

            if (StringUtils.isNotBlank(dateStartField) && StringUtils.isNotBlank(dateEndField)) {
                DateTime now = DateTime.now();
                String start = "", end = "";
                if (StringUtils.isNotBlank(dayScope)) {
                    switch (dayScope) {
                        case "1D":
                            start = now.toString(dayPattern) + " 00:00:00";
                            end = now.toString(dayPattern) + " 23:59:59";
                            postParams.put(dateStartField, start);
                            postParams.put(dateEndField, end);
                            break;
                        case "3D":
                            break;
                        case "10D":
                            break;
                        case "1M":
                            break;
                        case "3M":
                            break;
                        default:
                    }
                }
            }
            if (StringUtils.isNotBlank(dateStartField) && dateStartField.equalsIgnoreCase("sdt")) {
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.json(JSONObject.toJSONString(pageParams), "UTF-8"));
                request.putExtra("pageParams", pageParams);
            } else {
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
                request.putExtra("pageParams", pageParams);
            }
        }

        if (!post && ArrayUtils.isNotEmpty(needPlaceholderFields)) {
            if ("TIMESTAMP".equalsIgnoreCase(dayPattern)) {
                for (String field : needPlaceholderFields) {
                    String url = StringUtils.replace(request.getUrl(), field, String.valueOf(DateTime.now().getMillis()));
                    request.setUrl(url);
                }
            }
            if ("YYYY-MM-DD".equalsIgnoreCase(dayPattern)) {
                for (String field : needPlaceholderFields) {
                    String url = StringUtils.replaceAll(request.getUrl(), field, DateTime.now().toString(dayPattern));
                    request.setUrl(url);
                }
            }
            if ("yyyy:MM:dd".equalsIgnoreCase(dayPattern)) {
                for (String field : needPlaceholderFields) {
                    String url = null;
                    try {
                        url = StringUtils.replaceAll(request.getUrl(), field, URLEncoder.encode(DateTime.now().toString(dayPattern), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        log.error("", e);
                    }
                    request.setUrl(url);
                }
            }
        }

        request.putExtra("type", this.getType());
        return request;
    }
}
