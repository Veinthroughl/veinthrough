package veinthrough.leetcode.ac;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.leetcode.sum.AnySum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 无重复元素的子集，
 * 有重复元素的子集
 *
 * 相对于排列/组合：
 * 1. dfs内的index不是表示要填充的位置，而是表示正对nums[index]处理
 * 2. dfs内没有使用循环，但是会递归调用两次dfs;
 * 这是由1决定的:
 * (1) 没有循环，index不是表示要填充的位置，不需要一个循环来枚举这个位置可以填的值
 * (2) 表示正对nums[index]处理，那么通过递归调用两次dfs来表示是不是用这个nums[index]
 * 3. 鉴于这种(2^n)的递归，有大量重复的计算，可以使用迭代(动态规划)
 *
 * 第78题：无重复元素子集，
 * 给你一个整数数组 nums ，数组中的元素互不相同。返回该数组所有可能的子集（幂集）。
 * 解集 不能 包含重复的子集。你可以按 任意顺序 返回解集。
 */
@SuppressWarnings({"Duplicates", "unused"})
@Slf4j
public class Subset {
    private static int n;
    private static int[] nums;
    private static List<List<Integer>> res;

    @Test
    public void testSubsetD() {
        Stream.of(
                new int[]{1},
                new int[]{1, 2},
                new int[]{1, 2, 3},
                new int[]{1, 2, 3, 4})
                .forEach(nums ->
                {
                    subsetD(nums);
                    log.info(methodLog(
                            String.format("Subset of %s", Arrays.toString(nums)),
                            "" + res.size() + res));
                });
    }

    @Test
    public void test78() {
        testSubsetD();
    }

    @Test
    public void testSubset() {
        Stream.of(
                new int[]{1, 2, 2, 3, 3}, // 18
                new int[]{1, 2, 2, 3, 3, 4}) // 36
                .forEach(nums ->
                {
                    subset(nums);
                    log.info(methodLog(
                            String.format("Subset of %s", Arrays.toString(nums)),
                            "" + res.size() + res));
                });
    }


    /**
     * 无重复元素子集：
     * 方法1：使用dfs/backtrace(2^n)
     * 方法2：使用Combination
     * 方法3: 鉴于方法1中有很多重复计算，可以使用动态规划
     */
    private static void subsetD(int[] nums) {
        // boundary

        //
        Subset.nums = nums;
        Subset.n = nums.length;
        res = new ArrayList<>((int) Math.pow(2, n));
        // 方法1：使用dfs/backtrace(2^n)
        _dfsD(new ArrayList<>(n), 0);
        // 方法2：使用Combination
//        for (int i = 0; i <= n; i++) {
//            res.addAll(comb(nums, i));
//        }
        // 方法3: 鉴于方法1中有很多重复计算，可以使用动态规划(迭代)
//        res = _iterateD(nums);
    }

    /**
     * 无重复元素子集方法1：
     * 1. 使用dfs/backtrace(2^n)，和无限制的sum({@link AnySum#anySumD(int[], int)})差不多
     * 2. 这种方式会有重复计算, 可以使用动态规划
     *
     * @param index 注意这里的index不是表示要填充的位置，而是表示对nums[index]处理
     */
    public static void _dfsD(List<Integer> building, int index) {
        if (index == n) {
            res.add(new ArrayList<>(building));
            return;
        }
        // use/don't use会重复计算后面的, 可以用动态规划
        // (1) use nums[index]
        building.add(nums[index]);
        _dfsD(building, index + 1);
        // 这里不能使用building.remove(index), 因为这里的index不是表示要填充的位置，而是表示正对nums[index]处理
        building.remove(new Integer(nums[index]));

        // (2) don't use nums[index]
        _dfsD(building, index + 1);
    }


    /**
     * 无重复元素子集方法3:
     * 鉴于方法1中有很多重复计算，使用动态规划，
     * 思想就是给每个长度为i-1的子集(lastList)添加一个元素成为长度为i的子集(newList)，
     * 这里有两个层面的去重：
     * (1) 通过顺序来去重，只会逐个添加(i-1的子集的最后一个元素)后面的一个元素
     * 组合(强制顺序(【更高层】做限制))：index+1的坐标必须>index的坐标, 所以index选i, index+1只能从i+1开始选,
     * (2) 当添加一个元素的时候， 使用尾去重(【同一层】做限制)
     */
    private static List<List<Integer>> _iterateD(int[] nums) {
        List<List<Integer>> res = new ArrayList<>((int) Math.pow(2, n));
        List<List<Integer>> subsets, lastSubsets, tmp; // 为了便于操作，填充的是坐标
        // 1. 空集
        res.add(new ArrayList<>(0));
        // 2. 1个元素
        List<Integer> list;
        lastSubsets = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            list = new LinkedList<>();
            list.add(i);
            lastSubsets.add(list);
            res.add(list);
        }
        // 3. >=2个元素
        subsets = new LinkedList<>();
        List<Integer> newList;
        for (int i = 2; i <= n; i++) {
            // 3.(1) 给每个长度为i-1的子集(lastList)添加一个元素成为长度为i的子集(newList)
            for (List<Integer> lastList : lastSubsets) {
                // 最后一个元素
                Integer last = lastList.get(lastList.size() - 1);
                // 根据最后一个元素添加一个元素
                for (int j = last + 1; j < n; j++) {
                    newList = new LinkedList<>(lastList);
                    newList.add(j);
                    subsets.add(newList);
                }
            }
            // 3.(2) 把长度为i的所有子集加入res
            res.addAll(subsets);
            // 3.(3) 交换lastSubsets/subsets
            tmp = lastSubsets;
            lastSubsets = subsets;
            subsets = tmp;
            // 3.(4) 清空subsets
            subsets.clear();
        }
        // 4. 之前填充的是坐标，转化成值
        return res.stream()
                .map(subset -> subset.stream()
                        .map(i -> nums[i])
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * 有重复元素子集：
     * 方法1：使用dfs/backtrace
     * 方法2：使用Combination
     * 方法3: 鉴于方法1中有很多重复计算，可以使用动态规划
     */
    private static void subset(int[] nums) {
        Subset.nums = nums;
        n = nums.length;
        res = new LinkedList<>();
        // 方法1: 使用dfs/backtrace
//        _dfs(new ArrayList<>(n), 0);
        // 方法2：使用Combination
//        for (int i = 0; i <= n; i++) {
//            res.addAll(comb(nums, i));
//        }
        // 方法3: 鉴于方法1中有很多重复计算，可以使用动态规划(迭代)
        res = _iterate(nums);
    }

    /**
     * 有重复元素子集方法1：
     * 1. 使用dfs/backtrace(2^n)，和无限制的sum({@link AnySum#anySumD(int[], int)})差不多
     * 2. 相对于无重复元素
     * (1) 无重复元素({@link #_dfsD(List, int)})：对于某个元素nums[i], 可以选择/不选择(0个)
     * (2) 有重复元素：对于某个元素，可以选择(0...count(nums[i]))个
     * 3. 这种方式会有重复计算, 可以使用动态规划
     *
     * @param index 注意这里的index不是表示要填充的位置，而是表示对nums[index]处理
     */
    private static void _dfs(List<Integer> building, int index) {
        if (index == n) {
            res.add(new ArrayList<>(building));
            return;
        }

        // (1) use 1...count of nums[index]
        int nextIndex = index;
        //noinspection StatementWithEmptyBody
        while (++nextIndex < n && nums[nextIndex] == nums[index]) ;
        // 这里会有重复计算, 可以用动态规划
        for (int i = index; i < nextIndex; i++) {
            building.add(nums[index]); // 每次都会累加一个
            _dfs(building, nextIndex); // 每个接下来处理的都是nextIndex
        }
        // remove all
        building.removeIf(element -> element.equals(nums[index]));
        // 使用remove只会删除第一个等于nums[index]的元素
//        building.remove(new Integer(nums[index]));

        // (2) don't use nums[index]
        _dfs(building, nextIndex);
    }

    /**
     * 有重复元素子集方法3:
     * 鉴于方法1中有很多重复计算，使用动态规划，
     * 思想就是给每个长度为i-1的子集(lastList)添加一个元素成为长度为i的子集(newList)，
     * 1. 这里有两个层面的去重：
     * (1) 通过顺序来去重，只会逐个添加(i-1的子集的最后一个元素)后面的一个元素
     * 组合(强制顺序(【更高层】做限制))：index+1的坐标必须>index的坐标, 所以index选i, index+1只能从i+1开始选,
     * (2) 当添加一个元素的时候， 使用尾去重(【同一层】做限制)
     * 2. 相对于无重复元素子集：
     * (1) 需要排序
     * (2) 处理元素的时候需要分开处理并ignore duplicates
     */
    private static List<List<Integer>> _iterate(int[] nums) {
        List<List<Integer>> res = new ArrayList<>((int) Math.pow(2, n));
        List<List<Integer>> subsets, lastSubsets, tmp; // 为了便于操作，填充的是坐标
        // 1. sort, 相对于无重复元素子集
        Arrays.sort(nums);
        // 2. 空集
        res.add(new ArrayList<>(0));
        // 3. 1个元素， 相对于无重复元素子集：
        // 需要分开处理(0/1...)并ignore duplicates
        List<Integer> list;
        lastSubsets = new LinkedList<>();
        // 0
        list = new LinkedList<>();
        list.add(0);
        lastSubsets.add(list);
        res.add(list);
        // 1...
        for (int i = 1; i < n; i++) {
            // ignore duplicates
            if (nums[i] != nums[i - 1]) {
                list = new LinkedList<>();
                list.add(i);
                lastSubsets.add(list);
                res.add(list);
            }
        }
        // 4. >=2个元素
        subsets = new LinkedList<>();
        for (int i = 2; i <= n; i++) {
            // 4.(1) 给每个长度为i-1的子集(lastList)添加一个元素成为长度为i的子集(newList)
            for (List<Integer> lastList : lastSubsets) {
                // 最后一个元素
                Integer last = lastList.get(lastList.size() - 1);
                // 根据最后一个元素添加一个元素相对于无重复元素子集：
                // 需要分开处理(last+1/last+2...)来通过尾去重ignore duplicates
                if (last + 1 < n) {
                    // last+1
                    List<Integer> newList = new LinkedList<>(lastList);
                    newList.add(last + 1);
                    subsets.add(newList);
                    // last+2...
                    for (int j = last + 2; j < n; j++) {
                        // 【ignore duplicates】
                        if (nums[j] != nums[j - 1]) {
                            newList = new LinkedList<>(lastList);
                            newList.add(j);
                            subsets.add(newList);
                        }
                    }
                }
            }
            // 4.(2) 把长度为i的所有子集加入res
            res.addAll(subsets);
            // 4.(3) 交换lastSubsets/subsets
            tmp = lastSubsets;
            lastSubsets = subsets;
            subsets = tmp;
            // 4.(4) 清空subsets
            subsets.clear();
        }
        // 5. 之前填充的是坐标，转化成值
        return res.stream()
                .map(subset -> subset.stream()
                        .map(i -> nums[i])
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }
}
