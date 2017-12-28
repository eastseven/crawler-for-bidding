package com.har.sjfxpt.crawler.ggzyprovincial.ggzyshanxi;

import com.har.sjfxpt.crawler.core.model.DataItemDTO;
import com.har.sjfxpt.crawler.core.model.SourceCode;
import com.har.sjfxpt.crawler.core.utils.PageProcessorUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.jsoup.nodes.Element;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.AfterExtractor;

/**
 * Created by Administrator on 2017/12/12.
 *
 * @author luofei
 * @author dongqi
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@Document(collection = "data_item_ggzy_shanxi")
public class GGZYShanXiDataItem extends DataItemDTO implements AfterExtractor {

    public static final String collection = "data_item_ggzy_shanxi";

    public GGZYShanXiDataItem(String url) {
        this.id = DigestUtils.md5Hex(url);
        this.url = url;
        this.province = "山西";
        this.source = SourceCode.GGZYSHANXI.getValue();
        this.sourceCode = SourceCode.GGZYSHANXI.name();
        this.createTime = DateTime.now().toString("yyyyMMddHH");
    }

    @Id
    private String id;

    @Field("code")
    private String projectCode;

    @Override
    public void afterProcess(Page page) {

        Element body = page.getHtml().getDocument().body();
        if (!body.select("div.jiaoyihuanjie.ct").isEmpty()) {
            for (Element td : body.select("div.table_project_container table.table_content tr td")) {
                String text = td.text();
                if (text.equalsIgnoreCase("项目名称")) {
                    this.setProjectName(td.nextElementSibling().text());
                }

                if (text.equalsIgnoreCase("招标人")) {
                    this.setPurchaser(td.nextElementSibling().text());
                }
            }
        }

        if (!body.select("div.notice_content").isEmpty()) {
            String html = PageProcessorUtil.formatElementsByWhitelist(body.select("div.notice_content").first());
            this.setFormatContent(html);
        }
    }
}
