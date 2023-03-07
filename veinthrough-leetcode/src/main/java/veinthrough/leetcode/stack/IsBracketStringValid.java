package veinthrough.leetcode.stack;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第20题：
 * 给定一个只包括 '('，')'，'{'，'}'，'['，']'的字符串s，判断字符串是否有效。
 * 有效字符串需满足：
 * 左括号必须用相同类型的右括号闭合。
 * 左括号必须以正确的顺序闭合。
 */
@SuppressWarnings("unused")
@Slf4j
public class IsBracketStringValid {
    @Test
    public void test20() {
        Stream.of("({[)}", "){}(", "[]}", "()", "([[[]])]", "([[{}]])")
                .forEach(s ->
                        log.info(methodLog(
                                s, "" + isBracketStringValidByStack(s))));
    }

    /**
     * 方法1: 栈
     */
    private boolean isBracketStringValidByStack(String s) {
        if (s.length() % 2 != 0) return false;

        char[] chars = s.toCharArray();
        // 只需要存储一半
        char[] stack = new char[chars.length >> 1];
        int threshold = stack.length - 1;

        int top = -1;
        for (char ch : chars) {
            switch (ch) {
                case '(':
                case '[':
                case '{':
                    // case: ({[
                    if (++top > threshold) return false;
                    stack[top] = ch;
                    break;
                case ')':
                case ']':
                case '}':
                    // (): 28/29, []: 91/93, {}: 123/125
                    // case: []}
                    // case: [}
                    if (top < 0 ||
                            ch != stack[top] + 1 && ch != stack[top] + 2) return false;
                    top--;
                    break;
            }
        }
        return top == -1;
    }

    /**
     * 方法2(错误): 根据个数, 根本原因是不能确定顺序
     * (1) 有限定条件
     * > count[0] == 0 && (count[1] != 0 || count[2] != 0),
     * > count[1] == 0 && (count[0] != 0 || count[2] != 0)
     * > count[2] == 0 && (count[0] != 0 || count[1] != 0)
     * 例如([[{}]]), 第一个}处, count[2]==0 但是count[0]/count[1]都不为0, 将会判定成无效
     * (2) 无限定条件
     * 例如([[[]])], 将会判定成有效
     */
    private boolean isBracketStringValidByCounting(String s) {
        int len;
        if ((len = s.length()) % 2 != 0) return false;
        char[] chs = s.toCharArray();

        // (): 28/29, []: 91/93, {}: 123/125
        int half = len >> 1;
        int sum = 0;
        int[] count = new int[124];
        for (char ch : chs) {
            switch (ch) {
                case '(':
                    count[0]++;
                    if (++sum > half) return false;
                    break;
                case '[':
                    count[1]++;
                    if (++sum > half) return false;
                    break;
                case '{':
                    count[2]++;
                    if (++sum > half) return false;
                    break;

                case ')':
                    --count[0];
                    if (count[0] < 0 || (count[0] == 0 && (count[1] != 0 || count[2] != 0))) return false;
                    break;
                case ']':
                    --count[0];
                    if (count[1] < 0 || (count[1] == 0 && (count[0] != 0 || count[2] != 0))) return false;
                    break;
                case '}':
                    --count[0];
                    if (count[2] < 0 || (count[2] == 0 && (count[0] != 0 || count[1] != 0))) return false;
                    break;
            }
        }
        return count[0] == 0 && count[1] == 0 && count[2] == 0;
    }
}