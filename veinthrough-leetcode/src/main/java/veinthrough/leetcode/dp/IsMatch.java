package veinthrough.leetcode.dp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第10题：
 * 给你一个字符串 s 和一个字符规律 p，请你来实现一个支持 '.' 和 '*' 的正则表达式匹配。
 * '.' 匹配任意单个字符
 * '*' 匹配零个或多个前面的那一个元素
 * 所谓匹配，是要涵盖整个字符串s的，而不是部分字符串。
 *
 * 第44题：
 * 给定一个字符串(s)和一个字符模式(p) ，实现一个支持'?'和'*'的通配符匹配。
 * '?' 可以匹配任何单个字符。
 * '*' 可以匹配任意字符串（包括空字符串）。
 * 两个字符串完全匹配才算匹配成功。
 */
@Slf4j
public class IsMatch {
    @Test
    public void test10() {
        Stream.of(
                isMatch("aa", "a"), // false
                isMatch("aa", "a*"), // true
                isMatch("aab", "c*a*b"), // true
                isMatch("ab", ".*"), //true
                isMatch("mississippi", "mis*is*p*."), // false
                isMatch("aaa", "ab*ac*a"), // true
                isMatch("ssissippi", "s*is*ip*.")) // true
                .forEach(result -> log.info(methodLog("" + result)));
    }

    @Test
    public void test44_1() {
        Stream.of(
                isMatch2("", "***"), // true
                isMatch2("aa", "a"), // false
                isMatch2("aa", "*"), // true
                isMatch2("cb", "?a"), // false
                isMatch2("adceb", "*a*b"), //true
                isMatch2("acdcb", "a*c?b"), // false
                isMatch2("ho", "**ho")) // true
                .forEach(result -> log.info(methodLog("" + result)));
    }

    @Test
    public void test44_2() {
        Stream.of(
                isMatch3("", "***"), // true
                isMatch3("aa", "a"), // false
                isMatch3("aa", "*"), // true
                isMatch3("cb", "?a"), // false
                isMatch3("adceb", "*a*b"), //true
                isMatch3("acdcb", "a*c?b"), // false
                isMatch3("ho", "**ho")) // true
                .forEach(result -> log.info(methodLog("" + result)));
    }

    // 解法3【贪心算法】(题解)
    @Test
    public void test44_3() {
        Stream.of(
                isMatch4("", "***"), // true
                isMatch4("aa", "a"), // false
                isMatch4("aa", "*"), // true
                isMatch4("cb", "?a"), // false
                isMatch4("adceb", "*a*b"), //true
                isMatch4("acdcb", "a*c?b"), // false
                isMatch4("ho", "**ho")) // true
                .forEach(result -> log.info(methodLog("" + result)));
    }

    /**
     * 第10题：
     * dp[i][j]表示str[0:i-1](前i个)和pattern[0:j-1](前j个)能否匹配，为0表示无法匹配，为1表示可以匹配
     * 1. dp[0][j]表示p[0:j-1]与空串的匹配情况
     * (1) p[j-1]=='*', dp[0][j]=dp[0][j-2], 因为可以给最后的'x*'匹配为0个
     * p: xxxxxxx*
     * (2) p[j-1]!='*', false, 肯定不匹配
     * 2. dp[i][0]==false;
     * 3. 对于dp[i][j]
     * (1) 单字符直接匹配: 如果s[i-1]==p[j-1]或者p[j-1]==.，则dp[i][j] = dp[i-1][j-1]，即与s[1:i-1]和p[1:j-1]的匹配情况相同
     * (2) 'x*'匹配: 如果p[j-1]为*，则表示这个字符可以联合p[j-2]使用来匹配当前的s[i-1]做'x*'匹配:
     * <1> p[j-1]=*, p[j-2]为'.'【后记：实际上就是排列组合】
     * 选择匹配: dp[i-1][j-2], dp[i-2][j-2], dp[i-3][j-2].....合并起来其实是dp[i-1][j]
     * 选择不匹配: dp[i][j-2]
     * <2> p[j-1]=*, s[i-1]==p[j-2]
     * 选择匹配: dp[i-1][j-2], dp[i-2][j-2], dp[i-3][j-2].....合并起来其实是dp[i-1][j]
     * 选择不匹配: dp[i][j-2]
     * <3> p[j-1]=*, 空匹配'x*': dp[i][j-2]
     * (3) 既不能做单字符直接匹配又不能做'x*'匹配: false
     */
    private boolean isMatch(String _s, String _p) {
        int sl = _s.length(), pl = _p.length();
        char[] s = _s.toCharArray();
        char[] p = _p.toCharArray();
        boolean[][] dp = new boolean[sl + 1][pl + 1];

        dp[0][0] = true;
        // 1. dp[0][j]表示p[0:j-1]与空串的匹配情况
        for (int k = 1; k <= pl; k++)
            // 只有一个字符的p不可能是*
            if (p[k - 1] == '*') dp[0][k] = dp[0][k - 2];
        // 3. dp[i][j]
        for (int i = 1; i <= sl; i++) {
            for (int j = 1; j <= pl; j++) {
                // 3.(1) 单字符直接匹配
                if (s[i - 1] == p[j - 1] || p[j - 1] == '.')
                    dp[i][j] = dp[i - 1][j - 1];
                    // 3.(2).<1>或3.(2).<1>, 'x*'匹配
                else if (p[j - 1] == '*' && (p[j - 2] == '.' || p[j - 2] == s[i - 1]))
                    // 选择匹配/不匹配
                    dp[i][j] = dp[i - 1][j] | dp[i][j - 2];
                else if (p[j - 1] == '*')
                    // 3.(2).<3>, 空匹配b*
                    dp[i][j] = dp[i][j - 2];
            }
        }
        return dp[sl][pl];
    }

    /**
     * 第44题: 方法1(题解)
     * 1. 如果s[i-1]==p[i-1]
     * dp[i][j] == dp[i-1][j-1]
     * 2. 处理?: 如果p[i-1]==?,
     * dp[i][j] == dp[i-1][j-1]
     * 3. 处理*: 如果p[i-1]==*,
     * (1) *匹配：dp[i-1][j-1], dp[i-2][j-1], dp[i-3][j-1].....合并起来其实是dp[i-1][j]
     * (2) *不匹配: dp[i][j-1]
     */
    private boolean isMatch2(String _s, String _p) {
        // 1.
        if (_p.equals("*") ||
                _s.length() == 0 && _p.length() == 0) return true;
        // 连续的*可以匹配空串
//        if (_s.length() == 0 || _p.length() == 0) return false;

        // 2.
        char[] s = _s.toCharArray();
        char[] p = _p.toCharArray();
        int sl = s.length;
        int pl = p.length;
        boolean[][] dp = new boolean[sl + 1][pl + 1];

        // (1) 边界条件
        // dp[0][2...] = false
        // dp[1...][0]=false;
        dp[0][0] = true;
        // 处理*或者连续的*, 连续的*可以匹配空串
        for (int j = 1; j <= pl; j++) {
            if (p[j - 1] == '*') dp[0][j] = true;
            else break;
        }
        // (2) 计算dp[i][j]
        for (int i = 1; i <= sl; i++) {
            for (int j = 1; j <= pl; j++) {
                if (s[i - 1] == p[j - 1] || p[j - 1] == '?')
                    dp[i][j] = dp[i - 1][j - 1];
                else if (p[j - 1] == '*')
                    dp[i][j] = dp[i][j - 1] | dp[i - 1][j];
            }
        }
        return dp[sl][pl];
    }

    /**
     * 第44题：方法2(自己解法)
     * 1 2 3 4 5 6 7 8 9
     * a * b *
     * a x x b x x x x x b
     * 1. 因为p[0]==s[0]==a, 所以dp[1][1]=dp[0][0]=true
     * 2. 处理?
     * 3. 处理*: pattern循环在外面，
     * 处理p[i-1](即dp[i])的时候如果dp[i][j]==true, 我们记住dp[i]的firstMatch为i, 对应位dp[i+1]时的lastFirstMatch
     * 处理p[i](dp[i+1])的时候, 如果p[i]==*, 那么s[j]后面的所有都可以和匹配, 也就是dp[i+1][j+1...]都为true
     * (1) i=1,j=1, a和a匹配, dp[1][1]=true, 那么firstMatch=1, 因为p[1]=*, 那么dp[2][2....]都为true
     * (2) i=3,j=4, a*b和axxb匹配, dp[3][4]=true, 那么firstMatch=4, 因为p[4]=*, 那么dp[4][5....]都为true
     */
    private boolean isMatch3(String _s, String _p) {
        // pattern:*, 可能有多个*连续在一起
        if (_p.equals("*")) return true;
        if (_s.length() == 0 && _p.length() == 0) return true;
        // 连续的*可以匹配空串
//        if (_s.length() == 0 || _p.length() == 0) return false;

        char[] s = _s.toCharArray();
        // 1. delete consecutive '*'
        char[] p_ = _p.toCharArray();
        int sl = s.length, pl = p_.length;
        char[] p = new char[pl];
        for (int i = 0, j = i + 1, index = 0; i < p_.length; j = i + 1, index++) {
            p[index] = p_[i];
            if (p_[i] == '*') {
                while (j < p_.length && p_[j] == '*') {
                    j++;
                    pl--;
                }
            }
            i = j;
        }
        boolean[][] dp = new boolean[pl + 1][sl + 1];

        dp[0][0] = true;
        dp[1][0] = p[0] == '*';
        int lastFirstMatch = 0, firstMatch = sl + 1;
        for (int i = 1; i <= pl; i++, firstMatch = sl + 1) {
            for (int j = 1; j <= sl; j++) {
                // 3. 处理*
                if (p[i - 1] == '*' && j >= lastFirstMatch) {
                    dp[i][j] = true;
                    // 2. 处理?
                    // 1. 处理相等
                } else if (p[i - 1] == '?' || p[i - 1] == s[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
                if (dp[i][j] && j < firstMatch) firstMatch = j;
            }
            lastFirstMatch = firstMatch;
        }
        return dp[pl][sl];
    }

    /**
     * 第44题：方法3【贪心算法】(题解)
     * 说明见文档：
     * 主体为对：p 是 *u1∗u2∗u3∗⋯∗ux∗ 的形式的处理方法
     */
    public boolean isMatch4(String s, String p) {
        int sRight = s.length(), pRight = p.length();
        // 模式 p 的结尾字符不是星号, 必须从尾部开始往前全部匹配直到遇到*或者结束
        while (sRight > 0 && pRight > 0 && p.charAt(pRight - 1) != '*') {
            if (charMatch(s.charAt(sRight - 1), p.charAt(pRight - 1))) {
                --sRight;
                --pRight;
            } else {
                return false;
            }
        }

        if (pRight == 0) {
            return sRight == 0;
        }

        int sIndex = 0, pIndex = 0;
        int sRecord = -1, pRecord = -1;

        // 主体为对：p 是 *u1∗u2∗u3∗⋯∗ux∗ 的形式的处理方法
        // 将sRecord/pRecord置为-1以处理模式 p 的开头字符不是星号
        while (sIndex < sRight && pIndex < pRight) {
            // (1)
            if (p.charAt(pIndex) == '*') {
                ++pIndex;
                sRecord = sIndex;
                pRecord = pIndex;
            }
            // (2) 遇到非*需要一一匹配
            // 模式 p 的开头字符不是星号, 也必须从头开始往后全部匹配直到遇到*或者结束
            // 否则会由于sRecord==-1而没有重新进行匹配的机会直接进入(4)
            else if (charMatch(s.charAt(sIndex), p.charAt(pIndex))) {
                ++sIndex;
                ++pIndex;
            }
            // (3) 重新匹配
            // 如果sRecord==-1则没有重新进行匹配的机会直接进入(4)
            else if (sRecord != -1 && sRecord + 1 < sRight) {
                ++sRecord;
                sIndex = sRecord;
                pIndex = pRecord;
            }
            // (4)
            else {
                return false;
            }
        }

        return allStars(p, pIndex, pRight);
    }

    private boolean allStars(String str, int left, int right) {
        for (int i = left; i < right; ++i) {
            if (str.charAt(i) != '*') {
                return false;
            }
        }
        return true;
    }

    private boolean charMatch(char u, char v) {
        return u == v || v == '?';
    }
}