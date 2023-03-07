package veinthrough.leetcode.dp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第312题:
 * 有 n 个气球，编号为0 到 n-1，每个气球上都标有一个数字，这些数字存在数组nums中。
 * 现在要求你戳破所有的气球。戳破第i个气球，你可以获得nums[i-1] * nums[i] * nums[i+1] 枚硬币。
 * 这里的 i-1 和 i+1 代表和i相邻的两个气球的序号。如果 i-1 或 i+1 超出了数组的边界，那么就当它是一个数字为1的气球。
 * 求所能获得硬币的最大数量。
 *
 * 这个题目的关键在于构造【一个问题的最优解怎么由子问题的最优解得出】
 */
@Slf4j
@SuppressWarnings("Duplicates")
public class MaxCoins {
    @Test
    public void test312() {
        Stream.of(
                new int[]{3, 5, 1},
                new int[]{3, 1, 5, 8})
                .forEach(nums -> log.info(
                        methodLog(
                                "array", Arrays.toString(nums),
                                "逐个戳破【错误】", "" + maxCoins(nums),
                                "逐个插入dp【闭区间, 错误】", "" + maxCoins2(nums),
                                "逐个插入dp【开区间正确】", "" + maxCoinsOpen(nums))));
    }

    /**
     * 方法1(自解)：逐个戳破dp【错误】
     * 我们观察戳气球的操作，发现这会导致两个气球从不相邻变成相邻，使得后续操作难以处理【难以确立子问题】。
     * 不具备【无后效性】
     * 1. max(i,j)代表不同次序逐个戳破[i...j]的最大值
     * n=3
     * #    0        1       2       3       4
     * #    1*       3       5       1       1*
     * #    i-1      i       i+1     i+2         (gap=1)
     * #            i-1      i       i+1         (gap=1)
     * #    i-1      i       i+1     i+2         (gap=2)
     * max(3,5):
     * (3,5):15+5=20
     * (5,3):15+3=18
     * max(5,1):
     * (5,1):15+5=20
     * (1,5):5+15=20
     * max(3,5,1): 【并不等于max(3,5)+max(5,1)+3*1*5=20+20+15=55】【难以确立子问题】
     * (3,1,5):15+5+5=25
     * (3,5,1):15+5+1=21
     * (1,3,5):5+15+5=25
     * (1,5,3):5+15+3=23
     * (5,3,1):15+3+1=19
     * (5,1,3):15+3+3=21
     *
     * 【实际上需要将3,1,5排列组合】：
     * max(3)+max(1,5)= 这个max(3)为15
     * max(1,5)+max(3)= 这个max(3)是【最后一个拆】, 所以应该为3，所以就产生了矛盾
     * max(5)+max(3,1)= max(5)拆掉后, max(3,1)不可能再有乘以5,
     * max(3,1)+max(5)= 但是这个max(3,1)中包含了乘以5，这也是矛盾
     * max(1)+max(3,5)=
     * max(3,5)+max(1)=
     */
    private int maxCoins(int[] _nums) {
        // boundary
        int length;
        if ((length = _nums.length) == 1) return _nums[0];
        if (length == 2) return Math.max(_nums[0], _nums[1]) + _nums[0] * _nums[1];
        // 1. 两边添加1
        int[] nums = new int[length + 2];
        System.arraycopy(_nums, 0, nums, 1, length);
        nums[0] = nums[length + 1] = 1;
        int[][] max = new int[length + 1][length + 1];

        // 2. gap=1: max[i][i+1]
        for (int i = 1; i < length; i++) {
            max[i][i + 1] = Math.max(
                    nums[i - 1] * nums[i] * nums[i + 1] + nums[i - 1] * nums[i + 1] * nums[i + 2],// 先戳破i
                    nums[i] * nums[i + 1] * nums[i + 2] + nums[i - 1] * nums[i] * nums[i + 2]//先戳破i+1
            );
        }

        // 3. gap>=2
        int value;
        for (int gap = 2; gap < length; gap++) {
            for (int i = 1, j; i <= length - gap; i++) {
                j = i + gap;
                for (int k = i + 1; k < j; k++) {
                    value = max[i][k] + max[k][j] + nums[i] * nums[k] * nums[j];
                    if (value > max[i][j]) max[i][j] = value;
                }
            }
        }
        return max[1][length];
    }

    /**
     * 方法2(自解)：逐个插入dp【错误】
     * 1. 我们观察戳气球的操作，发现这会导致两个气球从不相邻变成相邻，使得后续操作难以处理。
     * 于是我们倒过来看这些操作，将全过程看作是每次添加一个气球。
     * (3,1,5):3+3+15=21【插入】  <-> (5,1,3):15+3+3=21【戳破】
     * (3,5,1):3+15+5=23【插入】  <-> (1,5,3):5+15+3=23【戳破】
     * (1,3,5):1+3+15=19【插入】  <-> (5,3,1):15+3+1=19【戳破】
     * (1,5,3):1+5+15=21【插入】  <-> (3,5,1):15+5+1=21【戳破】
     * (5,3,1):5+15+5=25【插入】  <-> (1,3,5):5+15+5=25【戳破】
     * (5,1,3):5+5+15=25【插入】  <-> (3,1,5):15+5+5=25【戳破】
     *
     * 2. max(i,j)代表不同次序逐个插入[i...j]的最大值
     * n=3
     * #    0        1       2       3       4
     * #    1*       3       5       1       1*
     * #    i-1      i       i+1     i+2         (gap=1)
     * #            i-1      i       i+1         (gap=1)
     * #    i-1      i       i+1     i+2         (gap=2)
     * max(3,5):
     * (3,5):3+15=18
     * (5,3):5+15=20
     * max(5,1):
     * (5,1):5+5=10
     * (1,5):1+5=6
     * max(3,5,1): 【并不等于max(3,5)+max(5,1)+3*1*5=20+10+15=45】【子问题还是不对】
     * 实际上需要将3,1,5排列组合：
     * max(3)+max(1,5)= 这个max(3)为【第一个插入】，应该为3
     * max(1,5)+max(3)= 这个max(3)应该为15, 所以就产生了矛盾
     * max(5)+max(3,1)=
     * max(3,1)+max(5)=
     * max(1)+max(3,5)=
     * max(3,5)+max(1)=
     * 3. 本质上来说max(i,j)赋予的意义不对，导致【一个问题的最优解不是由子问题的最优解得出】
     */
    private int maxCoins2(int[] _nums) {
        // boundary
        int length;
        if ((length = _nums.length) == 1) return _nums[0];
        if (length == 2) return Math.max(_nums[0], _nums[1]) + _nums[0] * _nums[1];

        // 1. 两边添加1
        int[] nums = new int[length + 2];
        System.arraycopy(_nums, 0, nums, 1, length);
        nums[0] = nums[length + 1] = 1;
        int[][] max = new int[length + 1][length + 1];

        // 2. gap=1: max[i][i+1]
        for (int i = 1; i < length; i++) {
            max[i][i + 1] = Math.max(
                    nums[i] + nums[i] * nums[i + 1],// 先插入i
                    nums[i + 1] + nums[i] * nums[i + 1]//先插入i+1
            );
        }

        // 3. 以gap>=2(已经提前插入了2个), max表示长度(gap)为j-i, 区间为[i,j]的最大值
        // n=5
        // 0    1   2   3   4   5   6
        // *    i   i+1     i   i+1 *(i+2)
        for (int gap = 2; gap < length; gap++) {
            for (int i = 1, j; i < length + 1 - gap; i++) {
                j = i + gap;
                for (int k = i + 1; k < j; k++) {
                    int value = max[i][k] + max[k][j] + nums[i] * nums[k] * nums[j];
                    if (value > max[i][j])
                        max[i][j] = value;
                }
            }
        }
        return max[1][length];
    }

    /**
     * 方法3(题解): 逐个插入dp
     * 1. 我们定义方法max，令max(i,j) 表示将【开区间 (i,j)】 内的位置全部填满气球能够得到的最多硬币数。
     * 【开区间】的意思就是说
     * (1) 由于是【开区间】，因此区间两端的气球的编号就是 i 和 j，对应着val[i] 和val[j]。
     * (2) 当i≥j−1 时，开区间中没有气球，max(i,j) 的值为 0；
     * 这里也就是和{@link #maxCoins2}的根本区别:
     * > {@link #maxCoins2}中, gap=1: max[i][i+1]为先插入i/先插入i+1的最大值
     * > 而这里, gap=1时，也就是gap<2时, max(i,j)都为0
     * (3) 当i<j−1时，我们枚举开区间(i,j)内的全部位置mid，令mid为当前区间第一个添加的气球，
     * 该操作能得到的硬币数为val[i]×val[mid]×val[j]。
     * 同时我们递归地计算分割出的两区间(i,k)/(k,j)对max(i,j) 的贡献，这三项之和的最大值，即为max(i,j)的值。
     * (4) 【这才是真正的子问题】因为(i,j)/(i,k)/(k,j)都为【开区间】,
     * 当k将(i,j)从中间分开时, (i,j)问题的最优解可以由子问题(i,k)/(k,j)的最优解得出
     */
    private int maxCoinsOpen(int[] _nums) {
        // boundary
        int length;
        if ((length = _nums.length) == 1) return _nums[0];
        if (length == 2) return Math.max(_nums[0], _nums[1]) + _nums[0] * _nums[1];
        // 1. 两边添加1
        int[] nums = new int[length + 2];
        System.arraycopy(_nums, 0, nums, 1, length);
        nums[0] = nums[length + 1] = 1;
        int[][] max = new int[length + 2][length + 2];

        // 2. 当i≥j−1 时，【开区间】中没有气球，max(i,j) 的值为 0；
        // 也就是gap=1: max[i][i+1]都为0, 这里也就是【根本区别】

        // 3.
        for (int gap = 2; gap < length + 2; gap++) {
            for (int i = 0; i < length + 2 - gap; i++) {
                int j = i + gap;
                for (int k = i + 1; k < j; k++) {
                    // 将[i,j]填满相当于
                    // (1) 先填k: nums[i] * nums[k] * nums[j]
                    // (2) 分别将[i,k]和[k,j]填满
                    int value = max[i][k] + max[k][j] + nums[i] * nums[k] * nums[j];
                    if (value > max[i][j])
                        max[i][j] = value;
                }
            }
        }
        return max[0][length + 1];
    }
}