package veinthrough.leetcode.enumerate.backtrace;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.api.array.Array.stringOf2DArray;

/**
 * 第36题：有效的数独
 * 判断一个 9x9 的数独是否有效。只需要根据以下规则，验证已经填入的数字是否有效即可。
 * 数字 1-9 在每一行只能出现一次。
 * 数字 1-9 在每一列只能出现一次。
 * 数字 1-9 在每一个以粗实线分隔的 3x3 宫内只能出现一次。
 * 数独部分空格内已填入了数字，空白格用 '.' 表示。
 *
 * 第37题：解数独
 * 编写一个程序，通过填充空格来解决数独问题。
 */
@Slf4j
public class Sudoku {
    private static char[][] valid1 = new char[][]{
            {'7', '6', '.', '9', '.', '4', '8', '.', '5'},
            {'.', '5', '.', '6', '.', '8', '1', '9', '7'},
            {'9', '2', '8', '1', '.', '7', '6', '.', '.'},
            {'2', '1', '9', '.', '.', '.', '3', '7', '8'},
            {'4', '8', '3', '2', '.', '9', '.', '1', '.'},
            {'5', '.', '.', '3', '.', '1', '9', '4', '2'},
            {'1', '.', '5', '.', '6', '2', '4', '.', '3'},
            {'.', '3', '2', '4', '.', '5', '7', '6', '.'},
            {'6', '4', '7', '8', '.', '3', '.', '5', '.'}};
    private static char[][] valid2 = new char[][]{
            {'5', '3', '.', '.', '7', '.', '.', '.', '.'},
            {'6', '.', '.', '1', '9', '5', '.', '.', '.'},
            {'.', '9', '8', '.', '.', '.', '.', '6', '.'},
            {'8', '.', '.', '.', '6', '.', '.', '.', '3'},
            {'4', '.', '.', '8', '.', '3', '.', '.', '1'},
            {'7', '.', '.', '.', '2', '.', '.', '.', '6'},
            {'.', '6', '.', '.', '.', '.', '2', '8', '.'},
            {'.', '.', '.', '4', '1', '9', '.', '.', '5'},
            {'.', '.', '.', '.', '8', '.', '.', '7', '9'}};
    private static char[][] invalid = new char[][]{
            {'8', '3', '.', '.', '7', '.', '.', '.', '.'},
            {'6', '.', '.', '1', '9', '5', '.', '.', '.'},
            {'.', '9', '8', '.', '.', '.', '.', '6', '.'},
            {'8', '.', '.', '.', '6', '.', '.', '.', '3'},
            {'4', '.', '.', '8', '.', '3', '.', '.', '1'},
            {'7', '.', '.', '.', '2', '.', '.', '.', '6'},
            {'.', '6', '.', '.', '.', '.', '2', '8', '.'},
            {'.', '.', '.', '4', '1', '9', '.', '.', '5'},
            {'.', '.', '.', '.', '8', '.', '.', '7', '9'}
    };

    @Test
    public void testValid() {
        Stream.of(valid1, valid2, invalid).forEach(
                board -> log.info(methodLog("" + isValidSudoku(board))));

    }

    @Test
    public void testSolve() {
        Stream.of(valid1, valid2).forEach(
                board -> {
                    solveSudoku(board);
                    log.info(methodLog(stringOf2DArray(board)));
                });
    }

    private char[][] board;
    private boolean[][] rows = new boolean[9][9];
    private boolean[][] columns = new boolean[9][9];
    private boolean[][] subs = new boolean[9][9];

    private boolean isValidSudoku(char[][] board) {
        this.board = board;
        this.rows = new boolean[9][9];
        this.columns = new boolean[9][9];
        this.subs = new boolean[9][9];
        int sub, num;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // sub board: 3X3
                sub = i / 3 * 3 + j / 3;
                // ignore empty
                if (board[i][j] != '.') {
                    // 1-9: no 0
                    num = board[i][j] - '1';
                    if (rows[i][num] || columns[j][num] || subs[sub][num]) return false;
                    else {
                        rows[i][num] = true;
                        columns[j][num] = true;
                        subs[sub][num] = true;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 方法1【无法实现】. 按left(剩余)回溯/按单元(i,j)回溯
     * (1) 试图通过构造排序的left(每个单元格剩余需填充)，并按照个数排序；
     * 但是不太好回溯，因为每一次都会填充单元都会修改left，
     * 回溯的时候不太好把left复原，即使去复原left, left中的元素顺序都改变了
     * 方法2.
     * 1. 回溯方式
     * (1) 递归, 使用递归栈来记住之前填充的单元
     * (2) 迭代/循环，使用一个List来记录之前填充的所有单元
     * 2. 优化
     * 在第一次的时候把只有1个需填充的直接填充
     */
    private void solveSudoku(char[][] board) {
        // 1. validate and fill rows/columns/subs
        if (isValidSudoku(board)) {
            // 2. 优化：填充只有1个需填充的单元 by rows/columns/subs
            int sub, num, count, singleNum = 0;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (board[i][j] == '.') {
                        sub = i / 3 * 3 + j / 3;
                        count = 0;
                        // from 0
                        for (num = 0; num < 9; num++) {
                            // rows/columns/subs from 0
                            if (!rows[i][num] && !columns[j][num] && !subs[sub][num]) {
                                count++;
                                singleNum = num;
                            }
                        }
                        // 优化: 在第一次的时候把只有1个需填充的直接填充
                        // fill if only 1 left
                        if (count == 1) {
                            _fill(i, j, singleNum);
                        }
                    }
                }
            }

            // 3. 回溯方式: dfs/iterate
            // (1) 递归, 使用递归栈来记住之前填充的单元
            // (2) 不使用递归，使用一个List来记录之前填充的所有单元
            dfs(0, 0);
//            iterate(0, 0);
        }
    }

    /**
     * 回溯方式: 递归, 使用递归栈来记住之前填充的单元
     * dfs(row,column)代表从(row,column)开始dfs
     *
     * 想法: 如果这里需要求出所有的解, 那么外面还需要套一个循环
     */
    private boolean dfs(int row, int column) {
        int sub, num;
        // 1. end
        if (row > 8) return true;

        // 2. fill and dfs if unfilled
        if (board[row][column] == '.') {
            sub = row / 3 * 3 + column / 3;
            for (num = 0; num < 9; num++) {
                if (!rows[row][num] && !columns[column][num] && !subs[sub][num]) {
                    // (1) 填充，正确/到达最后的话直接返回
                    _fill(row, column, num);
                    // dfs(next_row, next_column), 正确的话直接返回了
                    if (dfs(row + column / 8, (column + 1) % 9))
                        return true;
                    // (2) 走到这里说明当前num不成立, backtrace并尝试下一个num
                    _clear(row, column, num);
                }
            }
            // (3) 走到这里说明当前整个(row,column)不成立, 返回false让上一层backtrace
            return false;
        }

        // 3. dfs next
        return dfs(row + column / 8, (column + 1) % 9);
    }

    /**
     * 回溯方式: 迭代/循环，使用一个List来记录之前填充的所有单元
     */
    @SuppressWarnings("unused")
    private boolean iterate(int row, int column) {
        Deque<Integer> filledRows = new LinkedList<>();
        Deque<Integer> filledColumns = new LinkedList<>();
        Deque<Integer> filledNums = new LinkedList<>();
        int sub, num = 0;
        boolean found;
        
        // 从(row,column)开始, 这里不能使用for(i...){for(j...)}
        while (row <= 8 && column <= 8) {
            // 1. fill and dfs if unfilled
            if (board[row][column] == '.') {
                sub = row / 3 * 3 + column / 3;
                found = false;
                for (; num < 9; num++) {
                    // 1.1 当前格找到可用的num
                    if (!rows[row][num] && !columns[column][num] && !subs[sub][num]) {
                        // 1.1.(1) 填充，正确/到达最后的话直接返回
                        _fill(row, column, num);
                        filledRows.push(row);
                        filledColumns.push(column);
                        filledNums.push(num);
                        // 1.1.(2) next 【(row,column)】
                        row = row + column / 8;
                        column = (column + 1) % 9;
                        num = 0;
                        // 1.1.(3)
                        found = true;
                        break;
                    }
                }
                // 1.3 filledRows.isEmpty()说明连第一格都找不到可用的num, 说明找不到解:
                // (1) 一开始第一格就找不到
                // (2) 最终回溯到第一格的时候找不到
                if (!found && filledRows.isEmpty()) return false;

                // 1.2 只是当前格找不到可用的num, 退回上一步(next 【num】)
                if (!found) {
                    row = filledRows.pop();
                    column = filledColumns.pop();
                    num = filledNums.pop();

                    _clear(row, column, num);
                    num = num + 1; // next 【num】
                }
            }
            // 2. filled: next 【(row,column)】
            else {
                row = row + column / 8;
                column = (column + 1) % 9;
            }
        }
        return true;
    }

    private void _fill(int row, int column, int num) {
        int sub = row / 3 * 3 + column / 3;
        board[row][column] = (char) (num + '1');
        rows[row][num] = true;
        columns[column][num] = true;
        subs[sub][num] = true;
    }

    private void _clear(int row, int column, int num) {
        int sub = row / 3 * 3 + column / 3;
        board[row][column] = '.';
        rows[row][num] = false;
        columns[column][num] = false;
        subs[sub][num] = false;
    }
}
