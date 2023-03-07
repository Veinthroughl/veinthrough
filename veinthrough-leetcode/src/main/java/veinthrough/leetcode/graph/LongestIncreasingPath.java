package veinthrough.leetcode.graph;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.api.array.Array.stringOf2DArray;

/**
 * 第329题：矩阵中的最长递增路径
 * 给定一个整数矩阵，找出最长递增路径的长度。
 * 对于每个单元格，你可以往上，下，左，右四个方向移动。 你不能在对角线方向上移动或移动到边界外（即不允许环绕）。
 *
 * 类似dfs/bfs: {@link IsBipartite}
 *
 * 思考:
 * 1. 本题为图算法: 递归(循环 --> 递归?)dp
 * 2. 二叉树中的动态规划: 循环 -> 递归)? {@link veinthrough.leetcode.tree.Paths}
 */
@Slf4j
public class LongestIncreasingPath {
    private int[][] matrix;
    private int[][] deep;
    private int rows, columns;

    @Test
    public void test329() {
        Stream.of(
                new int[][]{{9, 9, 4}, {6, 6, 8}, {2, 1, 1}},
                new int[][]{{3, 4, 5}, {3, 2, 6}, {2, 2, 1}},
                new int[][]{{1}})
                .forEach(_matrix -> log.info(
                        methodLog(
                                "Matrix", stringOf2DArray(_matrix),
                                "By DFS", "" + longestIncreasingPath(_matrix),
                                "By BFS(Topo)", ""+longestIncreasingPathTopo(_matrix))));
    }

    /**
     * 方法一：记忆化深度优先搜索(DFS)
     * {@link IsBipartite#dfs(int)}
     * 1. 将矩阵看成一个有向图，每个单元格对应图中的一个节点，如果相邻的两个单元格的值不相等，
     * 则在相邻的两个单元格之间存在一条从较小值指向较大值的有向边。
     * (1) 【问题转化成在有向图中寻找最长路径】。
     * (2) 这里是【严格】递增路径, 明显不会有环;
     * (3) 如果不是【严格】递增路径， 在多个数字相等的情况下, 会形成环
     * 2. 深度优先搜索是非常直观的方法。从一个单元格开始进行深度优先搜索，即可找到从该单元格开始的最长递增路径。
     * 对每个单元格分别进行深度优先搜索之后，即可得到矩阵中的最长递增路径的长度。
     * 3. 但是如果使用朴素深度优先搜索，时间复杂度是指数级，会超出时间限制，因此必须加以优化。
     * 朴素深度优先搜索的时间复杂度过高的原因是进行了大量的重复计算，同一个单元格会被访问多次，每次访问都要重新计算。
     * 由于同一个单元格对应的最长递增路径的长度是固定不变的，因此可以使用记忆化的方法进行优化。
     * 用矩阵deep作为缓存矩阵，已经计算过的单元格的结果存储到缓存矩阵中,使用记忆化深度优先搜索，
     * (1) 当访问到一个单元格 (i,j)时，如果deep[i][j]!=0，说明该单元格的结果已经计算过, 则直接从缓存中读取结果，
     * (2) 如果deep[i][j]==0，说明该单元格的结果尚未被计算过，则进行搜索，并将计算得到的结果存入缓存中。
     * (3) deep[i][j]表示以(i,j)为【起点】的最长递增路径长度;而不是以(i,j)为【终点】
     * (4) 因为以(i,j)为【起点】, deep[i][j]不会在下一次访问的时候有变化
     *
     * 遍历完矩阵中的所有单元格之后，即可得到矩阵中的最长递增路径的长度。
     */
    int longestIncreasingPath(int[][] _matrix) {
        // boundary:
        if ((rows = _matrix.length) == 0) return 0;
        if ((columns = _matrix[0].length) == 0) return 0;
        if (rows == 1 && columns == 1) return 1;

        //
        matrix = _matrix;
        deep = new int[rows][columns];
        int max = 0;
        // 图不一定是全连接的, 所以需要使用一个循环
        // 可以把图理解为一个森林, 每次dfs都是一棵树
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                max = max(max, dfs(i, j));
        return max;
    }

    @SuppressWarnings("Duplicates")
    public int dfs(int i, int j) {
        // 已经计算过
        // 这里没有环, 所以不会出现正在计算deep[i][j]然后又重新进入计算deep[i][j]
        // 也就是说, 如果deep[i][j]那么一定是已经计算完了
        if (deep[i][j] > 0) return deep[i][j];
        // 未计算过
        deep[i][j]++;
        int up = i - 1, down = i + 1, left = j - 1, right = j + 1;
        if (up >= 0 && matrix[up][j] > matrix[i][j])
            deep[i][j] = max(deep[i][j], dfs(up, j) + 1);
        if (down < rows && matrix[down][j] > matrix[i][j])
            deep[i][j] = max(deep[i][j], dfs(down, j) + 1);
        if (left >= 0 && matrix[i][left] > matrix[i][j])
            deep[i][j] = max(deep[i][j], dfs(i, left) + 1);
        if (right < columns && matrix[i][right] > matrix[i][j])
            deep[i][j] = max(deep[i][j], dfs(i, right) + 1);
        return deep[i][j];
    }

    /**
     * 方法2: 拓扑排序(一种dp)
     * 思考:
     * (1) 拓扑排序是一种dp
     * (2) 拓扑排序是一种BFS: 应该是多个节点一起BFS
     * (3) BFS使用队列来实现:
     * {@link IsBipartite#bfs}
     *
     *
     * 从方法1可以看到，每个单元格对应的最长递增路径的结果只和相邻单元格的结果有关，那么是否可以使用动态规划求解？
     * (1) 根据方法1的分析，动态规划的状态定义和状态转移方程都很容易得到。
     * 方法1中使用的缓存矩阵deep 即为状态值，状态转移方程如下：
     * memo[i][j]=max{memo[x][y]}+1
     * 其中 (x,y) 与 (i,j) 在矩阵中相邻，并且 matrix[x][y]>matrix[i][j]
     * (2) 动态规划除了状态定义和状态转移方程，还需要考虑【边界情况】。这里的边界情况是什么呢？
     * 如果一个单元格的值比它的所有相邻单元格的值都要大，那么这个单元格对应的最长递增路径是1，这就是边界条件。
     * 这个边界条件并不直观，而是需要根据矩阵中的每个单元格的值找到作为边界条件的单元格。
     * (3) 仍然使用方法1的思想，将矩阵看成一个【有向图】，计算每个单元格对应的【出度】，即有多少条边从该单元格出发。
     * 对于作为边界条件的单元格，该单元格的值比所有的相邻单元格的值都要大，因此作为边界条件的单元格的【出度都是0】。
     * (4) 基于出度的概念，可以使用【拓扑排序】求解。从所有出度为 0 的单元格开始【广度优先搜索】，
     * 每一轮搜索都会遍历当前层的所有单元格，更新其余单元格的出度，并将出度变为0的单元格加入下一层搜索。
     * 当搜索结束时，搜索的总层数即为矩阵中的最长递增路径的长度。
     */
    @SuppressWarnings("Duplicates")
    public int longestIncreasingPathTopo(int[][] _matrix) {
        // boundary
        if ((matrix = _matrix) == null ||
                (rows = _matrix.length) == 0 || (columns = _matrix[0].length) == 0) return 0;
        if (rows == 1 && columns == 1) return 1;

        //
        // 1. out degrees
        int[][] outDegrees = new int[rows][columns];
        int[][] neighbors;
        Queue<int[]> cells = new LinkedList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                neighbors = largerNeighbors(i, j); // larger neighbors
                // 没有更大的邻居, 出度为0
                for (int[] neighbor : neighbors) if (neighbor != null) outDegrees[i][j]++;
                // push cells with 0-out-degree
                if (outDegrees[i][j] == 0)
                    cells.offer(new int[]{i, j});
            }
        }

        // 2. calculate deep by BFS(cells中的元素是按出度分成一层一层的)
        int deep = 0, size;
        int[] cell;
        // 必然有出度为0的元素, 最大就是, 所以初始的cells一定非空
        while (!cells.isEmpty()) {
            ++deep;
            size = cells.size();
            // 每次遍历完size就会进入下一层(根据出度)
            for (int n = 0; n < size; n++) {
                cell = cells.poll();
                //noinspection ConstantConditions
                neighbors = smallerNeighbors(cell[0], cell[1]); // smaller neighbors
                for (int[] neighbor : neighbors)
                    // push cells with 0-out-degree
                    if (neighbor != null && --outDegrees[neighbor[0]][neighbor[1]] == 0)
                        cells.offer(neighbor);
            }
        }
        return deep;
    }

    @SuppressWarnings("Duplicates")
    private int[][] largerNeighbors(int i, int j) {
        int[][] neighbors = new int[4][2];

        int up = i - 1, down = i + 1, left = j - 1, right = j + 1;
        neighbors[0] = up >= 0 && matrix[up][j] > matrix[i][j] ? new int[]{up, j} : null;
        neighbors[1] = down < rows && matrix[down][j] > matrix[i][j] ? new int[]{down, j} : null;
        neighbors[2] = left >= 0 && matrix[i][left] > matrix[i][j] ? new int[]{i, left} : null;
        neighbors[3] = right < columns && matrix[i][right] > matrix[i][j] ? new int[]{i, right} : null;

        return neighbors;
    }

    @SuppressWarnings("Duplicates")
    private int[][] smallerNeighbors(int i, int j) {
        int[][] neighbors = new int[4][2];

        int up = i - 1, down = i + 1, left = j - 1, right = j + 1;
        neighbors[0] = up >= 0 && matrix[up][j] < matrix[i][j] ? new int[]{up, j} : null;
        neighbors[1] = down < rows && matrix[down][j] < matrix[i][j] ? new int[]{down, j} : null;
        neighbors[2] = left >= 0 && matrix[i][left] < matrix[i][j] ? new int[]{i, left} : null;
        neighbors[3] = right < columns && matrix[i][right] < matrix[i][j] ? new int[]{i, right} : null;

        return neighbors;
    }
}