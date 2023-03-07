package veinthrough.leetcode.array;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static veinthrough.api.math.Math.min;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第41题：
 * 缺失的第一个正数，
 * 给你一个未排序的整数数组nums，请你找出其中没有出现的最小的正整数。
 * 进阶：你可以实现时间复杂度为 O(n) 并且只使用常数级别额外空间的解决方案吗？
 *
 * 字节跳动面试题：
 * 有一个整数数组7，3，10，9，6，4，5，15，11，13
 * 唯一限制条件：整数
 * 求：第一个间断的数：8
 * 要求：时间复杂度：o（N）
 * 空间复杂度：尽可能小
 */
@Slf4j
public class MissingNumber {
    @Test
    public void test41() {
        Stream.of(
                new int[]{1, 2, 0},
                new int[]{3, 4, -1, 1},
                new int[]{7, 8, 9, 11, 12})
                .forEach(nums -> log.info(methodLog(
                        Arrays.toString(nums), "" + firstMissingPositive(nums))));
    }

    @Test
    public void test() {
        Stream.of(
                new int[]{7, 3, 10, 9, 6, 4, 5, 15, 11, 13},
                new int[]{7, 0, 10, 9, 6, -1, 5, 15, 11, 13})
                .forEach(nums -> log.info(methodLog(
                        Arrays.toString(nums), "" + firstInterrupted(nums))));
    }

    /**
     * 【其实归根到底也是哈希的思维，只不过要确定取值范围为[1, n+1]】
     * 这个题目的难点在于要求 O(n) 的时间复杂度，和 O(1) 的空间复杂度。
     * 寻找缺失的第一个正数，需要先思考得出如下结论：
     * 数组长度已知，假设为 n，则缺失的第一个正数最大只能是 n+1，其取值范围为 [1, n+1]。
     * 因为对于一个长度为 n 的数组，缺失的第一个正数取最大值的情况是，数组中的 n 个数包括了从 1 到 n 的所有正数。
     * 此时缺失的第一个正数取最大值 n+1，不可能有比这还大的情况。
     * 所以就算缺失的第一个正数取最大值，其前面的所有正数也都可以映射到数组下标上。
     *
     * 算法过程为，两趟遍历：
     * 第一趟遍历，判断数组元素是否在1到数组长度n之间，如果在就把它放到数组的对应位置上。遍历结束后，值在 1 到 n 之间的元素，在数组中相对有序。
     * 第二趟遍历，寻找缺失的第一个正数。
     * 试图把i放在nums[i-1]上
     * 如果不是, 则交换
     * 4,2,1,3
     * i=0, 3,2,1,4
     * i=0, 1,2,3,4
     * i=1/i=2/i=3
     * 3,4,7,8
     * i=0, 7,4,3,8
     * i=0, 不管7
     * i=1, 7,8,3,4
     * i=1, 不管8
     * i=2/i=3
     * 3,4,-1,1
     * i=0, -1,4,3,1
     * i=0, 不管-1
     * i=1, -1,1,3,4
     * i=1, 1,-1,3,4
     * i=1, 不管-1
     * i=2/i=3
     */
    private int firstMissingPositive(int[] nums) {
        // 1.
        if (nums == null || nums.length == 0) return 1;
        // 2.(1) 第一趟遍历，判断数组元素是否在1到数组长度n之间，如果在就把它放到数组的对应位置上。
        int i = 0, k, temp;
        int n = nums.length;
        while (i < n) {
            // 1. nums[i]>0&&nums[i]<=n: 确定nums[i]的值对应的下标不越界
            // 2. nums[k = nums[i] - 1] != nums[i], 同时排除
            // (1) nums[i]本身位置正确: i=0, 1,3,2,4
            // (2) nums[nums[i]-1]位置正确  <--> nums[k = nums[i] - 1] == nums[i]
            //      位置: (num[i]-1)
            //      值(位置加一): (num[i]-1) + 1  --> nums[i]
            // nums[i]应该放入的位置nums[i]-1原本就是nums[i]: i=0, 4,3,2,4
            //   这里如果判定条件是nums[i]-1!=i, 那么将会无限循环
            if (nums[i] > 0 && nums[i] <= n && nums[k = nums[i] - 1] != nums[i]) {
                temp = nums[i];
                nums[i] = nums[k];
                nums[k] = temp;
            } else i++;
        }
        // 2.(2) 第二趟遍历，寻找缺失的第一个正数。
        for (i = 0; i < n; i++) {
            if (nums[i] != i + 1) break;
        }
        return i + 1;
    }

    /**
     * 【其实归根到底也是哈希的思维，只不过要确定只需要n个空间】
     * 只需要求第一个间断
     * 假设第一个间断为firstInt, 数组长度为n, 最小数为min
     * 如果数组都为连续没有间断, 那么firstInt = min+n
     * 否则 firstInt一定<min+n, 也就是说标记数组长度只需要为n
     * 【其实和firstMissingPositive差不多，每个元素减去最小值，也就是把最小值变成0】
     */
    private int firstInterrupted(int[] nums) {
        // 1.
        if (nums == null || nums.length == 0) return 0;
        if (nums.length == 1) return nums[0] + 1;

        // 2.(1) get min
        int min = min(nums);
        return firstMissingPositive(Arrays.stream(nums)
                .map(num -> num - min)
                .toArray())
                + min;
    }
}
