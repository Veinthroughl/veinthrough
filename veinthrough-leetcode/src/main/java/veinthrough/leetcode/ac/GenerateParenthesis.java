package veinthrough.leetcode.ac;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第22题，括号生成：
 * 数字n代表生成括号的对数，请你设计一个函数，用于能够生成所有可能的并且有效的括号组合。
 * 输入：n = 3
 * 输出：["((()))","(()())","(())()","()(())","()()()"]
 * 方法；使用回溯法不断地放入一个(或),
 * (1) 如果左括号数量不大于n，我们可以放一个左括号；
 * (2) 如果右括号数量小于左括号的数量，我们可以放一个右括号。
 */
@Slf4j
public class GenerateParenthesis {
    private List<String> result;
    private StringBuilder str;
    private int left, right, n, length;

    @Test
    public void test22() {
        Stream.of(1, 2, 3, 4, 5)
                .forEach(num -> {
                    List<String> list = generateParenthesis(num);
                    log.info(methodLog("" + num,
                            "" + list.size() + list));
                });
    }

    private List<String> generateParenthesis(int n) {
        this.n = n;
        if (n == 1) return Collections.singletonList("()");
        if (n == 2) return Arrays.asList("(())", "()()");

        result = new ArrayList<>();
        str = new StringBuilder("(");
        left = 1;
        right = 0;
        length = n << 1;
        backTrace(length - 1);
        return result;
    }

    private void backTrace(int k) {
        if (k == 2) {
            // 这里不直接修改str，因为修改之后还需要回退
            if (right < left) result.add(str.toString() + "))");
            if (left < k) result.add(str.toString() + "()");
            return;
        }
        if (right < left) {
            str.append(')');
            right++;
            backTrace(k - 1);
            str.deleteCharAt(length - k);
            right--;
        }
        if (left < n) {
            str.append('(');
            left++;
            backTrace(k - 1);
            str.deleteCharAt(length - k);
            left--;
        }
    }
}