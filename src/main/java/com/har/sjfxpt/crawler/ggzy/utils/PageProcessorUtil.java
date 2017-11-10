package com.har.sjfxpt.crawler.ggzy.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dongqi
 */
@Slf4j
public final class PageProcessorUtil {

    final static String REGEX_TAG_STYLE = "((<style>)|(<style type=.+))((\\s+)|(\\S+)|(\\r+)|(\\n+))(.+)((\\s+)|(\\S+)|(\\r+)|(\\n+))(<\\/style>)";

    public static String extractTextByWhitelist(Element root) {
        if (root == null) return null;
        String html = root.html();
        Whitelist whitelist = Whitelist.none();
        whitelist.removeTags("script");
        String textContent = Jsoup.clean(html, whitelist);
        textContent = StringUtils.removeAll(textContent, "<!-{2,}.*?-{2,}>|(&nbsp;)|<o:p>|</o:p>");
        return textContent;
    }

    public static String formatElementsByWhitelist(Element root) {
        if (root == null) return null;
        String html = root.html();
        Whitelist whitelist = Whitelist.relaxed();
        whitelist.removeTags("style");
        whitelist.removeTags("script");
        whitelist.removeAttributes("table", "style", "width", "height");
        whitelist.removeAttributes("td", "style", "width", "height");
        String formatContent = Jsoup.clean(html, whitelist);
        formatContent = StringUtils.removeAll(formatContent, "<!-{2,}.*?-{2,}>|(&nbsp;)|<o:p>|</o:p>");
        return formatContent;
    }

    final static Pattern yyyymmddhhmmPattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}");
    final static Pattern yyyymmddPattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");

    /**
     * @return
     */
    public static String dataTxt(String date) {
        if (StringUtils.contains(date, "发布时间：")) {
            date = StringUtils.remove(date, "发布时间：");
        }
        if (StringUtils.contains(date, "发布：")) {
            date = StringUtils.remove(date, "发布：");
        }

        Matcher matcher = yyyymmddhhmmPattern.matcher(date);
        String dataStr = null;
        if (matcher.find()) {
            dataStr = matcher.group();
        }

        matcher = yyyymmddPattern.matcher(date);
        if (matcher.matches()) {
            dataStr = matcher.group() + DateTime.now().toString(" HH:mm");
        }

        return StringUtils.defaultString(dataStr, DateTime.now().toString("yyyy-MM-dd HH:mm"));
    }

    /**
     * 正文解析
     */
    public static String formatText(String textContent) {
        String[] removeText = {
                "/wps(.+?)    ",
                "首页(.+?)详细信息",
        };
        for (int i = 0; i < removeText.length; i++) {
            textContent = StringUtils.removeAll(textContent, removeText[i]);
        }
        return textContent;
    }

    /**
     * 时间比较
     */
    public static boolean timeCompare(String date) {

        String dataStr = null;

        Matcher matcher = yyyymmddPattern.matcher(date);

        if (matcher.find()) {
            dataStr = matcher.group();
        }
        if (StringUtils.isNotBlank(dataStr)) {
            DateTime dateTime = new DateTime(dataStr);

            DateTime dateTime1=new DateTime(new DateTime(new Date()).toString("yyyy-MM-dd"));

            if(dateTime.isBefore(dateTime1)){
                return true;
            }else {
                return false;
            }
        }
        return false;
    }
}
