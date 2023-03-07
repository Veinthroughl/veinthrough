package veinthrough.leetcode.sum;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 总结：
 * 情况1. 数组元素中有相同，
 * {@link #anySum(int[], int)}
 * (1) 实现方式1：使用for循环来递归，for循环可以代替部分分支的递归，且可以过滤掉相同的情况(排序是剪枝/过滤的前提)
 * {@link #_dfsWithLoop(int, int, List)}
 * (2) 实现方式2：dfs中不使用循环
 * {@link #_dfsWithoutLoop(int, int, List)}
 *
 * 情况2. 数组元素不相同，但是数组元素可以随便用
 * (1) 转化成情况1
 * {@link #anySumDBy_D(int[], int)}
 * (2) 整个搜索过程用一个树来表达，每次的搜索都会延伸出两个分叉，直到递归的终止条件，这样我们就能不重复且不遗漏地找到所有可行解
 * {@link  #anySumD(int[], int)}
 *
 * 1. 第39题【情况2】：组合总和
 * 给定一个无重复元素的数组candidates和一个目标数target，找出candidates中所有可以使数字和为target的组合。
 * candidates中的数字可以无限制重复被选取。
 * 说明：
 * 所有数字（包括target）都是正整数。
 * 解集不能包含重复的组合。
 * 方法1：{@link #anySumDBy_D(int[], int)}
 * 方法2：{@link #anySumD(int[], int)}
 *
 * 2. 第40题【情况1】：组合总和
 * 给定一个数组candidates和一个目标数target，找出candidates中所有可以使数字和为target的组合。
 * candidates中的每个数字在每个组合中只能使用一次。
 * 说明：
 * 所有数字（包括目标数）都是正整数。
 * 解集不能包含重复的组合。
 * {@link  #anySum(int[], int)}
 *
 * 3. 编写一个函数，传入一个int型数组，返回该数组能否分成两组，使得两组中各元素加起来的和相等，
 * 并且，所有5的倍数必须在其中一个组中，所有3的倍数在另一个组中（不包括5的倍数），
 * 能满足以上条件，返回true；不满足时返回false。
 * {@link  #canSplit(int[])}
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class AnySum {
    private int N, target;
    private int[] nums;
    private List<int[]> lists;

    @Test
    public void test() {
        Stream.of(new int[]{3, 5, 20, 9, 1, 14},
                new int[]{3, 5, 20, 9, 1, 13},
                new int[]{3, 5, 20, 9, 4, 17})
                .forEach(nums -> log.info(methodLog(
                        Arrays.toString(nums), "" + canSplit(nums))));
    }

    /**
     * 情况1. 数组元素中有相同
     */
    @Test
    public void test40() {
        Stream.of(anySum(new int[]{10, 1, 2, 7, 6, 1, 5}, 8), // [[7, 1], [6, 2], [6, 1, 1], [5, 2, 1]]
                anySum(new int[]{2, 5, 2, 1, 2}, 5)) // [[5], [2, 2, 1]]
                .forEach(arrays -> log.info(methodLog(
                        arrays.stream()
                                .map(Arrays::toString)
                                .collect(Collectors.toList())
                                .toString())));
    }

    /**
     * 情况2. 数组元素不相同，但是数组元素可以随便用,
     * 方法1：转化成情况1
     */
    @Test
    public void test39() {
        Stream.of(anySumDBy_D(new int[]{2, 3, 6, 7}, 7), // [[7], [3, 2, 2]]
                anySumDBy_D(new int[]{2, 3, 5}, 8)) // [[5, 3], [3, 3, 2], [2, 2, 2, 2]]
                .forEach(arrays -> log.info(methodLog(
                        arrays.stream()
                                .map(Arrays::toString)
                                .collect(Collectors.toList())
                                .toString())));
    }

    /**
     * 情况2. 数组元素不相同，但是数组元素可以随便用,
     * 方法2: 整个搜索过程用一个树来表达，每次的搜索都会延伸出两个分叉，直到递归的终止条件
     */
    @Test
    public void test39_2() {
        Stream.of(anySumD(new int[]{2, 3, 6, 7}, 7), // [[7], [3, 2, 2]]
                anySumD(new int[]{2, 3, 5}, 8)) // [[5, 3], [3, 3, 2], [2, 2, 2, 2]]
                .forEach(arrays -> log.info(methodLog(
                        arrays.stream()
                                .map(Arrays::toString)
                                .collect(Collectors.toList())
                                .toString())));
    }


    /**
     * 情况1，数组元素中有相同：
     * 先逆序排序，然后使用for循环来递归，for循环可以代替部分分支的递归，且可以过滤掉相同的情况(排序是剪枝/过滤的前提)
     * 实现方式1：dfs中使用循环
     * 实现方式2：dfs中不使用循环
     */
    private List<int[]> anySum(int[] nums, int target) {
        this.nums = Arrays.stream(nums)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .mapToInt(Integer::intValue)
                .toArray();
        this.lists = new LinkedList<>();
        this.target = target;
        this.N = nums.length;
        // 实现方式1：dfs中使用循环
//        _dfsWithLoop(0, 0, new LinkedList<>());
        // 实现方式2：dfs中不使用循环
        _dfsWithoutLoop(0, 0, new LinkedList<>());
        return this.lists;
    }

    /**
     * 情况1，数组元素中有相同：
     * 实现方式1：dfs中使用循环
     * (1) 使用for循环来递归，for循环可以代替部分分支的递归，
     * (2) 尾去重：在dfs尾部过滤掉相同的情况(排序是剪枝/过滤的前提)
     */
    public void _dfsWithLoop(int start, int sum, List<Integer> list) {
        // 使用for循环来递归，for循环可以代替部分分支的递归
        for (int i = start; i < N; i++) {
            sum += nums[i];
            list.add(nums[i]);
            if (sum < target)
                // (1) recurse1： 使用i
                _dfsWithLoop(i + 1, sum, list);
            else if (sum == target)
                // (2) found one
                lists.add(list.stream().mapToInt(Integer::intValue).toArray());

            // (3) backtrace
            sum -= nums[i];
            // 注意index/Object混淆的情况
            list.remove(new Integer(nums[i]));
            // (4) ignore duplicates, 【尾去重】【同一层】
            while (i < N - 1 && nums[i] == nums[i + 1]) i++;
            // (5) recurse2(尾递归)： 不使用i，这里被循环代替了
        }
    }

    /**
     * 情况1，数组元素中有相同：
     * 实现方式2：dfs中不使用循环
     * (1) 不使用for循环来递归，类似于(2^n)的子集({@link veinthrough.leetcode.ac.Subset#_dfsD(List, int)})的方式
     * (2) 尾去重：在dfs尾部过滤掉相同的情况(排序是剪枝/过滤的前提)
     */
    private void _dfsWithoutLoop(int start, int sum, List<Integer> list) {
        if (start < N) {
            sum += nums[start];
            list.add(nums[start]);
            if (sum < target)
                // (1) recurse1： 使用i
                _dfsWithoutLoop(start + 1, sum, list);
            else if (sum == target)
                // (2) found one
                lists.add(list.stream().mapToInt(Integer::intValue).toArray());
            // (3) backtrace
            sum -= nums[start];
            // 注意index/Object混淆的情况
            list.remove(new Integer(nums[start]));
            // (4) ignore duplicates
            while (start < N - 1 && nums[start] == nums[start + 1]) start++;
            // (5) recurse2(尾递归)： 不使用i
            _dfsWithoutLoop(start + 1, sum, list);
        }
    }

    /**
     * 情况2，数组元素不相同，但是数组元素可以随便用：
     * 方法1: 转化成情况1
     */
    private List<int[]> anySumDBy_D(int[] distinctNums, int target) {
        Arrays.sort(distinctNums);
        int len = distinctNums.length;
        List<Integer> nums = new LinkedList<>();
        // sort: from large --> small
        // 转化成相同元素的情况
        for (int i = len - 1; i >= 0; i--)
            nums.addAll(Collections.nCopies(target / distinctNums[i], distinctNums[i]));
        // 使用情况1
        this.nums = nums.stream().mapToInt(Integer::intValue).toArray();
        this.lists = new LinkedList<>();
        this.target = target;
        this.N = this.nums.length;
        _dfsWithLoop(0, 0, new LinkedList<>());
        return this.lists;
    }

    /**
     * 情况2. 数组元素不相同，但是数组元素可以随便用
     * 方法2: 整个搜索过程用一个树来表达，每次的搜索都会延伸出两个分叉，直到递归的终止条件，
     * 这样我们就能不重复且不遗漏地找到所有可行解
     * 类似于(2^n)的子集({@link veinthrough.leetcode.ac.Subset#_dfsD(List, int)})的方式
     */
    public List<int[]> anySumD(int[] nums, int target) {
        this.nums = Arrays.stream(nums)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .mapToInt(Integer::intValue)
                .toArray();
        this.lists = new LinkedList<>();
        this.target = target;
        this.N = nums.length;
        _dfsD(0, 0, new LinkedList<>());
        return this.lists;
    }

    /**
     * 情况2. 数组元素不相同，但是数组元素可以随便用
     * 方法2: 整个搜索过程用一个树来表达，每次的搜索都会延伸出两个分叉，直到递归的终止条件，
     * 这样我们就能不重复且不遗漏地找到所有可行解
     * 实现方式1: 先不使用后再使用
     */
    private void _dfsD(int start, int sum, List<Integer> list) {
        if (start < N) {
            // 1. recurse1: 不使用start
            _dfsD(start + 1, sum, list);

            // 2. recurse2: 使用start
            sum += nums[start];
            list.add(nums[start]);
            if (sum == target)
                // (1) found, 结果输出用ArrayList
                lists.add(list.stream().mapToInt(Integer::intValue).toArray());
            else if (sum < target)
                // (2) dfs, 这里仍然是start而不是start+1
                _dfsD(start, sum, list);
            // 3. backtrace
            list.remove(new Integer(nums[start]));
            // 4. 不需要ignore duplicates
        }

    }

    /**
     * 现在需要让arr3和arr5两个数组中的和相等，就需要将other数组中的数分配到两个数组中，
     * 假设分配x到arr5中，分配y到arr3数组中，那么需要满足如下关系式。需要满足公式：
     * 1:sum5 + x = sum3 + y
     * 2:x + y = sumOther
     * 转换得到：
     * 1：sum5 - sum3 = y - x
     * 2：sumOther    = x + y
     * 两个公式相加：sum5 - sum3 + sumOther = 2*y
     * 进一步可以推出：
     * y = (sum5 - sum3 + other)/2
     * sum5，sum3，other这三个数已知，可以求出y的值。也就是说，需要在other数组中找出一些数，其值为y。可以使用深度优先搜索算法进行处理。
     * 因为数都是整数，所以对于y为浮点数的情况可以直接返回false。
     */
    private boolean canSplit(int[] nums) {
        // may use long instead of int?
        int sum5 = 0, sum3 = 0, sumOther = 0;
        List<Integer> others = new LinkedList<>();
        for (int num : nums) {
            if (num % 5 == 0)
                sum5 += num;
                // 这里已经除掉同时是5的倍数
            else if (num % 3 == 0)
                sum3 += num;
            else {
                sumOther += num;
                others.add(num);
            }
        }
        int y2 = sum5 - sum3 + sumOther;
        if (y2 % 2 != 0)
            return false;
        else
            return !anySum(others.stream().mapToInt(Integer::intValue).toArray(), y2 / 2).isEmpty();
    }
}
