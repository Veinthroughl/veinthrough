package veinthrough.leetcode.enumerate;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.generic.Pair;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第87题：扰乱字符串，
 * 使用下面描述的算法可以扰乱字符串 s 得到字符串 t ：
 * 1. 如果字符串的长度为 1 ，算法停止
 * 2. 如果字符串的长度 > 1 ，执行下述步骤：
 * (1) 在一个随机下标处将字符串分割成两个非空的子字符串。
 * 即，如果已知字符串s，则可以将其分成两个子字符串 x 和 y ，且满足 s = x + y 。
 * (2) 随机 决定是要「交换两个子字符串」还是要「保持这两个子字符串的顺序不变」。
 * 即，在执行这一步骤之后，s 可能是 s = x + y 或者 s = y + x 。
 * (3) 在 x 和 y 这两个子字符串上继续从步骤 1 开始递归执行此算法。
 * 给你两个长度相等 的字符串 s1 和 s2，判断 s2 是否是 s1 的扰乱字符串。如果是，返回 true；否则，返回 false。
 */
@SuppressWarnings("unused")
@Slf4j
public class IsScramble {
    private static final int TRUE = 1, FALSE = -1, NA = 0;
    private static final int CHARACTERS = 256;
    private int[][][] scramble;
    private char[] chs1;
    private char[] chs2;
    private int n;

    @Test
    public void test87() {
        Stream.of(
                Pair.of("eebaacbcbcadaaedceaaacadccd", "eadcaacabaddaceacbceaabeccd"), // 这个用递归方法有点慢false
                Pair.of("great", "rgeat"),
                Pair.of("abcde", "caebd"),
                Pair.of("a", "a"))
                .forEach(pair -> log.info(methodLog(
                        pair.toString(), "" + isScramble(pair.getFirst(), pair.getSecond()))));
    }

    /**
     * 思路都一样，dp[i][j][k]: 表示s1以i开始, s2以j开始长度为k
     * 方法1： 递归dp，可能会超时
     * 方法2： 迭代dp
     */
    private boolean isScramble(String s1, String s2) {
        // boundary
        if ((n = s1.length()) == 1) return s1.equals(s2);

        //
        chs1 = s1.toCharArray();
        chs2 = s2.toCharArray();
        scramble = new int[n][n][n + 1];
        // 非递归方法
//        return iter();
        // 递归方法
        return dfs(0, 0, n) == TRUE;
    }

    /**
     * 方法2：迭代dp
     */
    private boolean iter() {
        boolean[][][] dp = new boolean[n][n][n + 1];
        // 初始化单个字符的情况
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dp[i][j][1] = chs1[i] == chs2[j];
            }
        }

        // 枚举区间长度 2～n
        for (int len = 2; len <= n; len++) {
            // 枚举 S 中的起点位置
            for (int i = 0; i <= n - len; i++) {
                // 枚举 T 中的起点位置
                for (int j = 0; j <= n - len; j++) {
                    // 枚举划分位置
                    for (int k = 1; k <= len - 1; k++) {
                        // 第一种情况：S1 -> T1, S2 -> T2
                        // 第二种情况：S1 -> T2, S2 -> T1
                        // S1 起点 i，T2 起点 j + 前面那段长度 len-k ，S2 起点 i + 前面长度k
                        if (dp[i][j][k] && dp[i + k][j + k][len - k] ||
                                dp[i][j + len - k][k] && dp[i + k][j][len - k]) {
                            dp[i][j][len] = true;
                            break;      // 不用再枚举划分位置了，跳出第一层循环
                        }
                    }
                }
            }
        }
        return dp[0][0][n];
    }

    /**
     * 方法1：递归dp
     */
    private int dfs(int i1, int i2, int len) {
        // 1. 已经计算过
        if (scramble[i1][i2][len] != NA)
            return scramble[i1][i2][len];

        // 2. equals
        if (equals(i1, i2, len))
            return scramble[i1][i2][len] = TRUE;

        // 3. 含有不同字符串
        if (!sameContaining(i1, i2, len))
            return scramble[i1][i2][len] = FALSE;

        // 4. 递归
        for (int k = 1; k < len; k++) {
            if (dfs(i1, i2, k) == TRUE && dfs(i1 + k, i2 + k, len - k) == TRUE ||
                    dfs(i1, i2 + len - k, k) == TRUE && dfs(i1 + k, i2, len - k) == TRUE)
                return scramble[i1][i2][len] = TRUE;
        }
        // 5. 其他情况
        return FALSE;
    }

    public boolean equals(int start1, int start2, int len) {
        for (int i = start1, j = start2, k = 0; k < len; i++, j++, k++)
            if (chs1[i] != chs2[j]) return false;
        return true;
    }

    /**
     * 由相同的字符集合(字符个数、种类、每个字符的个数)组成
     */
    private boolean sameContaining(int start1, int start2, int len) {
        int[] charCount = new int[CHARACTERS];
        for (int i = start1, k = 0; k < len; i++, k++)
            charCount[chs1[i]]++;
        for (int j = start2, k = 0; k < len; j++, k++)
            if (--charCount[chs2[j]] < 0) return false;
        for (int k = 0; k < CHARACTERS; k++)
            if (charCount[k] != 0) return false;
        return true;
    }
}
