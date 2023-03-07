package veinthrough.leetcode.digit;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第7题：
 * 给你一个 32 位的有符号整数x，返回 x 中每位上的数字反转后的结果。
 * 如果反转后整数超过 32 位的有符号整数的范围 [−2^31,  2^31 − 1] ，就返回 0。
 * 假设环境不允许存储 64 位整数（有符号或无符号）。
 *
 * 主要考察各种特殊情况的考虑。
 */
@Slf4j
public class ReverseNum {
    private static final int MAX_DIGITS_INT = 10;

    @Test
    public void test7() {
        // int: -2147483648 - 2147483647
        Stream.of(
                // overflow
                1534236469,
                // overflow
                -2147483648,
                123, -123, -120)
                .forEach(num -> log.info(methodLog("" + num, "" + reverse(num))));
    }

    /**
     * 方法1：
     * 1. 关于digits <--> num, 见文档《#digit》
     * (1) [1,2,3] <--> 123,
     * (2) [1,2,3] <--> 321, 这里使用的是这个,
     * 回文串的判断也使用的是这个{@link veinthrough.leetcode.dbp.IsPalindrome#isPalindromeByLRNum}
     * 2. 因为使用的是int而不是long, 在中间就应该判断溢出, 而不是最后判断溢出。
     */
    private int reverse(int x) {
        // (1)
        if (x > -10 && x < 10) return x;

        // (2)
        int digit;
        int result = 0;
        // 这里是x!=0而不是x>0
        while (x != 0) {
            // 在1/10前就截掉，防止溢出
            if (result > Integer.MAX_VALUE / 10 || result < Integer.MIN_VALUE / 10)
                return 0;
            digit = x % 10;
            x = x / 10;
            result = 10 * result + digit;
        }
        return result;
    }

    /**
     * 方法2:
     * 1. 不符合"假设环境不允许存储 64 位整数"的条件
     * 2. 其实这里和方法1的思路一样, 如果使用int而不是long,
     * 应该在中间就判断溢出, 而不是到最后判断溢出。
     */
    @SuppressWarnings("unused")
    private int reverse2(int x) {
        // (1)
        if (x > -10 && x < 10) return x;

        boolean negative = x < 0;
        // should use _x(long) instead x(int)
        long _x = Math.abs((long) x);
        int[] digits = new int[MAX_DIGITS_INT];
        // should be long
        long result = 0;
        for (int i = 0; _x != 0; i++, _x /= 10) {
            digits[i] = (int) (_x % 10);
            result = 10 * result + digits[i];
        }
        // (2) overflow
        if (!negative && result > Integer.MAX_VALUE ||
                negative && result > (long) Integer.MAX_VALUE + 1) return 0;
        // (3)
        return (int) (negative ? 0 - result : result);
    }
}