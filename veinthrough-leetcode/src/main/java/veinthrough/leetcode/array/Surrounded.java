package veinthrough.leetcode.array;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.api.array.Array.stringOf2DArray;

/**
 * 第130题,
 * 这里用的应该是dfs,
 * TODO 还可以使用bfs(使用queue)
 */
@Slf4j
public class Surrounded {
    private int rows, cols;
    private char[][] board;
    private boolean[][] visited;

    private char[][] _board = new char[][]{
            {'X', 'X', 'X', 'X'},
            {'X', 'O', 'O', 'X'},
            {'X', 'X', 'O', 'X'},
            {'X', 'O', 'X', 'X'}};

    @Test
    public void test130() {
        log.info(methodLog(stringOf2DArray(_board)));
        solve(_board);
        log.info(methodLog(stringOf2DArray(_board)));

    }

    private void solve(char[][] _board) {
        // boundary
        if ((rows = _board.length) <= 2 || (cols = _board[0].length) <= 2) return;
        //
        board = _board;
        visited = new boolean[rows][cols];

        // 1. dfs
        // row 0/last
        for (int j = 0; j < cols; j++) {
            if (board[0][j] == 'O' && !visited[0][j]) dfs(0, j);
            if (board[rows - 1][j] == 'O' && !visited[rows - 1][j]) dfs(rows - 1, j);
        }
        // column 0/last
        for (int i = 1; i < rows - 1; i++) {
            if (board[i][0] == 'O' && !visited[i][0]) dfs(i, 0);
            if (board[i][cols - 1] == 'O' && !visited[i][cols - 1]) dfs(i, cols - 1);
        }

        // 2.
        // '#' -> 'O'
        // 'O' -> 'X'
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < cols - 1; j++) {
                if (board[i][j] == 'O') board[i][j] = 'X';
                else if (board[i][j] == '#') board[i][j] = 'O';
            }
        }
    }

    /**
     * 进dfs的时候已经是!visited
     * 1. 边界: dfs neighbors + visited
     * 2. 非边界: dfs neighbors + visited + 变成'#
     */
    private void dfs(int i, int j) {
        // 1. visited
        visited[i][j] = true;
        // 2. 变成‘#’
        if (i != 0 && i != rows - 1 && j != 0 && j != cols - 1) board[i][j] = '#';
        // 3. dfs neighbors
        for (int[] neighbor : neighbors(i, j)) {
            int row, col;
            if (neighbor != null &&
                    board[row = neighbor[0]][col = neighbor[1]] == 'O' &&
                    !visited[row][col])
                dfs(row, col);
        }
    }

    private int[][] neighbors(int i, int j) {
        int[][] res = new int[4][2];
        int left, right, up, down;

        // left
        if ((left = j - 1) >= 0) res[0] = new int[]{i, left};
        // right
        if ((right = j + 1) < cols) res[1] = new int[]{i, right};
        // up
        if ((up = i - 1) >= 0) res[2] = new int[]{up, j};
        // down
        if ((down = i + 1) < rows) res[3] = new int[]{down, j};

        return res;
    }
}
