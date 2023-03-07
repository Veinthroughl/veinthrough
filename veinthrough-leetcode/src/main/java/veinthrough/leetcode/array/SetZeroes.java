package veinthrough.leetcode.array;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.array.Array;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第73题：矩阵置零，
 * 给定一个 m x n 的矩阵，如果一个元素为0 ，则将其所在行和列的所有元素都设为0。请使用【原地】算法。
 */
@SuppressWarnings({"Duplicates", "unused"})
@Slf4j
public class SetZeroes {
    @Test
    public void test73() {
        Stream.of(
                // [[0,1,2,0],[3,4,5,2],[1,3,1,5]]
                // [[0,0,0,0],[0,4,5,0],[0,3,1,0]]
                new int[][]{{0, 1, 2, 0},
                        {3, 4, 5, 2},
                        {1, 3, 1, 5}},
                // [[1,2,3,4],[5,0,7,8],[0,10,11,12],[13,14,15,0]]
                // [[0,0,3,0],[0,0,0,0],[0,0,0,0],[0,0,0,0]]
                new int[][]{{1, 2, 3, 4},
                        {5, 0, 7, 8},
                        {0, 10, 11, 12},
                        {13, 14, 15, 0}},
                // [[-4,-2147483648,6,-7,0],[-8,6,-8,-6,0],[2147483647,2,-9,-6,-10]]
                // [[0,0,0,0,0],[0,0,0,0,0],[2147483647,2,-9,-6,0]]
                new int[][]{{-4, -2147483648, 6, -7, 0},
                        {-8, 6, -8, -6, 0},
                        {2147483647, 2, -9, -6, -10}})
                .forEach(matrix -> {
                    log.info(methodLog("Before", Array.stringOf2DArray(matrix)));
                    setZeroes2(matrix);
                    log.info(methodLog("After", Array.stringOf2DArray(matrix)));
                });
    }

    /**
     * 方法1：
     * 使用标记数组rows0/columns0
     */
    public void setZeroes(int[][] matrix) {
        // boundary

        //
        int row = matrix.length;
        int column = matrix[0].length;
        boolean[] rows0 = new boolean[row];
        boolean[] columns0 = new boolean[column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (matrix[i][j] == 0) {
                    rows0[i] = true;
                    columns0[j] = true;
                }
            }
        }

        for (int i = 0; i < row; i++)
            for (int j = 0; j < column; j++)
                if (rows0[i] || columns0[j]) matrix[i][j] = 0;
    }

    /**
     * 方法2：不使用标记数组，使用第一行/第一列作为标记数组
     * 1. 元素存在3个状态:
     * (1) 初始元素值
     * (2) 表示某行/列是否有0【第一行/第一列】
     * (3) 最终元素值
     * (1)(2)可以融合在一起
     * > 如果不为0则为初始元素值
     * > 如果为0表示某行/列有0，因为如果该行/列有0，它的最终元素值必为0，所以它的值已经不起任何作用了
     * 所以状态转换:
     * 第一行/第一列：(1) --> (1)(2) --> (3)
     * 其他：(1) --> (3)
     * 2. matrix[0][0]同时表示：第一行有0或第一列有0，当为0时不能确定是第一行有0还时第一列有0，
     * 所以需要一个额外变量,
     * 这里把第一行单独提出来(把第一列单独提出来类似)：
     * (1) 用row0表示第一行是否有0
     * (2) 用matrix[0][0]表示第一列是否有0
     * 3. 第一行/第一列元素必须在其他元素转换成状态(3)之后自己才能转换成状态(3),
     * 比如matrix[2][0]！=0(现在处在(1)(2)状态且不为0), 表示第2行没有0，
     * 但是如果matrix[0][0]==0(表示第一列有0)，
     * 如果按照left-->right的顺序进行状态(3)的转换，那么matrix[2][0]会先被matrix[0][0]影响成0,
     * 然后这个时候第2行所有元素都被matrix[2][0]影响成0，显然是错误的。
     * 【方法2】所以需要把对第一行和第一列的处理单独拿出来放到最后处理
     * 【方法3】但是可以通过按照right-->left的顺序进行状态(3)的转换，这样matrix[i][...]都在matrix[i][0]前转换成状态(3)
     * 这样就只需要把第一行而不需要把第一列单独提出来放到最后处理
     * 【方法4：不行】可不可以按照down-->up的顺序，从而第一行也不需要单独提出来放到最后处理？
     * 不可以，因为第一行依靠row0，和其他行依靠matrix[i][0]/matrix[0][j]的代码不能融合在一起
     *
     * --------------------------状态(1)-----------------------------
     * -4           -2147483648     6   -7  0
     * -8           6               -8  -6  0
     * 2147483647   2               -9  -6  -10
     * --------------------------1. 第一行状态(1)(2)-------------------------
     * row0: true
     * --------------------------2. 除第一行状态(1)(2)-----------------------
     * row: [1,n), up --> down, 这个顺序无所谓
     * column: [0,n), left --> right, 这个顺序无所谓
     * -4           -2147483648     6   -7  0
     * 0            6               -8  -6  0
     * 2147483647   2               -9  -6  -10
     * --------------------------3. 除第一行状态(3)------------------
     * column:(n,0], right-->left
     * row:(n,1], up --> down, 这个顺序无所谓
     * -4           -2147483648     6   -7  0
     * 0            0               0   0   0
     * 2147483647   2               -9  -6  0
     * --------------------------4. 第一行状态(3)------------------
     * row: 0
     * -4           -2147483648     6   -7  0
     * 0            0               0   0   0
     * 2147483647   2              -9  -6   0
     */
    private void setZeroes2(int[][] matrix) {
        // boundary

        //
        int row = matrix.length;
        int column = matrix[0].length;
        // 1. 第一行状态(1)(2)
        // 额外存储row0
        boolean row0 = false;
        for (int j = 0; j < column; j++) {
            if (matrix[0][j] == 0) {
                row0 = true;
                break;
            }
        }
        // 2. 除第一行状态(1)(2)
        // row: [1,n), up --> down, 这个顺序无所谓
        // column: [0,n), left --> right, 这个顺序无所谓
        for (int i = 1; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (matrix[i][j] == 0) {
                    matrix[0][j] = 0;
                    matrix[i][0] = 0;
                }
            }
        }

        // 3.  除第一行状态(3)
        // column:(n,0], right-->left
        // row:(n,1], up --> down, 这个顺序无所谓
        for (int i = 1; i < row; i++)
            // 必须逆序: right --> left
            // 比如matrix[2][0]！=0, 表示第2行没有0，
            // 但是如果matrix[0][0]==0,表示第一列有0，
            // 如果按照left-->right的顺序进行状态(3)转换，那么matrix[2][0]会先被matrix[0][0]影响成0,
            // 然后第2行所有元素都被matrix[2][0]影响成0，显然是错误的。
            for (int j = column - 1; j >= 0; j--)
                if (matrix[i][0] == 0 || matrix[0][j] == 0) matrix[i][j] = 0;


        // 4.  第一行状态(3)
        if (row0) for (int j = 0; j < column; j++) matrix[0][j] = 0;
    }
}
