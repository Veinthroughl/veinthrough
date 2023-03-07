package veinthrough.leetcode.array;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第350题：两个数组的交集 II
 * 给定两个数组，编写一个函数来计算它们的交集。
 * 【注意】有相同元素
 */
@SuppressWarnings({"Duplicates", "unused"})
@Slf4j
public class Intersect {
    @Test
    public void test350() {
        Stream.of(
                intersectByMap(new int[]{1, 2, 2, 1}, new int[]{2, 2}),
                intersectByMap(new int[]{9, 4, 9, 8, 4}, new int[]{4, 9, 5}))
                .forEach(common ->
                        log.info(methodLog(Arrays.toString(common))));
    }

    @Test
    public void test350_2() {
        Stream.of(
                intersectByDBP(new int[]{1, 2, 2, 1}, new int[]{2, 2}),
                intersectByDBP(new int[]{9, 4, 9, 8, 4}, new int[]{4, 9, 5}))
                .forEach(common ->
                        log.info(methodLog(Arrays.toString(common))));
    }

    /**
     * 方法1：使用map，build less map
     */
    private int[] intersectByMap(int[] nums1, int[] nums2) {
        // boundary
        if (nums1.length == 0 || nums2.length == 0) return new int[]{};

        // 1. more/less
        int[] more, less;
        if (nums1.length < nums2.length) {
            more = nums2;
            less = nums1;
        } else {
            more = nums1;
            less = nums2;
        }

        // 2. build [less map]
        Map<Integer, Integer> lessMap = new HashMap<>(less.length);
        for (int num : less)
            lessMap.merge(num, 1, Integer::sum);

        // 3. traverse [more]
        int[] common = new int[less.length];
        int count = 0;
        for (int num : more) {
            Integer value = lessMap.get(num);
            if (value != null && value > 0) {
                common[count++] = num;
                lessMap.merge(num, -1, Integer::sum);
            }
            /*
            // (1) 使用int而不是Integer不会产生exception
            // 直接使用merge负面影响: 扩张map; 需要提前判定num是否存在
            int value = lessMap.merge(num, -1, Integer::sum);
            if(value>=0)
                common[count++]=num;
            */
        }

        return java.util.Arrays.copyOf(common, count);
    }

    /**
     * 方法2: 双指针(双层循环)
     * 方法3: 排序之后使用双指针
     */
    private int[] intersectByDBP(int[] nums1, int[] nums2) {
        // 1. empty
        if (nums1.length == 0 || nums2.length == 0) return new int[]{};

        // 2. more/less
        // 方法2: 双指针(双层循环)
//        if (nums1.length < nums2.length) return _intersectByDBP(nums1, nums2);
//        else return _intersectByDBP(nums2, nums1);
        // 方法3: 排序之后使用双指针
        if (nums1.length < nums2.length) return _intersectByDBPSorted(nums1, nums2);
        else return _intersectByDBPSorted(nums2, nums1);
    }

    /**
     * 方法2: 双指针(双层循环)
     */
    private int[] _intersectByDBP(int[] less, int[] more) {
        // 3. double loop
        boolean[] visited = new boolean[less.length];
        int[] common = new int[less.length];
        int count = 0;
        for (int i : more) {
            for (int j = 0; j < less.length; j++) {
                if (!visited[j] && less[j] == i) { // 有相同元素, 需要使用visited
                    visited[j] = true;
                    common[count++] = less[j];
                    if (count == less.length) return common;
                    break;
                }
            }
        }
        return java.util.Arrays.copyOf(common, count);
    }


    /**
     * 方法3: 排序之后使用双指针
     */
    private int[] _intersectByDBPSorted(int[] less, int[] more) {
        // 3. sort
        Arrays.sort(less);
        Arrays.sort(more);

        // 4. double loop
        int[] common = new int[less.length];
        int count = 0;
        int i = 0, j = 0;
        while (i < less.length && j < more.length) {
            if (less[i] == more[j]) {
                common[count++] = less[i];
                i++;
                j++;
            } else if (less[i] < more[j]) i++;
            else j++;
        }
        return java.util.Arrays.copyOf(common, count);
    }
}