package veinthrough.leetcode.enumerate;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 面试题 16.18. 模式匹配:
 * 你有两个字符串，即pattern和value。 pattern字符串由字母"a"和"b"组成，用于描述字符串中的模式。
 * 例如，字符串"catcatgocatgo"匹配模式"aabab"（其中"cat"是"a"，"go"是"b"），该字符串也匹配像"a"、"ab"和"b"这样的模式。
 * 但需注意"a"和"b"不能同时表示相同的字符串。
 * 编写一个方法判断value字符串是否匹配pattern字符串。
 */
@Slf4j
public class PatternMatching {
    @Test
    public void test_16_18() {
        Stream.of(
                patternMatching("abba", "dogcatcatdog"), // true
                patternMatching("abba", "dogcatcatfish"), // false
                patternMatching("aaaa", "dogcatcatdog"), // false
                patternMatching("", ""), // true
                patternMatching("a", ""), // true
                patternMatching("bbb", "xxxxxx"), // true
                patternMatching("bbba", "xxxxxx"), // true
                patternMatching("abba", "dogdogdogdog"), // true
                patternMatching("bbbbbbbbbbbbbbabbbbb",
                        "ppppppppppppppjsftcleifftfthiehjiheyqkhjfkyfckbtwbelfcgihlrfkrwireflijkjyppppg"), // true
                patternMatching("aaaaaaaaaaaaaabaaaaa",
                        "ppppppppppppppjsftcleifftfthiehjiheyqkhjfkyfckbtwbelfcgihlrfkrwireflijkjyppppg")) // true
                .forEach(matched -> log.info(methodLog("" + matched)));
    }

    /**
     * 分析：
     * 主要是分别根据a/b的长度枚举
     */
    private boolean patternMatching(String _pattern, String _value) {
        // boundary
        // (1)
        if (_pattern.equals("") || _value.equals("")) return _pattern.equals(_value);
        // (2)
        if (_pattern.length() == 1) return true;

        // 
        char[] pattern = _pattern.toCharArray();
        char[] value = _value.toCharArray();
        int[] count = new int[]{0, 0};
        int[] len = new int[2];
        // 1. 统计a/b数量
        for (char c : pattern) count[c - 'a']++;
        // (3) 其中一个(比如a)只有一个, 那么另一个(比如b)可以为"", 实际上就是(2)
        if (count[0] == 1 || count[1] == 1) return true;
        // (4) pattern: 1 type
        if (count[0] == 0) return matchOne(value, count[1]);
        if (count[1] == 0) return matchOne(value, count[0]);

        // 2. general
        // 注意""的情况
        // (1) 第一层循环, 枚举第一个字符‘a'的长度: 
        // 最短: 0
        // 最长: (value的长度)/('a'的个数)
        for (len[0] = 0; len[0] <= value.length / count[0]; len[0]++) {
            // (2) 确定了'a'的长度, 那么'b'的长度也可以确定
            // 但首先(value剩下的长度)必须能够被('b'的个数)整除
            if ((value.length - count[0] * len[0]) % count[1] == 0) {
                len[1] = (value.length - count[0] * len[0]) / count[1];
                // (3) 'a'/'b'都有可能为空字符串
                // 其中有一个为空, 相当于pattern中只有一个字符
                if (len[0] == 0 || len[1] == 0) {
                    if (len[0] == 0 && matchOne(value, count[1]))
                        return true;
                    if (len[1] == 0 && matchOne(value, count[0]))
                        return true;
                }
                // (4) 'a'/'b'都不为空字符串
                else if (matchTwo(pattern, value, len))
                    return true;
            }
        }
        return false;
    }

    private boolean matchOne(char[] value, int count) {
        if (value.length % count != 0) return false;
        int len = value.length / count;
        int k;
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < len; j++) {
                k = i * len + j;
                if (value[k] != value[k + len]) return false;
            }
        }
        return true;
    }

    private boolean matchTwo(char[] pattern, char[] value, int[] len) {
        char[] pa = null;
        char[] pb = null;
        for (int pi = 0, vi = 0; pi < pattern.length; pi++) {
            if (pattern[pi] == 'a') {
                // (1) first time found/build pa('a' pattern)
                if (pa == null) {
                    pa = new char[len[0]];
                    // build pa
                    System.arraycopy(value, vi, pa, 0, len[0]);
                    // 题目要求a/b不能同时表示相同的字符串
                    if (pb != null && _equals(pa, pb)) return false;
                }
                // (2) check pa in value
                else
                    for (int j = 0; j < len[0]; j++)
                        if (pa[j] != value[vi + j]) return false;

                vi += len[0];
            } else {
                // (1) first time found/build pb('b' pattern)
                if (pb == null) {
                    pb = new char[len[1]];
                    // build pb
                    System.arraycopy(value, vi, pb, 0, len[1]);
                    // 题目要求a/b不能同时表示相同的字符串
                    if (pa != null && _equals(pa, pb)) return false;
                }
                // (2) check pb in value
                else
                    for (int j = 0; j < len[1]; j++)
                        if (pb[j] != value[vi + j]) return false;

                vi += len[1];
            }
        }
        return true;
    }

    private boolean _equals(char[] chars1, char[] chars2) {
        if (chars1.length != chars2.length) return false;
        for (int i = 0; i < chars1.length; i++) {
            if (chars1[i] != chars2[i]) return false;
        }
        return true;
    }
}