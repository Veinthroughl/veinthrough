package veinthrough.leetcode.dp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.generic.Tuple;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 问题：Serling公司购买长钢条，将其切割为短钢条出售。假设切割工序没有成本，不同长度的钢条的售价如下：
 * Length(i)  1  2  3  4  5   6   7   8   9   10
 * Price(pi)  1  5  8  9  10 17  17  20  24  30
 * 那么钢条切割问题就是：给定一段长度为n英尺的钢条和一个价格表为Pi(i=1,2,…n), 求切割钢条方案，
 * 使得销售收益Rn最大（单位为元）。注意：如果长度为n英尺的钢条的价格Pn足够大，那么最优解就是不需要切割。
 */
@Slf4j
public class RodCut {
    private int[] cutLens = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private int[] cutPrices = new int[]{1, 5, 8, 9, 10, 17, 17, 20, 24, 30};

    @Test
    public void test() {
        Stream.of(2, 7, 8, 9, 10)
                .forEach(rodLen -> log.info(methodLog(
                        "" + rodLen,
                        new Bag().bag(cutLens, cutPrices, rodLen) + ""
                                + rodCut(rodLen) + "")));
    }

    /**
     * 实际上就是完全背包问题: {@link Bag#bag(int[], int[], int)}
     */
    private Tuple<Integer, List<Integer>> rodCut(int rodLen) {
        int n = cutPrices.length;
        int[] r = new int[rodLen + 1];

        // boundary
        if (rodLen <= cutLens[0]) return Tuple.of(0, Collections.singletonList(cutLens[0]));

        int[] cut = new int[rodLen + 1]; //
        int max;
        // 1. 使用前1个钢条
        for(int i=1;i<=rodLen;i++) {
            cut[i] = 0;
            r[i] = i/cutLens[0]*cutPrices[0];
        }
        // 2. 使用前j个钢条, 这里必须【从前往后】
        for(int j=2;j<=n;j++) {
            for (int i = cutLens[j-1]; i <= rodLen; i++) {
                // 【同一层】这里r[i - cutLens[j-1]]相当于二维数组r[j][i - cutLens[j-1]], 所以必须【从前往后】
                if ((max = cutPrices[j - 1] + r[i - cutLens[j-1]]) > r[i]) {
                    cut[i] = j-1;
                    r[i] = max;
                }
            }
        }

        // 3. build cut series
        int left = rodLen;
        List<Integer> cutN = new LinkedList<>();
        while (left != 0) {
            cutN.add(cutLens[cut[left]]);
            left = left - cutLens[cut[left]];
        }
        return Tuple.of(r[rodLen], cutN);
    }
}
