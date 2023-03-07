package veinthrough.leetcode.enumerate.backtrace;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 八皇后问题：
 * 第51题：给你一个整数 n ，返回所有不同的 n 皇后问题 的解决方案。
 * 示例1：
 * 输入：n = 4
 * 输出：[[".Q..","...Q","Q...","..Q."],["..Q.","Q...","...Q",".Q.."]]
 * 解释：如上图所示，4 皇后问题存在两个不同的解法。
 * 示例 2：
 * 输入：n = 1
 * 输出：[["Q"]]
 *
 * 第52题；
 * 给你一个整数 n ，返回n皇后问题不同的解决方案的数量。
 */
@Slf4j
public class NQueens {
    @Test
    public void test51() {
        Stream.of(3, 4, 5, 6, 7, 8) // 0, 2, 10, 4, 40, 92
                .forEach(n ->
                {
                    List<List<String>> res = solveNQueens(n);
                    log.info(methodLog(
                            String.format("Queens of %d: ", n),
                            "" + res.size() + res));
                });
    }

    private List<List<String>> res;
    private int n;

    /**
     * 方法1：columns/diagonals1/diagonals2为boolean数组
     * 方法2: columns/diagonals1/diagonals2为int，采用位运算
     */
    private List<List<String>> solveNQueens(int n) {
        res = new LinkedList<>();
        // boundary
        if ((this.n = n) == 1) {
            res.add(Collections.singletonList("Q"));
            return res;
        }
        if (n == 2) return res;

        // 方法1：columns/diagonals1/diagonals2为boolean数组
//        dfsByArray(new int[n], 0, new boolean[n], new boolean[(n << 1) - 1], new boolean[(n << 1) - 1]);
        // 方法2: columns/diagonals1/diagonals2为int，采用位运算
        dfsByBit(new int[n], 0, 0, 0, 0);

        return res;
    }

    /**
     * 方法1：columns/diagonals1/diagonals2为boolean数组
     */
    @SuppressWarnings("unused")
    private void dfsByArray(int[] building, int row,
                            boolean[] columns, boolean[] diagonals1, boolean[] diagonals2) {
        if (row == n) {
            res.add(buildOut(building));
            return;
        }
        int diagonal1, diagonal2;
        for (int i = 0; i < n; i++) {
            diagonal1 = i - row + n - 1;
            diagonal2 = i + row;
            if (!columns[i] && !diagonals1[diagonal1] && !diagonals2[diagonal2]) {
                // (1)
                building[row] = i;
                columns[i] = true;
                diagonals1[diagonal1] = true;
                diagonals2[diagonal2] = true;
                // (2)
                dfsByArray(building, row + 1, columns, diagonals1, diagonals2);
                // (3) building不需要操作，因为只有获得一个解的时候正确的数据已经覆盖了原来未回撤的数据
                columns[i] = false;
                diagonals1[diagonal1] = false;
                diagonals2[diagonal2] = false;
            }
        }
    }

    /**
     * 方法2: columns/diagonals1/diagonals2为int，采用位运算
     * #        0       1       2       3       4       5       6       7
     * #0                       *
     * #1                                       *
     * #2      d2		        c	    d2	    c/d1	d1
     *
     * 1. 假设已经在第0行(0,2)和第1行(1,4)处放置，接下来应该是在第2行放置
     * 2. 第2行不能放置的列: 00111101(0/2/3/4/5) = columns|diagonals1|diagonals2
     * Columns: 	00010100， 2/4不能放
     * Diagonals1:	00110000， 4/5不能放, [2-0, 4-1] --> [4-2, 5-2]
     * Diagonals2: 00001001， 0/3不能放, [2+0, 4+1] --> [0+2, 3+2]
     * 3. availablePositions(可以放置的列): ((1 << n) - 1) & (~(columns | diagonals1 | diagonals2))
     * (1) ((1 << n) - 1): 11111111(n=8就有8个1)
     * (2) ~(columns | diagonals1 | diagonals2): 11000010(1/6/7)
     * 4. position: availablePositions & (-availablePositions)，
     * 实际上是得到只含第一个1(从尾部开始)的二进制
     * availablePositions:      0...11000010
     * -availablePositions:     1...00111110(补码：最后一个1及之后不变，其他相反)
     * position:                0...00000010
     * 5. column: Integer.bitCount(position - 1), 实际上这里是求log2(position)
     * position:                0...00000010
     * position-1:              0...00000001 --> 1
     * position:                0...00000100
     * position-1:              0...00000011 --> 2
     * 6. 更新availablePositions(可以放置的列): availablePositions & (availablePositions - 1),
     * 实际上就是消除第一个1
     * availablePositions:      0...11000010
     * availablePositions-1:    0...11000001(补码：最后一个1及之后不变，其他相反)
     * &:                       0...11000000(可用位置1/6/7 --> 6/7)
     * ---------------------------------------------------------------------------
     * #        0       1       2       3       4       5       6       7
     * #0                       *
     * #1                                       *
     * #2      d2		*        c	    d2	    c/d1	d1
     * #3      d2       c     c/d1/d2           c     d1       d1
     * #4
     * #5
     * #6
     * #7
     * 7. 更新diagonals1/更新diagonals2, dfs(row=3的时候使用)
     * Columns: 	00010110， 1/2/4不能放
     * Diagonals1:	01100100， 5/6/2不能放, [2-0, 4-1, 1-2] --> [5-3, 6-3, 2-3]
     * 5/6/2实际上就是之前4/5/1(position)向左移了一位
     * Diagonals2: 00000101， -1/2/0不能放, [2+0, 4+1, 1+2] --> [-1+3, 2+3, 0+3], 这个-1相当于被移掉了
     * -1/2/0实际上就是之前0/3/1(position)向右移了一位
     */
    private void dfsByBit(int[] building, int row, int columns, int diagonals1, int diagonals2) {
        if (row == n) {
            res.add(buildOut(building));
            return;
        }

        int availablePositions = ((1 << n) - 1) & (~(columns | diagonals1 | diagonals2));
        while (availablePositions != 0) {
            int position = availablePositions & (-availablePositions);
            int column = Integer.bitCount(position - 1);
            availablePositions = availablePositions & (availablePositions - 1);
            building[row] = column;
            dfsByBit(building, row + 1,
                    columns | position, (diagonals1 | position) << 1, (diagonals2 | position) >> 1);
            // building不需要操作，因为只有获得一个解的时候正确的数据已经覆盖了原来未回撤的数据
//            building[row] = -1;
        }
    }

    private List<String> buildOut(int[] built) {
        List<String> out = new ArrayList<>(n);
        char[] chsEmpty = new char[n];
        char[] chs;
        Arrays.fill(chsEmpty, '.');
        for (int i = 0; i < n; i++) {
            chs = Arrays.copyOf(chsEmpty, n);
            chs[built[i]] = 'Q';
            out.add(new String(chs));
        }
        return out;
    }
}
