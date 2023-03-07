package veinthrough.api.math;

import com.google.common.base.Preconditions;

/**
 * 00/编程题/《Math》
 */
@SuppressWarnings("Duplicates")
public class Math {
    /**
     * 求>=num的最小的2的幂次方:
     * (1) -1
     * (2) 前面的移位都是在将从高位到低位每位都变成1
     * (3) +1
     */
    public static int minGreater2Power(int c) {
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return n + 1;
    }

    /**
     * 求<=num的最大的2的幂次方:
     */
    public static int maxSmaller2Power(int c) {
        int min2Power = minGreater2Power(c);
        return c==min2Power ? min2Power : min2Power>>1;
    }

    /**
     * 效率比 {@link #minGreater2Power(int)} 低一些
     */
    @Deprecated
    public static int minGreater2Power2(int c) {
        int n = 1;
        while (n < c) n <<= 1;
        return n;
    }

    public static int[] swap(int[] num) {
        num[0] = num[0] ^ num[1];
        num[1] = num[1] ^ num[0];
        num[0] = num[0] ^ num[1];
        return num;
    }

    /**
     * max of a array
     */
    public static int max(int... nums) {
        int max = nums[0];
        for (int i = 1; i < nums.length; i++)
            if (nums[i] > max)
                max = nums[i];
        return max;
    }

    /**
     * max of a range from array: [start, end)
     */
    public static int max(int[] nums, int start, int end) {
        Preconditions.checkNotNull(nums);
        Preconditions.checkArgument(
                start >= 0 && end >= 0 && end <= nums.length &&
                        start < end);
        int max = nums[start];
        for (int i = start+1; i < end; i++)
            if (nums[i] > max)
                max = nums[i];
        return max;
    }

    /**
     * min of array
     */
    public static int min(int... nums) {
        int min = nums[0];
        for (int i = 1; i < nums.length; i++)
            if (nums[i] < min)
                min = nums[i];
        return min;
    }

    /**
     * min  of a range from array: [start, end)
     */
    public static int min(int[] nums, int start, int end) {
        Preconditions.checkNotNull(nums);
        Preconditions.checkArgument(
                start >= 0 && end >= 0 && end <= nums.length &&
                        start < end);
        int min = nums[start];
        for (int i = start+1; i < end; i++)
            if (nums[i] < min)
                min = nums[i];
        return min;
    }
}