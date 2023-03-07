package veinthrough.leetcode.stack;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第71题：简化路径，
 * 给你一个字符串 path ，表示指向某一文件或目录的 Unix 风格 绝对路径 （以 '/' 开头），请你将其转化为更加简洁的规范路径。
 * 在 Unix 风格的文件系统中：
 * 一个点（.）表示当前目录本身；
 * 两个点 （..） 表示将目录切换到上一级（指向父目录）；两者都可以是复杂相对路径的组成部分。
 * 任意多个连续的斜杠（即，'//'）都被视为单个斜杠 '/' 。
 * 对于此问题，任何其他格式的点（例如，'...'）均被视为文件/目录名称。
 *
 * 请注意，返回的 规范路径 必须遵循下述格式：
 *
 * 始终以斜杠 '/' 开头。
 * 两个目录名之间必须只有一个斜杠 '/' 。
 * 最后一个目录名（如果存在）不能 以 '/' 结尾。
 * 此外，路径仅包含从根目录到目标文件或目录的路径上的目录（即，不含 '.' 或 '..'）。
 * 返回简化后得到的规范路径。
 */
@Slf4j
public class SimplifyPath {
    @Test
    public void test71() {
        Stream.of(
                "/home/", "/../", "/.../", "/home//foo/", "//", "/a/./b/../../c/")
                .forEach(path -> log.info(methodLog(
                        "Path", path,
                        "Simplified", simplifyPath(path))));
    }

    private String simplifyPath(String path) {
        // 1. 根据/来划分
        String[] names = path.split("/");

        // 2. 使用stack来处理
        // (1) 遇到"..": 需要出栈(弹出上一个)
        // (2) 遇到""或者".": 不变
        // (3) 遇到其他字符: 当作名字，入栈
        Deque<String> stack = new LinkedList<>();
        for (String name : names) {
            if (name.equals("..")) {
                // stack.pop()相当于pollFirst()
                if (!stack.isEmpty()) stack.pop();
            } else if (!name.equals("") && !name.equals("."))
                // stack.push()相当于offerFirst()
                stack.push(name);
        }

        // 3. 根据栈输出字符串
        if (stack.isEmpty()) return "/";

        StringBuilder simplifiedPath = new StringBuilder();
        while (!stack.isEmpty())
            // 这里使用stack.pollLast()
            simplifiedPath.append("/").append(stack.pollLast());
        return simplifiedPath.toString();
    }
}
