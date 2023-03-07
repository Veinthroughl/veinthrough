package veinthrough.leetcode.sum;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第18题：
 * 给定一个包含n个整数的数组nums和一个目标值target，判断nums中是否存在四个元素 a，b，c和d，
 * 使得 a + b + c + d 的值与target相等？
 * 找出所有满足条件且不重复的四元组。
 *
 * 注意：答案中不可以包含重复的四元组。
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class FourSum {
    @Test
    public void test() {
        Stream.of(
                fourSum(new int[]{-1, 0, 1, 2, -1, -4}, 0),
                fourSum(new int[]{-1, 0, 1, 2, -1, 0, -3, 0, 2, -4, 5}, 0),
                fourSum(new int[]{1, -2, -5, -4, -3, 3, 3, 5}, -11),
                fourSum(new int[]{-1, 0, 1, 2, -1, -4}, -1))
                .forEach(result -> log.info(methodLog(result.toString())));
    }

    private List<List<Integer>> fourSum(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        // 1. 元素过少的特殊情况
        if (nums == null || nums.length < 4) return result;
        if (nums.length == 4 && (nums[0] + nums[1] + nums[2] + nums[3]) == target) {
            result.add(Arrays.asList(nums[0], nums[1], nums[2], nums[3]));
            return result;
        }

        // 2. 
        Arrays.sort(nums);
        int target2, target3;
        for (int i1 = 0; i1 < nums.length - 3; i1++) {
            // (1) skip duplicate i1, 【去重1】【尾去重】【同一层】
            if (i1 > 0 && nums[i1] == nums[i1 - 1]) continue;
            // (2) nums[i1]>target, nums[i2]/nums[i3]/nums[i4] will >target
            // return if 4*nums[i1]>target, 【优化1】
            if ((nums[i1] << 2) > target) return result;

            target2 = target - nums[i1];
            for (int i2 = i1 + 1; i2 < nums.length - 2; i2++) {
                // (1) skip duplicate i2, 【去重2】【尾去重】【同一层】
                if (i2 > i1 + 1 && nums[i2] == nums[i2 - 1]) continue;
                // (2) break/return if 3*nums[i2]>target2, 【优化2】
                if (((nums[i2] << 1) + nums[i2]) > target2) break;

                target3 = target2 - nums[i2];
                for (int i3 = i2 + 1, i4 = nums.length - 1; i3 < i4; ) {
                    // (1) break/return if 2*nums[i3]>target3, 2*nums[i4]<target, 【优化3】
                    if ((nums[i3] << 1) > target3 || (nums[i4] << 1) < target3) break;

                    if ((nums[i3] + nums[i4]) == target3) {
                        result.add(Arrays.asList(nums[i1], nums[i2], nums[i3], nums[i4]));
                        // (2) skip duplicate i3/i4, 【去重2】【尾去重】【同一层】
                        do i3++; while (i3 < i4 && nums[i3] == nums[i3 - 1]);
                        do i4--; while (i3 < i4 && nums[i4] == nums[i4 + 1]);
                    } else if ((nums[i3] + nums[i4]) < target3) {
                        // 这里如果用while去skip duplicate i3/i4, 效率更低(应找到的时候再skip)
                        i3++;
                    } else {
                        i4--;
                    }
                }
            }
        }
        return result;
    }
}