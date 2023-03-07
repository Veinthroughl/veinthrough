package veinthrough.leetcode.tree;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.leetcode.tree.Tree.TreeNode;

/**
 * 第96题：不同的二叉搜索树
 * 给你一个整数 n ，求恰由 n 个节点组成且节点值从 1 到 n 互不相同的 二叉搜索树 有多少种？返回满足题意的二叉搜索树的种数。
 *
 * 第95题：构造不同的二叉搜索树
 * 给你一个整数 n ，请你生成并返回所有由 n 个节点组成且节点值从 1 到 n 互不相同的不同 二叉搜索树 。
 * 可以按 任意顺序 返回答案。
 */
@SuppressWarnings("unused")
@Slf4j
public class DifferentTrees {
    @Test
    public void test96() {
        Stream.of(3, 4, 5, 6)
                .forEach(n -> log.info(methodLog(
                        "" + n, differentTrees(n) + " different trees.")));
    }

    @Test
    public void test95() {
        Stream.of(3, 4)
                .map(this::generateTrees)
                .map(list -> list.stream().map(Tree::treeString).collect(Collectors.toList()))
                .forEach(list -> log.info(methodLog(
                        ""+list.size(), list.toString())));
    }

    /**
     * 第96题：不同的二叉搜索树
     * [1...n]构成的二叉搜索树f(n):
     * (1) 选取从[1...n]中选取一个根节点j
     * (2) 左子树的个数为[1...j-1]构成的二叉搜索树的个数: f(j-1)
     * (3) 右子树的个数为[j+1...n]构成的二叉搜索树的个数: f(n-j)
     * 实际上[j+1...n]构成的二叉搜索树就是在[1...n-j]构成的二叉搜索树上(将每个节点的值+j)
     */
    private int differentTrees(int n) {
        // boundary
        if (n <= 1) return 1;

        int[] f = new int[n + 1];
        f[0] = 1;
        f[1] = 1;
        f[2] = 2;
        for (int i = 3; i <= n; i++) {
            f[i] = 0;
            for (int j = 1; j <= i; j++)
                f[i] += f[j - 1] * f[i - j];
        }
        return f[n];
    }

    /**
     * 第96题：构造不同的二叉搜索树
     *
     * 1. 方法1: 递归
     * 2. 方法2: DP, 按照{@link #differentTrees(int)}的思路:
     * [1...n]构成的二叉搜索树f(n):
     * (1) 选取从[1...n]中选取一个根节点j
     * (2) 左子树的个数为[1...j-1]构成的二叉搜索树
     * (3) 右子树的个数为[j+1...n]构成的二叉搜索树
     * 实际上[j+1...n]构成的二叉搜索树就是在[1...n-j]构成的二叉搜索树上(将每个节点的值+j)
     */
    private List<TreeNode> generateTrees(int n) {
//        return generateTreesRecursive(1, n);
        return generateTreesDP(n);
    }

    /**
     * 第96题：构造不同的二叉搜索树
     * 方法1: 递归
     */
    private List<TreeNode> generateTreesRecursive(int l, int r) {
        List<TreeNode> list = new LinkedList<>();
        if (l == r) {
            list.add(new TreeNode(l));
            return list;
        }

        for (TreeNode node : generateTreesRecursive(l + 1, r))              // 左子树为空, 右子树递归
            list.add(new TreeNode(l, null, node));
        for (TreeNode node : generateTreesRecursive(l, r - 1))              // 左子树递归, 右子树为空
            list.add(new TreeNode(r, node, null));
        for (int i = l + 1; i < r; i++) {
            for (TreeNode left : generateTreesRecursive(l, i - 1)) {        // 递归左子树
                for (TreeNode right : generateTreesRecursive(i + 1, r))     // 递归右子树
                    list.add(new TreeNode(i, left, right));
            }
        }
        return list;
    }

    /**
     * 第95题：构造不同的二叉搜索树
     * 方法2: DP, 按照{@link #differentTrees(int)}的思路:
     * [1...n]构成的二叉搜索树f(n):
     * (1) 选取从[1...n]中选取一个根节点j
     * (2) 左子树的个数为[1...j-1]构成的二叉搜索树
     * (3) 右子树的个数为[j+1...n]构成的二叉搜索树
     * 实际上[j+1...n]构成的二叉搜索树就是在[1...n-j]构成的二叉搜索树上(将每个节点的值+j)
     */
    private List<TreeNode> generateTreesDP(int n) {
        List<List<TreeNode>> lists = new ArrayList<>(n);
        List<TreeNode> trees;
        // 1. n=1
        trees = new LinkedList<>();
        lists.add(trees);
        //
        trees.add(new TreeNode(1));
        if (n == 1) return lists.get(0);

        // 2. n=2
        trees = new LinkedList<>();
        lists.add(trees);
        trees.add(new TreeNode(1, null, new TreeNode(2)));
        trees.add(new TreeNode(2, new TreeNode(1), null));

        // 3. 3...n
        for (int i = 3; i <= n; i++) {
            trees = new LinkedList<>();
            lists.add(trees);

            // 3.1 左子树为空/右子树为空, 另外一边有i-1个节点, 具有i-1个节点的(二叉树集)结构都相同, 只是各个节点的值不同
            for (TreeNode tree : lists.get(i - 2)) {
                // 左子树为null, 右子树(i-1个节点的二叉树基础上)各个节点+1
                trees.add(new TreeNode(1, null, cloneTreeAndAdd(tree, 1)));
                // 左子树为(i-1个节点的二叉树), 右子树为null
                trees.add(new TreeNode(i, tree, null));
            }
            for (int j = 2; j < i; j++) {
                for (TreeNode left : lists.get(j - 2)) {
                    for (TreeNode right : lists.get(i - j - 1)) {
                        // 实际上[j+1...n]构成的二叉搜索树就是在[1...n-j]构成的二叉搜索树上(将每个节点的值+j)
                        // 左子树为(j-1个节点的二叉树), 右子树(i-j个节点的二叉树基础上)各个节点+j
                        trees.add(new TreeNode(j, left, cloneTreeAndAdd(right, j)));
                    }
                }
            }
        }
        return lists.get(n - 1);
    }

    /**
     * 将树中的每个节点值+delta
     */
    private TreeNode cloneTreeAndAdd(TreeNode root, int delta) {
        TreeNode x = root;
        TreeNode x2, rootClone;
        x2 = rootClone = new TreeNode(root.val + delta);

        //
        TreeNode l, r;
        Deque<TreeNode> stack = new LinkedList<>();
        Deque<TreeNode> stack2 = new LinkedList<>();
        stack.push(x);
        stack2.push(x2);
        while (!stack.isEmpty()) {
            x = stack.pop();
            x2 = stack2.pop();
            // right comes before left
            if ((r = x.getRight()) != null) {
                x2.right = new TreeNode(r.val + delta);
                stack.push(r);
                stack2.push(x2.right);
            }
            if ((l = x.getLeft()) != null) {
                x2.left = new TreeNode(l.val + delta);
                stack.push(l);
                stack2.push(x2.left);
            }
        }
        return rootClone;
    }
}
