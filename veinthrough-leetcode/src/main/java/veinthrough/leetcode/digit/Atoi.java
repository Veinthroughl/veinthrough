package veinthrough.leetcode.digit;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static java.lang.Character.isDigit;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第8题：
 * 请你来实现一个 myAtoi(string s) 函数，使其能将字符串转换成一个 32 位有符号整数（类似 C/C++ 中的 atoi 函数）。
 * 函数 myAtoi(string s) 的算法如下：
 * 1. 读入字符串并丢弃无用的前导空格
 * 2. 检查下一个字符（假设还未到字符末尾）为正还是负号，读取该字符（如果有）。
 * 确定最终结果是负数还是正数。 如果两者都不存在，则假定结果为正。
 * 3. 读入下一个字符，直到到达下一个非数字字符或到达输入的结尾。字符串的其余部分将被忽略。
 * 4. 将前面步骤读入的这些数字转换为整数（即，"123" -> 123， "0032" -> 32）.
 * 如果没有读入数字，则整数为 0 。必要时更改符号（从步骤 2 开始）。
 * 5. 如果整数数超过 32 位有符号整数范围 [−2^31,  2^31 − 1] ，需要截断这个整数，使其保持在这个范围内。
 * 具体来说，小于 −2^31 的整数应该被固定为 −2^31 ，大于 2^31 − 1 的整数应该被固定为 2^31 − 1 。
 * 6. 返回整数作为最终结果。
 * 注意：
 * 本题中的空白字符只包括空格字符 ' ' 。
 * 除前导空格或数字后的其余字符串外，请勿忽略 任何其他字符。
 */
@Slf4j
public class Atoi {
    // [-2147483648,2147483647]
    private static final int MAX_DIGITS_INT = 10;
    private static final int MAX_HIGHEST_DIGIT_INT = 2;

    @Test
    public void test8() {
        // int: -2147483648 - 2147483647
        Stream.of(
                "   -42",
                "   -00042",
                "   -0j0042",
                "4193 with words",
                "words and 987",
                "2147483648",
                "-2147483646",
                "-91283472332",
                "-2147483648",
                "2147483646")
                .forEach(str -> log.info(methodLog(str, atoi(str) + "")));
    }

    private int atoi(String str) {
        // 1. empty
        if (str == null || str.equals("")) return 0;

        char[] chars = str.toCharArray();
        int i = 0;
        boolean negative = false;
        // 2. trim spaces(only ' ')
        while (i < chars.length && chars[i] == ' ') i++;
        // 3. first ch
        if (i >= chars.length) return 0;
        if (chars[i] == '+' || chars[i] == '-') {
            negative = chars[i] == '-';
            i++;
        } else if (!isDigit(chars[i])) return 0;
        // 4. trim '0'
        while (i < chars.length && chars[i] == '0') i++;
        // 5. no digits
        if (i >= chars.length || !isDigit(chars[i])) return 0;
        // 6. get digits
        int[] digits = new int[chars.length - i];
        digits[0] = chars[i] - '0';
        int count = 1;
        i++;
        while (i < chars.length && isDigit(chars[i])) {
            digits[count++] = chars[i++] - '0';
            // overflow by highest digit or num of digits
            if (count == MAX_DIGITS_INT && digits[0] > MAX_HIGHEST_DIGIT_INT ||
                    count > MAX_DIGITS_INT) {
                return negative ? MIN_VALUE : MAX_VALUE;
            }
        }
        // 7. handle digits, should be long
        // 【其实一边累加一边判断是否溢出也可以】
        long result = 0;
        for (i = 0; i < count; i++) {
            result = 10 * result + digits[i];
        }
        // underflow
        if (negative && result >= (long) MAX_VALUE + 1) return MIN_VALUE;
        // overflow
        if (result > (long) MAX_VALUE) return MAX_VALUE;

        return negative ? 0 - (int) result : (int) result;
    }
}