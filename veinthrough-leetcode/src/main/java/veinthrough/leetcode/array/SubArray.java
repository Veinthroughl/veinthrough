package veinthrough.leetcode.array;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static veinthrough.api.math.Math.max;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 剑指 Offer 42. 连续子数组的最大和
 * 输入一个整型数组，数组中的一个或连续多个整数组成一个子数组。求所有子数组的和的最大值。
 * 【注意】这里不能包含0个元素
 * 要求时间复杂度为O(n)。
 *
 *
 * 第53题，最大子数组和，
 * 给你一个整数数组 nums ，请你找出一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。
 * 子数组是数组中的一个连续部分。
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class SubArray {
    @Test
    public void test53() {
        Stream.of(
                new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}, // 6
                new int[]{1},                           // 1
                new int[]{5, 4, -1, 7, 8},             // 23
                new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4},       // 6
                new int[]{-2, -1, -3, -4, -1, -2, -1, -5, -4})  // 0
                .forEach(nums -> log.info(methodLog(
                        "Max sum of sub array of " + Arrays.toString(nums),
                        "" + maxSubArray(nums)
                )));
    }

    @Test
    public void testOffer42() {
        Stream.of(
                new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}, // 6
                new int[]{1},                           // 1
                new int[]{5, 4, -1, 7, 8},             // 23
                new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4},       // 6
                new int[]{-2, -1, -3, -4, -1, -2, -1, -5, -4})  // -1
                .forEach(nums -> log.info(methodLog(
                        Arrays.toString(nums), maxSubArrayNotEmpty(nums) + "")));
    }

    /**
     * 子集不能为空集
     */
    private int maxSubArrayNotEmpty(int[] nums) {
        int max = maxSubArray(nums);
        return max > 0 ? max : max(nums); // 因为子集不能为空集, 有可能所有元素都为负数
    }

    public int maxSubArray(int[] nums) {
        int sum = 0, max = 0;
        for (int num : nums) {
            sum += num;
            if (sum < 0) sum = 0;
            else if (sum > max) max = sum;
        }
        return max;
    }
}
