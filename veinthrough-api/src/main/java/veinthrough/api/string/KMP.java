package veinthrough.api.string;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings({"Duplicates", "unused"})
public class KMP {
    public static int search(String _s, String _p) {
        int ls = _s.length(), lp = _p.length();
        if (lp == 0) return 0;
        if (ls == 0) return -1;
        char[] s = _s.toCharArray(), p = _p.toCharArray();
        int[] next = getNext_Optimized(_p);
        int i = 0, j = 0;
        while (i < ls && j < lp) {
            //① 如果j = -1，或者当前字符匹配成功（即S[i] == P[j]），都令i++，j++
            if (j == -1 || s[i] == p[j]) {
                i++;
                j++;
            } else
                //② 如果j != -1，且当前字符匹配失败（即S[i] != P[j]），则令 i 不变，j = next[j]
                //next[j]即为j所对应的next值
                j = next[j];
        }
        if (j == lp)
            return i - j;
        else
            return -1;
    }

    public static List<Integer> searchAll(String _s, String _p) {
        int ls = _s.length(), lp = _p.length();
        if (lp == 0 || ls == 0) return null;
        char[] s = _s.toCharArray(), p = _p.toCharArray();
        // add 1 letter
        int[] next = getNext_Optimized(_p + "*");
        int i = 0, j = 0;
        List<Integer> founds = new LinkedList<>();
        while (i < ls) {
            while (i < ls && j < lp) {
                //①如果j = -1，或者当前字符匹配成功（即S[i] == P[j]），都令i++，j++
                if (j == -1 || s[i] == p[j]) {
                    i++;
                    j++;
                } else
                    //②如果j != -1，且当前字符匹配失败（即S[i] != P[j]），则令 i 不变，j = next[j]
                    //next[j]即为j所对应的next值
                    j = next[j];
            }
            if (j == lp) {
                founds.add(i - j);
                j = next[lp];
            }
        }
        return founds;
    }


    /**
     * next[i]表示第i个不匹配时, 其实也就是前i-1个的前缀/后缀最大公共子串
     * # i   k                          0   1   2   3   4   5   6   7   8   9   10
     * #                                D   A   D   A   C   D   A   D   A   D   D
     * # 0  -1(初始)                  (-1)  0
     * # 1   0                         -1   0
     * # 1  -1(递归k=next[0])          -1   0   0
     * # 2   0                         -1   0   0   1
     * # 3   1                         -1   0   0   1   2
     * # 4   2                         -1   0   0   1   2
     * # 4   1(递归k=next[2])          -1   0   0   1   2
     * # 4   0(递归k=next[1])          -1   0   0   1   2
     * # 4  -1(递归k=next[0])          -1   0   0   1   2   0
     * # 5   0                         -1   0   0   1   2   0   1
     * # 6   1                         -1   0   0   1   2   0   1   2
     * # 7   2                         -1   0   0   1   2   0   1   2   3
     * # 8   3                         -1   0   0   1   2   0   1   2   3   4
     * # 9   4                         -1   0   0   1   2   0   1   2   3   4
     * # 9   2(递归k=next[4])          -1   0   0   1   2   0   1   2   3   4   3
     * # i   k                          0   1   2   3   4   5   6   7   8   9   10
     */
    public static int[] getNext(String pattern) {
        int len = pattern.length();
        int[] next = new int[len];
        char[] p = pattern.toCharArray();
        next[0] = -1;

        int i = 0;
        int k = -1;
        while (i < len - 1) {
            // p[k]表示前缀头，p[j]表示后缀头
            // k==-1: (1)开始 (2)递归k=next[k]找不到相同字符
            if (k == -1 || p[i] == p[k]) {
                ++i;
                ++k;
                // next[i]表示【第i个不匹配】时, 其实也就是前i-1个的前缀/后缀最大公共子串
                // 所以pi[i]==p[k]实际上表示的时next[i+1], 应该先++i,++k再执行
                next[i] = k;
            } else
                k = next[k];
        }
        return next;
    }

    /**
     * # 0  1  2  3   4  5  6  7  8
     * # a  b  a  c*  a  b  a  b  c
     * # a  b  a  b*                  (i=3, k=3)
     * #       a  b*  a  b            (i=3, k=next[3]=1)
     * (1) next数组为: -1 0 0 1（0 0 1 2整体右移一位，初值赋为-1），
     * (2) 当它去匹配时，发现b(3)跟c(3)失配，于是模式串右移k - next[k] = 3 - 1 =2位。
     * 右移2位后，b又跟c失配。事实上，因为在上一步的匹配中，已经得知p[3]=b，与s[3]=c失配，
     * 而右移两位之后，让p[next[3]] = p[1] = b 再跟s[3]匹配时，必然失配。
     * 因为不能出现p[k] = p[next[k]](也就是p[k])，所以当出现时需要继续递归，k = next[k] = next[next[k]]
     * (3)
     * ① next[k]本意是考虑前k-1的前后缀, 不考虑k(因k不匹配)
     * ② 优化: 还要考虑k匹配的情况
     * > k和谁匹配: next[k]+1
     * > 求next[k]: 是先判断k-1匹配(p[i]==p[k-1]), 然后++i;++k;
     * 所以正好在++i;++k;之后判断p[i]!=p[k]
     *
     *
     * # i   k                          0   1   2   3   4   5   6   7   8   9   10
     * #                                D   A   D   A   C   D   A   D   A   D   D
     * # 0  -1(初始)                  (-1)  0
     * # 1   0                         -1   0
     * # 1  -1(递归k=next[0])          -1   0  -1#                                     # p[2]=p[0], next[2]: 0(k) --> -1(next[k])
     * # 2   0                         -1   0  -1   0                                  # p[3]=p[1], next[3]: 1(k) -->  0(next[k])
     * # 3   1                         -1   0  -1   0   2
     * # 4   2                         -1   0  -1   0   2
     * # 4  -1(递归k=next[2])          -1   0  -1   0   2   -1                 一步到位# p[5]=p[0], next[5]: 0(k) --> -1(next[k])
     * # 4   1(递归k=next[2])     -1   0  -1   0   2                        优化前#
     * # 4   0(递归k=next[1])     -1   0  -1   0   2                        优化前#
     * # 4  -1(递归k=next[0])     -1   0  -1   0   2  -1                    优化前#
     * # 5   0                         -1   0  -1   0   2  -1   0                      # p[6]=p[1], next[6]: 1(k) -->  0(next[k])
     * # 6   1                         -1   0  -1   0   2  -1   0  -1                  # p[7]=p[2], next[7]: 2(k) --> -1(next[k])
     * # 7   2                         -1   0  -1   0   2  -1   0  -1   0              # p[8]=p[3], next[8]: 3(k) -->  0(next[k])
     * # 8   3                         -1   0  -1   0   2  -1   0  -1   0   4
     * # 9   4                         -1   0  -1   0   2  -1   0  -1   0   4
     * # 9   2(递归k=next[4])          -1   0  -1   0   2  -1   0  -1   0   4   3
     * # i   k                          0   1   2   3   4   5   6   7   8   9   10
     */
    public static int[] getNext_Optimized(String pattern) {
        int len = pattern.length();
        int[] next = new int[len];
        char[] p = pattern.toCharArray();
        next[0] = -1;

        int i = 0;
        int k = -1;
        while (i < len - 1) {
            if (k == -1 || p[i] == p[k]) {
                ++i;
                ++k;
                if (p[i] != p[k])
                    // 较之前next数组求法，改动在下面4行
                    next[i] = k; // 之前只有这一行
                else
                    next[i] = next[k];
            } else
                k = next[k];
        }
        return next;
    }
}