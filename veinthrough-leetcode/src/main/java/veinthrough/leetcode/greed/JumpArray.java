package veinthrough.leetcode.greed;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第45题：跳跃游戏II:
 * 给你一个非负整数数组 nums ，你最初位于数组的第一个位置。
 * 数组中的每个元素代表你在该位置可以跳跃的最大长度。
 * 你的目标是使用最少的跳跃次数到达数组的最后一个位置。
 * 假设你总是可以到达数组的最后一个位置。
 * 1 <= nums.length <= 104
 * 0 <= nums[i] <= 1000
 *
 * 示例 1:
 * 输入: nums = [2,3,1,1,4]
 * 输出: 2
 * 解释: 跳到最后一个位置的最小跳跃数是 2。
 * 从下标为 0 跳到下标为 1 的位置，跳 1 步，然后跳 3 步到达数组的最后一个位置。
 *
 * 示例 2:
 * 输入: nums = [2,3,0,1,4]
 * 输出: 2
 *
 * 第55题，跳跃游戏，
 * 给定一个非负整数数组 nums ，你最初位于数组的 第一个下标 。
 * 数组中的每个元素代表你在该位置可以跳跃的最大长度。
 * 判断你是否能够到达最后一个下标。
 */
@Slf4j
public class JumpArray {
    private static final int MAX_VALUE = 10000;

    @Test
    public void test45() {
        Stream.of(
                new int[]{2, 3, 1, 1, 4}, // 2
                new int[]{2, 3, 0, 1, 4}, // 2
                new int[]{2, 3, 1, 2, 1, 2, 3}) // 4
                .forEach(nums -> log.info(methodLog(
                        Arrays.toString(nums), "" + minJump(nums))));
    }

    @Test
    public void test55() {
        Stream.of(
                new int[]{0, 1},        // false
                new int[]{0, 2, 3},     // false
                new int[]{1, 2},        // true
                new int[]{2,3,1,1,4},   // true
                new int[]{3,2,1,0,4},   // false
                new int[]{1, 0, 1, 0})  // false
                .forEach(nums -> log.info(methodLog(
                        Arrays.toString(nums), "" + canJump(nums))));
    }

    /**
     * 第45题，
     * 方法1：DP(个人开始做法，但是数组长度比较长时会超时)
     * 方法2：贪心，从后往前
     * 方法3：贪心，从前往后
     */
    private int minJump(int[] nums) {
        // boundary
        if (nums == null || nums.length <= 1) return 0;
        // 题目已经明确: 你总是可以到达数组的最后一个位置
        if (nums.length == 2) return 1;
        if (nums.length == 3) return nums[0] > 1 ? 1 : 2;

        return _jumpFromHead(nums);
    }

    /**
     * 方法1：dp(个人开始做法，但是数组长度比较长时会超时)
     * 定义jump[i][j]为从i跳到j至少需要的步数
     * 1. 间隔为1, 除非nums[i]为0, 否则都为1
     * 2. 间隔>=2
     * (1) 如果i+nums[i]>=j, 即nums[i]>=j-i, 即nums[i]>=gap, 为1
     * (2) ...i..k..j..., 在(i,j)中逐个选取一个中间点,
     * minJump[i][j]为所有的k中, minJump[i][k]+minJump[k][j]的最小值
     */
    @SuppressWarnings("unused")
    public int _jumpByDP(int[] nums) {
        int len = nums.length;
        int[][] jump = new int[len][len];
        // 1. gap=1
        int i, j, gap;
        for (i = 0; i < len - 1; i++)
            jump[i][i + 1] = nums[i] != 0 ? 1 : MAX_VALUE;
        // 2. gap>=2
        for (gap = 2; gap < len; gap++) {
            for (i = 0; i < len - gap; i++) {
                j = i + gap;
                if (nums[i] >= gap) jump[i][j] = 1;
                else {
                    // 使用Integer.Max_VALUE做加法时可能溢出导致负值
                    jump[i][j] = MAX_VALUE;
                    for (int k = i + 1; k < j; k++)
                        jump[i][j] = Math.min(jump[i][j], jump[i][k] + jump[k][j]);
                }
            }
        }
        return jump[0][len - 1];
    }

    /**
     * 方法2: 贪心算法1(反向查找出发位置)
     * 我们的目标是到达数组的最后一个位置，因此我们可以考虑最后一步跳跃前所在的位置，该位置通过跳跃能够到达最后一个位置。
     * 1. 如果有多个位置通过跳跃都能够到达最后一个位置，那么我们应该如何进行选择呢？
     * 直观上来看，我们可以「贪心」地选择距离最后一个位置最远的那个位置，也就是对应下标最小的那个位置。
     * 因此，我们可以从左到右遍历数组，选择第一个满足要求的位置。
     * 比如:
     * 0  1  2  3  4  5  6  7  8  9
     * x  x  x  2  3  4  3  2  x  x
     * 5/6/7都可用直接跳到9, 为什么要选择5呢？
     * 因为不管前面通过多少步跳到7, 假设s步, 那么一定能通过s步跳到5/6， 而且有可能需要的步数更少;
     * (1) 比如4(3)可用跳到7，那么一定可用跳到5/6
     * (2) 比如3(2)可用跳到5, 但不能跳到6/7, 这样5跳到就比6/7少一步
     *
     * 2. 找到最后一步跳跃前所在的位置之后，我们继续贪心地寻找倒数第二步跳跃前所在的位置，
     * 3. 以此类推，直到找到数组的开始位置。
     */
    @SuppressWarnings("unused")
    private int _jumpFromTail(int[] nums) {
        int len = nums.length;
        int end = len - 1;
        int step = 0;
        // 这里end是从后往前移动，而i是从前往后移动
        // 所以需要两个循环，时间复杂度为O(n^2)
        while (end != 0) {
            for (int i = 0; i < len - 1; i++) {
                if ((i + nums[i]) >= end) {
                    step++;
                    end = i;
                    break;
                }
            }
        }
        return step;
    }

    /**
     * 方法3: 贪心算法2(从前往后)
     * 0  1   2   3   4  5  6
     * 2 |3  *1  *2  |1  2  3
     * (1) i(0),从0开始，可用跳到1/2, 选取2作为end, step+1，
     * 也就是说在2以内(包括2)只需要1步；
     * 或者说1步最多跳到2
     * (2) i(1,2),1可用跳到4，2可用跳到3，选取4作为end, step+1
     * 也就是说在4以内(包括4)只需要2步；
     * 或者说2步最多跳到4；
     * (3) i(3，4),3可用跳到5，4可用跳到5，选取5作为end, step+1
     * 也就是说在5以内(包括5)只需要3步；
     * 或者说3步最多跳到5；
     * (4) i(5), 5可用跳到7, 选取7作为end, step+1
     * 也就是说在7以内(包括7)只需要4步；
     * 或者说4步最多跳到7
     * (5) 7(end)>=6(len-1)，结束
     */
    private int _jumpFromHead(int[] nums) {
        int len = nums.length;
        int end = 0;
        int step = 0;
        int max = 0;
        // 这里end是从前往后移动，而i是从前往后移动
        // 所以只需要一个循环，时间复杂度为O(n)
        int i = 0;
        while (end < len - 1) {
            max = Math.max(max, i + nums[i]);
            if (i == end) {
                end = max;
                step++;
            }
            i++;
        }
        return step;
    }


    /**
     * 第55题
     */
    private boolean canJump(int[] nums) {
        int n;
        // boundary
        if ((n = nums.length) == 1) return true;
        // 第一个为0
        if (nums[0] == 0) return false;
        // 第一个直接到达
        if (nums[0] >= n - 1) return true;

        int end = nums[0];
        int maxReach = 1;
        int reach;
        // 注意这里是n-1而不是n, 因为最后一个不能作为起跳点
        for (int i = 1; i < n - 1; i++) {
            if ((reach = nums[i] + i) > maxReach) {
                if (reach >= n - 1) return true; // 找到一个能跳到最后
                maxReach = reach;
            }
            if (i == end) {
                // 原地踏步必然:
                // (1) end前的最多到end
                // (2) nums[end] == 0
                if (maxReach == end) return false; // 原地踏步测试用例[1,0,1,0],[1,2,1,0,1]
                end = maxReach;
            }
        }
        return false;
    }
}
