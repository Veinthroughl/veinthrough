package veinthrough.leetcode.math;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.api.array.Array.stringOf2DArray;

/**
 * 第48题: 旋转矩阵
 * 给定一个 n × n 的二维矩阵 matrix 表示一个图像。请你将图像顺时针旋转 90 度。
 * 你必须在【原地】旋转图像，这意味着你需要直接修改输入的二维矩阵。请【不要使用另一个矩阵来旋转图像】。
 */
@SuppressWarnings("unused")
@Slf4j
public class RotateMatrix {
    @Test
    public void test48() {
        Stream.of(
                new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}},
                new int[][]{{5, 1, 9, 11}, {2, 4, 8, 10}, {13, 3, 6, 7}, {15, 14, 12, 16}}
        ).forEach(matrix -> {
            log.info(methodLog("Before rotate", stringOf2DArray(matrix)));
            rotate2(matrix);
            log.info(methodLog("After rotate", stringOf2DArray(matrix)));
        });
    }

    /**
     * 方法1:
     * (x,y) --> (y,n-1-x)
     * (y,n-1-x) --> (n-1-x,n-1-y)
     * (n-1-x,n-1-y) --> (n-1-y,x)
     * (n-1-y,x) --> (x,y)
     *
     * 1. 示例1：
     * (0,0) (0,1) (0,2)
     * (1,0) (1,1) (1,2)
     * (2,0) (2,1) (2,2)
     * --------------------
     * (0,0) --> (0,2) --> (2,2) --> (2,0) --> (0,0)
     * (0,1) --> (1,2) --> (2,1) --> (1,0) --> (0,1)
     * (1) 第一行 --> 最后一列:   (x,y) --> (y,2-x)
     * (0,0) --> (0,2)
     * (0,1) --> (1,2)
     * (2) 最后一列 --> 最后一行: (y,2-x) --> (2-x,2-y)
     * (0,2) --> (2,2)
     * (1,2) --> (2,1)
     * (3) 最后一行 --> 第一列: (2-x,2-y) --> (2-y,x)
     * (2,2) --> (2,0)
     * (2,1) --> (1,0)
     * (4) 第一列 --> 第一行: (2-y,x) --> (x,y)
     * (2,0) --> (0,0)
     * (1,0) --> (0,1)
     *
     * 2. 示例2:
     * (0,0) (0,1) (0,2) (0,3)
     * (1,0) (1,1) (1,2) (1,3)
     * (2,0) (2,1) (2,2) (2,3)
     * (3,0) (3,1) (3,2) (3,3)
     * ------------------------
     * (0,0) --> (0,3) --> (3,3) --> (3,0) --> (0,0)
     * (0,1) --> (1,3) --> (3,2) --> (2,0) --> (0,1)
     * (0,2) --> (2,3) --> (3,1) --> (1,0) --> (0,2)
     * (1,1) --> (1,2) --> (2,2) --> (2,1) --> (1,1)
     * (1) 第一行 --> 最后一列:   (x,y) --> (y,3-x)
     * (0,0) --> (0,3)
     * (0,1) --> (1,3)
     * (0,2) --> (2,3)
     * (1,1) --> (1,2)
     * (2) 最后一列 --> 最后一行: (y,3-x) --> (3-x,3-y)
     * (0,3) --> (3,3)
     * (1,3) --> (3,2)
     * (2,3) --> (3,1)
     * (1,2) --> (2,2)
     * (3) 最后一行 --> 第一列: (3-x,3-y) --> (3-y,x)
     * (3,3) --> (3,0)
     * (3,2) --> (2,0)
     * (3,1) --> (1,0)
     * (2,2) --> (2,1)
     * (4) 第一列 --> 第一行: (3-y,x) --> (x,y)
     * (3,0) --> (0,0)
     * (2,0) --> (0,1)
     * (1,0) --> (0,2)
     * (2,1) --> (1,1)
     */
    private void rotate(int[][] matrix) {
        int n;
        // boundary
        if (matrix == null || (n = matrix.length) == 1) return;

        // (x,y) --> (y,n-1-x)
        // (y,n-1-x) --> (n-1-x,n-1-y)
        // (n-1-x,n-1-y) --> (n-1-y,x)
        // (n-1-y,x) --> (x,y)
        int tmp;
        for (int x = 0; x < n >> 1; x++) {
            for (//noinspection SuspiciousNameCombination
                    int y = x; y < n - x - 1; y++) {
                tmp = matrix[x][y];
                matrix[x][y] = matrix[n - 1 - y][x];
                matrix[n - 1 - y][x] = matrix[n - 1 - x][n - 1 - y];
                matrix[n - 1 - x][n - 1 - y] = matrix[y][n - 1 - x];
                matrix[y][n - 1 - x] = tmp;
            }
        }
    }

    /**
     * 方法2: 用翻转代替旋转
     * 5   1   9   11                 15  14  12  16                     15   13   2   5
     * 2   4   8   10  --水平翻转-->  13  3   6   7   --主对角线翻转-->  14   3    4   1
     * 13  3   6   7                  2   4   8   10                     12   6    8   9
     * 15  14  12  16                 5   1   9   11                     16   7   10  11
     */
    private void rotate2(int[][] matrix) {
        int n;
        // boundary
        if (matrix == null || (n = matrix.length) == 1) return;

        int tmp;
        // 1. 水平翻转, 只需要枚举矩阵上半部分的元素，和下半部分的元素进行交换
        for (int i = 0; i < n / 2; i++) {
            for (int j = 0; j < n; j++) {
                tmp = matrix[i][j];
                matrix[i][j] = matrix[n - 1 - i][j];
                matrix[n - 1 - i][j] = tmp;
            }
        }
        // 2. 主对角线翻转, 只需要枚举对角线左侧的元素，和右侧的元素进行交换
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                tmp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = tmp;
            }
        }
    }
}
