package veinthrough.leetcode.math;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Math.pow;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 自守数：
 * 自守数是指一个数的平方的尾数等于该数自身的自然数。
 * 例如：25^2 = 625，76^2 = 5776，9376^2 = 87909376。请求出n以内的自守数的个数
 * 0, 1也是
 */
@Slf4j
public class AutomorphicNums {
    @Test
    public void test() {
        Stream.of(100, 1000, 10000, 100000)
                .forEach(num -> log.info(methodLog(
                        "" + num,
                        Arrays.toString(automorphicNums(num)))));
    }

    /**
     * 除了0以外, 其他的自守数必定是以1, 5, 6,结尾的数,  因此不必对所有的数都求平方
     * 只要平方减去数本身然后模%一下，如果是0就ok。
     */
    private int[] automorphicNums(int N) {
        // 1.
        if (N < 0) return null;
        if (N < 1) return new int[]{0};
        if (N < 5) return new int[]{0, 1};
        if (N < 6) return new int[]{0, 1, 5};
        if (N < 25) return new int[]{0, 1, 5, 6};
        if (N == 25) return new int[]{0, 1, 5, 6, 25};

        // 2.
        List<Integer> nums = Lists.newArrayList(0);
        for (int i = 1; i <= N; i += 10) {
            // 1,5,6结尾
            if (_isAutomorphic(i)) nums.add(i);
            if (_isAutomorphic(i + 4)) nums.add(i + 4);
            if (_isAutomorphic(i + 5)) nums.add(i + 5);
        }
        return nums.stream().mapToInt(Integer::intValue).toArray();
    }

    private boolean _isAutomorphic(int num) {
        int n = ("" + num).length();
        long s = num * num;
        return (s - num) % pow(10, n) == 0;
    }
}
