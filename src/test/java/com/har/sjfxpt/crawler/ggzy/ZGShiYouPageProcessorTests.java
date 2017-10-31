package com.har.sjfxpt.crawler.ggzy;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.zgsy.ZGShiYouPageProcessor;
import com.har.sjfxpt.crawler.zgsy.ZGShiYouPipeline;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

/**
 * Created by Administrator on 2017/10/30.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ZGShiYouPageProcessorTests {

    @Autowired
    ZGShiYouPageProcessor zgShiYouPageProcessor;

    @Autowired
    ZGShiYouPipeline zgShiYouPipeline;


    String[] urls = {
            "http://eportal.energyahead.com/wps/portal/ebid/!ut/p/c5/hY7JkqpAEEW_xQ8gKKCYlgjIJKOAUBsDlEEREOSB5dc33dFb-2WuMk7myUsicu0um69VNl37LruTCYm4kySyshRypsWyPgsMXYkEn5Mpy2VXnn7kMIb_uT5-__u8AcCf_Mf_zcGHkgDp6H1bkCmJ-NXC_Fr24moJ94HnhpDRdEiGZJKc-5cM7UU18jqh_XlIHZoX8QPzbWSwXOp3Rq2PIUEwgxEZwVwEAqFpctXvVAxtM1oWnM9WlsK3WcZPJS8ZYVseiep8vdwXa34nhXMBzBg2_hSHaImBPgxNA0-I4oZZ6fozpgWdd3rFwxUyGU6cbt32RRwOC9z8nZ_if_KnCy_30qJKiQerXaNiGuwyPOWaxby8SMdpBm95mQTyXDuhbSs4S4E9uvrzZWou0G5LC8FzW12kfa33iRSrPfOI0b0rKFvKbChURisEe3QKh3jERom0oyCNdI3EZlKK6vCPzwmqjpi3C1AvbTbko03A1atHdp2-ALdev00!/dl3/d3/L3dDbzIyMkEhL0lKU0FDSXdrQWdRaVFDQ0tKQUlFWWtBZ1RpUUNDQkpBSUlBIS9ZQzZfd0EhIS83X0E5M0NBVDZKSzVMOTUwSVRMUlBPVDQzRzE3/",
            "http://eportal.energyahead.com/wps/portal/ebid/!ut/p/c5/pY7JkqowGEafpR-gK4Ah6DIoEAFlkCFhYwUnlIsMpQw-fUPXXdxN24v7_8tT56sDEjD-nbfXC39cyzv_AyhI0B4v5CUOkGkFW6IJaxL5O0-VZ5Iljpz9w2XZk0e-CuceWoowgv9hW4Lwix1Pte_9N_y7buLCD4cFsCVlcQIMJMq4Mvu7Yi_GlcD2XSeAM4NAEADqs7xflrhbadSFl3xADq4dchx4evBKMSlDaR2naK9f2iVZLBDBmJhlEXQ6cnc4nldPexPMtU2U98W2i46nJ1c5LIZzOjTVGbbWEAc2jZo1qwhjvD71jfxAGcx2zIDv-0Rl6qOsU6Y-DU99eq51sajz4ZEa1qx3QzIwDm_pmdYm7YqsMNTB2XAjuq0-2eVGuC51_qE2Dl3k2dmqpFjXGvqMErlOnKNl4NAXFWya8PYUP-_3ul07Yp68crVFei7tXhLqLVi1Ck1UGvd7-fABqiKkwtXNGrn8-AJyC_Fj/dl3/d3/L2dJQSEvUUt3QS9ZQnZ3LzZfQTk1Q0FUNkpLVE5IRTBJSFZSU1FCNTMySzE!/",
            "http://eportal.energyahead.com/wps/portal/ebid/!ut/p/c5/pY7JkqowGEafpR-gK4Ah6DIoEAFlkCFhYwUnlIsMpQw-fUPXXdxN24v7_8tT56sDEjD-nbfXC39cyzv_AyhI0B4v5CUOkGkFW6IJaxL5O0-VZ5Iljpz9w2XZk0e-CuceWoowgv9hW4Lwix1Pte_9N_y7buLCD4cFsCVlcQIMJMq4Mvu7Yi_GlcD2XSeAM4NAEADqs7xflrhbadSFl3xADq4dchx4evBKMSlDaR2naK9f2iVZLBDBmJhlEXQ6cnc4nldPexPMtU2U98W2i46nJ1c5LIZzOjTVGbbWEAc2jZo1qwhjvD71jfxAGcx2zIDv-0Rl6qOsU6Y-DU99eq51sajz4ZEa1qx3QzIwDm_pmdYm7YqsMNTB2XAjuq0-2eVGuC51_qE2Dl3k2dmqpFjXGvqMErlOnKNl4NAXFWya8PYUP-_3ul07Yp68crVFei7tXhLqLVi1Ck1UGvd7-fABqiKkwtXNGrn8-AJyC_Fj/dl3/d3/L2dJQSEvUUt3QS9ZQnZ3LzZfQTk1Q0FUNkpLNTVRNTBJSERVOFE2QzFLRzI!/",
    };

    //爬去当日的数据
    @Test
    public void testzgShiYouPageProcessor() {

        String url = "http://eportal.energyahead.com/wps/portal/ebid/!ut/p/c5/hY7JkqpAEEW_xQ8gKKCYlgjIJKOAUBsDlEEREOSB5dc33dFb-2WuMk7myUsicu0um69VNl37LruTCYm4kySyshRypsWyPgsMXYkEn5Mpy2VXnn7kMIb_uT5-__u8AcCf_Mf_zcGHkgDp6H1bkCmJ-NXC_Fr24moJ94HnhpDRdEiGZJKc-5cM7UU18jqh_XlIHZoX8QPzbWSwXOp3Rq2PIUEwgxEZwVwEAqFpctXvVAxtM1oWnM9WlsK3WcZPJS8ZYVseiep8vdwXa34nhXMBzBg2_hSHaImBPgxNA0-I4oZZ6fozpgWdd3rFwxUyGU6cbt32RRwOC9z8nZ_if_KnCy_30qJKiQerXaNiGuwyPOWaxby8SMdpBm95mQTyXDuhbSs4S4E9uvrzZWou0G5LC8FzW12kfa33iRSrPfOI0b0rKFvKbChURisEe3QKh3jERom0oyCNdI3EZlKK6vCPzwmqjpi3C1AvbTbko03A1atHdp2-ALdev00!/dl3/d3/L3dDbzIyMkEhL0lKU0FDSXdrQWdRaVFDQ0tKQUlFWWtBZ1RpUUNDQkpBSUlBIS9ZQzZfd0EhIS83X0E5M0NBVDZKSzVMOTUwSVRMUlBPVDQzRzE3/";
        Map<String, Object> params = Maps.newHashMap();
        params.put("department", "");
        params.put("noticeName", "");
        params.put("projectType", "");
        params.put("fromDate", "2017-10-30");
        params.put("toDate", "2017-10-31");
        params.put("pageNo", 1);
        params.put("type", "公开招标公告");

        Request request = new Request(url);
        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
        request.putExtra("pageParams", params);

        Spider.create(zgShiYouPageProcessor)
                .addPipeline(zgShiYouPipeline)
                .addRequest(request)
                .run();
    }

}
