package veinthrough.leetcode.stack.monstack;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.Stack;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第42题/面试题 17.21. 直方图的水量:
 * 《直方图的水量》
 * 给定一个直方图(也称柱状图)，假设有人从上面源源不断地倒水，最后直方图能存多少水量?直方图的宽度为1。
 */
@Slf4j
public class Drop {
    @Test
    public void test42() {
        Stream.of(
                new int[]{4, 2, 0, 3, 2, 5}, // 9
                new int[]{2, 1, 0, 2}, // 3
                new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}, // 6
                new int[]{4, 1, 0, 2, 5, 1, 3, 2, 1, 3, 2}) // 14
                .forEach(height -> log.info(methodLog(
                        Arrays.toString(height), "" + maxDrop(height))));
    }

    private static int maxDrop(int[] height) {
        // 1.
        if (height.length < 3) return 0;
        // 2.
        return _maxDropStack(height);
    }

    /**
     * 方法1：dp
     * 对于下标 i，水能到达的最大高度等于下标i两边的最大高度的最小值，
     * 下标i处能接的水的量等于下标i处的水能到达的最大高度减去height[i]。
     * 朴素的做法是对于数组height中的每个元素，分别向左和向右扫描并记录左边和右边的最大高度，然后计算每个下标位置能接的水的量。
     * 假设数组height的长度为n，该做法需要对每个下标位置使用O(n)的时间向两边扫描并得到最大高度，因此总时间复杂度是 O(n^2)。
     * 上述做法的时间复杂度较高是因为需要对每个下标位置都向两边扫描。
     *
     * 如果已经知道每个位置两边的最大高度，则可以在 O(n)的时间内得到能接的水的总量。
     * 使用动态规划的方法，可以在 O(n)的时间内预处理得到每个位置两边的最大高度。
     * 创建两个长度为 n的数组leftMax 和rightMax。对于0≤i<n， leftMax[i] 表示下标 i及其左边的位置中，height 的最大高度；
     * rightMax[i] 表示下标i及其右边的位置中， height 的最大高度。
     *
     * 显然， leftMax[0]=height[0]， rightMax[n−1]=height[n−1]。两个数组的其余元素的计算如下：
     * 当1≤i≤n−1 时，leftMax[i]=max(leftMax[i−1],height[i])；
     * 当0≤i≤n−2 时，rightMax[i]=max(rightMax[i+1],height[i])。
     * 因此可以正向遍历数组height 得到数组leftMax 的每个元素值，反向遍历数组height 得到数组rightMax 的每个元素值。
     *
     * 在得到数组leftMax和rightMax的每个元素值之后，对于0≤i<n，下标i处能接的水的量等于 min(leftMax[i],rightMax[i])−height[i]。
     * 遍历每个下标位置即可得到能接的水的总量。
     */
    public static int _maxDropDP1(int[] height) {
        int[] leftMax, rightMax;
        int len = height.length;
        leftMax = new int[len];
        rightMax = new int[len];

        // (1) calculate leftMax
        leftMax[0] = height[0];
        for (int i = 1; i < len; i++)
            leftMax[i] = max(leftMax[i - 1], height[i]);

        // (2) calculate rightMax
        rightMax[len - 1] = height[len - 1];
        for (int i = len - 2; i >= 0; i--)
            rightMax[i] = max(rightMax[i + 1], height[i]);

        // (3) calculate drop
        int drop = 0;
        for (int i = 1; i < len - 1; i++)
            drop += min(leftMax[i], rightMax[i]) - height[i];

        return drop;
    }

    /**
     * 方法2：双指针，对dp的改进
     * 动态规划的做法中，需要维护两个数组leftMax和rightMax，因此空间复杂度是O(n)。是否可以将空间复杂度降到 O(1)？
     *
     * 注意到下标i处能接的水的量由leftMax[i]和rightMax[i] 中的最小值决定，由于数组leftMax是从左往右计算，
     * 数组 rightMax是从右往左计算，因此可以使用双指针和两个变量代替两个数组。
     *
     * 维护两个指针left 和right，以及两个变量leftMax 和rightMax，初始时left=0,right=n−1,leftMax=0,rightMax=0。
     * 指针left只会向右移动，指针right 只会向左移动，在移动指针的过程中维护两个变量leftMax 和rightMax 的值。
     *
     * 当两个指针没有相遇时，进行如下操作：
     * (1) 使用height[left] 和height[right] 的值更新leftMax和rightMax 的值；
     * (2) 如果height[left]<height[right]，则必有leftMax<rightMax，
     * 【因为最终接的水量为min(leftMax[i],rightMax[i])−height[i]，所以这时可以忽略更大的rightMax, 直接计算该下标能接的水量】
     * 下标left 处能接的水的量等于leftMax−height[left]，将下标left处能接的水的量加到能接的水的总量，
     * 然后将left 加1（即向右移动一位）；
     * (3) 如果height[left]≥height[right]，则必有leftMax≥rightMax，下标right 处能接的水的量等于 rightMax−height[right]，
     * 将下标right处能接的水的量加到能接的水的总量，然后将right减1（即向左移动一位）。
     * 当两个指针相遇时，即可得到能接的水的总量。
     */
    public static int _maxDropDP2(int[] height) {
        int len = height.length;
        int drop = 0;
        int left = 1, right = len - 2;
        int leftMax = height[0], rightMax = height[len - 1];
        // 这里使用left<=right和使用left<right效果一样
        // 因为最终肯定是在最高点相遇最后一个可算可不算, 算的话也是0
        while (left < right) {
            leftMax = max(leftMax, height[left]);
            rightMax = max(rightMax, height[right]);
            // 使用了leftMax<rightMax
            if (leftMax < rightMax) {
                drop += leftMax - height[left];
                left++;
            } else {
                drop += rightMax - height[right];
                right--;
            }
        }
        return drop;
    }

    /**
     * 相对于{@link #_maxDropDP2(int[])}这里使用了height[left]<height[right]而不是使用leftMax<rightMax
     * 1. 如果height[left]<height[right]，则必有leftMax<rightMax,
     * (1) 小的一方是移动的一方(r向左移但是一直r<l), lMax<rMax,
     * 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
     * 5, 1, 0, 2, 6, 1, 4, 2, 1, 3, 2
     * l              r     <--      r
     *   > r为最新的rMax, rMax=r<l≤lMax  =>  rMax<lMax
     *   > r不为最新rMax, lMax/rMax都不变 =>  rMax<lMax
     * (2)小的一方是非移动的一方
     * 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
     * 4, 1, 0, 2, 5, 1, 3, 2, 1, 3, 2
     * l           r
     *   > r为最新的rMax, l停留的地方肯定是(0...l)最大的, rMax=r>lMax=l
     * 2. 这里必须left从0开始,right从len-1开始才能使用height[left]<height[right]
     * 才能保证如果height[left]<height[right]，则必有leftMax<rightMax, 比如
     * 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
     * 4, 1, 0, 2, 5, 1, 3, 2, 1, 3, 2
     * 如果像{@link #_maxDropDP2(int[])}一样left从1开始, right从len-2开始,
     * 那么left=1, right=len-2时，height[left]<height[right]但是leftMax>rightMax,
     * 因为初始化时相当于left/right同时移动了一次而不是按照大小只移动一方。
     */
    @SuppressWarnings("unused")
    public static int _maxDropDP3(int[] height) {
        int len = height.length;
        int drop = 0;
        int left = 0, right = len - 1;
        int leftMax = 0, rightMax = 0; // 必须: left从0开始, right从len-1开始
        while (left < right) {
            leftMax = max(leftMax, height[left]);
            rightMax = max(rightMax, height[right]);
            // 使用了height[left]<height[right]而不是使用leftMax<rightMax
            if (height[left] < height[right]) {
                drop += leftMax - height[left];
                left++;
            } else {
                drop += rightMax - height[right];
                right--;
            }
        }
        return drop;
    }

    /**
     * 方法3：单调栈
     * 除了计算并存储每个位置两边的最大高度以外，也可以用单调栈计算能接的水的总量。
     *
     * 维护一个单调栈，单调栈存储的是下标，满足从栈底到栈顶的下标对应的数组height中的元素【递减】。
     *
     * 从左到右遍历数组，遍历到下标 i 时，如果栈内至少有两个元素，记栈顶元素为top，top 的下面一个元素是 left，
     * 则一定有 height[left]≥height[top]。如果 height[i]>height[top]【递增】，则得到一个可以接雨水的区域(高低高形成一个区域)，
     * 该区域的宽度是i−left−1，高度是min(height[left],height[i])−height[top]【计算过可以当作将水已经填充】，
     * 根据宽度和高度即可计算得到该区域能接的水的量。
     *
     * 为了得到left，需要将top出栈。在对top计算能接的水的量之后，left变成新的top，重复上述操作，直到栈变为空，
     * 或者栈顶下标对应的height中的元素大于或等于height[i]。
     *
     * 在对下标i处计算能接的水的量之后，将i入栈，继续遍历后面的下标，计算能接的水的量。遍历结束之后即可得到能接的水的总量。
     */
    static int _maxDropStack(int[] height) {
        int len = height.length;
        int left, top;
        int drop = 0;
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < len; i++) {
            while (!stack.empty() && height[i] > height[stack.peek()]) {
                top = stack.pop();
                // 必须之前已经存在2个元素，加上新的元素，形成[高低高]才能构造一个接水区域
                if (!stack.empty()) {
                    // 这里只能是peek不能是pop，因为这次是处理top，后面left可能还可以接水，不能pop left
                    // 例子[2,1,0,2],
                    left = stack.peek();
                    // drop width: i-left-1
                    // drop height: min(height[left], height[i]) - height[top]
                    drop += (min(height[left], height[i]) - height[top]) * (i - left - 1);
                }
            }
            stack.push(i);
        }
        return drop;
    }
}
