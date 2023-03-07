package veinthrough.leetcode.bs;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.min;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第4题：
 * 给定两个大小分别为 m 和 n 的正序（从小到大）数组 nums1 和 nums2。请你找出并返回这两个正序数组的 中位数 。
 *
 * 算法的时间复杂度应该为 O(log (m+n)) 。
 *
 * 方法1：
 * 见代码
 *
 * 方法2：
 * 见文档
 */
@Slf4j
public class MedianOfSortedArrays {

    @Test
    public void test4() {
        Stream.of(
                findMedianSortedArrays(new int[]{0, 1, 2, 3, 4, 5, 8, 9}, new int[]{6, 7}),
                findMedianSortedArrays(new int[]{0, 2, 4, 5, 6, 7, 8, 9}, new int[]{1, 3}),
                findMedianSortedArrays(new int[]{0, 1, 2, 3, 4, 5, 8}, new int[]{6, 7}),
                findMedianSortedArrays(new int[]{0, 2, 4, 5, 6, 7, 8}, new int[]{1, 3}),
                findMedianSortedArrays(new int[]{1, 1}, new int[]{1, 2}))
                .forEach(median -> log.info(methodLog("median", median + "")));
    }

    /**
     * from 0      from 0              from 1      from 1
     * 7       3           n/2=(n-1)/2         4           (n+1)/2=(n+2)/2
     * 8       3,4         (n-1)/2,n/2         4,5         (n+1)/2,(n+2)/2
     * 9       4           n/2=(n-1)/2         5           (n+1)/2=(n+2)/2
     * 如果某个有序数组长度是奇数，那么其中位数就是最中间那个，如果是偶数，那么就是最中间两个数字的平均值。
     * 这里对于两个有序数组也是一样的，假设两个有序数组的长度分别为m和n，由于两个数组长度之和m+n 的奇偶不确定，
     * 因此需要分情况来讨论，对于奇数的情况，直接找到最中间的数即可，偶数的话需要求最中间两个数的平均值。
     */
    private static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int total = nums1.length + nums2.length;
        if (total % 2 == 0)
            return (median(nums1, 0, nums2, 0, (total + 1) / 2) +
                    median(nums1, 0, nums2, 0, (total + 2) / 2)) / 2;
        else return median(nums1, 0, nums2, 0, (total + 1) / 2);
    }

    /**
     * 在两个有序数组中找到第n（中间）个元素
     *
     * 1. one is empty
     * 当某一个数组的起始位置大于等于其数组长度时，说明其所有数字均已经被淘汰了，
     * 相当于一个空数组，那么实际上就变成了在另一个数组中找中位数。
     * 2.比较pivot
     * (1) pivot的定位
     * 假设nums1长度为m, nums2长度为n
     * > pivot定位1, 这里代码使用的是这种思维
     * pivot1为nums1[k/2], pivot2为nums2[k/2]
     * 如果pivot1<pivot2, 那么第k个元素不可能出现在nums1的前[k/2]元素中,nums1的【start坐标】增为k/2
     * > pivot定位2，我觉得这样也可以, 而且既可以用【start坐标】/也可以使用【end坐标】
     * pivot1为nums1[m/2], pivot2为nums2[n/2]
     * 如果pivot1<pivot2, 那么第k个元素不可能出现在nums1的前[m/2]元素中, nums1的【start坐标】增为k/2;
     * 【且】                 第k个元素不可能出现在nums2的后[n/2]元素中, nums2的【end坐标】减为k/2;
     *
     * (2) 对于其中一个数组(比如nums2)少于n/2个元素;
     * 所以我们需要先检查一下，数组中到底存不存在第K/2个数字，如果存在就取出来，否则就赋值【整型最大值】，
     * 并且淘汰另一个数组的前K/2个数字。
     * 另外一个数组(比如nums1)必然大于n/2个元素,且前n/2(2)个元素中不可能含有第n个元素;
     * 假设nums1[n/2-1]是第n个元素, 即使把nums1的元素全加过来, 也凑不够n个, 与假设矛盾
     * #              0   1   2   3   4   5   6   7   8   9
     * # nums1(0)     1   3*  4   5   6   7   8
     * # nums2(0)     0   2*
     * total=9,n=4,n/2=2
     * pivot1=nums1[1]=3, pivot2=nums2[1]=2
     * pivot1>pivot2, 所以nums2的前n/2(2)个元素中不可能含有第n个元素, 实际上下一次递归就把nums2全部丢弃了
     * (3) 如果pivot1<pivot2, 那么第k个元素不可能出现在nums1的前[k/2]元素中,nums1的【start坐标】增为k/2
     * #              0   1   2   3   4   5   6   7   8   9
     * # nums1(0)     0   1*  2   3   6   7   8   9
     * # nums2(0)     4   5*
     * total=10,n=5,n/2=2
     * pivot1=nums1[1]=1, pivot2=nums2[1]=5
     * pivot1<pivot2, 所以nums1的前n/2(2)个元素中不可能含有第n个元素:
     * 假设nums1[n/2-1]是第n个元素,也就是说前面必须有n-1个小于它的元素,
     * 但是即使把nums2中的前n/2个元素都加进来, 加上自己, 也最多是n/2+n/2(这里n有可能是奇数)个元素,
     * 而不可能把nums2中的前n/2个元素都加进来, 因为nums2中的前n/2个元素包含大于它的元素, 与假设矛盾
     */

    private static double median(int[] nums1, int s1, int[] nums2, int s2, int n) {
        // 1. one is empty
        if (s1 >= nums1.length) return nums2[s2 + n - 1];
        if (s2 >= nums2.length) return nums1[s1 + n - 1];
        // n==1, 查找第1个元素
        if (n == 1) return min(nums1[s1], nums2[s2]);

        // 2. 比较pivot
        // 2.1 对于其中一个数组(比如nums2)少于n/2个元素
        int pivot1 = nums1.length - s1 < n / 2 ? MAX_VALUE : nums1[s1 + n / 2 - 1];
        int pivot2 = nums2.length - s2 < n / 2 ? MAX_VALUE : nums2[s2 + n / 2 - 1];

        // 2.2 pivot1==pivot2, 并且n为偶数
        if (pivot1 == pivot2 && n % 2 == 0) return pivot1;

        // 2.3 pivot1==pivot2, n为奇数
        //              0   1   2   3   4   5   6   7   8   9
        // nums1(0)     1   2*  4   5   6   7   8
        // nums2(0)     0   2*
        // total=9,n=4,n/2=2
        // pivot1=nums1[1]=3, pivot2=nums2[1]=2
        if (pivot1 == pivot2) {
            if (nums1.length - s1 < n / 2 + 1) return nums2[s1 + n / 2];
            if (nums2.length - s2 < n / 2 + 1) return nums1[s2 + n / 2];
            return min(nums1[s1 + n / 2], nums2[s2 + n / 2]);
        }
        // 2.4 pivot1<pivot2
        if (pivot1 < pivot2) return median(nums1, s1 + n / 2, nums2, s2, n - n / 2);
        // 2.5 pivot2>pivot1
        return median(nums1, s1, nums2, s2 + n / 2, n - n / 2);
    }
}