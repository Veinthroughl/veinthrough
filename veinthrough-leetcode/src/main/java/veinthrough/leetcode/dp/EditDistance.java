package veinthrough.leetcode.dp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.generic.Pair;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第72题：编辑距离，
 * 给你两个单词 word1 和 word2， 请返回将 word1 转换成 word2 所使用的最少操作数。
 * 你可以对一个单词进行如下三种操作：
 * 插入一个字符
 * 删除一个字符
 * 替换一个字符
 */
@Slf4j
public class EditDistance {
    @Test
    public void test72() {
        Stream.of(
                Pair.of("horse", "ros"),                // 3
                Pair.of("intention", "execution"))      // 5
                .forEach(pair -> log.info(methodLog(
                        pair.getFirst() + " -> " + pair.getSecond(),
                        "" + editDistance(pair.getFirst(), pair.getSecond()))));
    }

    /**
     * 更详细说明参考文档。
     * 1. 一开始试想着用最长公共子序列来做, 但是：
     * (1) 是不是子序列越长编辑距离就越短？需要处理序列中每个元素中间的编辑距离，然后累加
     * (2) 最长公共子序列可以有多个
     * 2. 用 D[i][j] 表示 A 的前 i 个字母和 B 的前 j 个字母之间的编辑距离。
     * 如上所述，当我们获得 D[i][j-1]，D[i-1][j] 和 D[i-1][j-1] 的值之后就可以计算出 D[i][j]。
     * (1) D[i][j-1] 为 A 的前 i 个字符和 B 的前 j - 1 个字符编辑距离的子问题。即对于 B 的第 j 个字符，
     * 我们在 A 的末尾添加了一个相同的字符，那么 D[i][j] 最小可以为 D[i][j-1] + 1；
     * (2) D[i-1][j] 为 A 的前 i - 1 个字符和 B 的前 j 个字符编辑距离的子问题。即对于 A 的第 i 个字符，
     * 我们在 B 的末尾添加了一个相同的字符，那么 D[i][j] 最小可以为 D[i-1][j] + 1；
     * (3) D[i-1][j-1] 为 A 前 i - 1 个字符和 B 的前 j - 1 个字符编辑距离的子问题。即对于 B 的第 j 个字符，
     * 我们修改 A 的第 i 个字符使它们相同，那么 D[i][j] 最小可以为 D[i-1][j-1] + 1。
     * 特别地，如果 A 的第 i 个字符和 B 的第 j 个字符原本就相同，那么我们实际上不需要进行修改操作。
     * 在这种情况下，D[i][j] 最小可以为 D[i-1][j-1]。
     * 那么我们可以写出如下的状态转移方程：
     * 若 A 和 B 的最后一个字母相同：
     * D[i][j]=min(D[i][j−1]+1,D[i−1][j]+1,D[i−1][j−1])=1+min(D[i][j−1],D[i−1][j],D[i−1][j−1]−1)
     * 若 A 和 B 的最后一个字母不同：
     * D[i][j]=1+min(D[i][j−1],D[i−1][j],D[i−1][j−1])
     * 3. 对于边界情况
     * 一个空串和一个非空串的编辑距离为 D[i][0] = i 和 D[0][j] = j，D[i][0] 相当于对word1执行i次删除操作，D[0][j] 相当于对 word1执行 j 次插入操作。
     * 4. 为什么我们总是在单词 A 和 B 的末尾插入或者修改字符，能不能在其它的地方进行操作呢？
     * 答案是可以的，但是我们知道，操作的顺序是不影响最终的结果的。例如对于单词 cat，我们希望在 c 和 a 之间添加字符 d 并且将字符 t 修改为字符 b，
     * 那么这两个操作无论为什么顺序，都会得到最终的结果 cdab。
     */
    private int editDistance(String word1, String word2) {
        int len1 = word1.length(), len2 = word2.length();
        // boundary
        if (len1 == 0) return len2;
        if (len2 == 0) return len1;

        //
        char[] chs1 = word1.toCharArray();
        char[] chs2 = word2.toCharArray();
        int[][] distance = new int[len1 + 1][len2 + 1];
        for (int i = 0; i <= len1; i++) distance[i][0] = i;
        for (int j = 0; j <= len2; j++) distance[0][j] = j;

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                distance[i][j] = chs1[i - 1] == chs2[j - 1] ? distance[i - 1][j - 1] : distance[i - 1][j - 1] + 1;
                distance[i][j] = distance[i][j - 1] > distance[i - 1][j] ?
                        Math.min(distance[i][j], distance[i - 1][j] + 1) :
                        Math.min(distance[i][j], distance[i][j - 1] + 1);
            }
        }
        return distance[len1][len2];
    }
}
