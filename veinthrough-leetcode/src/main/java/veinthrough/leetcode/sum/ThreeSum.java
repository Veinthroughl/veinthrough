package veinthrough.leetcode.sum;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第15题：
 * 给你一个包含n个整数的数组nums，判断nums中是否存在三个元素 a，b，c ，使得a+b+c=0？
 * 请你找出所有和为 0 且不重复的三元组。
 * 注意：答案中不可以包含重复的三元组。
 *
 * 第16题：
 * 给定一个包括 n 个整数的数组nums和一个目标值target。
 * 找出nums中的三个整数，使得它们的和与target最接近。返回这三个数的和。假定每组输入只存在唯一答案。
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class ThreeSum {
    @Test
    public void test15() {
        Stream.of(
                new int[]{-1, 0, 1, 2, -1, -4},
                new int[]{-1, 0, 1, 2, -1, 0, -3, 0, 2, -4, 5})
                .forEach(nums -> log.info(methodLog(
                        Arrays.toString(nums), "" + threeSum(nums))));
    }

    @Test
    public void test16() {
        Stream.of(
                threeSumClosest(new int[]{-1, 2, 1, -4}, 1),
                threeSumClosest(new int[]{-1, 0, 1, 1, 55}, 3),
                threeSumClosest(new int[]{1, 1, 1, 1}, 0))
                .forEach(result -> log.info(methodLog("" + result)));
    }

    @Test
    public void test16_2() {
        Stream.of(
                threeSumClosest2(new int[]{-1, 2, 1, -4}, 1),
                threeSumClosest2(new int[]{-1, 0, 1, 1, 55}, 3),
                threeSumClosest2(new int[]{1, 1, 1, 1}, 0))
                .forEach(result -> log.info(methodLog("" + result)));
    }

    /**
     * 第15题：排序+DBP
     */
    private List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        // 1. 元素过少
        if (nums == null || nums.length < 3) return result;
        if (nums.length == 3 && (nums[0] + nums[1] + nums[2]) == 0) {
            result.add(Arrays.asList(nums[0], nums[1], nums[2]));
            return result;
        }

        // 2. 
        Arrays.sort(nums);
        int target;
        for (int i = 0; i < nums.length - 2; i++) {
            // (1) skip duplicate i, 【去重1】【尾去重】【同一层】
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            // (2) return if >target
            // nums[i]>target, nums[j]/nums[k] will >target
            if (nums[i] > 0) return result;

            target = -nums[i];
            for (int j = i + 1, k = nums.length - 1; j < k; ) {
                // (3) return if >target
                if (nums[j] > target) break;
                if ((nums[j] + nums[k]) == target) {
                    result.add(Arrays.asList(nums[i], nums[j], nums[k]));
                    // (4) skip duplicate j/k, 【去重2】【尾去重】【同一层】
                    do j++;
                    while (j < k && nums[j] == nums[j - 1]);
                    do k--;
                    while (j < k && nums[k] == nums[k + 1]);
                }
                // (5) DBP: j++/k--
                // 这里如果用while去skip duplicate j/k, 效率更低(应找到的时候再skip)
                else if ((nums[j] + nums[k]) < target) j++;
                else k--;
            }
        }
        return result;
    }

    /**
     * 第16题：排序+DBP
     * 额外使用循环来skip j/k
     */
    private int threeSumClosest(int[] nums, int target) {
        // 1.
        if (nums.length == 3) return nums[0] + nums[1] + nums[2];

        // 2.
        Arrays.sort(nums);
        int minDelta, result;
        int length = nums.length;
        // (1) 构造minDelta
        // 优化minDelta, 减少后面比较次数
        if (length >= 8) {
            int middle = length >>> 1;
            result = length % 2 == 0 ?
                    nums[middle] + nums[middle - 1] :
                    nums[middle] + nums[middle - 1] + nums[middle + 1];
        } else result = nums[0] + nums[1] + nums[2];
        minDelta = Math.abs(result - target);

        int sum, delta;
        for (int i = 0; i < nums.length - 2; i++) {
            // (2) skip duplicate i, 【去重1】【尾去重】【同一层】
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            for (int j = i + 1, k = nums.length - 1; j < k; ) {
                sum = nums[i] + nums[j] + nums[k];
                delta = Math.abs(sum - target);
                // (3) equal
                if (delta == 0) return target;
                // (4) new minDelta
                if (delta < minDelta) {
                    minDelta = delta;
                    result = sum;
                }
                // (5) move forward, 额外使用循环来skip j/k
                if (sum < target) {
                    // skip duplicate j, 如果重复元素不多, while可能会增加开销, 【去重2】【尾去重】【同一层】
                    do j++;
                    while (j < k && nums[j] == nums[j - 1]);
                }
                // (5) move backward, 额外使用循环来skip j/k
                else {
                    // skip duplicate k, 【去重3】【尾去重】【同一层】
                    do k--;
                    while (j < k && nums[k] == nums[k + 1]);
                }
            }
        }
        return result;
    }

    /**
     * 第16题：排序+DBP
     * 不额外使用循环来skip j/k
     */
    private int threeSumClosest2(int[] nums, int target) {
        // 1.
        if (nums.length == 3) return nums[0] + nums[1] + nums[2];

        // 2.
        Arrays.sort(nums);
        int minDelta, result;
        int length = nums.length;
        // (1) 构造minDelta
        // 优化minDelta, 减少后面比较次数
        if (length >= 8) {
            int middle = length >>> 1;
            result = length % 2 == 0 ?
                    nums[middle] + nums[middle - 1] :
                    nums[middle] + nums[middle - 1] + nums[middle + 1];
        } else result = nums[0] + nums[1] + nums[2];
        minDelta = Math.abs(result - target);

        int sum, delta;
        for (int i = 0; i < length - 2; i++) {
            // (2) skip duplicate i, 【去重1】【尾去重】【同一层】
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            for (int j = i + 1, k = length - 1; j < k; ) {
                sum = nums[i] + nums[j] + nums[k];
                delta = Math.abs(sum - target);
                // (3) equal
                if (delta == 0) return target;
                // (4) new minDelta
                if (delta < minDelta) {
                    minDelta = delta;
                    result = sum;
                }
                // (5) move forward/backward
                // 不额外使用循环来skip j/k，重复元素在下一轮循环会被忽略
                if (sum < target) j++;
                else k--;
            }
        }
        return result;
    }
}