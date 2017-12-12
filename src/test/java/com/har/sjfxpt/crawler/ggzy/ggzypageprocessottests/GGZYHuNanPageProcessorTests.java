package com.har.sjfxpt.crawler.ggzy.ggzypageprocessottests;

import com.har.sjfxpt.crawler.ggzy.utils.SiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2017/12/8.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GGZYHuNanPageProcessorTests {


    @Test
    public void testGGZYHuNanPageProcessor() {

    }


    @Test
    public void testOthers() throws IOException {
        String urlAdress = "http://www.spdbccc.com.cn/spdb/cupd/imageServlet";
        URL url = new URL(urlAdress);
        DataInputStream dataInputStream = new DataInputStream(url.openStream());
        String imageName = "D:/pircture/test.jpg";
        FileOutputStream fileOutputStream = new FileOutputStream(new File(imageName));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = dataInputStream.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        byte[] context = output.toByteArray();
        fileOutputStream.write(output.toByteArray());
        dataInputStream.close();
        fileOutputStream.close();
    }


}
