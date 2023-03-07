package veinthrough.leetcode.string.substring;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.generic.Pair;

import java.util.*;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第3题：最长无重复字符子串
 * 给定一个字符串，请你找出其中不含有重复字符的 最长子串 的长度
 * only letters, 因为只有字母, 使用offsets减少了数组的长度
 *
 * 第5题：最长回文子串
 * 给你一个字符串 s，找到 s 中最长的回文子串。
 *
 * 第30题, 串联所有单词的子串{@link JointSubstring}
 * 给定一个字符串s和一些【长度相同】的单词words。找出s中恰好可以由words中所有单词串联形成的子串的起始位置。
 * 注意子串要与words中的单词完全匹配，中间不能有其他字符，但不需要考虑words中单词串联的顺序。
 *
 * 第32题：最长有效括号
 * 给你一个只包含 '(' 和 ')' 的字符串，找出最长有效（格式正确且连续）括号子串的长度。
 * 参考文档《最长有效括号》
 *
 * 第76题：最小覆盖子串，
 * 给你一个字符串s 、一个字符串t。返回 s 中涵盖 t 所有字符的最小子串。如果s中不存在涵盖 t 所有字符的子串，则返回空字符串 "" 。
 * 注意：
 * 对于 t 中重复字符，我们寻找的子字符串中该字符数量必须不少于 t 中该字符数量。
 * 如果 s 中存在这样的子串，我们保证它是唯一的答案。
 * 类似于{@link JointSubstring}中是要覆盖所有words,这里是要覆盖所有chars
 *
 * 双指针(滑动窗口)
 * (1) {@link #minWindow(String, String)}
 * (2) {@link #longestSubstringWithoutRepetition2(String)}
 * (3) {@link JointSubstring}
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class LongestSubstring {
    private static final int CHAR_SCALE = 256;

    @Test
    public void test3() {
        Stream.of(
                "abcadbcbb", "bbbbb", "pwwkew", "pwacwkew", "pwacdwkew",
                "aabaab!bb")
                .forEach(str -> log.info(
                        methodLog("string", str,
                                "双指针(自解)", longestSubstringWithoutRepetition2(str),
                                "双指针(题解)", "" + longestSubstringWithoutRepetition(str))));
    }

    @Test
    public void test5() {
        Stream.of(
                "babad", "cbbd", "babadabac")
                .forEach(str -> log.info(methodLog(
                        "string", str,
                        "By dp", longestPalindromeSubstring1(str),
                        "由一个中心向两边扩展", longestPalindromeSubstring2(str))));
    }


    @Test
    public void test32() {
        Stream.of(
                ")(((((()())()()))()(()))(",//22
                ")()())",//4
                "(()",//2
                "")//0
                .forEach(s -> log.info(methodLog(
                        s, "" + longestValidParentheses(s))));
    }

    @Test
    public void test76() {
        Stream.of(
                Pair.of("aabbabcccccdad", "abcd"),
                Pair.of("aaaaaaaaaaaabbbbbcdd", "abcdd"),
                Pair.of("cabwefgewcwaefgcf", "cae"),
                Pair.of("a", "b"),
                Pair.of("ab", "b"),
                Pair.of("a", "aa"),
                Pair.of("ADOBECODEBANC", "ABC"),
                Pair.of("a", "a"))
                .forEach(pair -> log.info(methodLog(
                        "String", pair.getFirst(),
                        "Subsequence", pair.getSecond(),
                        "Min window", minWindow(pair.getFirst(), pair.getSecond()))));
    }

    /**
     * 最长无重复字符子串方法1(自解)：使用类似与哈希(数组实现)
     */
    public static String longestSubstringWithoutRepetition2(String s) {
        String result = "";

        // letter -> position
        int[] positions = new int[CHAR_SCALE];
        Arrays.fill(positions, -1);
        // position -> letter
        int[] letters = new int[CHAR_SCALE];
        Arrays.fill(letters, -1);
        int len = 0;
        int max = len;
        char[] chars = s.toCharArray();
        // int[] chars = s.chars().toArray();
        for (char ch : chars) {
            // (1) no collision
            if (positions[ch] == -1) {
                positions[ch] = len;
                letters[len] = ch;
                len += 1;
                if (len > max) {
                    max = len;
                    result = new String(letters, 0, max);
                }
                // (2) handle collision
            } else {
                int collision = positions[ch];
                // 向左移动的个数
                int shift = collision + 1;
                // 剩余的个数
                int left = len - shift;
                // shift
                for (int j = 0; j < len; j++) {
                    if (j < shift) positions[letters[j]] = -1;
                    else positions[letters[j]] -= shift;
                    if (j < left) letters[j] = letters[j + shift];
                    else letters[j] = -1;
                }
                // add current
                positions[ch] = left;
                letters[left] = ch;
                len = left + 1;
            }
        }
        return result;
    }

    /**
     * 最长无重复字符子串方法2(题解)：其实和方法1原理差不多
     * 只记录了每个字母的计数，没记录位置，因为不需要输出
     */
    public static int longestSubstringWithoutRepetition(String s) { //双指针滑动窗口
        int[] map = new int[CHAR_SCALE];
        int max = 0;
        int n = s.length();
        int[] chars = s.chars().toArray();
        for (int i = 0, j = 0; j < n; j++) { // 当前判断是否重复的串为s[i..j]
            map[chars[j]]++;
            while (map[chars[j]] > 1) {
                map[chars[i++]]--;  // i指针右移，直到遇到冲突的字符
            }
            if (j - i + 1 > max)
                max = j - i + 1;
        }
        return max;
    }

    /**
     * 最长回文子串方法1：动态规划
     */
    private String longestPalindromeSubstring1(String s) {
        int len;
        if ((len = s.length()) <= 1) return s;

        // boundary
        char[] chars = s.toCharArray();
        if (len == 2)
            return chars[0] == chars[1] ?
                    s : new String(chars, 0, 1);

        boolean[][] palindrome = new boolean[len][len];
        int mi = 0, mg = 0;
        // 1. 1 char/2 chars
        for (int i = 0; i < len; i++) {
            palindrome[i][i] = true;  // 1 char都为true
            if (i < len - 1 && (palindrome[i][i + 1] = chars[i] == chars[i + 1])) {
                mi = i;
                mg = 1;
            }
        }
        // 2. 3... chars
        // 如果chars[i] == chars[i + gap]，
        // 则palindrome[i][i+gap] = palindrome[i + 1][i + gap - 1]
        for (int gap = 2; gap < len; gap++) {
            for (int i = 0; i < len - gap; i++) {
                if ((chars[i] == chars[i + gap]) && palindrome[i + 1][i + gap - 1]) {
                    palindrome[i][i + gap] = true;
                    if (gap > mg) {
                        mi = i;
                        mg = gap;
                    }
                }
            }
        }
        return new String(chars, mi, mg + 1);
    }

    /**
     * 最长回文子串方法2：由一个中心向两边扩展
     */
    private String longestPalindromeSubstring2(String s) {
        int n;
        // 1. s.length() <= 1
        if ((n = s.length()) <= 1) return s;
        // 2.
        char[] chars = s.toCharArray();
        String sub = s.substring(0, 1);
        int len, offset, max = 1;
        for (int i = 1; i < n; i++) {
            // (1) 由(i)一个向两边扩散
            offset = 1;
            while (i - offset >= 0 && i + offset < chars.length &&
                    chars[i - offset] == chars[i + offset]) offset++;
            len = 2 * offset - 1;
            if (len > max) {
                max = len;
                sub = s.substring(i - offset + 1, i + offset);
            }

            // (2) 由两个(i-1,i)向两边扩散
            if (chars[i] == chars[i - 1]) {
                offset = 1;
                while (i - offset - 1 >= 0 && i + offset < chars.length &&
                        chars[i - offset - 1] == chars[i + offset])
                    offset++;
                len = 2 * offset;
                if (len > max) {
                    max = len;
                    sub = s.substring(i - offset, i + offset);
                }
            }
        }
        return sub;
    }

    private int longestValidParentheses(String s) {
        // 1.
        if (s == null || s.length() < 2) return 0;
        if (s.length() == 2)
            return s.charAt(0) == '(' && s.charAt(1) == ')' ? 2 : 0;
        return _longestValidParenthesesDP2(s);
    }

    /**
     * 最长有效括号方法1(自解)：dp
     * 1. 以下思维是错误的：实际上(())(())有效但是不满足以下(1)(2)(3)任意一个
     * s[i,j]只在下面几种情况下有效,且s[i]必须为'(', s[j]必须为')':
     * (1) s[i+1,j-1]为有效, 且s[i]为"(", 且s[j]为")"
     * (2) s[i+2,j]有效, 且s[i]为"(", 且s[i+1]为")"
     * (3) s[i,j-2]有效, 且s[j-1]为"(", 且s[j]为")"
     *
     * 2. 正确的思维：
     * 1. 有效的括号字串必然为偶数个
     * 2. 长度为2：就是寻找"()"
     * 3. 长度为n, s[i]必须为'(', s[j]必须为')':
     * (1) (...i-2...)(...j-2...)， i+j=n,
     * (2) 如果i=0或者j=0实际上相当于: (...n-2...)
     */
    @SuppressWarnings("unused")
    public int _longestValidParenthesesDP(String s) {
        char[] chs = s.toCharArray();
        int len = chs.length;
        int max = 0;
        boolean[][] valid = new boolean[len - 1][len];
        // 1. 长度为2
        for (int i = 0; i <= len - 2; i++) {
            if (chs[i] == '(' && chs[i + 1] == ')') {
                max = 2;
                valid[i][i + 1] = true;
            }
        }
        // 2.
        for (int gap = 4; gap <= len; gap += 2) {
            for (int i = 0, j; i <= len - gap; i++) {
                j = i + gap - 1;
                // 3.(2)
                // 如果3.(2)已经为true, 就没有必要去求3.(1)了
                if (chs[i] == '(' && chs[j] == ')' &&
                        !(valid[i][j] |= valid[i + 1][j - 1])) {
                    // 3.(1)
                    for (int k = i + 1; k <= j - 2; k += 2) {
                        valid[i][j] |= valid[i][k] && valid[k + 1][j];
                    }
                }
                if (valid[i][j]) max = gap;
            }
        }
        return max;
    }

    /**
     * 最长有效括号方法2：dp
     * 我们定义dp[i]表示以下标i字符结尾的最长有效括号的长度。我们将dp数组全部初始化为0。
     * 显然有效的子串一定以 ‘)’ 结尾，因此我们可以知道以 ‘(’ 结尾的子串对应的dp 值必定为0，我们只需要求解‘)’ 在dp 数组中对应位置的值。
     * 说明见《最长有效括号》
     * 我们从前往后遍历字符串求解dp 值，我们每两个字符检查一次：
     * (1) s[i]=‘)’且 s[i−1]=‘(’，也就是字符串形如 “……()”(如下图中的[17,18], 最终可以与[3,16]合并)，我们可以推出：
     * dp[i]=dp[i−2]+2, 我们可以进行这样的转移，是因为结束部分的 "()" 是一个有效子字符串，并且将之前有效子字符串的长度增加了2 。
     * (2) s[i]=‘)’ 且s[i−1]=‘)’，也就是字符串形如 “……))”(如上图中的[22,23])，我们可以推出：
     * 如果s[i−dp[i−1]−1]=‘(’，那么dp[i]=dp[i−1]+dp[i−dp[i−1]−2]+2：
     * i=23, dp[i-1]=dp[22]=20, 为[3,22];
     * 因为s[i-dp[i-1]-1]=s[2]=’(’, dp[23]=dp[22]+dp[1]+2;
     * 也就是说图中蓝色的2和23可以组合成一个，再与黄色的[3,22]和[0,1]合并。
     */
    public int _longestValidParenthesesDP2(String s) {
        char[] chs = s.toCharArray();
        int len = chs.length;
        // valid[i]: 以i结尾的最大有小括号
        int[] valid = new int[len];
        // 1. 前2个
        valid[1] = chs[0] == '(' && chs[1] == ')' ? 2 : 0;
        int maxValid = valid[1];
        // 2. 以2结尾
        for (int i = 2; i < len; i++) {
            if (chs[i] == ')') {
                if (chs[i - 1] == '(')
                    valid[i] = valid[i - 2] + 2;
                else if (i - valid[i - 1] > 0 && chs[i - valid[i - 1] - 1] == '(') {
                    valid[i] = valid[i - 1] + 2 +
                            (i - valid[i - 1] >= 3 ?
                                    valid[i - valid[i - 1] - 2] : 0);
                }
                maxValid = max(maxValid, valid[i]);
            }
        }
        return maxValid;
    }

    /**
     * 最长有效括号方法3：栈
     * 撇开方法二提及的动态规划方法，相信大多数人对于这题的第一直觉是找到每个可能的子串后判断它的有效性，但这样的时间复杂度会达到 O(n^3)，
     * 无法通过所有测试用例。但是通过栈，我们可以在遍历给定字符串的过程中去判断到目前为止扫描的子串的有效性，同时能得到最长有效括号的长度。
     *
     * 具体做法是我们始终保持栈底元素为当前已经遍历过的元素中「最后一个没有被匹配的右括号的下标」，这样的做法主要是考虑了边界条件的处理，
     * 栈里其他元素维护左括号的下标：
     * (1) 对于遇到的每个 ‘(’ ，我们将它的下标放入栈中
     * (2) 对于遇到的每个)’ ，我们先弹出栈顶元素表示匹配了当前右括号：
     * > 如果栈为空，说明当前的右括号为没有被匹配的右括号，我们将其下标放入栈中来更新我们之前提到的「最后一个没有被匹配的右括号的下标」
     * > 如果栈不为空，当前右括号的下标减去栈顶元素即为「以该右括号为结尾的最长有效括号的长度」
     * (栈中的元素要么是最后一个未匹配的’)’，要么是还未匹配的’(’)
     * 我们从前往后遍历字符串并更新答案即可。
     *
     * 需要注意的是，如果一开始栈为空，第一个字符为左括号的时候我们会将其放入栈中，这样就不满足提及的「最后一个没有被匹配的右括号的下标」，
     * 为了保持统一，我们在一开始的时候往栈中放入一个值为 -1的元素。
     *
     * 思考：
     * 栈中的元素要么是最后一个未匹配的’)’，要么是还未匹配的’(’
     */
    @SuppressWarnings("unused")
    public int _longestValidParenthesesStack(String s) {
        char[] chs = s.toCharArray();
        int maxValid = 0;
        Stack<Integer> stack = new Stack<>();
        stack.push(-1);
        for (int i = 0; i < chs.length; i++) {
            if (chs[i] == '(') stack.push(i);
            else {
                stack.pop();
                if (stack.empty()) stack.push(i);
                else
                    maxValid = max(maxValid, i - stack.peek());
            }
        }
        return maxValid;
    }

    /**
     * 最长有效括号方法4, 贪心算法(模仿栈)：
     * 在此方法中，我们利用两个计数器left和right 。
     * (1) 从左到右遍历字符串，对于每个’(’,left++; 对于每个’)’, right++。
     * (2) 每当left==right时，我们计算当前有效字符串的长度，并且记录目前为止找到的最长子字符串。
     * (3) 当right>left时，我们将清0: left=right=0。
     * 这样的做法贪心地考虑了以当前字符下标结尾的有效括号长度，每次当右括号数量多于左括号数量的时候之前的字符我们都扔掉不再考虑，
     * 重新从下一个字符开始计算，但这样会漏掉一种情况，就是遍历的时候左括号的数量始终大于右括号的数量，即 (() ，
     * 这种时候最长有效括号是求不出来的。
     * 解决的方法也很简单，我们只需要从右往左遍历用类似的方法计算即可，只是这个时候判断条件反了过来;
     * 这样我们就能涵盖所有情况从而求解出答案。
     *
     * 思考：
     * 任何时候都保证了left>=right, 否则清0：left=right=0：
     * 所有保证任何时候的left==right，肯定是’(‘开头且出现left==right的时候必然是’(’开头，’)’结尾：
     * (1) 如不可能出现’)(’的时候left==right, 因为第1个’)’的时候会清0；
     * (2) 如’)())(()’, 第1个’)’和第3个’)’的时候会清0，第二个’)’正好是匹配前面的’(’
     */
    @SuppressWarnings("unused")
    public int _longestValidParenthesesGreed(String s) {
        int left = 0, right = 0;
        int maxValid = 0;
        char[] chs = s.toCharArray();
        for (char ch : chs) {
            if (ch == '(') left++;
            else right++;
            if (right > left) left = right = 0;
            else if (right == left) maxValid = max(maxValid, 2 * right);
        }

        left = right = 0;
        for (int i = chs.length - 1; i >= 0; i--) {
            if (chs[i] == '(') left++;
            else right++;
            if (left > right) left = right = 0;
            else if (right == left) maxValid = max(maxValid, 2 * left);
        }
        return maxValid;
    }

    /**
     * 方法1(题解方法)：滑动窗口
     * (1) map记录每个字符的数量，遇到map中的字符, count-1
     * (2) map中的value(count)都<=0时表示全部匹配，滑动start
     */
    @SuppressWarnings("ConstantConditions")
    public String minWindow(String s, String t) {
        // boundary

        //
        char[] schs = s.toCharArray();
        char[] tchs = t.toCharArray();
        // 1. 根据t构造map
        Map<Character, Integer> map = new HashMap<>();
        for (char tch : tchs) map.merge(tch, 1, ((old, increment) -> old + increment));

        // 2. start, 去除前面在map中不存在的字符
        int start = 0, end = -1;
        while (start < schs.length && map.get(schs[start]) == null) start++;

        // 3. 滑动窗口
        // (1) 遇到map中的字符, count-1
        // (2) 如果全部匹配(确定end), 滑动start
        // map递减而不是递增避免map的比较，更容易判断是否是全部匹配
        List<Integer> occurs = new ArrayList<>();
        int minLen = schs.length;
        char now;
        for (int i = start, index; i < schs.length; i++) {
            now = schs[i];
            if (map.containsKey(now)) {
                occurs.add(i);
                map.compute(now, (k, v) -> v - 1);
                while (check(map)) {
                    if (end == -1 || i - start < minLen) {
                        end = i;
                        minLen = i - start;
                    }
                    index = occurs.get(0);
                    map.compute(schs[index], (k, v) -> v + 1);
                    occurs.remove(0);
                    start = !occurs.isEmpty() ? occurs.get(0) : index + 1;
                }
            }
        }
        // start有可能是下一次的，而下一次可能没有对应的end
        // 所有只能用end
        return end == -1 ? "" : s.substring(end - minLen, end + 1);
    }

    /**
     * map中所有的value(count)<=0表示全部匹配
     */
    private boolean check(Map<Character, Integer> map) {
        for (Integer count : map.values())
            if (count > 0) return false;
        return true;
    }

    /**
     * 方法2(开始的想法，错误)，
     * 示例: aaaaaaaaaaaabbbbbcdd, abcdd
     * 因为遇到超出数量的就滑动，所以遇到第2个b就会把第1个b的前面的a给移出，最终结果找不到匹配
     * 与{@link #minWindow(String, String)}的区别：
     * (1) 某个字符超出了数量(不一定找到匹配)就滑动, 右移(重新补充map)
     * 实际上应该找到了匹配才能滑动
     * (2) count为0时从map删除/允许count为负值
     * map为空时表示全部匹配/map中的value(count)都<=0时表示全部匹配
     */
    @SuppressWarnings("unused")
    private String minWindow2(String s, String t) {
        // boundary

        //
        char[] schs = s.toCharArray();
        char[] tchs = t.toCharArray();
        // 1. 根据t构造tmap
        Map<Character, Integer> tmap = new HashMap<>();
        for (char tch : tchs) tmap.merge(tch, 1, ((old, increment) -> old + increment));

        // 2. start, 去除前面在tmap中不存在的字符
        int start = 0, end = -1;
        while (start < schs.length && tmap.get(schs[start]) == null) start++;

        // 3.
        // map递减而不是递增避免map的比较
        // 当map为空时表示匹配成功
        Map<Character, Integer> map = new HashMap<>(tmap);
        List<Integer> occurs = new ArrayList<>();
        int minLen = schs.length;
        Integer count;
        char now, ch;
        int index;
        for (int i = start; i < schs.length; i++) {
            now = schs[i];
            // 1. tmap存在该字符
            if (tmap.get(now) != null) {
                occurs.add(i);
                // (1) 某个字符超出了数量(不一定找到匹配)就滑动, 右移(重新补充map)
                // 实际上应该找到了匹配才能滑动
                if ((count = map.get(now)) == null) {
                    index = 0;
                    // 滑动到另一个和now相同的字符
                    while ((ch = schs[occurs.get(index)]) != now) {
                        map.merge(ch, 1, ((old, newValue) -> old + newValue));
                        index++;
                    }
                    start = occurs.get(index + 1);
                    occurs = occurs.subList(index + 1, occurs.size());

                    // map为空说明只是首位替换(当前字符去替换start)全部匹配
                    if (map.isEmpty()) {
                        if (end == -1 || i - start < minLen) {
                            end = i;
                            minLen = i - start;
                        }
                    }
                }
                // 最后一次
                // (1) map中删除
                // (2) 可能全部匹配(map为空)
                else if (count.equals(1)) {
                    map.remove(now);
                    // map为空说明全部匹配
                    if (map.isEmpty()) {
                        if (end == -1 || i - start < minLen) {
                            end = i;
                            minLen = i - start;
                        }
                    }
                }
                // 非最后一次(map中出现次数-1)
                else map.put(now, count - 1);
            }
            // 2. tmap不存在该字符
        }
        // start有可能是下一次的，所有只能用end
        return end == -1 ? "" : s.substring(end - minLen, end + 1);
    }
}