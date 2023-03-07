package veinthrough.leetcode.digit;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static java.lang.Math.max;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第66题：二进制加一
 * 给定一个由 整数 组成的 非空 数组所表示的非负整数，在该数的基础上加一。
 * 最高位数字存放在数组的首位，数组中每个元素只存储单个数字。
 * 你可以假设除了整数 0 之外，这个整数不会以零开头。
 *
 * 第67题：二进制求和
 * 给你两个二进制字符串，返回它们的和（用二进制表示）。
 * 输入为 非空 字符串且只包含数字 1 和 0。
 */
@Slf4j
public class AddBinary {
    @Test
    public void test66() {
        Stream.of(
                new int[]{1, 2, 3},
                new int[]{0},
                new int[]{9, 9, 9},
                new int[]{4, 3, 2, 1})
                .forEach(nums -> log.info(methodLog(
                        Arrays.toString(nums),
                        Arrays.toString(plusOne(nums)))));
    }

    @Test
    public void test67() {
        Stream.of(
                addBinary("1", "111"),
                addBinary("11", "1"))
                .forEach(result -> log.info(methodLog(result)));
    }


    private int[] plusOne(int[] digits) {
        int len;
        int carry = 1;
        int sum;
        for (int i = (len = digits.length) - 1; i >= 0; i--) {
            // 注意这里循环引用, 先算carry和先算digits[i]都不行，必须有一个sum
            sum = digits[i] + carry;
            carry = sum / 10;
            digits[i] = sum % 10;
        }

        if (carry == 0) return digits;
        int[] res = new int[len + 1];
        res[0] = carry;
        System.arraycopy(res, 1, digits, 0, len);
        return res;
    }

    private String addBinary(String a, String b) {
        char[] s1 = a.toCharArray();
        char[] s2 = b.toCharArray();
        char[] result = new char[max(s1.length, s2.length) + 1];
        char carry = '0';
        char[] sum;
        for (int i = s1.length - 1, j = s2.length - 1, k = result.length - 1;
             i >= 0 || j >= 0; i--, j--, k--) {
            if (i < 0) {
                // may have carry
                sum = _sum(carry, s2[j]);
                carry = sum[0];
                result[k] = sum[1];
            } else if (j < 0) {
                sum = _sum(carry, s1[i]);
                carry = sum[0];
                result[k] = sum[1];
            } else {
                sum = _sum(carry, s1[i], s2[j]);
                carry = sum[0];
                result[k] = sum[1];
            }
        }
        result[0] = carry;
        return result[0] == '1' ? new String(result) : new String(result).substring(1);
    }

    private char[] _sum(char... binaryDigits) {
        int count1 = 0;
        for (char binaryDigit : binaryDigits)
            if (binaryDigit == '1')
                count1++;
        if (count1 == 3) return new char[]{'1', '1'};
        if (count1 == 2) return new char[]{'1', '0'};
        if (count1 == 1) return new char[]{'0', '1'};
        return new char[]{'0', '0'};
    }
}