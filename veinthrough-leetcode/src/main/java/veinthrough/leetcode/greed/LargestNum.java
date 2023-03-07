package veinthrough.leetcode.greed;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static veinthrough.api.math.Math.max;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第179题: 数字最大组合数
 * 给定一组非负整数 nums，重新排列每个数的顺序（每个数不可拆分）使之组成一个最大的整数。
 * 注意：输出结果可能非常大，所以你需要返回一个字符串而不是整数。
 * 示例 1：
 * 输入：nums = [10,2]
 * 输出："210"
 *
 * 示例 2：
 * 输入：nums = [3,30,34,5,9]
 * 输出："9534330"
 */
@Slf4j
public class LargestNum {
    @Test
    public void test179() {
        Stream.of(
                new int[]{3, 30, 34, 5, 9}, // 9534330
                new int[]{10, 2}, // 210
                new int[]{1}) // 1
                .forEach(nums ->
                        Stream.<Function<int[], String>>of(
                                this::largestNumber, this::largestNumber2, this::largestNumber3)
                                .forEach(function ->
                                        log.info(methodLog(function.apply(nums)))));
    }

    /**
     * 方法1(自解): 【错误】
     * 试图转化成相同的位数再比较：
     * 例子：3, 30, 34, 5, 9  -->  30, 30, 34, 50, 90
     * 这里30/30就重复了且无法比较
     */
    private String largestNumber(int[] nums) {
        // 1.
        if (nums.length == 1) return "" + nums[0];

        // (1) 获取max并计算它是几位数
        int max = max(nums) / 10;
        int maxDigits = 0;
        while (max > 0) {
            maxDigits++;
            max /= 10;
        }
        // (2) 把其他数提升到max对应的位数
        // 并且建立map
        int[] numsLifted = Arrays.copyOf(nums, nums.length);
        Map<Integer, Integer> liftedToNum = new HashMap<>(nums.length);
        for (int i = 0, num; i < nums.length; i++) {
            num = nums[i] / 10;
            for (int j = 0; j < maxDigits; j++) {
                if (num > 0) num /= 10;
                else numsLifted[i] *= 10;
            }
            liftedToNum.put(numsLifted[i], nums[i]);
        }

        // (3) sort
        numsLifted = Arrays.stream(numsLifted)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .mapToInt(Integer::intValue)
                .toArray();

        // (4)
        StringBuilder str = new StringBuilder();
        for (int num : numsLifted)
            str.append(liftedToNum.get(num));

        return str.toString();
    }

    /**
     * 方法2(题解): 贪心解法
     * 对于nums中的任意两个值a和b，我们无法直接从常规角度上确定其大小/先后关系。
     * 但我们可以根据「结果」来决定a和b的排序关系：
     * 如果拼接结果ab要比ba好，那么我们会认为a应该放在b前面。
     */
    @SuppressWarnings("Duplicates")
    String largestNumber2(int[] _nums) {
        // 1. int -> string
        // and sort
        String[] nums = Arrays.stream(_nums)
                .boxed()
                .map(num -> "" + num)
                .sorted((s1, s2) -> (s2 + s1).compareTo(s1 + s2)) // 因为默认是从小到大排序
                .toArray(String[]::new);
        // 2. joint string[] to string
        StringBuilder result = new StringBuilder();
        for (String str : nums)
            result.append(str);

        // 3. 除非都是0， 否则首位不可能是0
        return result.charAt(0) == '0' ? "0" : result.toString();
    }

    /**
     * 方法3(测试[CE][CounterExample]): 贪心解法
     * 1. 字符串的比较结果确实是按第一位
     * "3".compareTo("30") = -1
     * "3".compareTo("03") = 3
     * 2. 例子
     * {3, 30, 34, 5, 9}
     * 这里不对, ‘30’比'3'大, 会排在'3'前面
     * (1) {@link #largestNumber2(int[])}
     * sorted((s1, s2) -> (s2 + s1).compareTo(s1 + s2)): 9534330
     * (2) sorted(Comparator.reverseOrder()): 9534303
     */
    @SuppressWarnings("Duplicates")
    private String largestNumber3(int[] _nums) {
        // 1. int -> string
        // and sort
        String[] nums = Arrays.stream(_nums)
                .boxed()
                .map(num -> "" + num)
                .sorted(Comparator.reverseOrder()) // 这里不对, ‘30’会排在'3'前面
                .toArray(String[]::new);
        // 2. joint string[] to string
        StringBuilder result = new StringBuilder();
        for (String str : nums)
            result.append(str);

        // 3. 除非都是0， 否则首位不可能是0
        return result.charAt(0) == '0' ? "0" : result.toString();
    }
}
