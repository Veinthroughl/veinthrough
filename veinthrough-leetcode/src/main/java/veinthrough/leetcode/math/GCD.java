package veinthrough.leetcode.math;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 最大公约数/最小公倍数
 */
@Slf4j
public class GCD {
    @Test
    public void test() {
        Stream.of(
                gcd(250, 80),
                gcd(135, 1422),
                gcm(250, 80),
                gcm(135, 1422))
                .forEach(result -> log.info(methodLog(""+result)));
    }

    // a小于b的情况下，通过gcd函数可以先交换a和b
    // common divisor
    private int gcd(int a, int b) {
        int temp;
        while (a % b != 0) {
            temp = a;
            a = b;
            b = temp%b;
        }
        return b;
    }

    // common multiple
    private int gcm(int a, int b) {
        return a * b / gcd(a, b);
    }
}
