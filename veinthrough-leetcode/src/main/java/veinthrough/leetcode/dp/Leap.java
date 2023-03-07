package veinthrough.leetcode.dp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.generic.Tuple;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第70题：爬楼梯/青蛙跳，
 * 假设你正在爬楼梯。需要 n 阶你才能到达楼顶。
 * 每次你可以爬 1 或 2 个台阶。
 * (1) 方法数：你有多少种不同的方法可以爬到楼顶呢？
 * (2) 输出所有跳：青蛙跳台阶问题(实际上是一种特殊的背包问题): 每次可以跳1/2步, 输出所有跳的可能
 *
 * Cmn:
 * 计算n x m的棋盘格子（n为横向的格子数，m为竖向的格子数）沿着各自边缘线从左上角走到右下角,
 * 总共有多少种走法，要求不能走回头路，即：只能往右和往下走，不能往左和往上走。
 */
@Slf4j
public class Leap {
    @Test
    public void test70() {
        Stream.of(1, 2, 3, 5, 6)
                .forEach(n -> log.info(methodLog(climbStairs(n)+"")));
    }
    @Test
    public void testLeap() {
        Stream.of(1, 2, 3, 5, 6)
                .forEach(n -> log.info(methodLog(leap(n).toString())));
    }

    @Test
    public void testMN() {
        Stream.of(
                mn(3, 4), // C(5,2)=10
                mn(4, 5)) // C(7,3)=35
                .forEach(result -> log.info(methodLog("" + result)));
    }

    /**
     * 实际上也是dp:  一维数组 --> 两个变量
     */
    private int climbStairs(int n) {
        if (n == 1) return 1;
        if (n == 2) return 2;
        int n1 = 1, n2 = 2;
        int res = 0;
        for (int i = 3; i <= n; i++) {
            res = n1 + n2;
            n1 = n2;
            n2 = res;
        }
        return res;
    }

    private Tuple<Integer, List<List<Integer>>> leap(int n) {
        // boundary
        List<List<Integer>> n1, n2, temp;
        // 最好是LinkedList
        n1 = new LinkedList<>(Collections.singletonList(
                new LinkedList<>(Collections.singletonList(1))));
        if (n == 1)
            return Tuple.<Integer, List<List<Integer>>>builder()
                    .first(1)
                    .second(n1)
                    .build();

        n2 = new LinkedList<>(Arrays.asList(
                new LinkedList<>(Arrays.asList(1, 1)),
                new LinkedList<>(Collections.singletonList(2))));
        if (n == 2)
            return Tuple.<Integer, List<List<Integer>>>builder()
                    .first(2)
                    .second(n2)
                    .build();

        //
        List<Integer> l1;
        // 轮换来节省空间
        // 每一轮结束: n2都是表示大的列表，n1表示小的列表
        for (int i = 3; i <= n; i++) {
            for (List<Integer> list : n1) {
                list.add(2);
            }
            for (List<Integer> list : n2) {
                l1 = new LinkedList<>(list);
                l1.add(1);
                n1.add(l1);
            }
            // swap
            temp = n1;
            n1 = n2;
            n2 = temp;
        }
        return Tuple.<Integer, List<List<Integer>>>builder()
                .first(n2.size())
                .second(n2)
                .build();
    }

    /**
     * 从(m, n)—>(0, 0)就分两步走：
     * (1) 往右走一步：f(m, n - 1)—>(0, 0)
     * (2) 加上下走一步：f(m - 1, n)—>(0, 0)
     * 注意：但凡是触碰到边界，也就是说f(x, 0)或者f(0,x)都只有一条直路可走了，这里的x是变量哈。
     * f(m, n) = f(m, n - 1) + f(m - 1, n)
     */
    private int mn(int m, int n) {
        // 使用滚动数组
        int[] c = new int[n + 1];
        Arrays.fill(c, 1);
        for (int i = 2; i <= m; i++) {
            for (int j = 2; j <= n; j++)
                c[j] += c[j - 1];
        }
        return c[n];
    }
}