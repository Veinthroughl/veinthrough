package veinthrough.leetcode.graph;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第785题： 判断二分图
 * 存在一个 无向图 ，图中有 n 个节点。其中每个节点都有一个介于 0 到 n - 1 之间的唯一编号。
 * 给你一个二维数组 graph ，其中 graph[u] 是一个节点数组，由节点 u 的邻接节点组成。
 * 形式上，对于 graph[u] 中的每个 v ，都存在一条位于节点 u 和节点 v 之间的无向边。该无向图同时具有以下属性：
 * 不存在自环（graph[u] 不包含 u）。
 * 不存在平行边（graph[u] 不包含重复值）。
 * 如果 v 在 graph[u] 内，那么 u 也应该在 graph[v] 内（该图是无向图）
 * 这个图可能不是连通图，也就是说两个节点 u 和 v 之间可能不存在一条连通彼此的路径。
 * 二分图 定义：如果能将一个图的节点集合分割成两个独立的子集 A 和 B ，
 * 并使图中的每一条边的两个节点一个来自 A 集合，一个来自 B 集合，就将这个图称为 二分图 。
 * 如果图是二分图，返回true；否则，返回 false。
 *
 * DFS/BFS: {@link LongestIncreasingPath#longestIncreasingPath(int[][])}
 */
@Slf4j
public class IsBipartite {
    private boolean DFS = true, BFS = false;
    private static final int ONE = 1, NA = 0;
    private int[] split;
    private int[][] graph;

    @Test
    public void test785() {
        Stream.of(new int[][]{{1, 2, 3}, {0, 2}, {0, 1, 3}, {0, 2}},
                new int[][]{{1, 3}, {0, 2}, {1, 3}, {0, 2}})
                .forEach(graph -> Stream.of(DFS, BFS)
                        .forEach(traverse -> log.info(
                                methodLog("" + isBipartite(graph, traverse)))));
    }

    /**
     * DFS/BFS
     */
    private boolean isBipartite(int[][] graph, boolean traverse) {
        int length;
        if ((length = graph.length) == 0 || length == 1 || length == 2) return true;

        this.graph = graph;
        split = new int[length];
        for (int i = 0; i < length; i++) {
            // 这里必然是另一棵树, 也就是图其实是一个森林
            // 每一次dfs都会把与该节点链接的所有节点计算一遍
            // 这里每次遍历一棵树
            if (graph[i].length != 0 && split[i] == NA) {
                // 每一次dfs都会(必须)把所有连接在一起的节点(一棵树)计算一遍
                // 否则一个空的节点你不能确定放在哪一边
                split[i] = ONE;
                if (traverse == DFS) if (!dfs(i)) return false;
                else if (!bfs(i)) return false;
            }
        }
        return true;
    }

    /**
     * 方法1: 深度优先遍历一棵树DFS
     * {@link LongestIncreasingPath#longestIncreasingPath(int[][])}
     */
    public boolean dfs(int i) {
        for (int neighbor : graph[i]) {
            if (split[neighbor] == split[i])
                return false;
            if (split[neighbor] == NA) {
                split[neighbor] = -split[i];
                // 每一次dfs都会把与该节点链接的所有节点计算一遍
                if (!dfs(neighbor)) return false;
            }
        }
        return true;
    }

    /**
     * 方法2： 广度优先遍历一棵树BFS
     * 广度优先遍历使用队列: {@link LongestIncreasingPath#longestIncreasingPathTopo(int[][])}
     */
    public boolean bfs(int i) {
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(i);
        int each;
        while (!queue.isEmpty()) {
            each = queue.poll();
            for (Integer neighbor : graph[each]) {
                if (split[neighbor] == split[each]) return false;
                if (split[neighbor] == NA) {
                    split[neighbor] = -split[each];
                    // 广度优先使用队列
                    queue.offer(neighbor);
                }
            }
        }
        return true;
    }
}