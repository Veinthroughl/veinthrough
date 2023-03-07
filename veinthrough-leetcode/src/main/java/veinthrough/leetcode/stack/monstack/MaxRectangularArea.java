package veinthrough.leetcode.stack.monstack;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.array.Array;

import java.util.Arrays;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第84题：柱状图中最大矩形，
 * 给定 n 个非负整数，用来表示柱状图中各个柱子的高度。每个柱子彼此相邻，且宽度为 1。
 * 求在该柱状图中，能够勾勒出来的矩形的最大面积。
 *
 * 第85题：矩阵中最大矩形，
 * 给定一个仅包含 0 和 1 、大小为 rows x cols 的二维二进制矩阵，找出只包含 1 的最大矩形，并返回其面积。
 */
@Slf4j
public class MaxRectangularArea {
    @Test
    public void test84() {
        Stream.of(
                new int[]{2, 4}, // 4
                new int[]{2, 1, 2}, // 3
                new int[]{2, 1, 5, 6, 2, 3}, // 10
                new int[]{2, 1, 5, 6}, // 10
                new int[]{4, 3, 5, 6, 2, 1, 4}) // 12
                .forEach(heights -> log.info(methodLog(
                        Arrays.toString(heights),
                        "" + maxRectangularArea(heights))));
    }

    @Test
    public void test85() {
        Stream.of(
                new int[][]{{0, 0}},
                new int[][]{{1}},
                new int[][]{{0}},
                new int[][]{{}},
                new int[][]{{0, 1}, {1, 0}}, // 1
                new int[][]{
                        {1, 0, 1, 0, 0},
                        {1, 0, 1, 1, 1},
                        {1, 1, 1, 1, 1},
                        {1, 0, 0, 1, 0}})    // 6
                .forEach(matrix -> log.info(methodLog(
                        Array.stringOf2DArray(matrix),
                        "" + maxRectangularArea(matrix))));
    }

    /**
     * 题目：柱状图中最大矩形，
     * 【注意: 让最后一次出栈的栈顶元素向左向右延伸的时候，原数组中的值已经改变了】
     * -------------------------------------------------------------------------
     * 对于每个矩形，我们求出它向左向右分别能延伸的长度，然后乘以它的高度，
     * 这就是以当前矩形为最低高度可以得到的最大的面积。只需要求最大值即可。
     * #    0,1,2,3,4,5,6
     * #    4,3,5,6,2,1,4
     * #          |
     * #        | |
     * #    |   | |     |
     * #    | | | |     |
     * #    | | | | |   |
     * #    | | | | | | |
     * 总结性的来说就是：
     * 1. 给定一序列，寻找某一子序列，使得子序列中的最小值乘以子序列的长度最大。这是单调栈的一种应用。
     * 使用【递减单调栈(栈顶最大)】: 当遇到更小的, 实际上【向右边扩展结束】，【从栈中找向左边扩展】
     *
     *
     * 2. 并且将数组最后一个元素设为最小值，以最后清空栈内所有元素
     * (1) 0(4)： 表示(值4)向左最多能扩展到(坐标0)
     *
     * (2) 0(4),
     * 0(4)遇到1(3), 0(4)不能再向右扩展;
     * 因为4不能跨越1(3), 向右扩展到1(3)后面的高度只能为3;
     * 0(4)处理完毕; 接下来0(4)修改成0(3), 0以目前已知的最小高度3继续处理;
     * 0(4) -> 0(3): 表示(值3)向左最多能扩展到(坐标0), (值4)已经不行
     * (3) 0(3), 2(5)
     * 0(3), 2(5), 3(6)
     *
     * 3(6)遇到4(2), 也就是3(6)向右扩展为4; 因为是递减单调栈, 向左扩展都为0; 所以左右扩展总长为1(4-3, 包括自己);
     * 3(6)处理完毕;
     * 2(5)遇到4(2), 也就是2(5)向右扩展为4; 因为是递减单调栈, 向左扩展都为0; 所以左右扩展总长为2(4-2, 包括自己);
     * 2(5)处理完毕;
     * 0(3)遇到4(2), 也就是0(3)向右扩展为4; 因为是递减单调栈, 向左扩展都为0; 所以左右扩展总长为4(4-0, 包括自己);
     * 0(3)处理完毕; 接下来0(3)修改成0(2), 0以目前已知的最小高度2继续处理;
     * 0(3), 2(5), 3(6)  -> 0(2)： 表示(值2)向左最多能扩展到(坐标0), (值3)已经不行
     *
     * (4) 0(2),
     * 0(2)遇到5(1), 0(2)不能再向右扩展;
     * 因为2不能跨越5(1), 向右扩展到5(1)后面的高度只能为1;
     * 0(2)处理完毕; 接下来0(2)修改成0(1), 0以目前已知的最小高度1继续处理;
     * 0(2) -> 0(1): 表示(值1)向左最多能扩展到(坐标0), (值2)已经不行
     *
     * (5) 0(1), 6(4)
     *
     * (6) 循环结束，handle the last value 0【假象的0, 可以假象它存在】：
     * 6(4)遇到7(0), 也就是6(4)向右扩展为1; 因为是递减单调栈, 向左扩展都为0; 所以左右扩展总长为1(7-6, 包括自己);
     * 6(4)处理完毕;
     * 0(1)遇到7(0), 也就是0(1)向右扩展为7; 因为是递减单调栈, 向左扩展都为0; 所以左右扩展总长为7(7-0, 包括自己);
     * 0(1)处理完毕;
     * 接下来0(1)不需要修改成0(0), 不需要re-push
     * return;
     * -------------------------------------------------------------
     * 另外一个例子：(4,3,5,6,4,2,4)
     * 0,1,2,3,4,5,6
     * 4,3,5,6,4,2,4
     * 0(4)
     * 遇到1(3): 0(4) --> 0(3)
     * 0(3),2(5)
     * 0(3),2(5),3(6)
     * 遇到4(4): 0(3),2(5),3(6) --> 0(3),4(4)
     * 遇到5(2): 0(3),4(4) --> 0(2)
     * 0(2),6(4)
     * 处理最后一个置0
     */
    private int maxRectangularArea(int[] heights) {
        int n;
        // boundary
        if ((n = heights.length) == 1) return heights[0];

        //
        int[] stack = new int[n];
        int top = -1;
        int topValue, topArea;
        int maxArea = 0;
        for (int i = 0; i < n; i++) {
            // if(栈为空或入栈元素符合单调栈) 入栈;
            if (top == -1 || heights[i] >= heights[stack[top]]) {
                stack[++top] = i;
                continue;
            }
            // while(栈非空并且栈顶元素不符合单调栈)
            // 将破坏栈单调性的元素都出栈后，最后一次出栈的元素就是当前入栈元素能拓展到的最左位置，
            while (top >= 0 && heights[i] <= (topValue = heights[stack[top]])) {
                if ((topArea = (i - stack[top]) * topValue) > maxArea)
                    maxArea = topArea;
                top--;
            }
            // push leftest index and i value
            // 更新其对应的值(如果需要)，并将其位置入栈。
            top++; // re-push
            // 【注意: 让最后一次出栈的栈顶元素向左向右延伸的时候，原数组中的值已经改变了】
            heights[stack[top]] = heights[i];
        }

        // 因为最后stack还剩下一些元素没有计算，
        // 可以假想在最后插入一个高度为0的柱子，这样就能计算完所有的元素了
        // 因为最后一个元素为手动放置的0, 所以不需要比较
        // while(top>=0 && (topValue=heights[stack[top]])>=heights[i]) {
        while (top >= 0) {
            if ((topArea = (n - stack[top]) * heights[stack[top]]) > maxArea)
                maxArea = topArea;
            top--;
            // 因为最后一个元素为手动放置的0, 所以不需要re-push
        }
        return maxArea;
    }

    /**
     * 矩阵中最大矩形
     * 参考文档《#stack.单调栈》
     *
     * @param matrix 矩阵中每个元素值为0/1
     * @return 矩阵中由1组成的最大的矩形面积
     */
    private int maxRectangularArea(int[][] matrix) {
        // boundary

        //
        int rows = matrix.length;
        int columns = matrix[0].length;
        int[][] heights = Array.copy(matrix);
        // 1. 第0行不用计算
        // 2. 第1...行
        for (int i = 1; i < rows; i++)
            for (int j = 0; j < columns; j++)
                // (1) 0: 该列(j列, 只包含前i行)的柱形高度为0
                // (2) 1: 该列(j列, 只包含前i行)的柱形高度为 = 该列(j列, 只包含前i-1行)的柱形高度 + 1
                if (heights[i][j] == 1) heights[i][j] += heights[i - 1][j];

        int maxArea = 0;
        for (int i = 0; i < rows; i++)
            maxArea = Math.max(maxArea, maxRectangularArea(heights[i]));

        return maxArea;
    }
}