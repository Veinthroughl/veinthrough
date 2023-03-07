package veinthrough.leetcode.ac;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 面试题 16.11. 跳水板：
 * 你正在使用一堆木板建造跳水板。有两种类型的木板，其中长度较短的木板长度为shorter，长度较长的木板长度为longer。
 * 你必须正好使用k块木板。编写一个方法，生成跳水板所有可能的长度。
 * 返回的长度需要从小到大排列。
 *
 * 示例 1
 * 输入：
 * shorter = 1
 * longer = 2
 * k = 3
 * 输出： [3,4,5,6]
 * 解释：
 * 可以使用 3 次 shorter，得到结果 3；使用 2 次 shorter 和 1 次 longer，得到结果 4 。以此类推，得到最终结果。
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class DivingBoard {
    @Test
    public void test_16_11() {
        Stream.of(
                divingBoard(1, 1, 0),
                divingBoard(1, 2, 3),
                divingBoard(2, 5, 3))
                .forEach(array ->
                        log.info(methodLog(Arrays.toString(array))));
    }

    @Test
    public void test2() {
        Stream.of(
                divingBoard2(1, 1, 0),
                divingBoard2(1, 2, 3),
                divingBoard2(2, 5, 3))
                .forEach(array ->
                        log.info(methodLog(Arrays.toString(array))));
    }

    private int[] divingBoard(int shorter, int longer, int k) {
        // boundary
        if (k == 0) return new int[0];
        if (shorter == longer) return new int[]{k * shorter};
        if (k == 1) return new int[]{shorter, longer};

        // 
        final int delta = longer - shorter;
        return IntStream.iterate(k * shorter, num -> num + delta).limit(k + 1).toArray();
    }

    private int[] divingBoard2(int shorter, int longer, int k) {
        // boundary
        if (k == 0) return new int[0];
        if (shorter == longer) return new int[]{k * shorter};
        if (k == 1) return new int[]{shorter, longer};

        // 
        final int delta = longer - shorter;
        int[] result = new int[k + 1];
        result[0] = k * shorter;
        for (int i = 1; i <= k; i++)
            result[i] = result[i - 1] + delta;
        return result;
    }

}