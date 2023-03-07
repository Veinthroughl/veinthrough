package veinthrough.leetcode.string.substring;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第97题, 交错字符串：
 * 给定三个字符串 s1、s2、s3，请你帮忙验证 s3 是否是由 s1 和 s2 交错 组成的。
 * 两个字符串 s 和 t 交错 的定义与过程如下，其中每个字符串都会被分割成若干 非空 子字符串：
 * s = s1 + s2 + ... + sn
 * t = t1 + t2 + ... + tm
 * |n - m| <= 1
 * 交错 是 s1 + t1 + s2 + t2 + s3 + t3 + ... 或者 t1 + s1 + t2 + s2 + t3 + s3 + ...
 */
@Slf4j
public class IsInterleave {
    @Test
    public void test97() {
        Stream.of(
                isInterleave("aabcc", "dbbca", "aadbbcbcac"), // true
                isInterleave("aabcc", "dbbca", "aadbbbaccc"), // false
                isInterleave("", "", "")) // true
                .forEach(result -> log.info(methodLog("" + result)));
    }

    @Test
    public void test97_2() {
        Stream.of(
                isInterleave2("aabcc", "dbbca", "aadbbcbcac"), // true
                isInterleave2("aabcc", "dbbca", "aadbbbaccc"), // false
                isInterleave2("", "", "")) // true
                .forEach(result -> log.info(methodLog("" + result)));
    }

    /**
     * 1. 如果s1的第i个元素和s3的第i+j个元素相等，那么s1的前i个元素和s2的前j个元素是否能交错组成s3的前i+j个元素
     * 取决于s1的前i-1个元素和s2的前j个元素是否能交错组成s3的前i+j−1个元素
     * 【不管s1的前i-1个元素和s2的前j个元素以s1/s2结束：
     * (1) 以s1结束, 那么s1继续补上第i个元素；
     * (2) 以s2结束，那么切换到s2】
     * 即此时f(i,j) 取决于f(i−1,j)，在此情况下如果f(i−1,j)为真，则f(i,j) 也为真。
     * 2. 同样的，如果s2的第j个元素和s3的第i+j个元素相等并且f(i,j−1) 为真，则f(i,j) 也为真。
     * 于是我们可以推导出这样的动态规划转移方程：
     * f(i, j) = [f(i - 1, j) and s1(i-1) =s3(i+j-1)] or [f(i,j-1) and s2(j-1) =s3(i+j-1)]
     * 3. 优化: 使用滚动数组代替二维数组
     */
    public boolean isInterleave(String s1, String s2, String s3) {
        int n = s1.length(), m = s2.length(), t = s3.length();

        if (n + m != t) {
            return false;
        }

        // 这里使用滚动数组而非二维数组来节省空间, 长度为列数
        boolean[] f = new boolean[m + 1];

        f[0] = true;
        for (int i = 0; i <= n; ++i) {
            for (int j = 0; j <= m; ++j) {
                int p = i + j - 1;
                // f(i, j) = [f(i - 1, j) and s1(i-1) =s3(p)] or [f(i,j-1) and s2(j-1) =s3(p)]
                // (1) 因为使用了滚动数组, 需要使用上一次的迭代, 也就是第i次的f[j]需要使用第i-1次的f[j],
                // 所以(1)必须在前面, 先把第i-1次的f[j]的值用起来否则会被冲掉
                if (i > 0) f[j] &= s1.charAt(i - 1) == s3.charAt(p);
//                    f[j] = f[j] && s1.charAt(i - 1) == s3.charAt(p);
                // (2)
                if (j > 0) f[j] |= (f[j - 1] && s2.charAt(j - 1) == s3.charAt(p));
//                    f[j] = f[j] || (f[j - 1] && s2.charAt(j - 1) == s3.charAt(p));
            }
        }
        return f[m];
    }

    /**
     * 方法2: 先计算第一行
     */
    public boolean isInterleave2(String s1, String s2, String s3) {
        int l1 = s1.length();
        int l2 = s2.length();
        int l3 = s3.length();

        // 1. 根据长度求特殊情况
        if ((l1 + l2) != l3) return false;
        if (l1 == 0) return s2.equals(s3);
        if (l2 == 0) return s1.equals(s3);

        char[] chs1 = s1.toCharArray();
        char[] chs2 = s2.toCharArray();
        char[] chs3 = s3.toCharArray();

        boolean[] evaluation = new boolean[l2 + 1];

        // 2. 先求第一行evaluation[j]实际上相当于二维数组的evaluation[0][j]
        // 就是s1的0个字符串和s2的j个字符串交错, 实际上就是s2与s3的字符的匹配
        evaluation[0] = true;
        for (int j = 1; j <= l2; j++) {
            // 出现s2与s3的字符不匹配, 以后肯定为false
            if (chs2[j - 1] != chs3[j - 1]) break;
            evaluation[j] = true;
        }

        // 3. 这里使用滚动数组而非二维数组来节省空间
        for (int i = 1, p; i <= l1; i++) {
            for (int j = 0; j <= l2; j++) {
                p = i + j - 1;
                // (1) 因为使用了滚动数组, 需要使用上一次的迭代, 也就是第i次的evaluation[j]需要使用第i-1次的evaluation[j],
                // 所以(1)必须再前面, 先把第i-1次的evaluation[j]的值用起来否则会被冲掉
                // if(chs1[i-1]==chs3[p]) evaluation[j] = evaluation[j];
                // else evaluation[j] = false;
                evaluation[j] &= (chs1[i - 1] == chs3[p]);
                // (2)
                if (j > 0 && chs2[j - 1] == chs3[p]) evaluation[j] |= evaluation[j - 1];
            }
        }
        return evaluation[l2];
    }
}