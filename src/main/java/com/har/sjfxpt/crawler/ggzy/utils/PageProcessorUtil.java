package com.har.sjfxpt.crawler.ggzy.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

@Slf4j
public final class PageProcessorUtil {

    /**
     * 抽取标签文本内容
     * 替换<code>extractText(Elements elements)</code>
     *
     * @param root
     * @return
     */
    public static String extractText(Element root) {
        if (root == null) return null;
        String html = root.html();
        String formatContent = StringUtils.removeAll(html, "<style>.*</style>");
        formatContent = StringUtils.removeAll(formatContent, "<\\w+[^>]*>|</\\w+>|<!-{2,}.*?-{2,}>|(&nbsp;)");
        return formatContent;
    }

    /**
     * 格式化指定标签，将标签属性全部剔除
     *
     * @param root
     * @return <tag>文本内容1</tag><tag>文本内容2</tag><tag>文本内容3</tag>
     */
    public static String formatElements(Element root) {
        if (root == null) return null;
        String html = root.html();
        final String regex = "(style=\".*?\")|(width=\".*?\")|(height=\".*?\")|<!-{2,}.*?-{2,}>|(&nbsp;)|<style>.*</style>";
        String formatContent = StringUtils.removeAll(html, regex);
        return formatContent;
    }

}
