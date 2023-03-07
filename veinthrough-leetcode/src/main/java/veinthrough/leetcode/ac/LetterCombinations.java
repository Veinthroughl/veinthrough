package veinthrough.leetcode.ac;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第17题(排列组合)：
 * 给定一个仅包含数字2-9的字符串，返回所有它能表示的字母组合。答案可以按 任意顺序 返回。
 * 给出数字到字母的映射如下（与电话按键相同）。注意 1 不对应任何字母。
 * 2: a,b,c
 * 3: d,e,f
 * 4: g,h,i
 * 5: j,k,l
 * 6: m,n,o
 * 7: p,q,r,s
 * 8: t,u,v
 */
@Slf4j
@SuppressWarnings("Duplicates")
public class LetterCombinations {
    @Test
    public void test17() {
        // 按结果顺序填充
        Stream.of("237", "56")
                .forEach(str ->
                        log.info(methodLog(str, letterCombinations(str).toString())));
    }

    @Test
    public void test2() {
        // 按字符填充
        Stream.of("237", "56")
                .forEach(str ->
                        log.info(methodLog(str, letterCombinations2(str).toString())));
    }

    /**
     * 1  1  1
     * 2  1  1
     * 1  2  1
     * 2  2  1
     * 1  1  2
     * 2  1  2
     * 1  2  2
     * 2  2  2
     * 按结果顺序填充：1-8
     */
    private List<String> letterCombinations(String _digits) {
        // 1. empty
        List<String> result = new ArrayList<>();
        if (_digits.length() == 0) return result;
        int[] lens = new int[]{3, 3, 3, 3, 3, 4, 3, 4};
        char[][] map = new char[][]{
                {'a', 'b', 'c'},
                {'d', 'e', 'f'},
                {'g', 'h', 'i'},
                {'j', 'k', 'l'},
                {'m', 'n', 'o'},
                {'p', 'q', 'r', 's'},
                {'t', 'u', 'v'},
                {'w', 'x', 'y', 'z'}};
        // 2. 1 digit
        if (_digits.length() == 1) {
            for (int i = _digits.charAt(0) - '2', j = 0; j < lens[i]; j++)
                result.add(String.valueOf(map[i][j]));
            return result;
        }

        // 3. 计算所有排列的个数
        char[] digits = _digits.toCharArray();
        int length = 1;
        for (char digit1 : digits) {
            length *= lens[digit1 - '2'];
        }
        char[][] array = new char[length][digits.length];
        // 4. 按每个字符填充
        for (int i = 0, gap = 1, digit = digits[i] - '2', len = lens[digit]; // len of each digit
             i < digits.length;
             i++, gap *= len) {
            char[] each = map[digit]; // each digit
            // 按结果顺序填充：1-8
            for (int j = 0; j < length; j++) {
                if (gap == 1) array[j][i] = each[j % len];
                    // 使用除法效率太低
                else //noinspection IntegerDivisionInFloatingPointContext
                    array[j][i] = each[(int) Math.ceil((j + 1) / gap) % len];
            }
        }

        for (int i = 0; i < length; i++)
            result.add(String.valueOf(array[i]));
        return result;
    }

    /**
     * 1  1  1
     * 2  1  1
     * 1  2  1
     * 2  2  1
     * 1  1  2
     * 2  1  2
     * 1  2  2
     * 2  2  2
     * 按字符填充：
     * 第一个字符: 按gap=1填充
     * 第二个字符：按gap=2填充
     * 第三个字符：按gap=4填充
     */
    private List<String> letterCombinations2(String _digits) {
        // 1. empty
        List<String> result = new ArrayList<>();
        if (_digits.length() == 0) return result;
        int[] lens = new int[]{3, 3, 3, 3, 3, 4, 3, 4};
        char[][] map = new char[][]{
                {'a', 'b', 'c'},
                {'d', 'e', 'f'},
                {'g', 'h', 'i'},
                {'j', 'k', 'l'},
                {'m', 'n', 'o'},
                {'p', 'q', 'r', 's'},
                {'t', 'u', 'v'},
                {'w', 'x', 'y', 'z'}};
        // 2. 1 digit
        if (_digits.length() == 1) {
            for (int i = _digits.charAt(0) - '2', j = 0; j < lens[i]; j++)
                result.add(String.valueOf(map[i][j]));
            return result;
        }

        // 3. 计算所有排列的个数
        char[] digits = _digits.toCharArray();
        int length = 1;
        for (char digit1 : digits) {
            length *= lens[digit1 - '2'];
        }
        /*
         * 1  1  1
         * 2  1  1
         * 1  2  1
         * 2  2  1
         * 1  1  2
         * 2  1  2
         * 1  2  2
         * 2  2  2
         */
        char[][] array = new char[length][digits.length];
        // (1) handle first digit, 把第一个提出来是为了提高效率
        int digit = digits[0] - '2';
        int len = lens[digit];
        char[] each = map[digit];
        int i, j, k;
        for (i = 0; i < length; i++) {
            array[i][0] = each[i % len];
        }
        int gap = len, step;
        char ch;
        // (2) handle other digits
        for (i = 1; i < digits.length; gap *= len, i++) {
            digit = digits[i] - '2';
            len = lens[digit];
            each = map[digit];
            // 按字符填充
            for (j = 0, step = 0; j < length / gap; j++, step += gap) {
                ch = each[j % len];
                for (k = 0; k < gap; k++)
                    // index: j*gap+k;
                    array[step + k][i] = ch;
            }
        }

        for (i = 0; i < length; i++)
            result.add(String.valueOf(array[i]));
        return result;
    }
}