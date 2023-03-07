package veinthrough.leetcode.ac;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 组合：强制顺序, dfs有start(不需要visited), 不可使用交换数组实现
 * 排列: 不强制顺序, dfs无start(从0开始, 需要visited), 或者可使用交换数组实现
 *
 * 1. 无重复元素的排列
 * 2. 有重复元素的排列
 * 第46题: 无重复地全排列
 * 给定一个不含重复数字的数组 numsList ，返回其 所有可能的全排列 。你可以 按任意顺序 返回答案。
 *
 * 第47题: 有重复地全排列
 */
@SuppressWarnings({"Duplicates", "unused"})
@Slf4j
public class Arrangement {
    private static int n, k;
    private static int[] nums;
    private static List<List<Integer>> res;

    /**
     * 无重复元素的排列
     */
    @Test
    public void testArrangementD() {
        int[] nums = new int[]{1, 2, 3, 4, 5};
        Stream.of(1, 2, 3)
                .forEach(k ->
                {
                    List<List<Integer>> res = arrangementD(nums, k);
                    log.info(methodLog(
                            String.format("A(%d,%d) from %s", n, k, Arrays.toString(nums)),
                            "" + res.size() + res));
                });
    }

    /**
     * 有重复元素的排列
     */
    @Test
    public void testArrangement() {
        int[] nums = new int[]{1, 2, 2, 3, 3, 3, 4};
        Stream.of(3, 2, 1)
                .forEach(k ->
                {
                    List<List<Integer>> res = arrangement(nums, k);
                    log.info(methodLog(
                            String.format("A(%d,%d) from %s", n, k, Arrays.toString(nums)),
                            "" + res.size() + res));
                });
    }

    /**
     * 无重复元素的全排列
     */
    @Test
    public void test46() {
        Stream.of(
                new int[]{1},
                new int[]{1, 2},
                new int[]{1, 2, 3},
                new int[]{1, 2, 3, 4})
                .forEach(nums ->
                {
                    List<List<Integer>> res = arrangementD(nums, nums.length);
                    log.info(methodLog(
                            String.format("A(%d,%d) from %s", n, k, Arrays.toString(nums)),
                            "" + res.size() + res));
                });
    }

    /**
     * 有重复元素的全排列
     */
    @Test
    public void test47() {
        Stream.of(
                new int[]{1, 2},            // 2
                new int[]{2, 2, 3, 3, 4},   // 30
                new int[]{1, 2, 2, 3},      // 12
                new int[]{1, 2, 2, 2},      // 12
                new int[]{2, 2, 3, 3},      // 6
                new int[]{1},               // 1
                new int[]{1, 1, 2})         // 3
                .forEach(nums ->
                {
                    List<List<Integer>> res = arrangement(nums, nums.length);
                    log.info(methodLog(
                            String.format("A(%d,%d) from %s", n, k, Arrays.toString(nums)),
                            "" + res.size() + res));
                });
    }

    /**
     * A(n,k)，无重复元素的排列
     * 方法1: 使用visited数组来dfs/backtrace
     * 方法2: 巧妙地通过(交换)来实现dfs/backtrace
     *
     * 组合：不强制顺序, dfs有start(不需要visited), 不可使用交换数组实现
     * 排列: 不强制顺序, dfs无start(从0开始, 需要visited), 或者可使用交换数组实现
     */
    private static List<List<Integer>> arrangementD(int[] nums, int k) {
        // init
        Arrangement.nums = nums;
        res = new LinkedList<>();
        Arrangement.k = k;

        // boundary
        if (Arrangement.nums == null || (n = Arrangement.nums.length) == 0) return res;
        if (k == 0) {
            res.add(new ArrayList<>(0));
            return res;
        }
        if (n == 1 && k == 1) {
            res.add(Collections.singletonList(Arrangement.nums[0]));
            return res;
        }

        // 方法1: 使用visited数组来dfs/backtrace
//        _dfsD(new ArrayList<>(k), new boolean[n], 0);
        // 方法2: 巧妙地通过(交换)来实现dfs/backtrace
        _dfsDBySwap(
                Arrays.stream(Arrangement.nums)
                        .boxed()
                        .collect(Collectors.toList()),
                0);
        return res;
    }

    /**
     * A(n,k)，无重复元素的排列
     * 方法1: 使用visited数组来dfs/backtrace
     * 组合：不强制顺序, dfs有start(不需要visited), 不可使用交换数组实现
     * 排列: 不强制顺序, dfs无start(从0开始, 需要visited), 或者可使用交换数组实现
     *
     * @param visited 通过visited数组来实现dfs/backtrace
     * @param index   表示正在填充的索引
     */
    private static void _dfsD(List<Integer> building, boolean[] visited, int index) {
        if (index == k) {
            res.add(new ArrayList<>(building));
            return;
        }

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                building.add(nums[i]);
                visited[i] = true;
                _dfsD(building, visited, index + 1);
                building.remove(index);
                visited[i] = false;
            }
        }
    }

    /**
     * A(n,k)，无重复元素的排列
     * 方法2: 巧妙地通过(交换)来实现dfs/backtrace
     * 组合: 不强制顺序, dfs有start(不需要visited), 不可使用交换数组实现
     * 排列: 不强制顺序, dfs无start(从0开始, 需要visited), 或者可使用交换数组实现
     *
     * @param building 通过(交换+index)来实现构建/回溯
     * @param index    表示正在填充的索引
     */
    private static void _dfsDBySwap(List<Integer> building, int index) {
        if (index == k) {
            res.add(new ArrayList<>(building));
            return;
        }
        // 【i从index开始】, 因为building在index前的元素表示已经填充了的(通过交换过去的)
        for (int i = index; i < n; i++) {
            // 通过(交换+index)来实现构建/回溯
            Collections.swap(building, i, index);
            _dfsDBySwap(building, index + 1);
            Collections.swap(building, i, index);
        }
    }

    /**
     * A(n,k)，有重复元素的排列
     * 组合：不强制顺序, dfs有start(不需要visited), 不可使用交换数组实现
     * 排列: 不强制顺序, dfs无start(从0开始, 需要visited), 或者可使用交换数组实现
     * 对去重的理解:
     * 限制会发生在两种情况下：
     * (1) 在【同一层】做限制(idx相同), 也就是在循环中的i++的时候
     * (2) 在【更高层】做限制(idx不同), 也就是同一个i，但是通过调用回溯/dfs进入了下一个idx
     * 1. 需要先排序, 这样重复的元素就在一起
     * 2. 方法：
     * 方法1：使用visited数组，在选择每个【元素前】【判断每个元素该不该选】达到去重效果
     * 实际上是在【更高层】做限制(idx不同): {@link #_dfsPre(List, boolean[], int)}
     * 方法2：使用visited数组，在选择某个【元素后】通过【跳过重复元素】达到去重效果
     * 实际上实在【同一层】做限制(idx相同): {@link #_dfsPost(List, boolean[], int)}
     * 尾部去重: {@link veinthrough.leetcode.sum.AnySum#_dfsWithLoop(int, int, List)}
     * 尾部去重: {@link Combination#_dfs(List, int, int)}
     * 方法3：模仿{@link #arrangementD(int[], int)}的方法2，通过(交换)来实现dfs/backtrace，实际上这种做法是【错误的】
     */
    private static List<List<Integer>> arrangement(int[] nums, int k) {
        // init
        res = new LinkedList<>();
        Arrangement.nums = nums;
        Arrangement.k = k;

        // boundary
        if (Arrangement.nums == null || (n = Arrangement.nums.length) == 0) return res;
        if (k == 0) {
            res.add(new ArrayList<>(0));
            return res;
        }
        if (n == 1 && k == 1) {
            res.add(Collections.singletonList(Arrangement.nums[0]));
            return res;
        }

        // sort then backtrace
        Arrays.sort(Arrangement.nums);
        // 方法1
        _dfsPre(new ArrayList<>(k), new boolean[n], 0);
        // 方法2
//        _dfsPost(new ArrayList<>(k), new boolean[n], 0);
        // 方法3：通过(交换)来实现dfs/backtrace，实际上这种做法是错误的
//        List<Integer> numsList = Arrays.stream(Arrangement.nums)
//                .sorted()
//                .boxed()
//                .collect(Collectors.toList());
//        _dfsBySwap(numsList, 0); // 通过(交换+idx)来实现构建/回溯，使用原列表
        return res;
    }

    /**
     * A(n,k)，有重复元素的排列
     * 组合：不强制顺序, dfs有start(不需要visited), 不可使用交换数组实现
     * 排列: 不强制顺序, dfs无start(从0开始, 需要visited), 或者可使用交换数组实现
     * 方法1：使用visited数组，在选择每个【元素前】【判断每个元素该不该选】达到去重效果；
     * (1) 类似于组合{@link Combination#_dfsD(List, int, int)}，
     * 通过限定【各个元素顺序】(从小到大)来达到去重效果(实际上组合本质上也是在排列中去重)
     * (2) 这里也是通过限定【相同元素顺序】来达到去重效果
     *
     * 对去重的理解:
     * 1. 限制会发生在两种情况下：
     * (1) 在同一层做限制(idx相同), 也就是在循环中的i++的时候
     * (2) 在更高层做限制(idx不同), 也就是同一个i，但是通过调用回溯/dfs进入了下一个idx
     * 但是这里的限制只会有(2)的限制, 因为在(1)中i++之前，回溯/dfs返回已经对visited[i-1]进行了重置
     *
     * 2.使用!visited[i - 1]不会出现重复，但是使用visited[i - 1]在【非全排列】的情况下会重复：
     *
     * #    0   1   2   3   4   5   6
     * #    1   2   2   3   3   3   4
     * #              3-3 3-4 3-5
     * ----------------------------
     * !visited[i - 1]来去重主要是通过限制一下两个相邻的重复数字的访问顺序：
     * 只有当visit nums[i-1]之后我们才去visit nums[i]， 也就是如果!visited[i-1]的话则continue
     * (1) 对于全排列，也就是3个3必须都出现, 因为限定了顺序，那么出现时一定是(3-3, 3-4, 3-5)，
     * 因为3-3不出现3-4就不会出现, 3-4不出现3-5也不会出现
     * (2) 对于非全排列，比如选2个3，那么出现时一定是(3-3,3-4)， 不可能出现(3-3,3-5)/(3-4,3-3)/(3-4,3-5)/(3-5,3-3)/(3-5,3-4),
     * 也不会出现重复
     * ----------------------------
     * 但是如果这里使用visited[i-1]限定访问顺序，就不一样：
     * 只有当没有visit nums[i-1]我们才去visit nums[i]， 也就是如果visited[i-1]的话则continue
     * (1) 对于全排列，也就是3个3必须都出现, 因为限定了顺序，那么出现时一定是(3-5, 3-4, 3-3)，
     * 因为3-3如果如果先出现的化3-4就不会出现, 3-4先出现3-5也不会出现；
     * (2) 对于非全排列，比如选2个3，那么出现时一定是(3-3,3-5)/(3-4,3-3)/(3-5,3-3)/(3-5,3-4)， 只有(3-3,3-4)/(3-4,3-5)不会出现,
     * 所以会出现重复
     *
     * @param building 通过增减元素来实现构建/回溯
     *                 而{@link #_dfsBySwap(List, int)}通过(交换+idx)来实现构建/回溯
     * @param idx      表示正在填充的索引
     */
    private static void _dfsPre(List<Integer> building, boolean[] visited, int idx) {
        if (idx == k) {
            res.add(new ArrayList<>(building));
            return;
        }

        // i从0开始
        for (int i = 0; i < n; i++) {
            // 去重使用!visited[i-1]而不能使用visited[i-1]
            // 因为visited[i-1]在非全排列情况下会重复
            if (visited[i] ||
                    i > 0 && nums[i] == nums[i - 1] && !visited[i - 1]) continue;

            building.add(nums[i]); // 放入值
            visited[i] = true;
            _dfsPre(building, visited, idx + 1);
            building.remove(idx); // 回退坐标
            visited[i] = false;
        }
    }

    /**
     * A(n,k)，有重复元素的排列
     * 方法2：使用visited数组，在选择某个【元素后】通过【跳过重复元素】达到去重效果
     * 组合：不强制顺序, dfs有start(不需要visited), 不可使用交换数组实现
     * 排列: 不强制顺序, dfs无start(从0开始, 需要visited), 或者可使用交换数组实现
     *
     * 对去重的理解:
     * 限制会发生在两种情况下：
     * (1) 在【同一层】做限制(idx相同), 也就是在循环中的i++的时候
     * (2) 在【更高层】做限制(idx不同), 也就是同一个i，但是通过调用回溯/dfs进入了下一个idx
     * 这里是使用(1)来去重
     */
    private static void _dfsPost(List<Integer> building, boolean[] visited, int index) {
        if (index == k) {
            res.add(new ArrayList<>(building));
            return;
        }
        
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                building.add(nums[i]);
                visited[i] = true;
                _dfsPost(building, visited, index + 1);
                // 这里应该使用remove(index)而不应该使用building.remove(new Integer(nums[i])),
                // 否则会删除第一个匹配的元素
                // 比如: building[2,1,2], 回溯的时候本来想删除最后一个2变成building[2,1]
                // 但是实际上删除了第一个2变成了[1,2]
//                building.remove(new Integer(nums[i]));
                building.remove(index);
                visited[i] = false;
                // ignore duplicates
                while (i + 1 < n && nums[i + 1] == nums[i]) i++;
            }
        }
    }

    /**
     * A(n,k)，有重复元素的排列
     * 方法3【错误】：模仿{@link #arrangementD(int[], int)}的方法2，通过(交换)来实现dfs/backtrace，实际上这种做法是错误的：
     * 1. 通过(交换+idx)来实现构建/回溯
     * 2. 在完成(idx处填充i, idx之后进行递归全排列填充)后,
     * (1) 回溯, idx处复原, 也就是交换回来
     * (2) 准备在idx处填充i+1, 但是如果nums[i+1]==numsList[i]， 将忽略来达到去除重复的效果,
     * 依据排序状态的去重: {@link veinthrough.leetcode.sum.AnySum#_dfsWithLoop(int, int, List)}
     *
     * 但是这里会出现错误, 这里不能使用(交换+idx)来实现回溯,
     * 因为交换会导致排序失效, 那么【依据排序状态的去重也就失效了】
     * 比如对于{2, 2, 3, 3, 4}来说，结果为：
     * ------------2开头的12个--------------------------
     * [2, 2, 3, 3, 4], [2, 2, 3, 4, 3], [2, 2, 4, 3, 3],
     * [2, 3, 2, 3, 4], [2, 3, 2, 4, 3], [2, 3, 3, 2, 4], [2, 3, 3, 4, 2], [2, 3, 4, 3, 2], [2, 3, 4, 2, 3],
     * [2, 4, 3, 3, 2], [2, 4, 3, 2, 3], [2, 4, 2, 3, 3],	// 12
     * ------------3开头的14个, 多了两个----------------
     * [3, 2, 2, 3, 4], [3, 2, 2, 4, 3], [3, 2, 3, 2, 4], [3, 2, 3, 4, 2], [3, 2, 4, 3, 2], [3, 2, 4, 2, 3],
     * [3, 3, 2, 2, 4], [3, 3, 2, 4, 2], [3, 3, 4, 2, 2],
     * --------这里0填充3(通过交换坐标0和2来实现), 1填充4(通过交换坐标1和3实现), 初始状态就是[3, 4, 2, 3, 2]
     * [3, 4, 2, 3, 2],----2填充2(第一个2),
     * 初始状态, 可以发现交换之后第一个2和第二个2分离了, 数组不再是排序状态,
     * 那么【依据排序状态的去重就失效了】, 导致后面出现了重复
     * [3, 4, 2, 2, 3],----2填充2(第一个2),
     * [3, 4, 3, 2, 2],----2填充3(第二个3),
     * [3, 4, 2, 3, 2],----出现重复, 试图在2填充2(第二个2)， 所以重复
     * [3, 4, 2, 2, 3],----出现重复, 试图在2填充2(第二个2)， 所以重复
     * -----------4开头的11个, 多了5个-------------------
     * [4, 2, 3, 3, 2], [4, 2, 3, 2, 3], [4, 2, 2, 3, 3],
     * [4, 3, 2, 3, 2], 4, 3, 2, 2, 3], [4, 3, 3, 2, 2],
     * [4, 3, 2, 3, 2],----出现重复
     * [4, 3, 2, 2, 3],----出现重复
     * [4, 2, 3, 3, 2],----出现重复
     * [4, 2, 3, 2, 3],----出现重复
     * [4, 2, 2, 3, 3],----出现重复
     * 综上所述, 不能使用交换来实现回溯
     *
     * @param building 通过(交换+idx)来实现构建/回溯
     * @param idx      表示正在填充的索引
     */
    @SuppressWarnings("unused")
    private static void _dfsBySwap(List<Integer> building, int idx) {
        // 通过(交换+idx)来实现构建/回溯时，idx==n-1实际上就已经完成填充了,
        // 因为building的元素一直没有变化只是被交换了, 最后一个元素已经在位置上了
        if (idx == k - 1) {
            res.add(new ArrayList<>(building));
            return;
        }
        // 2. backtrace
        // 【i从idx开始】, 因为building在idx前的元素表示已经填充了的(通过交换过去的)
        for (int i = idx; i < n; i++) {
            // 通过(交换+idx)来实现构建/回溯
            Collections.swap(building, i, idx);
            _dfsBySwap(building, idx + 1);
            Collections.swap(building, i, idx);
            // 【依据排序状态的去重】
            while (i + 1 < n && building.get(i + 1).equals(building.get(i))) i++;
        }
    }
}
