package veinthrough.leetcode.string;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第58题，最后一个单词的长度,
 * 给你一个字符串 s，由若干单词组成，单词前后用一些空格字符隔开。返回字符串中 最后一个 单词的长度。
 * 单词 是指仅由字母组成、不包含任何空格字符的最大子字符串。
 */
@Slf4j
public class LastWord {
    @Test
    public void test58() {
        Stream.of(
                "Hello Word", // 4
                "   fly me   to   the moon  ", // 4
                "luffy is still joyboy", // 6
                "Veinthrough", // 11
                "   ") // 0
                .forEach(str -> log.info(methodLog(
                        "Length of last word of " + str, "" + lengthOfLastWord(str))));
    }

    private int lengthOfLastWord(String s) {
        int len = s.length();
        int end = len - 1;
        while (end >= 0 && s.charAt(end) == ' ') end--;
        int start = end;
        while (start >= 0 && s.charAt(start) != ' ') start--;
        return end - start;
    }
}
