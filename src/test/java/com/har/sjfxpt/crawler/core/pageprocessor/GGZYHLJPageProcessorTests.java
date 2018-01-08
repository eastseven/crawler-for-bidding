package com.har.sjfxpt.crawler.core.pageprocessor;

import com.har.sjfxpt.crawler.core.annotation.SourceModel;
import com.har.sjfxpt.crawler.core.pipeline.HBasePipeline;
import com.har.sjfxpt.crawler.core.utils.SourceConfigAnnotationUtils;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhlj.GGZYHLJPageProcessor;
import com.har.sjfxpt.crawler.ggzyprovincial.ggzyhlj.GGZYHLJPipeline;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.List;
import java.util.stream.Collectors;

import static com.har.sjfxpt.crawler.ggzyprovincial.ggzyhlj.GGZYHLJSpiderLauncher.requestJudgment;

/**
 * Created by Administrator on 2017/12/5.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYHLJPageProcessorTests {

    @Autowired
    GGZYHLJPageProcessor ggzyhljPageProcessor;

    @Autowired
    GGZYHLJPipeline ggzyhljPipeline;

    @Autowired
    HBasePipeline hBasePipeline;

    @Test
    public void testGGZYHLJPageProcessor() {

        String[] urls = {
                "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=1",
                "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=5",
                "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=7",
                "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=4",
                "http://hljggzyjyw.gov.cn/trade/tradezfcg?cid=16&pageNo=1&type=3",
        };

        Request[] requests = new Request[urls.length];
        for (int i = 0; i < urls.length; i++) {
            requests[i] = requestJudgment(urls[i]);
        }

        Spider.create(ggzyhljPageProcessor)
                .addRequest(requests)
                .addPipeline(ggzyhljPipeline)
                .thread(4)
                .run();
    }

    @Test
    public void testGGYZHLJAnnotation() {
        List<SourceModel> sourceModelList = SourceConfigAnnotationUtils.find(ggzyhljPageProcessor.getClass());
        List<Request> requestList = sourceModelList.parallelStream().map(SourceModel::createRequest)
                .collect(Collectors.toList());
        Spider.create(ggzyhljPageProcessor)
                .addRequest(requestList.toArray(new Request[requestList.size()]))
                .addPipeline(hBasePipeline)
                .thread(8)
                .run();
    }

}
