package veinthrough.leetcode.array;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第410题: 分割数组的最大值
 * 给定一个非负整数数组和一个整数 m，你需要将这个数组分成 m 个非空的【连续】子数组。
 * 设计一个算法使得这 m 个子数组各自和的最大值最小。
 * 输入: nums = [7,2,5,10,8], m = 2
 * 输出: 18
 * 解释:
 * 一共有四种方法将nums分割为2个子数组。
 * 其中最好的方式是将其分为[7,2,5]和[10,8]，
 * 因为此时这两个子数组各自的和的最大值为18，在所有情况中最小。
 */
@Slf4j
public class SplitArray {
    private int[] nums, sums, max;

    @Test
    public void test() {
        Stream.of(
                new int[]{7, 2, 5, 10, 8},
                new int[]{1, 2, 3, 4, 5},
                new int[]{1, 4, 4})
                .forEach(nums ->
                        Stream.of(2, 3).forEach(parts -> log.info(methodLog(
                                String.format("Split %s into %d parts", Arrays.toString(nums), parts),
                                "" + splitArray(nums, parts)))));
    }

    /**
     * 「将数组分割为m段，求……」是动态规划题目常见的问法。
     * 本题中，我们可以令 f[i][j]表示将数组的前i个数分割为j段所能得到的最大连续子数组和的最小值。
     * (1) 在进行[状态转移]时，我们可以考虑第j段的具体范围，
     * 即我们可以枚举k，其中前k个数被分割为j-1段，而第k+1到第i个数为第j段。
     * 此时，这j段子数组中和的最大值，
     * 就等于f[k][j−1]与sub(k+1,i)中的较大值，其中sub(i,j)表示数组nums中下标落在区间[i,j]内的数的和。
     * (2) 边界条件就是: 分成1段
     */
    private int splitArray(int[] _nums, int m) {
        int len;
        // boundary
        if ((nums = _nums) == null) return 0;
        if ((len = nums.length) == 0) return 0;

        // 1. 计算:
        // sums[i]: 前i(不包括0)个元素的和
        // max[i]: 前i(不包括0)个元素的最大值
        maxAndSum();

        // boundary: 分成1段
        if (m == 1) return sums[len];
        // boundary: n个元素分成n段(大于n段?)
        if (m >= len) return max[len];

        // 2. f[i][j]将前i个元素分割成j段
        int[][] f = new int[len + 1][m + 1];
        // (1) 分成1段
        for (int i = 1; i <= len; i++)
            f[i][1] = sums[i];
        // (2) 分成j段
        for (int j = 2, left, right; j <= m; j++) {
            // j个元素分成j段
            f[j][j] = max[j];
            // 将i个元素
            for (int i = j + 1; i <= len; i++) {
                f[i][j] = sums[i]; // 最大值
                for (int k = j - 1; k < i; k++) {
                    left = f[k][j - 1];
                    right = sums[i] - sums[k];
                    f[i][j] = Math.min(f[i][j], Math.max(left, right));
                }
            }
        }
        return f[nums.length][m];
    }

    private void maxAndSum() {
        // sums[i]: 前i(不包括0)个元素的和
        sums = new int[nums.length + 1];
        // max[i]: 前i(不包括0)个元素的最大值
        max = new int[nums.length + 1];
        max[1] = sums[1] = nums[0];
        for (int i = 2; i <= nums.length; i++) {
            max[i] = Math.max(max[i - 1], nums[i - 1]);
            sums[i] = sums[i - 1] + nums[i - 1];
        }
    }
}