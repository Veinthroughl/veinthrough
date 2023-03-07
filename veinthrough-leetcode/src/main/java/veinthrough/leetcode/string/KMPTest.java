package veinthrough.leetcode.string;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.string.KMP;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第28题: 字符串匹配
 * 实现 strStr() 函数。
 * 给你两个字符串 haystack 和 needle ，请你在 haystack 字符串中找出 needle 字符串出现的第一个位置（下标从 0 开始）。
 * 如果不存在，则返回  -1 。
 */
@SuppressWarnings("ConstantConditions")
@Slf4j
public class KMPTest {
    @Test
    public void testNext() {
        Stream.of(
                "dadacdadadd",
                "abcdabd", "abcdabc",
                "aaaa", "aaaadc",
                "abab",
                "dabcdabde")
                .forEach(pattern ->
                        ImmutableMap.<String, Function<String, int[]>>of(
                                "next", KMP::getNext,
                                "next optimized", KMP::getNext_Optimized)
                                .forEach((taskName, task) ->
                                        log.info(methodLog(
                                                taskName + " of " + pattern,
                                                Arrays.toString(task.apply(pattern))))));
    }

    /**
     * 第28题: 寻找第一个位置
     */
    @Test
    public void test28() {

        Stream.of(
                KMP.search("hello", "ll"),
                KMP.search("aaaaa", "bba"))
                .forEach(index -> log.info(methodLog("" + index)));
    }

    /**
     * 寻找所有位置
     */
    @Test
    public void testSearchAll() {
        Stream.of(
                KMP.searchAll("wordgoodgoodgoodbestword", "good"))
                .forEach(result ->
                        log.info(methodLog(result.toString())));
    }
}
