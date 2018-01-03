package com.har.sjfxpt.crawler.core.annotation;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

/**
 * @author dongqi
 */
@Data
public class SourceModel {

    private String url;
    private String type;
    private boolean post;
    private String jsonPostParams;
    private Map<String, Object> postParams;
    private String dayPattern = "yyyy-MM-dd";
    private String[] needPlaceholderFields;

    public Request createRequest() {
        Request request = new Request(this.getUrl());
        if (StringUtils.isNotBlank(this.jsonPostParams)) {
            Map<String, Object> pageParams = JSONObject.parseObject(this.jsonPostParams, Map.class);
            this.setPostParams(pageParams);

            if (ArrayUtils.isNotEmpty(this.getNeedPlaceholderFields())) {
                for (String field : this.getNeedPlaceholderFields()) {
                    this.getPostParams().put(field, DateTime.now().toString(this.getDayPattern()));
                }
            }

            request.setMethod(HttpConstant.Method.POST);
            request.setRequestBody(HttpRequestBody.form(pageParams, "UTF-8"));
            request.putExtra("pageParams", pageParams);
        }

        if (!post && ArrayUtils.isNotEmpty(needPlaceholderFields)) {
            if ("TIMESTAMP".equalsIgnoreCase(dayPattern)) {
                for (String field : needPlaceholderFields) {
                    String url = StringUtils.replace(request.getUrl(), field, String.valueOf(DateTime.now().getMillis()));
                    request.setUrl(url);
                }
            }
        }

        request.putExtra("type", this.getType());
        return request;
    }
}
