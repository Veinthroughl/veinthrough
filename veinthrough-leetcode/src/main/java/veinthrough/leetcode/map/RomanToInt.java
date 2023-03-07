package veinthrough.leetcode.map;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第13题：
 * 给定一个罗马数字，将其转换成整数。
 */
@Slf4j
public class RomanToInt {
    // 0,  1,  2,    3,    |  4,  5,  6
    // 1,  10, 100,  1000, |  5,  50, 500
    private static final char[] keys = new char[]{'I', 'X', 'C', 'M', 'V', 'L', 'D'};
    private static final int[] values = new int[]{1, 10, 100, 1000, 5, 50, 500};

    @Test
    public void test13() {
        Stream.of(
                "III", "IV", "IX", "LVIII", "MCMXCIV", "MVIII")
                .forEach(roman -> log.info(methodLog(
                        roman, ""+romanToInt(roman), ""+romanToInt2(roman))));
    }

    /**
     * 方法1：
     * 个位: 1-3, 4, 5-8, 9
     * 十位: 1-3, 4, 5-8, 9
     * 百位: 1-3, 4, 5-8, 9
     * 千位: 1-3
     *
     * 1-M: M*(1-3)
     * 5-D: dc*(5-8)
     * 1-C: CM(9), CD(4), C*(1-3)
     * 5-L: LX*(5-8)
     * 1-X: XC(9), X(4), X*(1-3)
     * 5-V: VI*(5-8)
     * 1-I: IV(9), IX(4), I*(1-3)
     */
    private int romanToInt(String _s) {
        char[] s = _s.toCharArray();
        int number = 0;
        for (int i = 0, j = i + 1, first, second; i < s.length; j = i + 1) {
            first = digit(s[i]);
            // 5-8
            if (first >= 4) {
                number += values[first];
                while (j < s.length && s[j] == keys[first - 4]) j++;
                number += (j - i - 1) * values[first - 4];
                // 4/9, M is an exception
                // (1) 4: second==first+4
                // (2) 9: second==first+1
            } else if (j < s.length && s[i] != 'M' &&
                    ((second = digit(s[j])) == first + 1 || second == first + 4)) {
                number += values[second] - values[first];
                j++;
                // 1-3
            } else {
                while (j < s.length && s[j] == s[i]) j++;
                number += (j - i) * values[first];
            }
            i = j;
        }
        return number;
    }

    private int digit(char ch) {
        switch (ch) {
            case 'I':
                return 0;
            case 'X':
                return 1;
            case 'C':
                return 2;
            case 'M':
                return 3;
            case 'V':
                return 4;
            case 'L':
                return 5;
            case 'D':
                return 6;
            default:
                return -1;
        }
    }

    /**
     * 方法2(题解)：
     * (1) 左边数字≤右边数字, +左边+右边
     * 例如XXVIIX+X+V+I+I=10+10+5+1+1=27。
     * (2) 左边数字>右边数字, -左边+右边
     * 例如XIV可视作X−I+V=10−1+5=14。
     */
    private int romanToInt2(String _s) {
        // 1.
        if (_s == null) return 0;

        // 2. 
        int num = 0;
        char[] s = _s.toCharArray();
        int l = s.length;
        int left, right;
        for (int i = 0; i < l; i++) {
            left = values[digit(s[i])];
            // left/right一起处理
            if (i < l - 1 && (right=values[digit(s[i + 1])]) > left) {
                num -= left;
                num += right;
                i++;
            }
            // 这一轮不能处理right, 因为下一轮right可能小于(right的右边)
            else
                num += left;
        }
        return num;
    }
}