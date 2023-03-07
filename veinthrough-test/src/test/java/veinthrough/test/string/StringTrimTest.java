package veinthrough.test.string;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * This program demonstrate how to trim blanks/0000... from a string to a number.
 */

@Slf4j
public class StringTrimTest {
    /**
     * 1. 正则表达式在某个字符串上的匹配
     * 1. {@link Pattern} + {@link Matcher}
     * (1) 新建正则表达式{@link Pattern}: {@link Pattern#compile(String regex, int flags)}
     * (2) 正则表达式关联某个字符串: {@link Pattern#matcher(CharSequence)}
     * 2. 当然也可以两个操作一步直接完成:
     * {@link Pattern#matches(String regex, CharSequence)}
     * 3. 匹配
     * {@link Matcher#lookingAt()}
     * {@link Matcher#start()}/{@link Matcher#end()}
     */
    @Test
    public void numberTrimTest() {
        String stream = "   00060000";

        Pattern pattern = Pattern.compile("0*",
                Pattern.CASE_INSENSITIVE);

        // trim before match
        String trimmedStream = stream.trim();
        Matcher matcher = pattern.matcher(trimmedStream);
        // 匹配到了"0*"
        if (matcher.lookingAt()) {
            // matcher.end(): the last match place of "0*"
            // 为3
            trimmedStream = trimmedStream.substring(matcher.end());
        }
        log.info(methodLog("number string", stream,
                "trimmed number string", trimmedStream));
    }
}
