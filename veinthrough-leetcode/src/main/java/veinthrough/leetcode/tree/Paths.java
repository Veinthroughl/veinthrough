package veinthrough.leetcode.tree;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.math.Math;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.leetcode.tree.Tree.of;

/**
 * 1. 二叉树路径: {@link veinthrough.leetcode.tree.Paths}
 * 2. 二维数组路径: {@link veinthrough.leetcode.dp.Paths}
 *
 * 思考:
 * 二叉树中的动态规划: (循环 -> 递归)?
 *
 * 第112题：二叉树目标路径和
 * 给你二叉树的根节点root和一个表示目标和的整数targetSum，判断该树中是否存在【根节点】到【叶子节点】的路径，
 * 这条路径上所有节点值相加等于目标和targetSum 。
 * 叶子节点是指没有子节点的节点。
 *
 * 第124题：二叉树max path sum
 * 路径被定义为一条从树中【任意节点出发】，沿父节点-子节点连接，达到任意节点的序列。
 * 同一个节点在一条路径序列中至多出现一次。该路径至少包含一个节点，且不一定经过根节点。
 * 路径和是路径中各节点值的总和。
 * 给你一个二叉树的根节点root，返回其最大路径和。
 */
@Slf4j
public class Paths {

    private int max;

    @Test
    public void test112() {
        Stream.of(
                hasPathSum(Tree.of(new Integer[]{-2, null, -3}), -5),
                hasPathSum(Tree.of(new Integer[]{1, 2, null, 3, null, 4, null, 5}), 6))
                .forEach(hasPathSum -> log.info(methodLog("" + hasPathSum)));
    }

    @Test
    public void test112_2() {
        Stream.of(
                hasPathSum2(Tree.of(new Integer[]{-2, null, -3}), -5),
                hasPathSum2(Tree.of(new Integer[]{1, 2, null, 3, null, 4, null, 5}), 6))
                .forEach(hasPathSum -> log.info(methodLog("" + hasPathSum)));
    }

    @Test
    public void test124() throws NumberFormatException {
        Stream.of(
                new Integer[]{141, -230, 35, 740, 345, -975, -607, -434, -504, -265, 674, null, -917, 859, 739, 940},
                new Integer[]{-10, 9, 20, null, null, 15, 7},
                new Integer[]{1, 2, 3},
                new Integer[]{1, null, -7, -9, -8, null, null, 3, null, null, -2})
                .forEach(array -> {
                    Tree.TreeNode root = of(array);
                    log.info(methodLog(
                            "tree root", root.toString(),
                            "max path sum", "" + maxPathSum(root),
                            "max path sum ", "" + maxPathSum2(root)));
                });
    }

    /**
     * 二叉树目标路径和方法1: 使用递归
     */
    private boolean hasPathSum(Tree.TreeNode root, int sum) {
        // boundary
        if (root == null) return false;

        //
        if (root.left == null && root.right == null && sum == root.val) return true;
        boolean left = hasPathSum(root.left, sum - root.val);
        boolean right = hasPathSum(root.right, sum - root.val);
        return left || right;
    }

    /**
     * 二叉树目标路径和方法2: 使用分层遍历
     * 我觉得用什么遍历都行, 因为保存了每个节点的path
     */
    private boolean hasPathSum2(Tree.TreeNode root, int sum) {
        // boundary
        if (root == null) return false;
        if (root.left == null && root.right == null)
            return sum == root.val;

        //
        Queue<Integer> paths = new LinkedList<>();
        Queue<Tree.TreeNode> nodes = new LinkedList<>();
        nodes.offer(root);
        paths.offer(root.val);
        Tree.TreeNode node;
        int pathSum;
        while (!nodes.isEmpty()) {
            node = nodes.poll();
            //noinspection ConstantConditions
            pathSum = paths.poll();
            if (node.left == null && node.right == null && pathSum == sum) return true;
            if (node.left != null) {
                nodes.offer(node.left);
                paths.offer(pathSum + node.left.val);
            }
            if (node.right != null) {
                nodes.offer(node.right);
                paths.offer(pathSum + node.right.val);
            }
        }
        return false;
    }

    private int maxPathSum(Tree.TreeNode root) {
        if (root.getLeft() == null && root.getRight() == null) return root.getVal();
        max = _maxPathSum(root)[1];
        return max;
    }

    private int maxPathSum2(Tree.TreeNode root) {
        if (root.getLeft() == null && root.getRight() == null) return root.getVal();
        max = root.getVal();
        _maxGain(root);
        return max;
    }

    /**
     * 二叉树最大路径和方法1(题解):
     * 考虑实现一个简化的函数 _maxGain(node)，该函数计算二叉树中的一个节点的最大贡献值，
     * 具体而言，就是在以该节点为根节点的子树中寻找以该节点为起点的一条路径，使得该路径上的节点值之和最大。
     * 计算如下:
     * 空节点的最大贡献值等于0。
     * 非空节点的最大贡献值等于节点值与其子节点中的最大贡献值之和（对于叶节点而言，最大贡献值等于节点值）。
     *
     * max: 不一定经过该节点
     * maxGain: 经过该节点(以该节点为子树)能获取到的最大值
     */
    private int _maxGain(Tree.TreeNode node) {
        if (node == null) return 0;

        // (1) 【相对于_maxPathSum()】使用了0的技巧, 来避免分类
        int leftGain = Math.max(_maxGain(node.getLeft()), 0);
        int rightGain = Math.max(_maxGain(node.getRight()), 0);
        // (2) 只需要计算一定包括该节点的max[node], 然后再和max比较
        // 【相对于_maxPathSum()】不需要计算包括该节点及该节点下的(即是否包括该节点)max, 因为前面所有的节点已经和max比较过
        max = Math.max(max, leftGain + rightGain + node.getVal());
        // 返回最大Gain
        return Math.max(leftGain, rightGain) + node.getVal();
    }

    /**
     * 二叉树最大路径和方法2(自己实现)
     * current[0]表示以当前节点为根且把其当做子路径(要被父节点使用)的最大路径和(其实就是{@link #_maxGain(Tree.TreeNode)});
     * current[1]表示以当前节点为根的最大路径和,不一定经过当前节点
     *
     * 方法1{@link #_maxGain(Tree.TreeNode)}中的max是全局的, 每个节点中计算的都是以root为根节点的最大路径和，
     * 而这里(方法2)的current[1]在每个节点中计算了以每个节点为根节点的最大路径和
     *
     * 这里使用0处理有点像(连续子数组的最大和):
     * {@link veinthrough.leetcode.array.SubArray#maxSubArray(int[])}
     */
    private static int[] _maxPathSum(Tree.TreeNode node) {
        if (node == null) return new int[]{0, 0};

        int[] left;
        int[] right;
        int[] current = new int[2];

        left = _maxPathSum(node.getLeft());
        right = _maxPathSum(node.getRight());
        // 1. 计算current[0]
        // 1.方法1: 经过0处理:
        // left[0]>=0  => left[0]+node.getVal()>=node.getVal()
        // right[0]>=0  => right[0]+node.getVal()>=node.getVal()
        left[0] = Math.max(left[0], 0);
        right[0] = Math.max(right[0], 0);
        current[0] = Math.max(left[0], right[0]) + node.getVal();
        // 1.方法2: 未经过0处理
//        current[0] = Math.max(node.getVal(), node.getVal() + left[0], node.getVal() + right[0]);

        // 2. 计算current[1]
        // 2.方法1: 经过0处理
        current[1] = Math.max(left[1], right[1], node.getVal() + left[0] + right[0]);
        // 2.方法2: 未经过0处理
//        current[1] = Math.max(left[1], right[1], node.getVal(), node.getVal() + left[0], node.getVal() + right[0],
//                node.getVal() + left[0] + right[0]);
        return current;
    }
}