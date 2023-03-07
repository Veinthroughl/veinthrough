package veinthrough.leetcode.string;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 字符中所有出现的数字前后加上符号“*”，其他字符保持不变
 * 输入：Jkdi234klowe90a3
 * 输出：Jkdi*234*klowe*90*a*3*
 */
@Slf4j
public class MarkNums {
    @Test
    public void test() {
        Stream.of("Jkdi234klowe90a3",
                "aour9q4ja0f80a8fc")
                .forEach(str -> log.info(methodLog(
                        str, markNums(str))));
    }
    private String markNums(String str) {
        if (str == null || str.length() == 0) return str;

        char[] chs = str.toCharArray();
        StringBuilder result = new StringBuilder();
        for (char ch : chs) {
            if(ch>='0'&&ch<='9')
                result.append("*").append(ch).append("*");
            else
                result.append(ch);
        }
        return result.toString().replace("**", "");
    }
}
