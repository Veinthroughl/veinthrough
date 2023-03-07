package veinthrough.leetcode.array;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第88题： 合并两个有序数组
 * 给你两个有序整数数组 nums1 和 nums2，请你将 nums2 合并到 nums1 中，使 nums1 成为一个有序数组。
 * 初始化 nums1 和 nums2 的元素数量分别为 m 和 n 。
 * 你可以假设 nums1 的空间大小等于 m + n，这样它就有足够的空间保存来自 nums2 的元素。
 *
 * 示例 1：
 * 输入：nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3
 * 输出：[1,2,2,3,5,6]
 *
 * 示例 2：
 * 输入：nums1 = [1], m = 1, nums2 = [], n = 0
 * 输出：[1]
 */
@Slf4j
public class MergeArray {
    @Test
    public void test() {
        int[] nums1 = new int[]{1, 2, 3, 0, 0, 0};
        int[] nums2 = new int[]{2, 5, 6};
        log.info(methodLog("Before", Arrays.toString(nums1)));
        merge(nums1, 3, nums2, 3);
        log.info(methodLog("After", Arrays.toString(nums1)));

        nums1 = new int[]{4, 5, 6, 0, 0, 0};
        nums2 = new int[]{1, 2, 3};
        log.info(methodLog("Before", Arrays.toString(nums1)));
        merge(nums1, 3, nums2, 3);
        log.info(methodLog("After", Arrays.toString(nums1)));

        nums1 = new int[]{4, 5, 6, 0, 0, 0, 0};
        nums2 = new int[]{1, 2, 3, 7};
        log.info(methodLog("Before", Arrays.toString(nums1)));
        merge(nums1, 3, nums2, 4);
        log.info(methodLog("After", Arrays.toString(nums1)));

        nums1 = new int[]{4, 5, 6, 9, 0, 0, 0, 0, 0};
        nums2 = new int[]{1, 2, 3, 7, 8};
        log.info(methodLog("Before", Arrays.toString(nums1)));
        merge(nums1, 4, nums2, 5);
        log.info(methodLog("After", Arrays.toString(nums1)));

        nums1 = new int[]{1};
        nums2 = new int[]{};
        log.info(methodLog("Before", Arrays.toString(nums1)));
        merge(nums1, 1, nums2, 0);
        log.info(methodLog("After", Arrays.toString(nums1)));

        nums1 = new int[]{0};
        nums2 = new int[]{1};
        log.info(methodLog("Before", Arrays.toString(nums1)));
        merge(nums1, 0, nums2, 1);
        log.info(methodLog("After", Arrays.toString(nums1)));
    }

    /**
     * 方法1: 双指针, 从前往后
     * 方法2: 双指针, 从后往前, 不需要移动数组
     */
    private void merge(int[] nums1, int m, int[] nums2, int n) {
        // boundary
        if (n == 0) return;

        //
        mergeForward(nums1, m, nums2, n);
        mergeBackward(nums1, m, nums2, n);
    }

    /**
     * 方法1: 双指针, 从前往后
     */
    private void mergeForward(int[] nums1, int m, int[] nums2, int n) {
        int i, j, k;
        // i==m+j: 说明nums中的元素已经遍历完，只剩下nums2中的元素
        for (i = 0, j = 0; j < n && i != m + j; ) {
            // 1, nums2中的元素更小
            if (nums2[j] < nums1[i]) {
                // (1) 将nums1中i后面的元素向后移动一个
                for (k = m + j; k >= i + 1; k--)
                    nums1[k] = nums1[k - 1];
                // (2) 放置nums2中的元素
                nums1[i++] = nums2[j++];
                // 2. nums1中的元素更小
            } else i++;
        }
        // 3. 最后剩余的nums2
        for (k = j; k < n; )
            nums1[m + k] = nums2[k++];

    }

    /**
     * 方法2: 双指针, 从后往前, 不需要移动数组
     */
    private void mergeBackward(int[] nums1, int m, int[] nums2, int n) {
        int i, j, k;
        for (i = m - 1, j = n - 1, k = m + n - 1; i >= 0 && j >= 0; k--) {
            if (nums2[j] >= nums1[i]) {
                nums1[k] = nums2[j];
                j--;
            } else {
                nums1[k] = nums1[i];
                i--;
            }
        }
        while (j >= 0) nums1[k--] = nums2[j--];
    }
}
