package veinthrough.leetcode.dp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.generic.Pair;

import java.util.function.Function;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 1. 二叉树路径: {@link veinthrough.leetcode.tree.Paths}
 * 2. 二维数组路径: {@link veinthrough.leetcode.dp.Paths}
 *
 * 第62题: 二维数组路径数(无障碍)，
 * 一个机器人位于一个 m x n 网格的左上角 （起始点在下图中标记为“Start” ）。
 * 机器人每次只能向下或者向右移动一步。机器人试图达到网格的右下角（在下图中标记为“Finish”）。
 * 问总共有多少条不同的路径？
 *
 * 第63题: 二维数组路径数(有障碍)，
 * 一个机器人位于一个 m x n 网格的左上角 （起始点在下图中标记为“Start” ）。
 * 机器人每次只能向下或者向右移动一步。机器人试图达到网格的右下角（在下图中标记为“Finish”）。
 * 现在考虑网格中有障碍物。那么从左上角到右下角将会有多少条不同的路径？
 * 网格中的障碍物和空位置分别用 1 和 0 来表示。
 * 问总共有多少条不同的路径？
 *
 * 第64题: 二维数组min path sum，
 * 给定一个包含非负整数的 m x n 网格，请找出一条从左上角到右下角的路径，使得路径上的数字总和为最小。
 * 说明：每次只能向下或者向右移动一步。
 *
 * 第120题: 三角形最小路径和，
 * 给定一个三角形，找出自顶向下的最小路径和。每一步只能移动到下一行中相邻的结点上。
 * 相邻的结点 在这里指的是 下标 与 上一层结点下标 相同或者等于 上一层结点下标 + 1 的两个结点。
 * 例如，给定三角形：
 * #           4
 * #        1   7
 * #     0   3   6
 * #   2   5   9   3
 * 自顶向下的最小路径和为 11（即，2 + 3 + 5 + 1 = 11）。
 *
 * 第174题：地下城游戏最小初始生命值,
 * 一些恶魔抓住了公主（P）并将她关在了地下城的右下角。地下城是由 M x N 个房间组成的二维网格。
 * 我们英勇的骑士（K）最初被安置在左上角的房间里，他必须穿过地下城并通过对抗恶魔来拯救公主。
 * 骑士的初始健康点数为一个正整数。如果他的健康点数在某一时刻降至 0 或以下，他会立即死亡。
 * 有些房间由恶魔守卫，因此骑士在进入这些房间时会失去健康点数（若房间里的值为负整数，则表示骑士将损失健康点数）；
 * 其他房间要么是空的（房间里的值为 0），要么包含增加骑士健康点数的魔法球（若房间里的值为正整数，则表示骑士将增加健康点数）。
 * 为了尽快到达公主，骑士决定每次只向右或向下移动一步。
 *
 * 编写一个函数来计算确保骑士能够拯救到公主所需的最低初始健康点数。
 *
 * 例如，考虑到如下布局的地下城，如果骑士遵循最佳路径 右 -> 右 -> 下 -> 下，则骑士的初始健康点数至少为 7。
 * -2(K)    -3      3
 * -5       -10     1
 * 10       30      -5 (P)
 * 说明:
 * 骑士的健康点数没有上限。
 * 任何房间都可能对骑士的健康点数造成威胁，也可能增加骑士的健康点数，包括骑士进入的左上角房间以及公主被监禁的右下角房间。
 */
@Slf4j
public class Paths {
    @Test
    public void test62() {
        Stream.of(
                Pair.of(3,7), // 28
                Pair.of(3,2)) // 3
                .forEach(pair ->
                        log.info(methodLog("" + paths(pair.getFirst(),pair.getSecond()))));
    }
    
    @Test
    public void test63() {
        Stream.of(new int[][]{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}, // 2
                new int[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 0}}) // 0
                .forEach(grid ->
                        log.info(methodLog("" + pathsWithObstacle3(grid))));
    }

    @Test
    public void test64() {
        Stream.of(
                new int[][]{{1, 3, 1}, {1, 5, 1}, {4, 2, 1}}, // 7
                new int[][]{{1, 2, 3}, {4, 5, 6}}) // 12
                .forEach(grid ->
                        log.info(methodLog("" + minPathSum(grid))));
    }

    @Test
    public void test120() {
        Stream.of(
                new int[][]{{2}, {3, 4}, {6, 5, 7}, {4, 1, 8, 3}},
                new int[][]{{2}, {3, 4}, {6, 5, 7}, {4, 1, 8, 3}})
                .forEach(array -> log.info(
                        methodLog(
                                "From left to right(两个滚动数组)", "" + minimumTopDown(array),
                                "From right to left(一个滚动数组)", "" + minimumTopDown2(array))));
    }

    @Test
    public void test174() {
        Stream.of(
                new int[][]{{-2, -3, 3}, {-5, -10, 1}, {10, 30, -5}},
                new int[][]{{100}},
                new int[][]{{3, -20, 30}, {-3, 4, 0}},
                new int[][]{{2}, {1}},
                new int[][]{{-61, -52, 10, -82}, {-77, -22, -83, 48}, {-59, -61, -42, -34}, {231, 47, -6, 19}},
                new int[][]{{1, -3, 3}, {0, -2, 0}, {-3, -3, -3}})
                .forEach(dungeon ->
                        Stream.<Function<int[][], Integer>>of(
                                this::minimumHP, this::minimumHP_back)
                                .forEach(minimumHP ->
                                        log.info(methodLog("" + minimumHP(dungeon)))));
    }

    private int paths(int m, int n) {
        int[] paths = new int[n+1];
        // 1. 第一行为1
        paths[1] = 1;
        // 2. 其他行
        for(int i=1;i<=m;i++)
            for(int j=2;j<=n;j++)
                paths[j] += paths[j-1];

        return paths[n];
    }

    /**
     * 第63题: 二维数组路径数
     * 方法1 最原始的dp：
     * 循环里面需要特殊考虑第一行/第一列，或者把第一行/第一列提出来；
     */
    @SuppressWarnings("unused")
    private int pathsWithObstacle(int[][] grid) {
        // boundary: start with obstacle
        if (grid[0][0] == 1) return 0;

        //
        int rows = grid.length;
        int columns = grid[0].length;
        int[][] paths = new int[rows][columns];
        paths[0][0] = 1;
        int i, j;
        // 1. first column
        for (i = 1; i < rows; i++)
            if (grid[i][0] != 1)
                paths[i][0] = paths[i - 1][0];

        // 2. first row
        for (j = 1; j < columns; j++)
            if (grid[0][j] != 1)
                paths[0][j] = paths[0][j - 1];

        // 3. others
        for (i = 1; i < rows; i++)
            for (j = 1; j < columns; j++)
                if (grid[i][j] != 1)
                    paths[i][j] = paths[i - 1][j] + paths[i][j - 1];

        return paths[rows - 1][columns - 1];
    }


    /**
     * 第63题: 二维数组路径数
     * 2. 优化1: 分别在上面和左边添加一个边界, 就不需要特殊考虑第一行/第一列
     * grid:
     * *  *
     * *  *
     * *  *
     * pathsWithObstacle 初始化:
     * 0  1  0  0
     * 0  *  *  *
     * 0  *  *  *
     * 0  *  *  *
     */
    @SuppressWarnings("unused")
    private int pathsWithObstacle2(int[][] grid) {
        // boundary: start with obstacle
        if (grid[0][0] == 1) return 0;

        //
        int width = grid[0].length;
        int height = grid.length;
        int[][] paths = new int[height + 1][width + 1];
        // 1. 在上面和左边添加一个边界
        paths[0][1] = 1;
        int i, j;
        // 2. 第一行+其他行
        for (i = 0; i < height; i++)
            for (j = 0; j < width; j++)
                if (grid[i][j] != 1)
                    paths[i + 1][j + 1] = paths[i][j + 1] + paths[i + 1][j];
        return paths[height][width];
    }

    /**
     * 第63题: 二维数组路径数
     * 3. 优化2: 使用滚动数组代替二维数组,
     * 【注意】如果有障碍的时候需要清0, 因为使用滚动数组代替了二维数组，初始值不是0
     * 对于path[i][j], 如果只用到以下数据, 那么可以用滚动数组代替;
     * (1) 上一层的数据(row=i-1), 只能是i-1否则就要保存多层数据
     * (2) 左边的数据(column<=j)
     */
    private int pathsWithObstacle3(int[][] grid) {
        // boundary: start with obstacle
        if (grid[0][0] == 1) return 0;

        //
        int rows = grid.length;
        int columns = grid[0].length;
        int[] path = new int[columns + 1];
        // 1. 在上面和左边添加一个边界
        path[1] = 1;
        // 2. 第一行+其他行
        for (int i = 1; i <= rows; i++)
            for (int j = 1; j <= columns; j++)
                // 注意这里需要清0, 因为使用滚动数组代替了二维数组，初始值不是0
                if (grid[i - 1][j - 1] == 1) path[j] = 0;
                else path[j] += path[j - 1];
        return path[columns];
    }


    /**
     * 第64题: 二维数组min path sum，
     * 优化：使用一个一维数组而不是二维数组，节省空间
     */
    private int minPathSum(int[][] grid) {
        int rows, columns;
        rows = grid.length;
        columns = grid[0].length;
        // 使用一个一维数组而不是二维数组，节省空间
        int[] min = new int[columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // (1) 来自上(包括了第一列的情况)
                min[j] += grid[i][j];
                // 第一行的特殊情况
                if (i == 0 && j > 0) {
                    min[j] += min[j - 1];
                    continue;
                }
                // (2) 来自左边
                if (j > 0) min[j] = Math.min(min[j], min[j - 1] + grid[i][j]);
            }
        }
        return min[columns - 1];
    }

    /**
     * 第64题: 二维数组min path sum，
     * 1. 优化:
     * 【优化1】【错误】: 分别在上面和左边添加一个边界, 就不需要特殊考虑第一行/第一列
     * 【优化2】: 使用滚动数组代替二维数组
     * 【注意】在{@link Paths#pathsWithObstacle3(int[][])}中
     * 在【添加边界】并且【使用滚动数组】之后，代码需要做调整;
     * 所以【优化1】【优化2】同时使用时要注意。
     *
     * 2. 这里为什么使用了【滚动数组】之后不能【添加边界】
     * 我们试图在min(滚动数组, 长度为columns+1)左边添加一个边界
     * (1) 为了在每次计算paths[1]的时候用的是从上到下的路径, 将paths[0]初始为一个比较大的值
     * (因为paths[1]实际上是在不添加边界时的paths[0], 代表第0列，而第0列只有从上到下的一条路径)，
     * 这样在计算paths[1]的最小值的时候就不会是从左边过来的(使用path[0]),
     * 这里姑且将paths[0]设置为grid中第0列的和
     * (2) 同样我们在第一次计算paths的时候(也就是第一行), 我们希望走的是从左到右的这条唯一路径,
     * 也就是说我们同样需要将paths[1], paths[2]....初始为一个比较大的值
     * (3) 那么问题发生了, 我们已经增加了路径的值
     * 示例:
     * 1  3  1
     * 1  5  1
     * 4  2  1
     * -----------------------
     * > paths: 0, 0, 0
     * > paths: 1, 4, 5
     * > paths: 2, 7, 6
     * > paths: 6, 8, 7
     * -----------------------
     * > paths(边界): 6|, 7,  7,  7
     * > paths: 6|, 7,  10, 8
     * > paths: 6|, 7,  12, 9
     * > paths: 6|, 10, 12, 10
     */
    @SuppressWarnings("unused")
    private int minPathSum2(int[][] grid) {
        int rows, columns;
        rows = grid.length;
        columns = grid[0].length;
        // 优化: 使用一个一维数组而不是二维数组，节省空间
        int[] min = new int[columns + 1];
        for (int i = 0; i < rows; i++) {
            min[0] += grid[i][0];
        }
        for (int j = 1; j <= columns; j++) {
            min[j] = min[0] + 1;
        }
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= columns; j++) {
                min[j] += grid[i - 1][j - 1];
                min[j] = Math.min(min[j], min[j - 1] + grid[i - 1][j - 1]);
            }
        }
        return min[columns];
    }

    /**
     * 三角形最小路径和:
     * 优化: 使用【两个】滚动数组代替二维数组
     * 1. 这里必须使用【两个】滚动数组【From left to right, j: 0 --> i】,
     * 因为path[i]在下一行有2个相邻，会被2个使用
     * (1) path[i]会使用上一层的path[i], 如果只使用一个滚动数组, 此时path[i]已经覆盖上一层的path[i]
     * (2) path[i+1]仍然需要使用上一层的path[i]
     * 2. 使用【一个】滚动数组【From right to left, j: i --> 0】: {@link #minimumTopDown2(int[][])}
     * 3. 这里是既可以【From right to left】也可以【From left to right】，
     * 既可以【From up to down】也可以【From down to up】，
     * 像求最小值/最大值的情况就不行。
     *
     * #   0   1   2   3
     * #           4
     * #        1   7
     * #     0   3   6
     * #   2   5   9   3
     */
    @SuppressWarnings("Duplicates")
    private int minimumTopDown(int[][] triangle) {
        int scale = triangle.length - 1;
        if (scale == 0) return triangle[0][0];
        if (scale == 1) return triangle[1][0] < triangle[1][1] ?
                triangle[0][0] + triangle[1][0] : triangle[0][0] + triangle[1][1];

        // 1. 第2行(i=1)
        // 不能使用一个滚动数组，因为path[i]在下一行有2个相邻，会被2个使用
        // (1) path[i]会使用上一层的path[i], 如果只使用一个滚动数组, 此时path[i]已经覆盖上一层的path[i]
        // (2) path[i+1]仍然需要使用上一层的path[i]
        // 但是可以使用两个滚动数组
        int[] last = new int[scale + 1];
        int[] path = new int[scale + 1], temp;
        last[0] = triangle[0][0] + triangle[1][0];
        last[1] = triangle[0][0] + triangle[1][1];
        int min = Integer.MAX_VALUE;
        // 2. 从第3行(i=2)开始迭代
        for (int i = 2; i <= scale; i++) {
            // (1) 单独提取出第i行的第1个/最后1个
            path[0] = last[0] + triangle[i][0];
            path[i] = last[i - 1] + triangle[i][i];
            // (2) 最后一行初始化min
            if (i == scale) min = path[0] < path[i] ? path[0] : path[i];
            // (3) 普通行普通列
            for (int j = 1; j < i; j++) {
                path[j] = Math.min(last[j - 1], last[j]) + triangle[i][j];
                // (4) 最后一行更新min
                if (i == scale && path[j] < min) min = path[j];
            }
            temp = path;
            path = last;
            last = temp;
        }
        return min;
    }

    /**
     * 三角形最小路径和:
     * 优化: 使用【一个】滚动数组代替二维数组
     * 1. 这里使用【一个】滚动数组【From right to left, j: i --> 0】
     * 2. 使用【两个】滚动数组【From left to right, j: 0 --> i】: {@link #minimumTopDown(int[][])}
     * 3. 同样可以采用【From down to up】的方式, 也只需要【一个】滚动数组
     * 4. 这里是既可以【From right to left】也可以【From left to right】，
     * 既可以【From up to down】也可以【From down to up】，
     * 像求最小值/最大值的情况就不行。
     *
     * #           4
     * #        1   7
     * #     0   3   6
     * #   2   5   9   3
     */
    @SuppressWarnings("Duplicates")
    private int minimumTopDown2(int[][] triangle) {
        int scale = triangle.length - 1;
        if (scale == 0) return triangle[0][0];
        if (scale == 1) return triangle[1][0] < triangle[1][1] ?
                triangle[0][0] + triangle[1][0] : triangle[0][0] + triangle[1][1];

        // 1. 第2行(i=1)
        // 不能使用一个滚动数组，因为path[i]在下一行有2个相邻，会被2个使用
        // (1) path[i]会使用上一层的path[i], 如果只使用一个滚动数组, 此时path[i]已经覆盖上一层的path[i]
        // (2) path[i+1]仍然需要使用上一层的path[i]
        // 但是可以使用两个滚动数组
        int[] path = new int[scale + 1];
        path[0] = triangle[0][0] + triangle[1][0];
        path[1] = triangle[0][0] + triangle[1][1];
        int min = Integer.MAX_VALUE;
        // 2. 从第3行(i=2)开始迭代
        for (int i = 2; i <= scale; i++) {
            // (1) 单独提取出第i行的最后一个(第i个)
            path[i] = path[i - 1] + triangle[i][i];
            // (2) 最后一行初始化min
            if (i == scale) min = path[i];
            for (int j = i - 1; j >= 0; j--) {
                // (3) 第i行的第0个, 这里不能像最后一个单独提出去,
                // 因为要按从右到左的顺序否则path[0]的上一层结果会提前被冲掉
                if (j == 0) path[j] += triangle[i][j];
                    // (4) 普通行普通列
                else path[j] = Math.min(path[j - 1], path[j]) + triangle[i][j];
                // (5) 最后一行更新min
                if (i == scale && path[j] < min) min = path[j];
            }
        }
        return min;
    }


    /**
     * 地下城游戏最小初始生命值方法1(自解):
     * dungeon:
     * -2	-3	3
     * -5	-10	1
     * 10	30	-5
     * dp: [0]表示该点的最大的最小负能量(同一条路径使用最小min，不需要置0；不同路径之间使用最大max),
     * [1]表示经过该点的最大路径和(给后面使用)
     * (-2,-2)	(-5,-5)     (-5,-2)
     * (-7,-7)	(-15,-15)	(-5,-1)
     * (-7,3)	(-7,33)     (-6,-6)
     */
    private int minimumHP(int[][] dungeon) {
        int rows = dungeon.length, columns = dungeon[0].length;
        int[][][] dp = new int[rows][columns][2];
        dp[0][0][0] = dungeon[0][0];
        dp[0][0][1] = dungeon[0][0];

        int leftPath, left, upPath, up;
        // 1. 第一行
        for (int j = 1; j < columns; j++) {
            // leftPath
            dp[0][j][1] = dp[0][j - 1][1] + dungeon[0][j];
            // 1. 从每条路径中选出最小负能量:【同一条路径使用min】，不需要置0
            // (1) 该点之前(不包括该点)的最小负能量
            // (2) 该点的最小负能量(该点之前最大路径和+该点值)
            dp[0][j][0] = Math.min(dp[0][j][1], dp[0][j - 1][0]);
        }
        // 2. 第一列
        for (int i = 1; i < rows; i++) {
            // upPath
            dp[i][0][1] = dp[i - 1][0][1] + dungeon[i][0];
            // 1. 从每条路径中选出最小负能量:【同一条路径使用min】，不需要置0
            // (1) 该点之前(不包括该点)的最小负能量
            // (2) 该点的最小负能量(该点之前最大路径和+该点值)
            dp[i][0][0] = Math.min(dp[i][0][1], dp[i - 1][0][0]);
        }
        // 3.
        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < columns; j++) {
                if (dungeon[i][j] >= 0) {
                    dp[i][j][0] = Math.max(dp[i - 1][j][0], dp[i][j - 1][0]);
                    dp[i][j][1] = Math.max(dp[i - 1][j][1], dp[i][j - 1][1]) + dungeon[i][j];
                } else {
                    // 1. 从每条路径中【分别】选出最小负能量:【同一条路径使用min】，不需要置0
                    // (1) 该点之前(不包括该点)的最小负能量
                    // (2) 该点的最小负能量(该点之前最大路径和+该点值)
                    // 上
                    leftPath = dp[i][j - 1][1] + dungeon[i][j];
                    left = Math.min(leftPath, dp[i][j - 1][0]); // 同路径使用min
                    // 左
                    upPath = dp[i - 1][j][1] + dungeon[i][j];
                    up = Math.min(upPath, dp[i - 1][j][0]); // 同路径使用min
                    // 2. 从2条路径中选出最大的最小负能量:【不同路径之间使用max】
                    // (1) 从2条路径中选出最大的最小负能量
                    // (2) 从2条路径中选出最大路径和
                    dp[i][j][0] = Math.max(left, up); // 不同路径使用max
                    dp[i][j][1] = Math.max(leftPath, upPath);
                }
            }
        }
        return dp[rows - 1][columns - 1][0] >= 0 ? 1 : -dp[rows - 1][columns - 1][0] + 1;
    }

    /**
     * 地下城游戏最小初始生命值方法2(题解): 使用反向dp:
     *
     * 1. 正向
     * 如果按照从左上往右下的顺序进行动态规划，对于每一条路径，我们需要同时记录两个值。
     * 第一个是「从出发点到当前点的路径和」，第二个是「从出发点到当前点所需的最小初始值」。而这两个值的重要程度相同
     * 我们希望「从出发点到当前点的路径和」尽可能大，而「从出发点到当前点所需的最小初始值」尽可能小。这两条路径各有优劣。
     * 因为有两个重要程度相同的参数同时影响后续的决策。也就是说，这样的动态规划是不满足「无后效性」的。
     * [思考:] 感觉就是要瞻前顾后
     * (1) 顾后(路径1): 「从出发点到当前点的最大路径和」  ==> 「到当前节点需要的最小HP」
     * (2) 瞻前(路径2): 「从出发点到当前点所需的最小HP」:  「到当前节点需要的最小HP」/ 「到之前节点所需的最小HP」
     * 路径1和路径2可能不同, 如果「从出发点到当前点的路径和」的值大于等于0, 也不能清0
     * 2. 逆向
     * 「从当前点到终点的最大路径和」  ==> 「从当前节点出发需要的最小HP」
     * 如果「从当前点到终点的最大路径和」的值大于等于0, 「从当前节点出发需要的最小HP」可以清0
     * 
     * dungeon:
     * -2	-3	 3
     * -5	-10	 1
     * 10	30	-5
     * dp: 表示该点最大的负能量(使用max, 需要置0)
     * -6	-4	-1
     * -5	-10	-4
     * 0	0	-5
     *
     * 2. 每次dp[i][j]>0时需置0
     */
    private int minimumHP_back(int[][] dungeon) {
        int li = dungeon.length - 1, ci = dungeon[0].length - 1;
        int[][] dp = new int[li + 1][ci + 1];
        dp[li][ci] = dungeon[li][ci] > 0 ? 0 : dungeon[li][ci];
        // 1. 最右列
        for (int i = li - 1; i >= 0; i--) {
            dp[i][ci] = dp[i + 1][ci] + dungeon[i][ci];
            // >0时置0
            if (dp[i][ci] > 0) dp[i][ci] = 0;
        }
        // 2. 最下行
        for (int i = ci - 1; i >= 0; i--) {
            dp[li][i] = dp[li][i + 1] + dungeon[li][i];
            // >0时置0
            if (dp[li][i] > 0) dp[li][i] = 0;
        }
        // 3.
        for (int i = li - 1; i >= 0; i--) {
            for (int j = ci - 1; j >= 0; j--) {
                dp[i][j] = Math.max(dp[i + 1][j] + dungeon[i][j], dp[i][j + 1] + dungeon[i][j]);
                // >0时置0
                if (dp[i][j] > 0) dp[i][j] = 0;
            }
        }
        return -dp[0][0] + 1;
    }
}