package veinthrough.leetcode.math;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.Math.min;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第263题: 判断丑数
 * 给你一个整数n，请你判断n是否为丑数。如果是，返回true；否则，返回false 。
 * 丑数就是只包含质因数2、3和/或5的正整数。
 * 
 * 第264：寻找第N个丑数
 * 给你一个整数n，请你找出并返回第n个丑数。
 */
@Slf4j
public class UglyNum {
    @Test
    public void test263() {
        Stream.of(6, 8, 14, 1)
                .forEach(n -> log.info(methodLog("" + n, "" + isUgly(n))));
    }

    @Test
    public void test264() {
        Stream.of(6, 8, 14, 20)
                .forEach(n ->
                        // auto boxed
                        Stream.<Function<Integer, Integer>>of(UglyNum::nthUglyNumber, UglyNum::nthUglyNumber2)
                                .forEach(function ->
                                        log.info(methodLog("" + n, "" + function.apply(n)))));
    }

    /**
     * 第263题
     */
    private static boolean isUgly(int n) {
        if (n <= 0) return false;
        if (n <= 6) return true;

        while (n % 2 == 0) n = n / 2;
        while (n % 3 == 0) n = n / 3;
        while (n % 5 == 0) n = n / 5;

        return n == 1;
    }

    /**
     * 第264题方法1:
     * 其实是一种动态规划：dp[i]=min(dp[p2]×2,dp[p3]×3,dp[p5]×5)
     * 而且是天然的滚动数组?
     * 1:1
     * 2:2  <--                            1*2,   1*3, 1*5
     *                                    |
     * 3:3  <--                         [2]*2,   1*3, 1*5
     *                                          |
     * 4:4  <--                         2*2,  [2]*3, 1*5
     *                                 |
     * 5:5  <--                      [3]*2, 2*3, 1*5
     *                                          |
     * 6:6  <--                  3*2,   2*3,  [2]*5
     *                          |      |
     * 7:8  <--               [4]*2, [3]*3, 2*5
     *                        |
     * 8:9  <--             [5]*2,  3*3, 2*5
     *                             |
     * 9:10 <--             5*2, [4]*3,  2*5
     *                     |            |
     * 10:12<--          [6]*2, 4*3,  [3]*5
     *                   |     |
     * 11:15<--        [8]*2, [5]*3, 3*5
     *                 |     |
     * 12:16<--       8*2,  [6]*3, 4*5
     *               |
     * 13:18<--    [9]*2,   6*3, 4*5
     *             |       |
     * 14:20<--  [10]*2, [8]*3, 4*5
     * ...
     */
    public static int nthUglyNumber(int n) {
        int[] uglyNum = new int[n];
        uglyNum[0] = 1;
        int p2 = 0, p3 = 0, p5 = 0;
        int n2 = 2, n3 = 3, n5 = 5;
        for (int i = 1; i < n; i++) {
            uglyNum[i] = min(min(n2, n3), n5);
            if (n2 == uglyNum[i]) {
                p2++;
                n2 = uglyNum[p2] * 2;
            }
            if (n3 == uglyNum[i]) {
                p3++;
                n3 = uglyNum[p3] * 3;
            }
            if (n5 == uglyNum[i]) {
                p5++;
                n5 = uglyNum[p5] * 5;
            }
        }
        return uglyNum[n - 1];
    }

    /**
     * 第264题方法2:
     * 要得到从小到大的第n个丑数，可以使用最小堆实现。
     * 初始时堆为空。首先将最小的丑数1加入堆。
     * 每次取出堆顶元素x，则x是堆中最小的丑数，由于2x,3x,5x也是丑数，因此将2x,3x,5x加入堆。
     * 上述做法会导致堆中出现重复元素的情况。为了避免重复元素，可以使用哈希集合去重，避免相同元素多次加入堆。
     * 在排除重复元素的情况下，第n次从最小堆中取出的元素即为第n个丑数。
     * (1) 时间复杂度：O(nlogn)。得到第n个丑数需要进行n次循环，每次循环都要从最小堆中取出1个元素以及向最小堆中加入最多3个元素，
     * 因此每次循环的时间复杂度是O(logn+log3n)=O(logn)，总时间复杂度是O(nlogn)。
     * (2) 空间复杂度：O(n)。空间复杂度主要取决于最小堆和哈希集合的大小，最小堆和哈希集合的大小都不会超过3n。
     * (3) 缺点：会预先存储较多的丑数，导致空间复杂度较高，维护最小堆的过程也导致时间复杂度较高。
     */
    @SuppressWarnings("ConstantConditions")
    private static int nthUglyNumber2(int n) {
        // min heap
        PriorityQueue<Integer> heap = new PriorityQueue<>(n);
        Set<Integer> set = new LinkedHashSet<>(n);
        heap.offer(1);

        for (int i = 0, ug, ug2, ug4; i < n - 1; i++) {
            ug = heap.poll();
            ug2 = ug << 1;
            ug4 = ug << 2;
            if (set.add(ug2)) heap.offer(ug2); // X2
            if (set.add(ug + ug2)) heap.offer(ug + ug2); // X3
            if (set.add(ug + ug4)) heap.offer(ug + ug4); // X5
        }
        return heap.poll();
    }
}
