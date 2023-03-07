package veinthrough.leetcode.dp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 一条包含字母A-Z的消息通过以下映射进行了编码 ：
 * 'A' -> "1"
 * 'B' -> "2"
 * ...
 * 'Z' -> "26"
 * 要 解码 已编码的消息，所有数字必须基于上述映射的方法，反向映射回字母（可能有多种方法）。例如，"11106" 可以映射为：
 * "AAJF"，将消息分组为(1 1 10 6)
 * "KJF"，将消息分组为(11 10 6)
 * 注意，消息不能分组为(1 11 06)，因为 "06" 不能映射为 "F" ，这是由于 "6" 和 "06" 在映射中并不等价。
 * 给你一个只含数字的 非空 字符串 s ，请计算并返回 解码 方法的 总数 。
 * 题目数据保证答案肯定是一个 32 位 的整数。
 *
 * 示例 1：
 * 输入：s = "12"
 * 输出：2
 * 解释：它可以解码为 "AB"（1 2）或者 "L"（12）。
 * 示例 2：
 *
 * 输入：s = "226"
 * 输出：3
 * 解释：它可以解码为 "BZ" (2 26), "VF" (22 6), 或者 "BBF" (2 2 6) 。
 * 示例 3：
 *
 * 输入：s = "06"
 * 输出：0
 * 解释："06" 无法映射到 "F" ，因为存在前导零（"6" 和 "06" 并不等价）。
 */
@SuppressWarnings({"Duplicates", "unused"})
@Slf4j
public class NumDecodings {
    @Test
    public void test91() {
        Stream.of(
                "06", "810254", "226", "12", "0")
                .forEach(str -> log.info(methodLog(
                        str, "" + numDecodings2(str))));
    }

    /**
     * 1. dp与递归
     * (1) dp[1...n] = dp[2...n]+dp[3...n], 这样其实更适合递归(尾部)
     * (2) dp[1...n] = dp[1...n-1]+dp[1...n-2], 这样更适合dp(首部)
     * 2. 方法1: 尾部
     * 3. 方法2: 首部
     * 4. 方法3: 优化，数组 --> 3个变量
     */
    public int numDecodings(String s) {
        return numDecodings1(s);
//        return numDecodings2(s);
//        return numDecodings3(s);
    }

    /**
     * 方法1：尾部
     * 0	    不可能(00)
     * 1-9	    0
     * 10/20	1
     * 11-26	2
     * 27-..	1
     *
     * 2. 【错误】dp[1...n] = dp[1...n-1]*dp[n]+dp[1...n-2]*dp[n-1...n],
     * 因为dp[n-1...n]包含了dp[n]的情况
     */
    private int numDecodings1(String s) {
        if (s.charAt(0) == '0') return 0;
        int n;
        int[] nums = s.chars().map(code -> code - '0').toArray();
        if ((n = nums.length) == 1) return 1;

        int[][] dp = new int[n][n];
        // gap: 0/1
        dp[0][0] = nums[0] != 0 ? 1 : 0;  // 单个数字
        for (int i = 0, combine, j; i < n; i++) {
            j = i + 1;
            if (j < n) {
                dp[j][j] = nums[j] != 0 ? 1 : 0;  // 单个数字j
                combine = 10 * nums[i] + nums[j];
                if (nums[i] != 0) dp[i][j] += dp[j][j]; // i/j不一起
                if (combine >= 10 && combine <= 26) dp[i][j] += 1; // i/j一起
            }
        }

        // gap: 2...
        for (int gap = 2, combine; gap < n; gap++)
            for (int i = 0, j; i < n - gap; i++) {
                j = i + gap;
                combine = 10 * nums[i] + nums[i + 1];
                if (nums[i] != 0) dp[i][j] += dp[i + 1][j]; // i/j不一起
                if (combine >= 10 && combine <= 26) dp[i][j] += dp[i + 2][j]; // i/j一起
            }
        return dp[0][n - 1];
    }

    /**
     * 方法2：首部
     */
    private int numDecodings2(String s) {
        int n = s.length();
        int[] f = new int[n + 1];
        // 1. 边界f[0]
        f[0] = 1;
        for (int i = 1; i <= n; ++i) {
            // 2. (i-1,i)划分
            if (s.charAt(i - 1) != '0') {
                f[i] += f[i - 1]; // i-1/i不一起
            }
            // 3. (i-1,i)
            if (i > 1 && s.charAt(i - 2) != '0' &&
                    ((s.charAt(i - 2) - '0') * 10 + (s.charAt(i - 1) - '0') <= 26)) {
                f[i] += f[i - 2]; // i-1/i一起
            }
        }
        return f[n];
    }

    /**
     * 方法3: 在状态转移方程中，f(i)的值仅与f(i-1)和f(i-2)有关，因此我们可以使用三个变量进行状态转移，省去数组的空间。
     */
    private int numDecodings3(String s) {
        int n = s.length();
        // a = f[i-2], b = f[i-1], c=f[i]
        int a = 0, b = 1, c = 0;
        for (int i = 1; i <= n; ++i) {
            c = 0;
            if (s.charAt(i - 1) != '0') {
                c += b;
            }
            if (i > 1 && s.charAt(i - 2) != '0' &&
                    ((s.charAt(i - 2) - '0') * 10 + (s.charAt(i - 1) - '0') <= 26)) {
                c += a;
            }
            a = b;
            b = c;
        }
        return c;
    }
}
