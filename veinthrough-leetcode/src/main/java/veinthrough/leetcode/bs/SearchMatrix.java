package veinthrough.leetcode.bs;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.array.Array;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第74题：搜索矩阵，
 * 编写一个高效的算法来判断 m x n 矩阵中，是否存在一个目标值。该矩阵具有如下特性：
 * 每行中的整数从左到右按升序排列。
 * 每行的第一个整数大于前一行的最后一个整数。
 */
@Slf4j
public class SearchMatrix {
    @Test
    public void test74() {
        int[][] matrix = new int[][]{
                {1, 3, 5, 7},
                {10, 11, 16, 20},
                {23, 30, 34, 60}};
        Stream.of(3, 13)
                .forEach(target -> log.info(methodLog(
                        String.format("Search %d in %s.", target, Array.stringOf2DArray(matrix)),
                        "" + searchMatrix(matrix, target))));
    }

    private boolean searchMatrix(int[][] matrix, int target) {
        // boundary
        int row = matrix.length;
        int column = matrix[0].length;
        if (target < matrix[0][0] || target > matrix[row - 1][column - 1]) return false;
        if (target == matrix[0][0] || target == matrix[row - 1][column - 1]) return true;

        // 1. 搜索第一列，找到所在行
        // start: first larger than target
        // end: first lesser than target
        int start = 0, end = row - 1, mid;
        while (start <= end) {
            mid = (start + end) >> 1;
            if (target == matrix[mid][0]) return true;
            else if (target < matrix[mid][0]) end = mid - 1;
            else start = mid + 1;
        }

        // 2. 搜索所在行(end: first lesser than target)
        int targetRow = end;
        start = 0;
        end = column - 1;
        while (start <= end) {
            mid = (start + end) >> 1;
            if (target == matrix[targetRow][mid]) return true;
            else if (target < matrix[targetRow][mid]) end = mid - 1;
            else start = mid + 1;
        }
        return false;
    }
}
