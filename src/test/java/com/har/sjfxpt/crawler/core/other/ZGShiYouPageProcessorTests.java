package com.har.sjfxpt.crawler.core.other;

import com.google.common.collect.Maps;
import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.other.PetroChinaPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/10/30.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ZGShiYouPageProcessorTests {

    @Autowired
    PetroChinaPageProcessor petroChinaPageProcessor;

    @Autowired
    HBasePipeline hBasePipeline;

    String[] urls = {
            "http://eportal.energyahead.com/wps/portal/ebid/!ut/p/c5/hY7JkqpAEEW_xQ8gKKCYlgjIJKOAUBsDlEEREOSB5dc33dFb-2WuMk7myUsicu0um69VNl37LruTCYm4kySyshRypsWyPgsMXYkEn5Mpy2VXnn7kMIb_uT5-__u8AcCf_Mf_zcGHkgDp6H1bkCmJ-NXC_Fr24moJ94HnhpDRdEiGZJKc-5cM7UU18jqh_XlIHZoX8QPzbWSwXOp3Rq2PIUEwgxEZwVwEAqFpctXvVAxtM1oWnM9WlsK3WcZPJS8ZYVseiep8vdwXa34nhXMBzBg2_hSHaImBPgxNA0-I4oZZ6fozpgWdd3rFwxUyGU6cbt32RRwOC9z8nZ_if_KnCy_30qJKiQerXaNiGuwyPOWaxby8SMdpBm95mQTyXDuhbSs4S4E9uvrzZWou0G5LC8FzW12kfa33iRSrPfOI0b0rKFvKbChURisEe3QKh3jERom0oyCNdI3EZlKK6vCPzwmqjpi3C1AvbTbko03A1atHdp2-ALdev00!/dl3/d3/L3dDbzIyMkEhL0lKU0FDSXdrQWdRaVFDQ0tKQUlFWWtBZ1RpUUNDQkpBSUlBIS9ZQzZfd0EhIS83X0E5M0NBVDZKSzVMOTUwSVRMUlBPVDQzRzE3/",
            "http://eportal.energyahead.com/wps/portal/ebid/!ut/p/c5/pY7JcoJAFEW_xQ-wuoFmcInI2KJgIAwbqyERkEFBENqvj6SyjVnkveWpc-uAGDy_IfciI31xaUgFQhALR3nFK7InWJjnXR6axsaXXEFhsM4-efQrR-_oHzaG8A87mGtf-y_4d93M4S8nQ7AzLvUniEAsPle4n5Xt6rnibQ_O3kOcbiDggfAQlZNykceNGjooK5Why_DjyqRswantOiSVddZYdphM55HfT2nuqgrF60aaSA5H7YbFYVRP2lGL1fgsWe_hti2C_e7e1LATOMGidl_RaiI0UA5sjdsmDYpkyD5csynF132MOPeF0SjOfao892mlSlmoEdonOuYmxzdoRNA5STn_7Z7vPNveUBJBu9sbt8nS91Cn7iElgTxWKSo5vHPNNkj8dqCnYbJlYiMpM2tp2sZHr2NOqr-k9hWtw-vmIVqJ3q-rj5pbdW_9QJcr5iwvFuBa-yEsnLzjL4svc7U7YQ!!/dl3/d3/L0lDU0NTQ1FvS1VRIS9JSFNBQ0lLRURNeW01dXBnLzRDMWI4SWtmb2lUSmVLQVEvN19BOTNDQVQ2Sks1TDk1MElUTFJQT1Q0M0dINi9zZWFyY2g!/",
    };

    //生成request
    public Request requestGenerator(String url, String dateBegin, String dateEnd) {
        Request request = new Request(url);
        Map<String, Object> params = Maps.newHashMap();
        params.put("department", "");
        params.put("noticeName", "");
        params.put("projectType", "");
        params.put("fromDate", dateBegin);
        params.put("toDate", dateEnd);
        params.put("pageNo", 1);
        if (url.contains("L3dDbzIyMkEhL0lKU0FDSXdrQWdRaVFDQ0tKQUlFWWtBZ1RpUUNDQkpBSUlBIS9ZQzZfd0EhIS83X0E5M0NBVDZKSzVMOTUwSVRMUlBPVDQzRzE3")) {
            params.put("type", "公开招标公告");
        }
        if (url.contains("L0lDU0NTQ1FvS1VRIS9JSFNBQ0lLRURNeW01dXBnLzRDMWI4SWtmb2lUSmVLQVEvN19BOTNDQVQ2Sks1TDk1MElUTFJQT1Q0M0dINi9zZWFyY2g")) {
            params.put("type", "资格预审公告");
        }

        request.setMethod(HttpConstant.Method.POST);
        request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
        request.putExtra("pageParams", params);
        return request;
    }

    @Test
    public void testAnnotation() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(petroChinaPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(petroChinaPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(hBasePipeline)
                .thread(8)
                .run();
    }

}
