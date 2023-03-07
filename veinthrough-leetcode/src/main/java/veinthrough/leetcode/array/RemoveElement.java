package veinthrough.leetcode.array;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第26题：原地删除重复元素
 * 给定一个排序数组，你需要在原地删除重复出现的元素，使得每个元素只出现1次，返回移除后数组的新长度。
 * 由于在某些语言中不能改变数组的长度，所以必须将结果放在数组nums的第一部分。更规范地说，如果在删除重复项之后有 k 个元素，那么 nums 的前 k 个元素应该保存最终结果。
 * 将最终结果插入 nums 的前 k 个位置后返回 k 。
 * 不要使用额外的数组空间，你必须在原地修改输入数组 并在使用 O(1) 额外空间的条件下完成。
 *
 * 第27题：原地删除某一个值(val)的元素
 * 给你一个数组 nums 和一个值 val，你需要 原地 移除所有数值等于val的元素，并返回移除后数组的新长度。
 * 不要使用额外的数组空间，你必须仅使用 O(1) 额外空间并 原地 修改输入数组。
 * 元素的顺序可以改变。你不需要考虑数组中超出新长度后面的元素。
 *
 * 第80题：删除有序数组中的重复项 II
 * 给你一个有序数组 nums ，请你 原地 删除重复出现的元素，使每个元素 最多出现2次 ，返回删除后数组的新长度。
 * 不要使用额外的数组空间，你必须在 原地 修改输入数组 并在使用 O(1) 额外空间的条件下完成。
 */
@Slf4j
public class RemoveElement {

    @Test
    public void test26() {
        Stream.of(
                new int[]{1, 1, 2},
                new int[]{0, 0, 1, 1, 1, 2, 2, 3, 3, 4},
                new int[]{1, 1},
                new int[]{1, 2, 2})
                .forEach(nums -> log.info(methodLog(
                        "Origin", Arrays.toString(nums),
                        "Duplicates Removed", "(" + removeDuplicates(nums) + ")" + Arrays.toString(nums))));
    }

    @Test
    public void test27() {
        Stream.of(
                new int[]{3, 2, 2, 3},
                new int[]{0, 1, 2, 2, 3, 0, 4, 2})
                .forEach(nums -> log.info(methodLog(
                        "Origin", Arrays.toString(nums),
                        "3 Removed", "(" + removeElement(nums, 3) + ")" + Arrays.toString(nums))));
    }

    @Test
    public void test80() {
        int[][] inputs = new int[][]{
                {0, 0, 1, 1, 1, 1, 2, 3, 3},
                {1, 1, 1, 2, 2, 3}};
        Arrays.stream(inputs)
                .peek(input -> log.info(methodLog("" + removeDuplicates2(input))))
                .forEach(array -> log.info(methodLog(Arrays.toString(array))));
    }

    /**
     * 第26题, 使每个元素最多出现1次
     */
    private static int removeDuplicates(int[] nums) {
        if (nums.length <= 1) return nums.length;

        int shrink = 0;
        for (int i = 1; i < nums.length; i++) {
            // duplicate val
            if (nums[i] == nums[i - 1])
                shrink++;
            else
                nums[i - shrink] = nums[i];
        }
        return nums.length - shrink;
    }

    /**
     * 第27题
     */
    @SuppressWarnings("SameParameterValue")
    private static int removeElement(int[] nums, int val) {
        if (nums.length == 0) return 0;

        int shrink = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == val)
                shrink++;
            else
                nums[i - shrink] = nums[i];
        }

        return nums.length - shrink;
    }

    /**
     * 第80题, 使每个元素最多出现2次
     */
    private static int removeDuplicates2(int[] nums) {
        // boundary
        if (nums.length <= 2) return nums.length;
        
        int shrink = 0;
        for (int i = 1, count = 0; i < nums.length; i++) {
            // (1) 重复且超过两个, 舍弃nums[i]
            if (nums[i] == nums[i - 1] && ++count >= 2) {
                shrink++;
            }
            // (2)
            else {
                // 不重复需重置count, 【必须在前面】
                if (nums[i] != nums[i - 1]) count = 0;
                // 将nums[i]往前移，【必须在后面】, 否则会影响nums[i]!=nums[i-1]的测试
                nums[i - shrink] = nums[i];
            }

        }
        return nums.length - shrink;
    }
}