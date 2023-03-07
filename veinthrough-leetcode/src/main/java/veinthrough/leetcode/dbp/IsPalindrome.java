package veinthrough.leetcode.dbp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第9题：
 * 给你一个【整数】 x ，如果 x 是一个回文整数，返回 true ；否则，返回 false 。
 * 回文数是指正序（从左向右）和倒序（从右向左）读都是一样的整数。例如，121 是回文，而 123 不是。
 *
 * 第125题：
 * 给定一个【字符串】，验证它是否是回文串，只考虑字母和数字字符，可以忽略字母的大小写。
 * 说明：本题中，我们将空字符串定义为有效的回文串。
 */
@Slf4j
public class IsPalindrome {
    @Test
    public void test9() {
        Stream.of(121, -121, 10, 1221)
                .forEach(num -> log.info(methodLog("" + num,
                        "" + isPalindrome(num))));
    }

    @Test
    public void test125() {
        Stream.of(
                "A man, a plan, a canal: Panama",
                "race a car",
                ".,")
                .forEach(str -> log.info(methodLog(str, "" + isPalindrome(str))));
    }

    /**
     * 1. 方法1:
     * 我们试图将x分成两半：整个过程我们不断将left除以10，将right数字乘上10
     * 实际上比较的是left/right两边由各digit组合成的数, 省去了拆成digit
     *
     * 2. 方法2: 字符串操作，实际上类似于(也是)digit操作
     */
    private boolean isPalindrome(int x) {
        // boundary
        if (x < 0 || x == 10) return false;
        if (x < 10) return true;


        //
        return isPalindromeByLRNum(x);
//        return isPalindromeByString(x);
    }

    /**
     * 方法1:
     * 我们试图将x分成两半：整个过程我们不断将left除以10，将right数字乘上10
     * 实际上比较的是left/right两边由各digit组合成的数, 省去了拆成digit
     *
     * 关于digits <--> num, 见文档《#digit》
     * [1,2,3] <--> 123,
     * [1,2,3] <--> 321
     */
    public boolean isPalindromeByLRNum(int x) {
        int left, right, digit;
        left = x;
        right = 0;
        while (left > right) {
            digit = left % 10;
            left /= 10;
            right = right * 10 + digit;
        }
        return left == right || left == right / 10;
    }

    /**
     * 方法2: 字符串操作，实际上类似于(也是)digit操作
     */
    @SuppressWarnings("unused")
    private boolean isPalindromeByString(int x) {
        if (x < 0) return false;
        if (x < 10) return true;
        // 直接使用String.reverse()
        String str = String.valueOf(x);
        StringBuilder sb = new StringBuilder(str);
        return str.equals(sb.reverse().toString());
    }

    private boolean isPalindrome(String s) {
        if (s == null || s.length() == 1) return true;

        int i = 0, j = s.length() - 1;
        while (i < j) {
            // i<s.length()
            while (i < s.length() && !Character.isLetterOrDigit(s.charAt(i))) i++;
            // j>=0
            while (j >= 0 && !Character.isLetterOrDigit(s.charAt(j))) j--;
            // i<s.length() && j>=0
            if (i != j && i < s.length() && j >= 0) {
                char left = s.charAt(i++);
                left = Character.isLetter(left) ? Character.toLowerCase(left) : left;
                char right = s.charAt(j--);
                right = Character.isLetter(right) ? Character.toLowerCase(right) : right;
                if (left != right) return false;
            }
        }
        return true;
    }
}