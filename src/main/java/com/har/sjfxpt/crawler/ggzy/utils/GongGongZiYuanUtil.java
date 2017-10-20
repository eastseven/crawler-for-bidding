package com.har.sjfxpt.crawler.ggzy.utils;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.Map;

/**
 * @author dongqi
 */
@Slf4j
public final class GongGongZiYuanUtil {

    public static final String DEAL_CLASSIFY = "DEAL_CLASSIFY";
    public static final String PAGE_NUMBER = "PAGENUMBER";

    public static final String SEED_URL = "http://deal.ggzy.gov.cn/ds/deal/dealList.jsp";

    public static final String YYYYMMDD = "yyyy-MM-dd";

    public static Map<String, Object> getPostPageParams() {
        return getPostPageParams(DateTime.now().minusDays(1).toString(YYYYMMDD));
    }

    /**
     * 当天的分页参数
     * @param type 01 工程建设 02 政府采购
     * @return
     */
    public static Map<String, Object> getPageParamsByType(String type) {
        String current = DateTime.now().toString(YYYYMMDD);
        return getPostPageParams(current, current, type, 1);
    }

    public static Map<String, Object> getPageParamsByType(String type, String date) {
        return getPostPageParams(date, date, type, 1);
    }

    public static Map<String, Object> getPageParamsByType(String type, String begin, String end) {
        return getPostPageParams(begin, end, type, 1);
    }

    public static Map<String, Object> getPostPageParams(String type, int pageNumber) {
        String current = DateTime.now().toString(YYYYMMDD);
        return getPostPageParams(current, current, type, pageNumber);
    }

    public static Map<String, Object> getPostPageParams(String start) {
        return getPostPageParams(start, null, "01", 1);
    }

    public static Map<String, Object> getPostPageParams(String start, String end, String type, int pageNumber) {
        if (StringUtils.isBlank(start)) {
            start = DateTime.now().minusDays(1).toString(YYYYMMDD);
        }

        if (StringUtils.isBlank(end)) {
            end = DateTime.now().toString(YYYYMMDD);
        }

        Map<String, Object> param = Maps.newHashMap();
        param.put("TIMEBEGIN", start);
        param.put("TIMEBEGIN_SHOW", start);
        param.put("TIMEEND", end);
        param.put("TIMEEND_SHOW", end);
        // 06 指定时间区间
        param.put("DEAL_TIME", "06");
        //00 不限 01 工程建设 02 政府采购
        param.put("DEAL_CLASSIFY", type);
        param.put("DEAL_STAGE", type + "00");
        param.put("DEAL_PROVINCE", "0");
        param.put("DEAL_CITY", "0");
        param.put("DEAL_PLATFORM", "0");
        param.put("DEAL_TRADE", "0");
        param.put("isShowAll", "1");
        param.put("PAGENUMBER", String.valueOf(pageNumber));
        param.put("FINDTXT", "");
        return param;
    }
}
