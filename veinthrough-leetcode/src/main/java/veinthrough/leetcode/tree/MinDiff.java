package veinthrough.leetcode.tree;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.leetcode.tree.Tree.*;

import java.util.Arrays;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.leetcode.tree.Tree.*;

/**
 * 第783题: 二叉搜索树节点最小距离
 * 给你一个二叉搜索树的根节点root，返回树中任意两不同节点值之间的最小差值。
 */
@Slf4j
public class MinDiff {
    @Test
    public void test() {
        Stream.of(
                of(new Integer[]{4, 2, 6, 1, 3}),
                of(new Integer[]{1, 0, 48, null, null, 12, 49}))
                .forEach(tree -> log.info(methodLog(
                        treeString(tree), "" + minDiffInBST(tree))));

    }

    /**
     * 使用中序遍历
     */
    private static int minDiffInBST(TreeNode root) {
        int minDiff = root.val;
        // 中序遍历
        int[] values = Arrays.stream(inOrder(root))
                .mapToInt(TreeNode::getVal)
                .toArray();
        for (int i = 1; i < values.length; i++)
            minDiff = Math.min(minDiff, values[i] - values[i - 1]);
        return minDiff;
    }
}
