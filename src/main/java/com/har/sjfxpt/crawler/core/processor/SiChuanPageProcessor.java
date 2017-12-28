package com.har.sjfxpt.crawler.core.processor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.har.sjfxpt.crawler.core.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.Map;

/**
 * 四川省公共资源交易发网平台
 * http://www.scztb.gov.cn/Info/Index.html
 *
 * @author dongqi
 */
@Slf4j
@Component
public class SiChuanPageProcessor implements PageProcessor {

    @Override
    public void process(Page page) {
        log.debug("\n{}\n", page.getJson());

        Map map = page.getJson().toObject(Map.class);
        for (Object key : map.keySet()) {
            Object value = map.get(key);
            log.debug("{}, {}, {}", key, value.getClass(), value);

            if ("data".equalsIgnoreCase(key.toString())) {
                JSONArray jsonArray = JSONArray.parseArray(value.toString());
                for (Object object : jsonArray) {
                    JSONObject jsonObject = (JSONObject) object;
                    log.debug(">>> {}", jsonObject);
                }
            }
        }
    }

    @Override
    public Site getSite() {
        return SiteUtil.get();
    }
}
