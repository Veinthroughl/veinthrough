package veinthrough.leetcode.bs;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第33题：搜索旋转排序数组
 * 整数数组nums按升序排列，数组中的值互不相同 。
 * 在传递给函数之前，nums 在预先未知的某个下标 k（0 <= k < nums.length）上进行了 旋转，
 * 使数组变为 [nums[k], nums[k+1], ..., nums[n-1], nums[0], nums[1], ..., nums[k-1]]（下标 从 0 开始 计数）。
 * 例如， [0,1,2,4,5,6,7] 在下标 3 处经旋转后可能变为 [4,5,6,7,0,1,2] 。
 * 给你 旋转后 的数组 nums 和一个整数 target ，如果 nums 中存在这个目标值 target ，则返回它的索引，否则返回-1。
 * 进阶：设计一个时间复杂度为 O(log n) 的解决方案
 * 示例 1：
 * 输入：nums = [4,5,6,7,0,1,2], target = 0
 * 输出：4
 * 示例 2：
 * 输入：nums = [4,5,6,7,0,1,2], target = 3
 * 输出：-1
 * 示例 3：
 * 输入：nums = [1], target = 0
 * 输出：-1
 *
 * 第81题： 搜索旋转排序数组 II
 * 在第33题的基础上，数组中的值不必互不相同。
 *
 * 第153题:寻找旋转排序数组中的最小值
 * 已知一个长度为n的数组，预先按照升序排列，经由1到n次旋转后，得到输入数组。例如，原数组 nums = [0,1,2,4,5,6,7] 在变化后可能得到：
 * 若旋转 4 次，则可以得到 [4,5,6,7,0,1,2]
 * 若旋转 7 次，则可以得到 [0,1,2,4,5,6,7]
 * 给你一个元素值互【不相同】的数组nums，它原来是一个升序排列的数组，并按上述情形进行了多次旋转。请你找出并返回数组中的最小元素 。
 *
 * 第154题：
 * 在第153题的基础上，数组中的值不必互不相同
 */
@SuppressWarnings("unused")
@Slf4j
public class BinarySearch_Rotated {
    @Test
    public void test33() {
        Stream.of(
                distinctSearch(new int[]{5, 1, 3}, 3),
                distinctSearch(new int[]{4, 5, 6, 7, 0, 1, 2}, 0),
                distinctSearch(new int[]{5, 6, 7, 0, 1, 2}, 3),
                distinctSearch(new int[]{1, 3, 5}, 0))
                .forEach(result -> log.info(methodLog("" + result)));
    }

    @Test
    public void test81() {
        Stream.of(
                // 有可能相同的地方被旋转
                search(new int[]{2, 2, 2, 0, 0, 1}, 0),
                search(new int[]{1, 0, 1, 1, 1}, 0),
                search(new int[]{2, 5, 6, 0, 0, 1, 2}, 3),
                search(new int[]{2, 5, 6, 0, 0, 1, 2}, 0))
                .forEach(result -> log.info(methodLog("" + result)));
    }

    @Test
    public void test153() {
        Stream.of(
                distinctFindMin(new int[]{3, 4, 5, 1, 2}),
                distinctFindMin(new int[]{4, 5, 6, 7, 0, 1, 2}),
                distinctFindMin(new int[]{11, 13, 15, 17}))
                .forEach(result -> log.info(methodLog("" + result)));
    }

    @Test
    public void test154() {
        Stream.of(
                findMin(new int[]{2, 2, 2, 0, 1}),
                findMin(new int[]{3, 1, 2, 3, 3, 3, 3}),
                findMin(new int[]{3, 3, 3, 3, 4, 2, 3}),
                findMin(new int[]{4, 5, 3, 3, 3, 3, 3}),
                findMin(new int[]{3, 3, 3, 3, 1, 2, 2}))
                .forEach(result -> log.info(methodLog("" + result)));
    }

    private static int distinctSearch(int[] nums, int target) {
        // 1.
        if (nums.length < 1 || nums.length == 1 && nums[0] != target) return -1;
        // 2. 看nums[0], 因为nums[0]是一个转折点
        if (nums[0] == target) return 0;
        // 3. 方法1/方法2
        return _BSRotated2(nums, target);
    }

    /**
     * 方法1：
     * (1) 先求出pivot, 旋转的点： 只有在旋转后对接的地方才会出现后一个数小于前一个数
     * 比如[4,5,6,7,0,1,2]，只有在7/0的地方会出现后一个数小于前一个数
     * (2) 再根据pivot进行二分搜索
     */
    private static int _BSRotated(int[] nums, int target) {
        // (1) find pivot
        int len = nums.length;
        int left = 0, right = len - 1, mid;
        int pivot = -1;
        while (left <= right) {
            mid = (left + right) / 2;
            if (mid < len - 1 && nums[mid] > nums[mid + 1]) {
                pivot = mid;
                break;
            } else if (mid > 0 && nums[mid - 1] > nums[mid]) {
                pivot = mid - 1;
                break;
            } else if (nums[mid] > nums[0]) left = mid + 1;
            else right = mid - 1;
        }

        // (2) binary distinctSearch
        if (pivot == -1) return _BS(nums, 0, len - 1, target);
        else if (target > nums[0]) return _BS(nums, 1, pivot, target);
        else return _BS(nums, pivot + 1, len - 1, target);
    }

    // general binary search
    private static int _BS(int[] nums, int start, int end, int target) {
        int left = start, right = end, mid;
        while (left <= right) {
            mid = (left + right) / 2;
            if (nums[mid] == target) return mid;
            else if (nums[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    /**
     * 方法2：pivot
     * 二分搜索，每次mid的左边/或者右边至少会有一边是排序的：
     * 1. target==nums[mid], return
     * 2. nums[mid]>=nums[left]，左边有序:
     * (1) target>nums[left]且target<nums[mid], right=mid-1;
     * (2) 其他: left=mid+1;
     * 3. nums[mid]<nums[left]，右边有序:
     * (1) target>nums[mid]且target<nums[right], left=mid+1;
     * (2) 其他: right=mid-1;
     */
    private static int _BSRotated2(int[] nums, int target) {
        int len = nums.length;
        int left = 0, right = len - 1, mid;
        while (left <= right) {
            mid = (left + right) / 2;
            // 1.
            if (nums[mid] == target) return mid;
            // 2. 左边有序
            if (nums[mid] >= nums[left]) {
                // 注意这里需要==nums[left]，这里使用nums[left]比使用nums[0]更准确
                if (target == nums[left]) return left;
                else if (target > nums[left] && target < nums[mid]) right = mid - 1;
                else left = mid + 1;
            }
            // 3.右边有序
            else {
                // 注意这里需要==nums[right]
                if (target == nums[right]) return right;
                else if (target > nums[mid] && target < nums[right]) left = mid + 1;
                else right = mid - 1;
            }
        }
        return -1;
    }

    /**
     * 第81题：
     * 二分搜索，每次mid的左边/或者右边至少会有一边是排序的：
     * 1. target==nums[mid], return
     *
     * 2. nums[left]==nums[mid]==nums[right]时不能确定哪边有序
     * 0,1,2,3,4,5,6
     * 3,1,2,3,3,3,3: mid=3, 右边有序
     * 3,3,3,3,4,2,3: mid=3, 左边有序
     *
     * 3. nums[mid]>=nums[left]，至少左边有序:
     * 0,1,2,3,4,5,6
     * 3,4,4,4,4,4,4: mid=3, nums[mid]>nums[left], 至少左边有序
     * 3,4,4,4,1,2,3: mid=3, nums[mid]>nums[left], 至少左边有序
     * 3,3,3,3,1,2,2: mid=3, nums[mid]==nums[left], 至少左边有序
     * (1) target>nums[left]且target<nums[mid], right=mid-1;
     * (2) 其他: left=mid+1;
     *
     * 4. nums[mid]<nums[left], 至少右边有序:
     * (1) target>nums[mid]且target<nums[right], left=mid+1;
     * (2) 其他: right=mid-1;
     */
    private static boolean search(int[] nums, int target) {
        int len = nums.length;
        int i = 0, j = len - 1, mid;
        while (i <= j) {
            mid = (i + j) / 2;
            // 1.
            if (nums[mid] == target) return true;
            // 2. 不能确定哪边有序
            if (nums[mid] == nums[i] && nums[mid] == nums[j]) {
                i++;
                j--;
            }
            // 3. 至少左边有序
            else if (nums[mid] >= nums[i]) {
                // 注意这里需要==nums[i]
                if (target == nums[i]) return true;
                else if (target > nums[i] && target < nums[mid]) j = mid - 1;
                else i = mid + 1;
            }
            // 4. 至少右边有序
            else {
                // 注意这里需要==nums[j]
                if (target == nums[j]) return true;
                else if (target > nums[mid] && target < nums[j]) i = mid + 1;
                else j = mid - 1;
            }
        }
        return false;
    }

    /**
     * 第153题:
     * 在二分查找的每一步中，左边界为i，右边界为j，区间的中点为m，最小值就在该区间内。
     * 我们将中轴元素nums[m]与右边界元素nums[j] 进行比较(【其实另一种做法也可以与nums[i]比较】)，可能会有以下的二种情况：
     * (1) nums[m]<nums[j]。这说明nums[m]是最小值右侧的元素/或者就是最小值，因此我们可以忽略二分查找区间的右半部分。
     * (2) nums[m]>nums[j]。如下图所示，这说明nums[m]是最小值左侧的元素，因此我们可以忽略二分查找区间的左半部分。
     * 由于数组不包含重复元素，并且只要当前的区间长度不为1, m就不会与j重合；
     * 而如果当前的区间长度为1，这说明我们已经可以结束二分查找了。因此不会存在nums[m]=nums[j]的情况。
     *
     * 当二分查找结束时，我们就得到了最小值所在的位置。
     * (1) 本例中要维持i<j,即(i,j)区间最少为1, 最终i==j
     * (2) 前面题目中要维持i<=j， 最终i=j+1
     */
    private static int distinctFindMin(int[] nums) {
        int i = 0, j = nums.length - 1;
        int mid;
        // 要维持[i,j]区间最少为1, 最终i==j
        while (i < j) {
            mid = (i + j) / 2;
            if (nums[mid] < nums[j])
                // 注意这里是mid而不是mid-1
                j = mid;
            else
                i = mid + 1;
        }
        return nums[i];
    }

    /**
     * 第154题:
     * 1. nums[left]==nums[mid]==nums[right]时不能确定最小值在mid的左边/右边
     * 0,1,2,3,4,5,6
     * 3,1,2,3,3,3,3: mid=3, min在左边
     * 3,3,3,3,4,2,3: mid=3, min在右边
     *
     * 2. nums[mid]<=nums[right], min肯定在mid左边/mid本身
     * 0,1,2,3,4,5,6
     * 3,4,4,4,4,4,4: mid=3, nums[mid]==nums[right]
     * 3,4,4,1,2,3,3: mid=3, nums[mid]<nums[right], 至少左边有序
     *
     * 3. nums[mid]>=nums[right], min肯定在mid右边/mid本身
     * 3,3,3,3,1,2,2
     */
    private static int findMin(int[] nums) {
        int i = 0, j = nums.length - 1;
        int mid;
        // 要维持[i,j]区间最少为1, 最终i==j
        while (i < j) {
            mid = (i + j) / 2;
            // 1. nums[i]==nums[mid]==nums[j]时不能确定最小值在mid的左边/右边
            if (nums[mid] == nums[i] && nums[mid] == nums[j]) {
                i++;
                j--;
            }
            // 2. nums[mid]<=nums[j], min肯定在mid左边/mid本身
            else if (nums[mid] <= nums[j]) {
                // 注意这里是mid而不是mid+1
                j = mid;
            }
            // 3. nums[mid]>=nums[j], min肯定在mid右边/mid本身
            else
                i = mid + 1;
        }
        return nums[i];
    }
}
