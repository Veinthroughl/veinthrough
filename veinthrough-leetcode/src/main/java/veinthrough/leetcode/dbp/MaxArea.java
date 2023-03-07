package veinthrough.leetcode.dbp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static java.lang.Math.min;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第11题：
 * 给你 n 个非负整数 a1，a2，...，an，每个数代表坐标中的一个点(i, ai) 。在坐标内画 n 条垂直线，
 * 垂直线i的两个端点分别为(i, ai) 和 (i, 0)。找出其中的两条线，使得它们与x轴共同构成的容器可以容纳最多的水。
 * 说明：你不能倾斜容器。
 * 【图见文档】
 */
@Slf4j
public class MaxArea {
    @Test
    public void test11() {
        Stream.of(
                new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7},
                new int[]{4, 3, 2, 1, 4})
                .forEach(heights ->
                        log.info(methodLog(
                                Arrays.toString(heights), "" + maxArea(heights))));
    }

    /**
     * 一开始两个指针一个指向开头一个指向结尾，此时容器的底是最大的，接下来随着指针向内移动，会造成容器的底变小，
     * 在这种情况下想要让容器盛水变多，就只有在容器的高上下功夫。那我们该如何决策哪个指针移动呢？
     * 我们能够发现不管是左指针向右移动一位，还是右指针向左移动一位， 容器的底都是一样的，都比原来减少了1。
     * 这种情况下我们想要让指针移动后的容器面积增大，就要使移动后的容器的高尽量大，
     * 所以我们选择指针所指的高较小的那个指针进行移动，这样我们就保留了容器较高的那条边，放弃了较小的那条边，以获得有更高的边的机会。
     */
    private int maxArea(int[] heights) {
        if (heights.length == 1) return 0;
        if (heights.length == 2) return min(heights[0], heights[1]);
        int i = 0, j = heights.length - 1, k;
        int max = Math.min(heights[i], heights[j]) * j;
        int area;
        while (i < j) {
            if (heights[i] < heights[j]) {
                k = i;
                do i++; while (i < j && heights[i] <= heights[k]);
            } else {
                k = j;
                do j--; while (i < j && heights[j] <= heights[k]);
            }
            area = Math.min(heights[i], heights[j]) * (j - i);
            if (area > max) max = area;
        }
        return max;
    }
}