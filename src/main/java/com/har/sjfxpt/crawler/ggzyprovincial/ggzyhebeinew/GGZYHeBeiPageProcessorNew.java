package com.har.sjfxpt.crawler.ggzyprovincial.ggzyhebeinew;

import com.alibaba.fastjson.JSONObject;
import com.har.sjfxpt.crawler.ggzy.processor.BasePageProcessor;
import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.select.Elements;
import org.mortbay.util.ajax.JSON;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/26.
 */
@Slf4j
@Component
public class GGZYHeBeiPageProcessorNew implements BasePageProcessor {
    @Override
    public void handlePaging(Page page) {
        try {
            String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(JSON.parse(page.getRawText()));
            log.info("\n{}\n", json);
            Map<String, Object> pageParams = page.getRequest().getExtras();
            GGZYHeBeiPageParameter ggzyHeBeiPageParameter = (GGZYHeBeiPageParameter) pageParams.get("pageParams");
            int pageCount = ggzyHeBeiPageParameter.getPn();
            if (pageCount == 0) {
                GGZYHeBeiDirectoryParameter ggzyHeBeiDirectoryParameter = JSONObject.parseObject(page.getRawText(), GGZYHeBeiDirectoryParameter.class);
                int totalCount = ggzyHeBeiDirectoryParameter.getResult().getTotalcount();
                for (int i = 1; i <= totalCount; i++) {
                    String url = page.getUrl().get();
                    String typeId = ggzyHeBeiPageParameter.getCondition().get(0).getEqual();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleContent(Page page) {

    }

    @Override
    public List parseContent(Elements items) {
        return null;
    }

    @Override
    public void process(Page page) {
        handlePaging(page);
        handleContent(page);
    }

    @Override
    public Site getSite() {
        return SiteUtil.get().setSleepTime(10000);
    }
}
