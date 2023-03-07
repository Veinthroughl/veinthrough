package veinthrough.leetcode.string.substring;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 1. 第14题：最长公共前缀
 * 编写一个函数来查找字符串数组【多个字符串】中的最长公共前缀。
 * 如果不存在公共前缀，返回空字符串 ""。
 *
 * 2. 第1143题：最长公共子序列(longest common sub-sequence)
 * 参考文档《最长公共子序列》
 *
 * 3. 最长公共子串(longest common substring)
 */
@SuppressWarnings("unused")
@Slf4j
public class LCS {
    @Test
    public void test14() {
        Stream.of(
                new String[]{"flower", "flow", "flight"},
                new String[]{"dog", "racecar", "car"})
                .forEach(strs ->
                        log.info(methodLog(
                                "strs", Arrays.toString(strs),
                                "以第一个str为参照", longestCommonPrefix(strs),
                                "以最短str为参照", longestCommonPrefix2(strs))));
    }

    @Test
    public void test1143() {
        Stream.of(
                LCSubsequence("abcde", "ace"),
                LCSubsequence("abc", "abc"),
                LCSubsequence("abc", "def"),
                LCSubsequence("13456778", "357486782"))
                .forEach(lcs -> log.info(methodLog(
                        "(" + lcs.getLeft() + "," + Arrays.toString(lcs.getRight()) + ")")));
        Stream.of(
                LCSubsequence(new int[]{1, 3, 4, 5, 6, 7, 7, 8}, new int[]{3, 5, 7, 4, 8, 6, 7, 8, 2}))
                .forEach(lcs -> log.info(methodLog(
                        "(" + lcs.getLeft() + "," + Arrays.toString(lcs.getRight()) + ")")));
    }

    @Test
    public void testLCSubstring() {
        Stream.of(
                LCSubstring("acbad", "abcadf"), // [a,d]
                LCSubstring("acbaed", "abcadfcbae")) // [c,b,a,e]
                .forEach(lcs -> log.info(methodLog(
                        "(" + lcs.getLeft() + "," + Arrays.toString(lcs.getRight()) + ")")));
    }

    /**
     * 最长公共前缀方法1: 以第一个str为参照
     */
    @SuppressWarnings("Duplicates")
    private String longestCommonPrefix(String[] strs) {
        if (strs.length == 0) return "";
        if (strs.length == 1) return strs[0];

        StringBuilder commonPrefix = new StringBuilder();
        char ch;
        for (int i = 0; i < strs[0].length(); i++) {
            ch = strs[0].charAt(i);
            for (int j = 1; j < strs.length; j++) {
                // 某一个str结束/某一个str有不同字符
                if (strs[j].length() == i || strs[j].charAt(i) != ch) return commonPrefix.toString();
            }
            commonPrefix.append(ch);
        }
        return commonPrefix.toString();
    }

    /**
     * 最长公共前缀方法2: 以最短str为参照
     */
    @SuppressWarnings("Duplicates")
    private String longestCommonPrefix2(String[] strs) {
        if (strs.length == 0) return "";
        if (strs.length == 1) return strs[0];

        StringBuilder commonPrefix = new StringBuilder();
        char ch;
        int length, minLength = strs[0].length();
        boolean firstTime = true;
        for (int i = 0; i < minLength; i++, firstTime = false) {
            ch = strs[0].charAt(i);
            for (int j = 1; j < strs.length; j++) {
                // first time to calculate minLength
                if (firstTime) {
                    length = strs[j].length();
                    minLength = length < minLength ? length : minLength;
                    // 有空str/第一个字符就存在不同
                    if (length == 0 || strs[j].charAt(i) != ch) return commonPrefix.toString();
                    // 存在不相同字符
                } else if (strs[j].charAt(i) != ch) return commonPrefix.toString();
            }
            commonPrefix.append(ch);
        }
        return commonPrefix.toString();
    }

    @SuppressWarnings("Duplicates")
    static Pair<Integer, char[]> LCSubsequence(String str1, String str2) {
        if (str1 == null || str2 == null ||
                str1.length() == 0 || str2.length() == 0) return null;

        // String -> int[]
        Pair<Integer, int[]> result =
                _LCSubsequence(str1.chars().toArray(), str2.chars().toArray());
        // int[] -> String
        int[] ins = result.getRight();
        int len = ins.length;
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) chars[i] = (char) ins[i];
        return Pair.of(result.getLeft(), chars);
    }

    public static Pair<Integer, int[]> LCSubsequence(int[] nums1, int[] nums2) {
        if (nums1 == null || nums2 == null ||
                nums1.length == 0 || nums2.length == 0) return null;
        return _LCSubsequence(nums1, nums2);
    }

    @SuppressWarnings("Duplicates")
    private static Pair<Integer, char[]> LCSubstring(String str1, String str2) {
        if (str1 == null || str2 == null ||
                str1.length() == 0 || str2.length() == 0) return null;

        // String -> in[]
        Pair<Integer, int[]> result =
                _LCSubstring(str1.chars().toArray(), str2.chars().toArray());
                // _LCSubstring2直接扫描对角线, 算法更优但是没法得出具体的substring
//                _LCSubstring2(str1.chars().toArray(), str2.chars().toArray());
        // int[] -> String
        int[] ins = result.getRight();
        // _LCSubstring2直接扫描对角线, 算法更优但是没法得出具体的substring
        if (ins.length == 0) return Pair.of(result.getLeft(), new char[0]);
        
        int len = ins.length;
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) chars[i] = (char) ins[i];
        return Pair.of(result.getLeft(), chars);
    }

    @SuppressWarnings("unused")
    public static Pair<Integer, int[]> LCSubstring(int[] nums1, int[] nums2) {
        if (nums1 == null || nums2 == null ||
                nums1.length == 0 || nums2.length == 0) return null;
        return _LCSubstring(nums1, nums2);
    }

    /**
     * 最长公共子序列(longest common sub-sequence)：使用dp,
     * 1. 递推式
     * (1) 假如S1的最后一个元素与S2的最后一个元素相等，那么S1和S2的LCS就等于{S1减去最后一个元素}与{S2减去最后一个元素}的LCS再加上1；
     * (2) 假如S1的最后一个元素与S2的最后一个元素不等，那么S1和S2的LCS就等于：
     * {S1减去最后一个元素}与S2的LCS，{S2减去最后一个元素}与S1的LCS 中的最大的那个序列。
     * 2. 不能使用滚动数组,
     * (1) 因为相当于lcs[i-1][j-1](非滚动数组)在计算lcs[j-1](使用滚动数组)的时候已经被冲掉,
     * 而计算lcs[i][j]的时候仍然需要使用lcs[i-1][j-1],
     * 所以这里使用滚动数组的话也至少需要用两个
     * (2) 如果使用了滚动数组，就只能求出LCS的长度而不能逆推去获得LCS
     */
    private static Pair<Integer, int[]> _LCSubsequence(int[] nums1, int[] nums2) {
        // 1. get lcs[i][j]
        int len1 = nums1.length, len2 = nums2.length;
        int[][] lcs = new int[len1 + 1][len2 + 1];
        // (1) lcs[0][i...]=0/lcs[i...][0]=0
        // (2)
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                // 不太好使用滚动数组, 因为lcs[i-1][j-1]已经被冲掉
                if (nums1[i - 1] == nums2[j - 1])
                    lcs[i][j] = lcs[i - 1][j - 1] + 1;
                else
                    lcs[i][j] = max(lcs[i - 1][j], lcs[i][j - 1]);
            }
        }

        // 2. get lcs string
        LinkedList<Integer> lcsNums = new LinkedList<>();
        int i = len1, j = len2;
        while (i > 0 && j > 0) {
            // (1) lcs[i][j]同时大于上/左
            if (lcs[i][j] > lcs[i - 1][j] && lcs[i][j] > lcs[i][j - 1]) {
                // chs1[i-1]==chs2[j-1]
                lcsNums.addFirst(nums1[i - 1]);
                i--;
                j--;
            }
            // (2) lcs[i][j]最多只大于上/左中的一个, 这里可以有多个选择
            // > 大于上/左中的1个, (1) 选上/左中大的那个 (2) 如果chs1[i-1]==chs2[j-1], 也可以选对角
            // > 大于上/左中的0个, (1) 选上/左中任意一个 (2) 如果chs1[i-1]==chs2[j-1], 也可以选对角
            else if (lcs[i - 1][j] > lcs[i][j - 1]) i--;
            else j--;
        }

        return Pair.of(lcs[len1][len2],
                lcsNums.stream().mapToInt(Integer::intValue).toArray());
    }

    /**
     * longest common substring: 使用dp
     * 1. lcs[i][j]表示以nums1[i-1]和nums2[j-1]结尾的最长公共子串:
     * 【注意】这里【子串要以这个结尾】，所以最终的结果需要一个max来暂存而不一定是lcs[len1-1][len2-1]
     * (1) 如果nums1[i-1]==nums2[j-1], lcs[i][j]=lcs[i-1][j-1]+1
     * (2) 如果nums1[i-1]!=nums2[j-1], lcs[i][j]=0
     * 2. 同{@link #_LCSubsequence(int[], int[])}因为需要逆推去获得LCS而不是只求出LCS的长度, 不能使用滚动数组
     */
    private static Pair<Integer, int[]> _LCSubstring(int[] nums1, int[] nums2) {
        // 1. get lcs[i][j]
        int len1 = nums1.length, len2 = nums2.length;
        int[][] lcs = new int[len1 + 1][len2 + 1];
        int max = 0, maxi = 0;
        // (1) lcs[0][i...]=0/lcs[i...][0]=0
        // (2)
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (nums1[i - 1] == nums2[j - 1]) {
                    lcs[i][j] = lcs[i - 1][j - 1] + 1;
                    if (lcs[i][j] > max) {
                        max = lcs[i][j];
                        maxi = i;
                    }
                }
            }
        }

        // 2. get lcs string
        return Pair.of(max,
                Arrays.copyOfRange(nums1, maxi - max, maxi));
    }

    /**
     * longest common substring:
     * (1) 空间复杂度实际上是可以减少至O（1）的，因为计算每一个dp[i][j]的时候只需要计算dp[i-1][j-1],
     * 所以按照斜线(左上 -> 右下)方向计算所有的值，只需要一个变量就可以计算
     * (2) 其实也是可以求出具体的substring
     */
    private static Pair<Integer, int[]> _LCSubstring2(int[] nums1, int[] nums2) {
        int len1 = nums1.length, len2 = nums2.length;
        int max = 0, len;
        int row = 0, column = len2 - 1;
        int i, j;
        // 遍历每一条对角线
        // (1) 所有对角线
        // > 从右上的对角线开始: (0,len2-1)
        // > 到左下角结束: (len1-1,0)
        // > 下一条对角线, 先是column--, 然后是row++
        // (2) 对角线内(左上 -> 右下)
        while (row < len1) {
            i = row;
            j = column;
            len = 0;
            // 遍历该条对角线内(左上 -> 右下)所有元素
            while (i < len1 && j < len2) {
                len = nums1[i] == nums2[j] ? ++len : 0;
                if(len>max) max = len;
                // 对角线内(左上 -> 右下), 所以是i++,j++
                i++;
                j++;
            }
            // 下一条对角线, 先是column--, 然后是row++
            if (column > 0) column--;
            else row++;
        }
        // 其实也求出具体的substring, 上面同时记住i/j就可以了, 相当于结尾
        return Pair.of(max, new int[0]);
    }
}
