package veinthrough.test.string;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * How to split string.
 *
 * APIs
 * 1. 使用 {@link String#split(String)}:
 * 不返回分隔符, 连续分隔符会分别一个一个处理, 但是会忽略最后的连续分隔符
 * 2. 使用 {@link StringTokenizer}
 * (2) {@link StringTokenizer#StringTokenizer(String, String, boolean returnDelims)}
 * > returnDelims为true: 返回分隔符, 且连续分隔符会分别一个一个处理(返回)
 * > returnDelims为false: 不返回分隔符，连续分隔符当作整体一个处理
 * (1) 使用{@link StringTokenizer#StringTokenizer(String, String)},
 * 相当于returnDelims为false
 */
@Slf4j
public class StringSplitTest {
    private static final String str = "Aa|||D|E||";
    @SuppressWarnings("RegExpEmptyAlternationBranch")
    private static final String delim = "|";
    private static final String delimRegex = "\\|";
    private static final String empty = "";

    /**
     * 1. 使用 {@link String#split(String)}:
     * 不返回分隔符, 连续分隔符会分别一个一个处理, 但是会忽略最后的连续分隔符;
     * 分割"Aa|||D|E||":
     * \|(5个):  Aa, , , D, E;
     * |:  A, a, |, |, |, D, |, E, |, |
     */
    @Test
    public void splitTest() {
        // "a", "", "", "D", "E"
        log.info(methodLog(
                // "\\|"支持正则表达式, "|"不支持
                delimRegex, arrayString(str.split(delimRegex)),
                delim, arrayString(str.split(delim))));
    }

    /**
     * 2.(2) {@link StringTokenizer#StringTokenizer(String, String, boolean returnDelims)}
     * returnDelims为false: 不返回分隔符，连续分隔符当作整体一个处理;
     * 分割"Aa|||D|E||":
     * 3个: "Aa", "D", "E"
     */
    @Test
    public void falseTokenizerTest() {
        List<String> strList = new ArrayList<>();
        // Unless you ask StringTokenizer to give you the tokens,
        // it silently discards multiple null tokens.
        // StringTokenizer( str, delim, false) is the same as StringTokenizer( str, delim);
        StringTokenizer st = new StringTokenizer(str, delim, false);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            strList.add(token);
        }
        log.info(methodLog(
                arrayString(strList.toArray(new String[0]))));
    }

    /**
     * 2.(2) {@link StringTokenizer#StringTokenizer(String, String, boolean returnDelims)}
     * returnDelims为true: 返回分隔符, 且连续分隔符会分别一个一个处理(返回),
     * 分割"Aa|||D|E||", 可以在这基础上达到需要的效果:
     * 7个: "Aa", "", "", "D", "E", "", ""
     */
    @Test
    public void trueTokenizerTest() {
        List<String> strList = new ArrayList<>();
        // Unless you ask StringTokenizer to give you the tokens,
        // it silently discards multiple null tokens.
        StringTokenizer st = new StringTokenizer(str, delim, true);
        int i = 0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            // 处理分隔符
            if (token.equals(delim)) {
                // i表示token(包括分隔的字符串和分隔符)的数量,
                // list.size()不包括分隔符
                // (1) consecutive delimiters
                if (strList.size() < ++i) {
                    strList.add(empty);
                }
                // (2) the last is delim
                if (!st.hasMoreTokens()) {
                    strList.add(empty);
                }
            } else {
                strList.add(token);
            }
        }
        log.info(methodLog(arrayString(strList.toArray(new String[0]))));
    }

    private String arrayString(String[] strs) {
        // noinspection OptionalGetWithoutIsPresent
        return Stream.of(strs)
                .map(str -> ", " + str)
                .reduce((str1, str2) -> str1 + str2)
                .get()
                // delete the first ", "
                .substring(1);
    }
}