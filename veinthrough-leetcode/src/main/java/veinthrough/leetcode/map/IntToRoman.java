package veinthrough.leetcode.map;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第12题：
 * 罗马数字包含以下七种字符： I， V， X， L，C，D 和 M。
 * 字符          数值
 * I             1
 * V             5
 * X             10
 * L             50
 * C             100
 * D             500
 * M             1000
 * 例如， 罗马数字2写做II，即为两个并列的1。12写做II，即为X+I。27写做XVII, 即为XX+V+II。
 * 通常情况下，罗马数字中小的数字在大的数字的右边。但也存在特例，例如4不写做IIII，而是IV。
 * 数字1在数字5的左边，所表示的数等于大数5减小数1得到的数值 4 。同样地，数字9表示为IX。
 * 这个特殊的规则只适用于以下六种情况：
 * I 可以放在 V (5) 和 X (10) 的左边，来表示 4 和 9。
 * X 可以放在 L (50) 和 C (100) 的左边，来表示 40 和 90。 
 * C 可以放在 D (500) 和 M (1000) 的左边，来表示 400 和 900。
 * 给定一个整数，将其转为罗马数字。输入确保在 1 到 3999 的范围内。
 */
@Slf4j
public class IntToRoman {
    private static final char[] chs1 = new char[]{'I', 'X', 'C', 'M'};
    private static final char[] chs5 = new char[]{'V', 'L', 'D'};

    @Test
    public void test12() {
        Stream.of(3, 4, 9, 58, 1994).forEach(
                num -> log.info(methodLog(
                        "" + num, intToRoman(num))));
    }

    @Test
    public void test12_2() {
        Stream.of(3, 4, 9, 58, 1994).forEach(
                num -> log.info(methodLog(
                        "" + num, intToRoman2(num))));
    }

    /**
     * 方法1：
     * 个位: 1-3, 4, 5-8, 9
     * 十位: 1-3, 4, 5-8, 9
     * 百位: 1-3, 4, 5-8, 9
     * 千位: 1-3
     */
    private String intToRoman(int num) {
        if (num == 0) return "";

        StringBuilder str = new StringBuilder();
        for (int i = 0; num != 0; i++) {
            str.insert(0, eachDigit(i, num % 10));
            num /= 10;
        }
        return str.toString();
    }

    // handle each digit
    private String eachDigit(int index, int digit) {
        if (digit == 0) return "";

        StringBuilder str = new StringBuilder();

        // 1-3
        if (digit >= 1 && digit <= 3) {
            while (digit-- > 0) str.append(chs1[index]);
        // 4
        } else if (digit == 4) {
            str.append(chs1[index]).append(chs5[index]);
        // 9
        } else if (digit == 9) {
            str.append(chs1[index]).append(chs1[index + 1]);
        // 5-8
        } else {
            str.append(chs5[index]);
            while (digit-- > 5) str.append(chs1[index]);
        }
        return str.toString();
    }

    /**
     * 方法2(题解)
     */
    private String intToRoman2(int num) {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] reps = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 13; i++) {
            while (num >= values[i]) {
                num -= values[i];
                res.append(reps[i]);
            }
        }
        return res.toString();
    }
}