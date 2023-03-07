package veinthrough.leetcode.math;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static java.lang.Math.ceil;
import static java.lang.Math.pow;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第69题：x的平方根，
 * 给你一个非负整数 x ，计算并返回 x 的 算术平方根 。
 * 由于返回类型是整数，结果只保留 整数部分 ，小数部分将被 舍去 。
 * 注意：不允许使用任何内置指数函数和算符，例如 pow(x, 0.5) 或者 x^0.5 。
 */
@SuppressWarnings("unused")
@Slf4j
public class Sqrt {
    @Test
    public void test69() {
        Stream.of(2, 4, 2147395599)
                .forEach(num -> log.info(methodLog(
                        "" + num, "" + sqrtByBS(num))));
    }

    /**
     * 方法1：见文档
     */
    public int sqrtByPowAndLog(int x) {
        if (x == 0) {
            return 0;
        }
        int ans = (int) Math.exp(0.5 * Math.log(x));
        return (long) (ans + 1) * (ans + 1) <= x ? ans + 1 : ans;
    }

    /**
     * 方法2：二分查找(可以不使用long)
     * 1. right的确定
     * (1) right为x: 不对进行限制, 无法通过溢出来判断int right=(int)(1.414*pow(2,15));
     * 溢出的时候并不一定是<0 ，比如
     * (..高32位..)(..低32位..)
     * (..1010)(01111..)
     * (2) right=(int)(1.414*pow(2,15));
     * 实际上sqrt(Integer.MAX_VALUE): 46340.950001051984
     * 而1.414*pow(2,15): 为46333.952
     * 所以结果不能表示46334-46340区间内的结果
     * (3) right=(int)(1.415*pow(2,15))
     * (..高32位..)(..低32位..)
     * 溢出的时候最多是(000...)(1...)
     * 连(000...1)(0...)都不可能出现，所以溢出的时候必然是<0
     */
    private int sqrtByBS(int x) {
        // boundary
        if (x < 1) return 0;
        if (x < 4) return 1;
        if (x < 9) return 2;

        //
        // Integer.Max_VALUE = 2^31-1
        int left = 0;
        int right = (int) ceil(1.415 * pow(2, 15));
        int mid;
        int square;
        while (left <= right) {
            mid = (left + right) >> 1;
            square = mid * mid;
            // 溢出，溢出的时候并不一定是<0
            // (..高32位..)(..低32位..)
            if (square < 0 || square > x) right = mid - 1;
            else if (square == x) return mid;
            else left = mid + 1;
        }
        return left - 1;
    }

    /**
     * 方法3：牛顿迭代，见文档
     */
    public int sqrtByNewton(int x) {
        if (x == 0) {
            return 0;
        }

        double x0 = x;
        while (true) {
            double xi = 0.5 * (x0 + (double) x / x0);
            if (Math.abs(x0 - xi) < 1e-7) {
                break;
            }
            x0 = xi;
        }
        return (int) x0;
    }
}
