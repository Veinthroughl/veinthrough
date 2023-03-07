package veinthrough.leetcode.ac;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 组合：不强制顺序, dfs有start(不需要visited), 不可使用交换数组实现
 * 排列: 不强制顺序, dfs无start(从0开始, 需要visited), 或者可使用交换数组实现
 *
 * 1. 无重复元素的组合
 * 2. 有重复元素的组合
 *
 * 第77题：组合,
 * 给定两个整数 n 和 k，返回范围 [1, n] 中所有可能的 k 个数的组合。
 * 你可以按 任何顺序 返回答案。
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class Combination {
    static private int n, k;
    static private int[] nums;
    static private List<List<Integer>> res;

    @Test
    public void test77() {
        Stream.of(4, 5, 6, 7)
                .forEach(n ->
                {
                    List<List<Integer>> res = combD(IntStream.range(1, n + 1).toArray(), 2);
                    log.info(methodLog(
                            String.format("C(%d,%d) from %s", n, k, Arrays.toString(nums)),
                            "" + res.size() + res));
                });
    }

    /**
     * 无重复元素的组合
     */
    @Test
    public void testCombD() {
        int[] nums = new int[]{1, 2, 3, 4, 5};
        Stream.of(1, 2, 3, 4, 5)
                .forEach(k ->
                {
                    List<List<Integer>> res = combD(nums, k);
                    log.info(methodLog(
                            String.format("C(%d,%d) from %s", n, k, Arrays.toString(nums)),
                            "" + res.size() + res));
                });
    }

    /**
     * 有重复元素的组合
     */
    @Test
    public void testComb() {
        int[] nums = new int[]{1, 2, 2, 3, 4, 4};
        Stream.of(1, 2, 3, 4, 5)
                .forEach(k ->
                {
                    List<List<Integer>> res = comb(nums, k);
                    log.info(methodLog(
                            String.format("C(%d,%d) from %s", n, k, Arrays.toString(nums)),
                            "" + res.size() + res));
                });
    }

    /**
     * 无重复元素的组合
     */
    private static List<List<Integer>> combD(int[] nums, int k) {
        res = new LinkedList<>();
        if (k == 0) {
            res.add(new ArrayList<>(0));
            return res;
        }

        Combination.nums = nums;
        n = nums.length;
        Combination.k = k;
        _dfsD(new ArrayList<>(k), 0, 0);
        return res;
    }

    /**
     * 有重复元素的组合
     */
    private static List<List<Integer>> comb(int[] nums, int k) {
        res = new LinkedList<>();

        // boundary
        if (k == 0) {
            res.add(new ArrayList<>(0));
            return res;
        }

        //
        Combination.nums = nums;
        // (1) sort
        Arrays.sort(Combination.nums);
        n = nums.length;
        Combination.k = k;
        // (2) dfs
        _dfs(new ArrayList<>(k), 0, 0);
        return res;
    }

    /**
     * 无重复元素的组合dfs
     * 组合：不强制顺序, dfs有start(不需要visited), 不可使用交换数组实现
     * 排列: 不强制顺序, dfs无start(从0开始, 需要visited), 或者可使用交换数组实现
     * 强制顺序(【更高层】做限制)：index+1的坐标必须>index的坐标, 所以index选i, index+1只能从i+1开始选
     */
    static void _dfsD(List<Integer> building, int index, int start) {
        if (index == k) {
            res.add(new ArrayList<>(building));
            return;
        }

        // 这里对坐标进行优化：
        // 前面已经有index个，还差k-index个
        // (i+0),....(i+k-index-1)
        // i+k-index-1<n --> i<n-k+index+1
        for (int i = start; i <= n - k + index; i++) {
            building.add(nums[i]);
            _dfsD(building, index + 1, i + 1);
            building.remove(new Integer(nums[i]));
        }
    }

    /**
     * 有重复元素的组合dfs
     * 组合：不强制顺序, dfs有start(不需要visited), 不可使用交换数组实现
     * 排列: 不强制顺序, dfs无start(从0开始, 需要visited), 或者可使用交换数组实现
     * 强制顺序(【更高层】做限制)：index+1的坐标必须>index的坐标, 所以index选i, index+1只能从i+1开始选
     * 尾部去重(【同一层】做限制)
     */
    static void _dfs(List<Integer> building, int index, int start) {
        if (index == k) {
            res.add(new ArrayList<>(building));
            return;
        }

        // 这里对坐标进行优化：
        // 前面已经有index个，还差k-index个
        // (i+0),....(i+k-index-1)
        // i+k-index-1<n --> i<n-k+index+1
        for (int i = start; i <= n - k + index; i++) {
            building.add(nums[i]);
            _dfs(building, index + 1, i + 1);
            building.remove(new Integer(nums[i]));

            // ignore if equal
            while (i + 1 <= n - k + index && nums[i + 1] == nums[i]) i++;
        }
    }
}
