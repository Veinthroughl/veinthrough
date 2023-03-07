package veinthrough.leetcode.dp;

import lombok.extern.slf4j.Slf4j;
import veinthrough.leetcode.array.SplitArray;
import veinthrough.leetcode.stack.monstack.Drop;
import veinthrough.leetcode.string.substring.LongestSubstring;

/**
 * [#DP]:动态规划, 参考文档《#DP》
 * 1. 面试题 17.21. 直方图的水量:
 * {@link Drop#_maxDropDP1(int[])}
 *
 * 2. 第5题
 * {@link LongestSubstring}
 *
 * 3. 第32题：最长有效括号
 * {@link LongestSubstring#_longestValidParenthesesDP(String)}
 * {@link LongestSubstring#_longestValidParenthesesDP2(String)}
 * 给你一个只包含 '(' 和 ')' 的字符串，找出最长有效（格式正确且连续）括号子串的长度。
 * 《最长有效括号》
 *
 * 4. 第45题：跳跃游戏II
 * {@link veinthrough.leetcode.greed.JumpArray#_jumpByDP(int[])}
 *
 * 5. 第97题, 交错字符串
 * {@link veinthrough.leetcode.string.substring.IsInterleave#isInterleave(String, String, String)}
 * {@link veinthrough.leetcode.string.substring.IsInterleave#isInterleave2(String, String, String)}
 *
 * 6. 第264：丑数II
 * 给你一个整数n，请你找出并返回第n个丑数。
 * {@link veinthrough.leetcode.math.UglyNum#nthUglyNumber(int)}
 *
 * 7. LCS
 * {@link veinthrough.leetcode.string.substring.LCS#LCSubsequence(int[], int[])}
 * {@link veinthrough.leetcode.string.substring.LCS#LCSubstring(int[], int[])}
 * 
 * 8. LIS
 * {@link veinthrough.leetcode.string.substring.LIS#_lisLcs(int[])}
 * {@link veinthrough.leetcode.string.substring.LIS#_lisGeneralDP(int[])}
 *
 * 9. 第329题:  矩阵中的最长递增路径
 * {@link veinthrough.leetcode.graph.LongestIncreasingPath#longestIncreasingPathTopo(int[][])}
 *
 * 10. 第410题: 分割数组的最大值
 * {@link SplitArray}
 *
 * 11. 第1025题: 除数博弈
 * {@link veinthrough.leetcode.math.DivisorGame#divisorGame(int)}
 *
 * 12. 第87题: 扰动字符串
 * {@link veinthrough.leetcode.enumerate.IsScramble}
 */
@SuppressWarnings("unused")
@Slf4j
public class DP {
}
