package veinthrough.leetcode;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第6题：Z字形变换，
 * 将一个给定字符串 s 根据给定的行数 numRows ，以从上往下、从左到右进行 Z 字形排列。
 * 比如输入字符串为 "PAYPALISHIRING" 行数为 3 时，排列如下：
 * #    P   A   H   N
 * #    A P L S I I G
 * #    Y   I   R
 * 之后，你的输出需要从左往右逐行读取，产生出一个新的字符串，比如："PAHNAPLSIIGYIR"。
 */
@Slf4j
public class ZConvert {
    @Test
    public void test6() {
        Stream.of(
                "LEETCODEISHIRING", "", "AB")
                .forEach(str ->
                        Stream.of(1, 2, 3, 4)
                                .forEach(rows ->
                                        log.info(methodLog(
                                                "\"" + str + "\"" + "(" + rows + " Z rows)",
                                                "\"" + convert(str, rows) + "\""))));
    }

    private String convert(String s, int numRows) {
        // (1)
        if (s.length() <= numRows) return s;
        // (2)
        if (numRows == 1) return s;

        char[] chars = s.toCharArray();
        char[] result = new char[chars.length];
        // (3) 2行实际上就是奇偶分类
        if (numRows == 2) {
            for (int i = 0, j = 0, k = (chars.length + 1) / 2; i < chars.length; i += 2) {
                result[j++] = chars[i];
                if (i + 1 < chars.length) result[k++] = chars[i + 1];
            }
        }
        // (4) 往上/往下
        int downUpDelta, upDownDelta;
        // i代表行，一行一行的处理
        for (int i = 0, j = i, k = 0; i <= numRows - 1; i++, j = i) {
            result[k++] = chars[j];
            downUpDelta = 2 * (i == numRows - 1 ? numRows - 1 : numRows - 1 - i);
            upDownDelta = 2 * (i == 0 ? numRows - 1 : i);
            // 处理每一行
            while (j < chars.length) {
                // handle down up
                j += downUpDelta;
                if (j < chars.length) {
                    result[k++] = chars[j];

                    // handle up down
                    j += upDownDelta;
                    if (j < chars.length) result[k++] = chars[j];
                }
            }
        }
        return String.valueOf(result);
    }
}