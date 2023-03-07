package veinthrough.leetcode.math;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第50题：_pow_recursive(x,n)
 * 实现 _pow_recursive(x, n) ，即计算 x 的 n 次幂函数（即，xn ）。
 */
@Slf4j
public class Pow {
    @Test
    public void test50() {
        // 2.000: 1024.0, 8.0, 0.25
        // 2.1000: 1667.9880978201004, 9.261000000000001, 0.22675736961451246
        Stream.of(2.000, 2.1000)
                .forEach(x ->
                        Stream.of(10, 3, -2).forEach(n ->
                                log.info(methodLog(String.format("pow(%f,%d)", x, n), "" + pow(x, n)))));
    }
    private double pow(double x, int n) {
        return n > 0 ?
//                _pow(x, n) : 1 / _pow(x, -n); // non-recursive, 快速累乘
                _pow_recursive(x, n) : 1 / _pow_recursive(x, -n); // recursive
    }

    /**
     * 方法1: 递归
     */
    @SuppressWarnings("unused")
    private double _pow_recursive(double x, int n) {
        if(n==0) return 1;
        double result = _pow_recursive(x, n / 2);
        result *= result;
        return n % 2 == 0 ? result : result * x;
    }

    /**
     * 方法2：快速幂
     * 参考文档， 实际上相当于将n化解为一个二进制表示，每一位对应不同的weight
     */
    double _pow(double x, int n) {
        double result = 1;
        double weight = x;
        while (n != 0) {
            if(n%2!=0) result*=weight;
            n/=2;
            weight *= weight;
        }
        return result;
    }

}
