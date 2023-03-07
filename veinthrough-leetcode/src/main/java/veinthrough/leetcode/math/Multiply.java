package veinthrough.leetcode.math;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第43题: 符串相乘
 * 给定两个以字符串形式表示的非负整数num1和num2，返回num1和num2的乘积，它们的乘积也表示为字符串形式。
 */
@Slf4j
public class Multiply {
    private static final int[][] table = new int[][]{
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {2, 4, 6, 8, 10, 12, 14, 16, 18},
            {3, 6, 9, 12, 15, 18, 21, 24, 27},
            {4, 8, 12, 16, 20, 24, 28, 32, 36},
            {5, 10, 15, 20, 25, 30, 35, 40, 45},
            {6, 12, 18, 24, 30, 36, 42, 48, 54},
            {7, 14, 21, 28, 35, 42, 49, 56, 63},
            {8, 16, 24, 32, 40, 48, 56, 64, 72},
            {9, 18, 27, 36, 45, 54, 63, 72, 81}};

    @Test
    public void test43() {
        Stream.of("123", "999", "88").forEach(num1 ->
                Stream.of("456", "999", "88").forEach(num2 ->
                        log.info((methodLog(
                                num1 + "*" + num2, multiply(num1, num2))))));
    }

    private String multiply(String _num1, String _num2) {
        // boundary
        if (_num1.equals("0") || _num2.equals("0")) return "0";
        //
        char[] num1 = _num1.toCharArray();
        char[] num2 = _num2.toCharArray();
        int len1 = num1.length;
        int len2 = num2.length;
        int len = len1 + len2;
        int[] products = new int[len1 + len2];
        int product;
        for (int i = len1 - 1, d1 = 0; i >= 0; i--, d1++) {
            for (int j = len2 - 1, d2 = 0; j >= 0; j--, d2++) {
                if (num1[i] != '0' && num2[j] != '0') {
                    product = table[num1[i] - '1'][num2[j] - '1'];
                    int high = product / 10;
                    int low = product % 10;
                    int digit = d1 + d2, carry;
                    // (1) low digit
                    product = products[digit] + low;
                    products[digit] = product % 10;
                    carry = product / 10;

                    // (2) high digit
                    product = products[++digit] + high + carry;
                    products[digit] = product % 10;
                    carry = product / 10;

                    // (3) 可能还有进位, 需要循环处理
                    while (carry != 0) {
                        product = products[++digit] + carry;
                        products[digit] = product % 10;
                        carry = product / 10;
                    }
                }
            }
        }

        StringBuilder result = new StringBuilder();
        for (int i = products[len - 1] == 0 ? len - 2 : len - 1; i >= 0; i--)
            result.append(products[i]);
        return result.toString();
    }
}
