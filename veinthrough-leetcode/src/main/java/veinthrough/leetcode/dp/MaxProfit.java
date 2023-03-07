package veinthrough.leetcode.dp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第309题：
 * 给定一个整数数组，其中第i个元素代表了第i天的股票价格。​
 * 设计一个算法计算出最大利润。在满足以下约束条件下，你可以尽可能地完成更多的交易（多次买卖一支股票）:
 * 你不能同时参与多笔交易（你必须在再次购买前出售掉之前的股票）。
 * 卖出股票后，你无法在第二天买入股票 (即冷冻期为 1 天)。
 * 示例:
 * 输入: [1,2,3,0,2]
 * 输出: 3
 * 解释: 对应的交易状态为: [买入, 卖出, 冷冻期, 买入, 卖出]
 */
@Slf4j
public class MaxProfit {
    @Test
    public void test309() {
        Stream.of(new int[]{1, 2, 3, 0, 2},
                new int[]{7, 6, 3, 4, 3, 2, 1, 0, 5, 3, 9, 0, 9},
                new int[]{7, 6, 3, 4, 3, 2, 1, 0, 5, 3, 6, 9, 0, 9})
                .forEach(prices -> log.info(methodLog(
                        "array", Arrays.toString(prices),
                        "profit", "" + maxProfit(prices),
                        "profit by 滚动数组", ""+maxProfit2(prices))));
    }

    /**
     * 方法1(dp)：
     * 1. 用f[i]表示第i天结束之后的「累计最大收益」, 因此会有三种不同的状态:
     * 目前持有一支股票，对应的「累计最大收益」记为 f[i][0]；
     * 目前不持有任何股票，并且处于冷冻期中【本次操作是卖】，对应的「累计最大收益」记为 f[i][1]；
     * 目前不持有任何股票，并且不处于冷冻期中【本次操作不是卖】，对应的「累计最大收益」记为 f[i][2]。
     * 2. 将「买入」和「卖出」分开进行考虑：「买入」为负收益，而「卖出」为正收益。
     * 所以如果只有买而没有卖那么只能获得负收益，只有卖出后才能获得正收益。
     * 3. 注意到如果在最后一天（第n−1天）结束之后，手上仍然持有股票，那么显然是没有任何意义的。
     * 4. 递推公式
     * f[i][0]: max(f[i-1][0], f[i-1][2]-prices[i])
     * f[i][1]: f[i-1][0]+prices[i]
     * f[i][2]: max(f[i-1][1], f[i-1][2])
     * 思考: 这里为什么要分成(1)(2)(3)种状态，
     * 举例: 因为「买入」为负收益, 假设输入为[1,1,1,1,10]
     * f[i][0]中有f[i-1][2]-prices[i]), 而f[i][2]中有f[i-1][2],
     * 如果不分开, f[i-1][2]-prices[i]不可能大于f[i-1][2], 不可能会被max到
     * 也就是说已买入就是负数, 所以不可能会买入。
     * 5. 起始
     * f[0][0]: -prices[0]
     * f[0][1]: 0
     * f[0][2]: 0
     */
    private int maxProfit(int[] prices) {
        // boundary
        if (prices==null || prices.length <=1) return 0;
        if (prices.length == 2) return prices[1] > prices[0] ? prices[1] - prices[0] : 0;

        //
        // 因为这里只涉及到i-1/i的迭代, 也就是只涉及到本次和上次的迭代,
        // 所以其实不需要一个二维数组, 可以使用滚动数组
        int[][] profits = new int[prices.length][3];
        profits[0][0] = -prices[0];

        for (int i = 1; i < prices.length; i++) {
            profits[i][0] = Math.max(profits[i - 1][0], profits[i - 1][2] - prices[i]);
            profits[i][1] = profits[i - 1][0] + prices[i];
            profits[i][2] = Math.max(profits[i - 1][1], profits[i - 1][2]);
        }

        // 注意到如果在最后一天（第 n-1n−1 天）结束之后，手上仍然持有股票，那么显然是没有任何意义的。
        return Math.max(profits[prices.length - 1][1], profits[prices.length - 1][2]);
    }

    /**
     * 方法2(dp, 使用滚动数组):
     * 这里有循环以来必须使用两个滚动数组
     */
    private int maxProfit2(int[] prices) {
        int len;
        // boundary
        if(prices==null || (len=prices.length)<=1) return 0;
        if(prices.length==2) return prices[1]>prices[0] ? prices[1]-prices[0] : 0;

        //
        int[] profit = new int[3];
        int[] profitLast = new int[3], temp;
        profitLast[0] = -prices[0];
        for (int i = 1; i < len; i++) {
            // 因为有循环依赖([0] <--> [2] <--> [1] <--> [0]), 需要使用两个滚动数组
            // 没卖出的话当前持有只算成本, 只有卖出后才能获得正收益。
            profit[0] = Math.max(profitLast[0], profitLast[2]-prices[i]);
            profit[1] = profitLast[0]+prices[i];
            profit[2] = Math.max(profitLast[1],profitLast[2]);

            temp = profitLast;
            profitLast = profit;
            profit = temp;
        }
        return Math.max(profitLast[1], profitLast[2]);
    }
}