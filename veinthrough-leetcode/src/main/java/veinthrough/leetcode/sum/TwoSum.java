package veinthrough.leetcode.sum;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第1题：
 * 给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 的那两个整数，并返回它们的数组下标。
 * 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素不能使用两遍。
 * 你可以按任意顺序返回答案。
 *
 * 第167题：
 * 给定一个已按照升序排列的整数数组numbers，请你从数组中找出两个数满足相加之和等于目标数target。
 * 函数应该以长度为 2 的整数数组的形式返回这两个数的下标值。
 * numbers 的下标 从 1 开始计数 ，所以答案数组应当满足 1 <= answer[0] < answer[1] <= numbers.length 。
 * 你可以假设每个输入只对应唯一的答案，而且你不可以重复使用相同的元素。
 */
@Slf4j
public class TwoSum {
    @Test
    public void test1() {
        Stream.of(twoSum(new int[]{2, 7, 11, 15}, 9),
                twoSum(new int[]{1, -5, 3, 4, 6}, 7))
                .forEach(result -> log.info(methodLog(Arrays.toString(result))));
    }

    @Test
    public void test167() {
        Stream.of(twoSum2(new int[]{2, 7, 11, 15}, 9),
                twoSum2(new int[]{2, 3, 4}, 6))
                .forEach(result -> log.info(methodLog(Arrays.toString(result))));
    }

    /**
     * 非排序输入, 使用HashMap
     */
    private static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> peers = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (peers.containsKey(nums[i])) {
                return new int[]{peers.get(nums[i]), i};
            }
            peers.put(target - nums[i], i);
        }
        return null;
    }

    /**
     * 排序输入, 使用DBP
     */
    private int[] twoSum2(int[] numbers, int target) {
        int sum;
        int[] result = null;
        for (int i = 0, j = numbers.length - 1; i < j; ) {
            sum = numbers[i] + numbers[j];
            if (sum == target) {
                result = new int[]{i + 1, j + 1};
                break;
            } else if (sum < target)
                i++;
            else
                j--;
        }
        return result;
    }
}