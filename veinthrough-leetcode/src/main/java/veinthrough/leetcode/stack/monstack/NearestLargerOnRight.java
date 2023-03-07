package veinthrough.leetcode.stack.monstack;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 给定一个整型数组，数组元素随机无序的，要求打印出所有元素右边第一个大于该元素的值。
 */
@Slf4j
public class NearestLargerOnRight {
    @Test
    public void test() {
        Stream.of(
                new int[]{1, 5, 3, 6, 4, 8, 9, 10}, // 5,6,6,8,8,9,10,-1
                new int[]{8, 2, 5, 4, 3, 9, 7, 2, 5}) // 9,5,9,9,9,-1,-1,5,-1
                .forEach(nums -> log.info(methodLog(
                        Arrays.toString(nums),
                        Arrays.toString(nearestLargerOnRight(nums)))));
    }

    /**
     * 1, 5, 3, 6, 4, 8, 9, 10
     * (1) stack: 1(0)|
     * nearest:
     * (2) stack: 5(1)|
     * nearest: 5,
     * (3) stack: 5(1),3(2)|
     * nearest: 5,
     * (4) stack: 6(3)|
     * nearest: 5,6,6
     * (5) stack: 6(3),4(4)|
     * nearest: 5,6,6
     * (6) stack: 8(5)|
     * nearest: 5,6,6,8,8
     * (7) stack: 9(6)|
     * nearest: 5,6,6,8,8,9
     * (8) stack: 10(7)|
     * nearest: 5,6,6,8,8,9,10
     */
    private int[] nearestLargerOnRight(int[] nums) {
        int[] nearest = new int[nums.length];
        Arrays.fill(nearest, -1);
        int[] stack = new int[nums.length];
        int top = -1;

        for (int i = 0; i < nums.length; i++) {
            // if(栈为空或入栈元素符合单调栈) 入栈;
            if (top == -1 || nums[i] <= nums[stack[top]]) {
                stack[++top] = i;
            } else {
                // while(栈非空并且栈顶元素不符合单调栈)
                // 将破坏栈单调性的元素都出栈后，最后一次出栈的元素就是【当前入栈元素能拓展到的最左位置】，
                while (top >= 0 && nums[i] > nums[stack[top]])
                    nearest[stack[top--]] = nums[i];
                // push i
                // 更新其对应的值(如果需要)，并将其位置入栈。
                stack[++top] = i;
            }
        }
        return nearest;
    }
}