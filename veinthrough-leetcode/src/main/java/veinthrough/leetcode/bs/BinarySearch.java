package veinthrough.leetcode.bs;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第35题：
 * 给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。
 * 如果目标值不存在于数组中，返回它将会被按顺序插入的位置。
 * 你可以假设数组中无重复元素。
 * 【如果数组中有重复元素呢？】
 *
 * 第34题：在排序数组中查找元素的第一个和最后一个位置
 * 给定一个按照升序排列的整数数组 nums，和一个目标值 target。找出给定目标值在数组中的开始位置和结束位置。
 * 如果数组中不存在目标值 target，返回 [-1, -1]。
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class BinarySearch {
    @Test
    public void test35_1() {
        Stream.of(
                searchInsert(new int[]{1, 3, 5, 6}, 2), // 1
                searchInsert(new int[]{1, 3, 5, 6}, 7), // 4
                searchInsert(new int[]{1, 3, 5, 6}, 0), // 0
                searchInsert(new int[]{1, 3, 5, 6}, 5), // 2
                searchInsert(new int[]{1, 3, 5, 5, 5, 5, 6}, 5), // 3
                searchInsert(new int[]{1, 3, 5, 5, 6}, 5), // 2
                searchInsert(new int[]{1, 3, 5, 5, 5, 6}, 5)) // 2
                .forEach(result -> log.info(methodLog("" + result)));
    }

    @Test
    public void test35_2() {
        Stream.of(
                searchInsert2(new int[]{1, 3, 5, 6}, 2), // 1
                searchInsert2(new int[]{1, 3, 5, 6}, 7), // 4
                searchInsert2(new int[]{1, 3, 5, 6}, 0), // 0
                searchInsert2(new int[]{1, 3, 5, 6}, 5), // 2
                searchInsert2(new int[]{1, 3, 5, 5, 5, 5, 6}, 5), // 2
                searchInsert2(new int[]{1, 3, 5, 5, 6}, 5), // 2
                searchInsert2(new int[]{1, 3, 5, 5, 5, 6}, 5)) // 2
                .forEach(result -> log.info(methodLog("" + result)));
    }

    @Test
    public void test35_3() {
        Stream.of(
                searchInsert3(new int[]{1, 3, 5, 6}, 2), // 0
                searchInsert3(new int[]{1, 3, 5, 6}, 7), // 3
                searchInsert3(new int[]{1, 3, 5, 6}, 0), // 0
                searchInsert3(new int[]{1, 3, 5, 6}, 5), // 2
                searchInsert3(new int[]{1, 3, 5, 5, 5, 5, 6}, 5), // 5
                searchInsert3(new int[]{1, 3, 5, 5, 6}, 5), // 3
                searchInsert3(new int[]{1, 3, 5, 5, 5, 6}, 5)) // 4
                .forEach(result -> log.info(methodLog("" + result)));
    }
    
    @Test
    public void test34() {
        Stream.of(
                searchRange(new int[]{5, 7, 7, 8, 8, 10}, 8),
                searchRange(new int[]{5, 7, 7, 8, 8, 10}, 6))
                .forEach(result -> log.info(methodLog(Arrays.toString(result))));
    }

    /**
     * 第35题
     * 方法1：
     * 1. 对于target不在数组中的情况,
     * 最终的状态就是(...j,i...)，其中target应该介于(nums[j],nums[i])中间,
     * 如果target<nums[0]，那么j=-1, i=0;
     * 如果target>nums[len-1], 那么j=len-1, i=len;
     * (1) 最终i=j+1,
     * (2) i：第一个>target的位置
     * (3) j: 第一个<target的位置
     * 2. 对于target在数组中的情况,
     * 返回的位置m肯定==target, 但如果存在重复元素【不能完全明确是哪个】
     */
    private int searchInsert(int[] nums, int target) {
//        if (target < nums[0]) return 0;
//        if (target > nums[nums.length - 1]) return nums.length;

        int i = 0, j = nums.length - 1, m;
        while (i <= j) {
            m = (i + j) >> 1;
            if (target == nums[m]) return m;
            else if (target < nums[m]) j = m - 1;
            else i = m + 1;
        }
        // 如果target不在数组中，i为第一个>target的位置
        return i;
    }

    /**
     * 第35题
     * 方法2:
     * 1. 对于target不在数组中的情况,
     * 最终的状态就是(...j,i...)，其中target应该介于(nums[j],nums[i])中间,
     * 如果target<nums[0]，那么j=-1, i=0;
     * 如果target>nums[len-1], 那么j=len-1, i=len;
     * (1) 最终i=j+1;
     * (2) 如果target>nums[length-1], 那么p将不会被赋值，所以需要【将p初始化为len】
     * (3) 返回的p为【第一个>target的位置】或者【开始初始化的len】
     * 2. 对于target在数组中的情况,
     * 返回的p位置为【第一个==target的位置】
     *
     * 说明：
     * 1：只要一直target<nums[mid], j/mid/p就会一直左移(被赋值)，所以是【第一个>target的位置】，
     * 但有可能target大于最后一个元素(不存在nums[mid]>target导致p不会被赋值)，所以p初始化为len
     * 2: 只要一直target==nums[mid], j/mid/p就会一直左移，所以是【第一个==target的位置】
     */
    private int searchInsert2(int[] nums, int target) {
//        if (target < nums[0]) return 0;
//        if (target > nums[nums.length - 1]) return nums.length;

        int i = 0, j = nums.length - 1, mid;
        int p = nums.length;
        while (i <= j) {
            mid = (i + j) >> 1;
            if (target <= nums[mid]) {
                j = mid - 1;
                p = mid;
            } else i = mid + 1;
        }
        // (1) 存在: 第一个==target的位置
        // (2) 不存在: p为第一个>target的位置
        return p;
    }

    /**
     * 第35题
     * 方法3:
     * 1. 对于target不在数组中的情况,
     * 最终的状态就是(...j,i...)，其中target应该介于(nums[j],nums[i])中间,
     * 如果target<nums[0]，那么j=-1, i=0;
     * 如果target>nums[len-1], 那么j=len-1, i=len;
     * (1) 最终i=j+1;
     * (2) 如果target<nums[0], 那么p将不会被赋值，所以需要【将p初始化为-1】
     * (3) 返回的i为【第一个<target的位置】【包括开始初始化的-1】
     * 2. 对于target在数组中的情况,
     * 返回的i位置为【最后一个==target的位置】
     *
     * 说明：
     * 1：只要一直target>nums[mid], i/mid/p就会一直右移，所以是【第一个<target的位置】；
     * 但有可能target小于第一个元素(不存在nums[mid]<target导致p不被赋值)，所以初始化为-1
     * 2：只要一直target==nums[mid], i/mid/p就会一直右移，所以是【最后一个==target的位置】
     */
    private int searchInsert3(int[] nums, int target) {
//        if (target < nums[0]) return 0;
//        if (target > nums[nums.length - 1]) return nums.length;

        int i = 0, j = nums.length - 1, mid;
        int p = -1;
        while (i <= j) {
            mid = (i + j) >> 1;
            if (target >= nums[mid]) {
                i = mid + 1;
                p = mid;
            } else
                j = mid - 1;
        }

        // (1) p==-1: target<nums[0]导致p不被赋值
        // (2) 不存在我们需要返回第一个>target的位置，而原来p是第一个<target的位置，所以+1
        if (p == -1 || nums[p] != target) return p + 1;
        else return p;
    }

    /**
     * 第34题：
     * (1) searchFirst使用方法2
     * (2) searchLast使用方法3
     */
    private int[] searchRange(int[] nums, int target) {
        int first = searchFirst(nums, target);
        if (first == nums.length || nums[first] != target)
            return new int[]{-1, -1};
        int end = searchEnd(nums, target);
        return new int[]{first, end};
    }


    /**
     * 使用方法2
     */
    private int searchFirst(int[] nums, int target) {
        int i = 0, j = nums.length - 1, mid;
        int p = nums.length;
        while (i <= j) {
            mid = (i + j) >> 1;
            if (target <= nums[mid]) {
                j = mid - 1;
                p = mid;
            } else i = mid + 1;
        }
        return p;
    }

    /**
     * 使用方法3
     */
    private int searchEnd(int[] nums, int target) {
        int i = 0, j = nums.length - 1, mid;
        int p = -1;
        while (i <= j) {
            mid = (i + j) >> 1;
            if (target >= nums[mid]) {
                i = mid + 1;
                p = mid;
            } else
                j = mid - 1;
        }

        // (1) p==-1: target<nums[0]导致p不被赋值
        // (2) 不存在我们需要返回第一个>target的位置，而原来p是第一个<target的位置，所以+1
        if (p == -1 || nums[p] != target) return p + 1;
        else return p;
    }
}