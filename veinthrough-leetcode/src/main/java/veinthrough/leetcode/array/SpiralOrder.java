package veinthrough.leetcode.array;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.array.Array;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第54题，螺旋矩阵，
 * 给你一个 m 行 n 列的矩阵 matrix ，请按照顺时针螺旋顺序，返回矩阵中的所有元素。
 *
 * 第59题，螺旋矩阵II，
 * 给你一个正整数n，生成一个包含1到n^2 所有元素，且元素按顺时针顺序螺旋排列的 n ✖ n 正方形矩阵 matrix。
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class SpiralOrder {
    @Test
    public void test54() {
        Stream.of(
                new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}},       // [1,2,3,6,9,8,7,4,5]
                new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}}) // [1,2,3,4,8,12,11,10,9,5,6,7]
                .forEach(matrix -> log.info(methodLog(
                        "Matrix", Array.stringOf2DArray(matrix),
                        "spiral order", "" + spiralOrder(matrix)
                )));
    }

    @Test
    public void test59() {
        Stream.of(1, 2, 3, 4).forEach(n -> log.info(methodLog(
                "" + n, Array.stringOf2DArray(generateMatrix(n)))));
    }

    /**
     * 3阶
     * #    1   2   3
     * #    8   9   4
     * #    7   6   5
     *
     * 4阶
     * #    1   2   3   4
     * #   12  13  14   5
     * #   11  16  15   6
     * #   10   9   8   7
     */
    private List<Integer> spiralOrder(int[][] matrix) {
        // boundary

        //
        List<Integer> res = new LinkedList<>();
        int m = matrix.length, n = matrix[0].length;
        int right = n - 1, down = m - 1, left = 0, up = 0;
        int i, j;
        while (true) {
            i = up;
            // left --> right, (2,2,0,0) --> (2,2,0,1)
            for (j = left; j <= right; j++)
                res.add(matrix[i][j]);
            up++;
            if (up > down) return res;

            // up --> down, (2,2,0,1) --> (1,2,0,1)
            j = right;
            for (i = up; i <= down; i++)
                res.add(matrix[i][j]);
            right--;
            if (right < left) return res;

            // right --> left, (1,2,0,1) --> (1,1,0,1)
            i = down;
            for (j = right; j >= left; j--)
                res.add(matrix[i][j]);
            down--;
            if (down < up) return res;

            // down --> up, (1,1,0,1) --> (1,1,1,1)
            j = left;
            for (i = down; i >= up; i--)
                res.add(matrix[i][j]);
            left++;
            if (left > right) return res;
        }
    }

    private int[][] generateMatrix(int n) {
        // boundary
        if (n == 1) return new int[][]{{1}};
        if (n == 2) return new int[][]{{1, 2}, {4, 3}};

        //
        int left = 0, right = n - 1;
        int up = 0, down = n - 1;
        int i, j;
        int num = 0;
        int[][] res = new int[n][n];
        while (true) {
            // 1. left --> right
            i = up;
            for (j = left; j <= right; j++) res[i][j] = ++num;
            up++;
            if (up > down) return res;

            // 2. up --> down
            j = right;
            for (i = up; i <= down; i++) res[i][j] = ++num;
            right--;
            if (right < left) return res;

            // 3. right --> left
            i = down;
            for (j = right; j >= left; j--) res[i][j] = ++num;
            down--;
            if (down < up) return res;

            // 4. down --> up
            j = left;
            for (i = down; i >= up; i--) res[i][j] = ++num;
            left++;
            if (left > right) return res;
        }
    }
}
