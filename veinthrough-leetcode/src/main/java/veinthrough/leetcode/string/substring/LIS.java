package veinthrough.leetcode.string.substring;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.leetcode.string.substring.LCS.LCSubsequence;

/**
 * 第300题：
 * 参考文档《最长递增子序列》：Longest increasing sub-sequence
 * 给你一个整数数组 nums ，找到其中最长严格递增子序列的长度。
 * 子序列是由数组派生而来的序列，删除（或不删除）数组中的元素而不改变其余元素的顺序。
 * 例如，[3,6,2,7] 是数组 [0,3,1,6,2,2,7] 的子序列。
 */
@Slf4j
public class LIS {
    @Test
    public void test300() {
        Stream.of(
                new int[]{2, 1, 5, 3, 6, 4, 8, 9, 7, 8, 10},
                new int[]{2, 1, 5, 3, 6, 4, 8, 9, 7})
                .forEach(nums ->
                {
                    Pair<Integer, int[]> lisByLcs = _lisLcs(nums);
                    Pair<Integer, int[]> lisGeneralDP = _lisGeneralDP(nums);
                    Pair<Integer, int[]> lisGreed = _lisGreed(nums);
                    log.info(methodLog(
                            "By lcs", "(" + lisByLcs.getLeft() + "," + Arrays.toString(lisByLcs.getRight()) + ")",
                            "By general dp", "(" + lisGeneralDP.getLeft() + "," + Arrays.toString(lisGeneralDP.getRight()) + ")",
                            "By greed", "(" + lisGreed.getLeft() + "," + Arrays.toString(lisGreed.getRight()) + ")"));
                });

    }

    @SuppressWarnings("unused")
    private static Pair<Integer, int[]> lis(int[] nums) {
        if (nums == null || nums.length == 0) return null;
        return _lisGreed(nums);
    }

    /**
     * 方法1：使用最长公共子序列
     * 这个问题可以转换为最长公共子序列问题。
     * 如例子中的数组A{5，6， 7， 1， 2， 8}，则我们排序该数组得到数组A‘{1， 2， 5， 6， 7， 8}，
     * 然后找出数组A和A’的最长公共子序列即可。显然这里最长公共子序列为{5, 6, 7, 8}，也就是原数组A最长递增子序列。
     */
    public static Pair<Integer, int[]> _lisLcs(int[] nums) {
        int[] nums2 = new int[nums.length];
        System.arraycopy(nums, 0, nums2, 0, nums.length);
        Arrays.sort(nums2);
        return LCSubsequence(nums, nums2);
    }

    /**
     * 方法2：
     * (1) 设长度为N的数组为{a0，a1, a2, ...an-1)，则假定以aj结尾的数组序列的最长递增子序列长度为L(j)，
     * 【注意】这里【子序列要以这个结尾】，所以最终的结果需要一个max来暂存而不一定是lis[len11]
     * 则L(j)={ max(L(i))+1, i<j且a[i]<a[j] }。也就是说，我们需要遍历在j之前的所有位置i(从0到j-1)，
     * 找出满足条件a[i]<a[j]的L(i)，求出max(L(i))+1即为L(j)的值。
     * (2) 最后，我们遍历所有的L(j)（从0到N-1），找出最大值即为最大递增子序列。时间复杂度为O(N^2)
     */
    public static Pair<Integer, int[]> _lisGeneralDP(int[] nums) {
        int len = nums.length;
        int[] lis = new int[len];

        // 1. init as 1
        for (int i = 0; i < len; i++)
            lis[i] = 1;

        // 2. lis[j]: 以nums[j]结尾的最长递增子序列
        // 以nums[j]结尾，遍历不同的i
        for (int j = 1; j < len; j++) {
            for (int i = 0; i < j; i++) {
                if (nums[j] >= nums[i])
                    lis[j] = max(lis[j], lis[i] + 1);
            }
        }

        // 3. max,maxj
        int max = lis[len - 1], maxj = len - 1;
        // max个字符最多全为递增子序列, 如果剩下的字符都不够max个, 不可能再存在更长的递增子序列
        for (int i = len - 1; i >= max - 1; i--) {
            if (lis[i] > max) {
                max = lis[i];
                maxj = i;
            }
        }

        // 4. 构造序列
        LinkedList<Integer> lisNums = new LinkedList<>();
        lisNums.add(nums[maxj]);
        int last = maxj;
        for (int i = last - 1; i >= 0; i--) {
            // (1) 比last的子序列短1个 (2) <= last
            if (lis[i] == lis[last] - 1 && nums[i] <= nums[last]) {
                lisNums.addFirst(nums[i]);
                last = i;
            }
        }

        return Pair.of(max,
                lisNums.stream().mapToInt(Integer::intValue).toArray());
    }

    /**
     * 方法3：贪心算法
     * 案例：
     * 1. 通过B求得max
     * #         B	                		   max
     * D[0]=2	2(0)            				1
     * D[1]=1	1(1)            				1
     * D[2]=5	1(1),5(2)           			2
     * D[3]=3	1(1),3(3)           			2
     * D[4]=6	1(1),3(3),6(4)      			3
     * D[5]=4	1(1),3(3),4(5)      			3
     * D[6]=8	1(1),3(3),4(5),8(6)     		4
     * D[7]=9	1(1),3(3),4(5),8(6),9(7)		5
     * D[8]=7	1(1),3(3),4(5),7(8),9(7)		5(这里7只能替换8，不能把8,9都替换掉，7可以为后面做准备；
     * 如果后面来个>9的,那么7的作用不大；但是如果后面来个x,>7,<9的，那么x可以替换掉9，重新开始另一个序列)
     * D[9]=8	1(1),3(3),4(5),7(8),8(9)		5
     * D[10]=10	1(1),3(3),4(5),7(8),8(9),10(10)	6
     *
     * 2. 构造最终的递增子序列, 注意这里B并不是最终的递增子序列
     * 2，1 ，5 ，3 ，6，4， 8 ，9， 7    B:1,3,4,7,9；其中一个序列:1,3,4,8,9
     * 输出其中一个序列:
     * 从后往前,选取D[i]必须>=B[i]&&<D[i+1]
     * 2，1 ，5 ，3 ，6，4， 8 ，9， 7
     * 1(>=1,<3)  3(>=3,<4)  4(>=4,<8)  8(>=7,<9)  9
     */
    public static Pair<Integer, int[]> _lisGreed(int[] nums) {
        int len = nums.length;
        int[] b = new int[len];

        // 1. construct b
        // store index not value
        b[0] = 0;
        int max = 1, index;
        for (int i = 1; i < len; i++) {
            if (nums[i] >= nums[b[max - 1]]) {
                b[max++] = i;
            } else {
                // 返回数组元素需要插入的位置(第一个大于nums[i]的位置)
                index = _biSearch(Arrays.stream(b).map(x -> nums[x]).toArray(),
                        max, nums[i]);
                b[index] = i;
            }
        }

        // 2. construct sequence from the end
        // max: index of last num in sequence
        // b is not real of the sequence
        LinkedList<Integer> lisNums = new LinkedList<>();
        int left, right = b[max - 1];
        lisNums.add(nums[right]);
        // D[i]必须>=nums[B[j-1]]&&<nums[j]
        for (int j = right - 1, k = max - 2; j >= 0 && k >= 0; j--) {
            left = b[k];
            if (nums[j] >= nums[left] && nums[j] <= nums[right]) {
                lisNums.addFirst(nums[j]);
                right = left;
                k--;
            }
        }

        return Pair.of(max,
                lisNums.stream().mapToInt(Integer::intValue).toArray());
    }

    private static int _biSearch(int[] nums, int len, int value) {
        int i = 0, j = len - 1, mid;
        while (i <= j) {
            mid = (i + j) / 2;
            if (nums[mid] < value)
                i = mid + 1;
            else if (nums[mid] > value)
                j = mid - 1;
            else
                return mid;
        }
        return i;
    }
}
