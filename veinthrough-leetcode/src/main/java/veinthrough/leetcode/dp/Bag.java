package veinthrough.leetcode.dp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.math.Math;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static veinthrough.api.math.Math.min;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 1. 0-1背包问题
 * 2. 完全背包问题
 * 3. 多重背包问题
 */
@SuppressWarnings({"Duplicates", "unused"})
@Slf4j
public class Bag {
    private int[] w1 = new int[]{77, 22, 29, 50, 99};
    private int[] v1 = new int[]{92, 22, 87, 46, 90};
    private int[] w2 = new int[]{79, 58, 86, 11, 28, 62, 15, 68};
    private int[] v2 = new int[]{83, 14, 54, 79, 72, 52, 48, 62};
//    private int[] w3 = new int[]{2, 3, 4, 7};
//    private int[] v3 = new int[]{1, 3, 5, 9};

    @Test
    public void testBag01() {
        Stream.of(
                bag01(w1, v1, 100), // 133
                bag01(w2, v2, 200)) // 334
                .forEach(max -> log.info(methodLog(
                        "" + max)));
    }

    @Test
    public void testBag() {
        Stream.of(
                bag(w1, v1, 1000), // 2958
                bag(w2, v2, 2000)) // 14299
                .forEach(max -> log.info(methodLog(
                        "" + max)));
    }

    private int bag01(int[] w, int[] v, int weight) {
        // boundary

        // 不同方法
//        return _bag01(w, v, weight); // 非滚动数组
        return _bag01Roll(w, v, weight); // 滚动数组
    }

    /**
     * 0-1背包: 二维数组
     */
    @SuppressWarnings("unused")
    private int _bag01(int[] w, int[] v, int weight) {
        int n = w.length;
        int[][] max = new int[n + 1][weight + 1];
        // max[0][...] = 0
        // max[1][w0...] = v[0]
        for (int j = w[0]; j <= weight; j++) max[1][j] = v[0];
        // max[2...][...]
        // i: 使用w/v中的前i个
        // j: 容量
        for (int i = 2; i <= n; i++) {
            // (1) 从前往后
            // (2) 在min(w, 0, i)前， j不足以使用任何一个, 结果为0
            // (3) 递推:
            //   > max[i - 1][j]: 不使用i
            //   > v[i - 1] + max[i - 1][j - w[i - 1]]: 使用i
            for (int j = min(w, 0, i); j <= weight; j++)
                max[i][j] = j >= w[i - 1] ?
                        max(max[i - 1][j], v[i - 1] + max[i - 1][j - w[i - 1]]) :
                        max[i - 1][j];
        }
        return max[n][weight];
    }


    /**
     * 0-1背包: 滚动数组
     */
    private int _bag01Roll(int[] w, int[] v, int weight) {
        int n = w.length;
        int[] max = new int[weight + 1];
        // max[0][...]=0
        // max[1][...]
        for (int j = w[0]; j <= weight; j++) max[j] = v[0];
        // max[2...][...]
        for (int i = 2; i <= n; i++) {
            // (1) 【从后往前】
            // (2) 在min(w, 0, i)前， j不足以使用任何一个, 结果为0
            // (3) 递推:
            //   > max[j]: 不使用i(使用i-1层的max[j])
            //   > v[i - 1] + max[j - w[i - 1]]: 使用i
            // (4) 滚动数组
            //   > 第i层的数组只使用了第i-1层的数据, 所以只需要一个滚动数组
            //   > 为了使用滚动数组, j必须【从后往前】, 因为计算(第i层j比较大)会用到(第i-1层j比较小)【上一层】，
            //     如果【从前往后】, (i-1层j比较小)已经被(i层j比较小)覆盖
            for (int j = weight; j >= min(w, 0, i); j--)
                max[j] = j >= w[i - 1] ?
                        max(max[j], v[i - 1] + max[j - w[i - 1]]) :
                        max[j];
        }
        return max[weight];
    }

    int bag(int[] w, int[] v, int weight) {
        // boundary

        // 不同方法
//        return _bag(w, v, weight);
//        return _bagRoll(w, v, weight);
        return _bagEnum(w, v, weight);
//        return _bagBy01(w, v, weight);
    }

    /**
     * 完全背包
     */
    private int _bag(int[] w, int[] v, int weight) {
        // boundary

        //
        int n = w.length;
        int[][] max = new int[n + 1][weight + 1];
        // max[0][...] = 0
        // max[1][w0...] = v[0]
        for (int j = w[0]; j <= weight; j++)
            max[1][j] = j / w[0] * v[0];
        // max[2...][...]
        // 线性  --> 区间
        for (int i = 2; i <= n; i++) {
            for (int j = min(w, 0, i); j <= weight; j++) {
                max[i][j] = j >= w[i - 1] ?
                        max(max[i - 1][j], v[i - 1] + max[i][j - w[i - 1]]) :
                        max[i - 1][j];
            }
        }
        return max[n][weight];
    }

    /**
     * 完全背包：滚动数组
     */
    private int _bagRoll(int[] w, int[] v, int n) {
        int len = w.length;
        int[] max = new int[n + 1];
        // max[0][...] = 0
        // max[1][w0...] = v[0]
        for (int j = w[0]; j <= n; j++)
            max[j] = j / w[0] * v[0];
        // max[2...][...]
        // 线性  --> 区间
        for (int i = 2; i <= len; i++) {
            // 必须【从前往后】, 因为计算(第i层j比较大)会用到(第i层j比较小)
            for (int j = min(w, 0, i); j <= n; j++) {
                max[j] = j >= w[i - 1] ?
                        // 【同一层】这里max[j - w[i - 1]]相当于max[i][j-w[i-1]], 所以必须【从前往后】
                        max(max[j], v[i - 1] + max[j - w[i - 1]]) :
                        max[j];
            }
        }
        return max[n];
    }

    /**
     * 完全背包：转化成多重背包问题
     */
    private int _bagEnum(int[] w, int[] v, int n) {
        return _bagMux(w, v,
                IntStream.of(w)
                        .map(weight -> n / weight)
                        .toArray(),
                n);
    }

    /**
     * 完全背包：转化成多重背包问题  -->  转化成0-1背包问题
     * 二进制优化
     */
    private int _bagBy01(int[] w, int[] v, int n) {
        return _bagMuxBy01(w, v,
                IntStream.of(w)
                        .map(weight -> n / weight)
                        .toArray(),
                n);
    }

    /**
     * 多重背包
     */
    private int _bagMux(int[] w, int[] v, int[] mux, int weight) {
        int len = w.length;
        int[] max = new int[weight + 1];
        // max[0][...] = 0
        // max[1][w0...] = v[0]
        for (int j = w[0]; j <= weight; j++)
            max[j] = j / w[0] * v[0];
        // max[2...][...]
        // 线性  --> 区间
        for (int i = 2; i <= len; i++)
            // 必须【从后往前】, 因为这里用的是i-1
            for (int j = weight; j >= min(w, 0, i); j--)
                if (j >= w[i - 1])
                    // 枚举k
                    for (int k = 1, used = w[i - 1], gotten = v[i - 1];
                         used <= j && k <= mux[i - 1];
                         used += w[i - 1], gotten += v[i - 1], k++)
                        // 【上一层】这里max[j - used]相当于max[i-1][j - used], 所以必须【从后往前】
                        max[j] = max(max[j], gotten + max[j - used]);

        return max[weight];
    }

    /**
     * 多重背包：转化成0-1背包问题
     * 二进制优化
     */
    private int _bagMuxBy01(int[] w, int[] v, int[] mux, int n) {
        int len = w.length;
        int[] max2Power = IntStream.of(mux)
                .map(Math::maxSmaller2Power)
                .toArray();
        List<Integer> wNew = new LinkedList<>();
        List<Integer> vNew = new LinkedList<>();
        int power;
        for (int i = 0; i < len; i++) {
            power = max2Power[i];
            while (power != 0) {
                wNew.add(power * w[i]);
                vNew.add(power * v[i]);
                power >>= 1;
            }
        }
        return _bag01Roll(
                wNew.stream().mapToInt(Integer::new).toArray(),
                vNew.stream().mapToInt(Integer::new).toArray(),
                n);
    }
}
