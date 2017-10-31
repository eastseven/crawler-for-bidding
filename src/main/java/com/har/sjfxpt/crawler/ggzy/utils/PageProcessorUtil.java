package com.har.sjfxpt.crawler.ggzy.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dongqi
 */
@Slf4j
public final class PageProcessorUtil {

    final static String REGEX_TAG_STYLE = "((<style>)|(<style type=.+))((\\s+)|(\\S+)|(\\r+)|(\\n+))(.+)((\\s+)|(\\S+)|(\\r+)|(\\n+))(<\\/style>)";

    /**
     * 抽取标签文本内容
     * 替换<code>extractText(Elements elements)</code>
     *
     * @param root
     * @return
     */
    @Deprecated
    public static String extractText(Element root) {
        if (root == null) return null;

        String html = root.html();
        String formatContent = StringUtils.removeAll(html, REGEX_TAG_STYLE);
        formatContent = StringUtils.removeAll(formatContent, "<o:p>|</o:p>");
        formatContent = StringUtils.removeAll(formatContent, "<\\w+[^>]*>|</\\w+>|<!-{2,}.*?-{2,}>|(&nbsp;)");
        return formatContent;
    }

    public static String extractTextByWhitelist(Element root) {
        if (root == null) return null;
        String html = root.html();
        String textContent = Jsoup.clean(html, Whitelist.none());
        textContent = StringUtils.removeAll(textContent, "<!-{2,}.*?-{2,}>|(&nbsp;)|<o:p>|</o:p>");
        return textContent;
    }

    /**
     * 格式化指定标签，将标签属性全部剔除
     *
     * @param root
     * @return <tag>文本内容1</tag><tag>文本内容2</tag><tag>文本内容3</tag>
     */
    @Deprecated
    public static String formatElements(Element root) {
        if (root == null) return null;
        String html = root.html();
        final String regex = "(style=\".*?\")|(width=\".*?\")|(height=\".*?\")|<!-{2,}.*?-{2,}>|(&nbsp;)|<o:p>|</o:p>";
        String formatContent = StringUtils.removeAll(html, REGEX_TAG_STYLE);
        formatContent = StringUtils.removeAll(formatContent, regex);
        return formatContent;
    }

    public static String formatElementsByWhitelist(Element root) {
        if (root == null) return null;
        String html = root.html();
        Whitelist whitelist = Whitelist.relaxed();
        whitelist.removeTags("style");
        whitelist.removeAttributes("table", "style", "width", "height");
        whitelist.removeAttributes("td", "style", "width", "height");
        String formatContent = Jsoup.clean(html, whitelist);
        formatContent = StringUtils.removeAll(formatContent, "<!-{2,}.*?-{2,}>|(&nbsp;)|<o:p>|</o:p>");
        return formatContent;
    }

   final static Pattern pattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}");

    /**
     * @return
     */
    public static String dataTxt(String date) {
        Matcher matcher = pattern.matcher(date);
        String dataStr = null;
        if (matcher.find()) {
            dataStr = matcher.group();
        }

        return StringUtils.defaultString(dataStr, DateTime.now().toString("yyyy-MM-dd HH:mm"));
    }
}
