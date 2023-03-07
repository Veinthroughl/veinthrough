package veinthrough.leetcode.ac;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.generic.Pair;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第31题，下一个排列序列，
 * 实现获取 下一个排列 的函数，算法需要将给定数字序列重新排列成字典序中下一个更大的排列。
 * 如果不存在下一个更大的排列，则将数字重新排列成最小的排列（即升序排列）。
 * 必须 原地 修改，只允许使用额外常数空间。
 *
 * 第60题，第n个排列，
 * 给出集合 [1,2,3,...,n]，其所有元素共有 n! 种排列。
 * 按大小顺序列出所有排列情况，并一一标记，当 n = 3 时, 所有排列如下：
 * "123"
 * "132"
 * "213"
 * "231"
 * "312"
 * "321"
 * 给定 n 和 k，返回第 k 个排列。
 */
@SuppressWarnings("unused")
@Slf4j
public class Permutation {
    @Test
    public void test31() {
        Stream.of(
                new int[]{1, 4, 3, 2, 2}, // 2,1,2,3,4
                new int[]{1, 3, 2}, // 2,1,3
                new int[]{2, 3, 1}, // 3,1,2
                new int[]{2, 2, 7, 5, 4, 3, 2, 2, 1}, // 2,3,1,2,2,2,4,5,7
                new int[]{2, 1, 7, 5, 4, 3, 2, 2, 1}, // 2,2,1,1,2,3,4,5,7
                new int[]{2, 1, 2, 2, 2, 2, 2, 1}, // 2,2,1,1,2,2,2,2
                new int[]{1, 2, 3, 4, 3, 3, 8, 4, 3, 1}, // 1,2,3,4,3,4,1,3,3,8
                new int[]{1, 2, 3, 4, 3, 3, 8, 2, 1, 0}, // 1,2,3,4,3,8,0,1,2,3
                new int[]{2, 3, 1, 3, 3}, // 2,3,3,1,3
                new int[]{1, 2, 3}, // 1,3,2
                new int[]{3, 2, 1}) // 1,2,3
                .forEach(nums -> {
                    nextPermutation(nums);
                    log.info(methodLog(Arrays.toString(nums)));
                });
    }

    @Test
    public void test60() {
        Stream.of(
                Pair.of(4, 13),
                Pair.of(4, 9),
                Pair.of(3, 3),
                Pair.of(3, 1))
                .forEach(pair -> log.info(methodLog(
                        String.format("(%d,%d)", pair.getFirst(), pair.getSecond()),
                        nthPermutation(pair.getFirst(), pair.getSecond()))));

    }

    /**
     * #                0	1	2	3	4	5	6	7	8	9
     * #                1	2	3	4	3	3	8	4	3	1
     * # find l/r/c                        l	r   c
     * # swap l/c       1	2	3	4	3	4	8	3	3	1
     * # reverse right  1	2	3	4	3	4	1   3   3   8
     * -----------------------c==r------------------------------
     * #                1   2	3	4	3	3	8	2	1	0
     * # find l/r/c                        l  r/c
     * ---------------------------------------------------------
     * #                2   2   7   5   4   3   2   2   1
     * # find l/r/c         l   r           c
     * ---------------------c(rightest)-------------------------
     * #                2   1   7   5   4   2   2   2   1
     * # find l/r/c         l   r                   c
     * ---------------------纯倒序------------------------------
     * #                9   8   7   6   5   4   3   2   1
     * # find l/r/c     r
     * ---------------------纯正序------------------------------
     * #                1   2   3   4   5   6   7   8   9
     * # find l/r/c                                 l  r/c
     */
    private void nextPermutation(int[] nums) {
        // 1.
        if (nums.length <= 1) return;
        if (nums.length == 2) {
            nums[0] ^= nums[1];
            nums[1] ^= nums[0];
            nums[0] ^= nums[1];
            return;
        }

        int len = nums.length, left, right, chosen;
        // 2. (1) find rightest part: desc order
        right = len - 1;
        while (right > 0 && nums[right - 1] >= nums[right]) right--;
        // (2) chose the minimum element(if duplicate, rightest) that >nums[left],
        //     [chosen] may be [right]
        //      then swap nums[left]/nums[chosen]
        if (right > 0) {
            left = right - 1;
            chosen = nums.length - 1;
            while (chosen >= 0 && nums[chosen] <= nums[left]) chosen--;
            // swap
            int temp = nums[left];
            nums[left] = nums[chosen];
            nums[chosen] = temp;
        }
        // (3) rightest part: desc order -> asc order
        // right=0: all desc order
        _reverse(nums, right);
    }

    private void _reverse(int[] nums, int start) {
        int left = start, right = nums.length - 1;
        int temp;
        while (left < right) {
            // swap
            temp = nums[left];
            nums[left] = nums[right];
            nums[right] = temp;
            //
            left++;
            right--;
        }
    }

    /**
     * 第n个排列
     */
    private String nthPermutation(int n, int k) {
        // boundary
        if (n == 1 && k == 1) return "1";
        if (n == 2)
            if (k == 1) return "12";
            else if (k == 2) return "21";

        // 实现1：自己的做法(思路都一样，使用列表)
        return _nthPermutation(n, k);
        // 实现2：题解的做法(思路都一样，使用valid数组): 这个【k--】用的很传神
//        return _nthPermutation2(n, k);
    }

    /**
     * 自己的做法(思路都一样，使用列表)
     * 比如要求(1,2,3,4)的第4个排列
     * 1234,1243,1324,1342,1423,1432
     * 2134,2143,2314,2341,2413,2431
     * 3124,3142,3214,3241,3412,3421
     * 4123,4132,4213,4231,4312,4321
     * #          0  1  2  3
     * #  f       1  2  6  24
     * #  nums    1  2  3  4
     * -----------------k=12-------------------------------
     * (1) i=2, k=12, index=ceil(12/f[2]-1)=1
     * #          0  1  2
     * #  nums    1  3  4【列表】
     * ans: 2
     * k: 12%f[2]=0
     * (2) k=0, 反向迭代添加剩下的nums
     * ans: 2,4,3,1
     * -----------------k=13-------------------------------
     * (1) i=2, k=13, index=ceil(13/f[2]-1)=2
     * #          0  1  2
     * #  nums    1  2  4【列表】
     * ans: 3
     * k: 13%f[2]=1
     * (2) i=1, k=1, index=ceil(1/f[1]-1)=0
     * #          0  1
     * #  nums    2  4  【列表】
     * ans: 3,1
     * k: 1%f[1]=1
     * (3) k=0, 反向迭代添加剩下的nums
     * ans: 3,1,2,4
     */
    private String _nthPermutation(int n, int k) {
        int[] factorials = new int[n];
        LinkedList<Integer> nums = new LinkedList<>();
        // 1. calculate factorials
        factorials[0] = 1;
        factorials[1] = 2;
        nums.add(1);
        nums.add(2);
        for (int i = 2; i < n; i++) {
            factorials[i] = factorials[i - 1] * (i + 1);
            nums.add(i + 1);
        }

        // 2.计算每一位
        StringBuilder str = new StringBuilder();
        int index;
        for (int i = n - 2; i >= 0 && k > 0; i--) {
            index = (int) (Math.ceil((double) k / factorials[i] - 1));
            str.append(nums.get(index));
            nums.remove(index);
            k = k % factorials[i];
        }

        // 3. 处理k变成0后的情况, 反向迭代添加剩下的nums
        // 剩余的数需要按降序输出
        Iterator iterator = nums.descendingIterator();
        while (iterator.hasNext()) {
            str.append(iterator.next());
        }

        return str.toString();
    }

    /**
     * 题解的做法(思路都一样，使用valid数组): 这个【k--】用的很传神
     * 比如要求(1,2,3,4)的第4个排列
     * 1234,1243,1324,1342,1423,1432
     * 2134,2143,2314,2341,2413,1431
     * 3124,3142,3214,3241,3412,3421
     * 4123,4132,4213,4231,4312,4321
     * #          0  1  2  3  4
     * #  f       1  1  2  6  24
     * #  valid   1  1  1  1  1
     * -----------------k=12-------------------------------
     * (1) i=1, k=11, order=11/f[3]+1=2
     * #             1  2  3  4
     * #  valid      1 (0) 1  1【valid数组】
     * ans: 2
     * k: 11%f[3]=5
     * (2) i=2, k=5, order=5/f[2]+1=3
     * #             1  2  3  4
     * #  valid      1 (0) 1 (0)【valid数组】
     * ans: 2,4
     * k: 5%f[2]=1
     * (3) i=3, k=1, order=1/f[1]+1=2
     * #             1  2  3  4
     * #  valid      1 (0)(0)(0)【valid数组】
     * ans: 2,4,3
     * k: 1%f[1]=0
     * (4) i=4, k=0, order=0/f[0]+1=1
     * #             1  2  3  4
     * #  valid     (0)(0)(0)(0)【valid数组】
     * ans: 2,4,3,1
     * k: 0%f[0]=0
     * -----------------k=13-------------------------------
     * (1) i=1, k=12, order=12/f[3]+1=3
     * #             1  2  3  4
     * #  valid      1  1 (0) 1
     * ans: 3
     * k: 12%f[3]=0
     * (2) i=2, k=0, order=0/f[2]+1=1
     * #             1  2  3  4
     * #  valid     (0)(0) 1  1
     * ans: 3,1
     * k: 0%f[2]=0
     * (3) i=3, k=0, order=0/f[1]+1=1
     * #             1  2  3  4
     * #  valid     (0)(0)(0) 1
     * ans: 3,1,2
     * k: 0%f[1]=0
     * (4) i=4, k=0, order=0/f[0]+1=1
     * #             1  2  3  4
     * #  valid     (0)(0)(0)(0)
     * ans: 3,1,2,4
     * k: 0%f[0]=0
     */
    private String _nthPermutation2(int n, int k) {
        int[] factorial = new int[n];
        factorial[0] = 1;
        // 1. 计算f
        for (int i = 1; i < n; ++i) {
            factorial[i] = factorial[i - 1] * i;
        }

        // 2. 计算每一位：这个【k--】用的很传神
        --k;
        StringBuilder ans = new StringBuilder();
        int[] valid = new int[n + 1];
        Arrays.fill(valid, 1);
        for (int i = 1; i <= n; ++i) {
            // (1) 因为--k, 计算order的方式不同
            int order = k / factorial[n - i] + 1;
            // (2) 这一段代码代替了【列表】的功能
            for (int j = 1; j <= n; ++j) {
                order -= valid[j];
                if (order == 0) {
                    ans.append(j);
                    valid[j] = 0;
                    break;
                }
            }
            k %= factorial[n - i];
        }
        return ans.toString();
    }
}
