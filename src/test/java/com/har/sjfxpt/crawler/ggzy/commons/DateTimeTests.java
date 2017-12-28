package com.har.sjfxpt.crawler.ggzy.commons;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

@Slf4j
public class DateTimeTests {

    @Test
    public void test() {
        DateTime dt = new DateTime("2017-12-27T10:30");
        String pattern = "yyyy-MM-dd HH:mm";
        String date = DateTime.now().toString(pattern);
        DateTime now = DateTimeFormat.forPattern(pattern).parseDateTime(date);

        log.info(">>> {} : {} = {}", dt.toString(pattern), now.toString(pattern), dt.compareTo(now));
    }

    @Test
    public void testTime() {
        log.debug("{}", DateTime.now().toString("yyyy-MM-dd") + " 23:59:59");
        log.debug("{}", DateTime.now().minusDays(20).toString("yyyy-MM-dd") + " 00:00:00");
    }
}
