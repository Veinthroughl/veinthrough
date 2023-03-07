package veinthrough.leetcode.tree;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.generic.Pair;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.leetcode.tree.Tree.TreeNode;
import static veinthrough.leetcode.tree.Tree.of;

/**
 * 第100题：相同的二叉树，
 * 给你两棵二叉树的根节点 p 和 q ，编写一个函数来检验这两棵树是否相同。
 * 如果两个树在结构上相同，并且节点具有相同的值，则认为它们是相同的。
 * 
 * 第101题：对称的二叉树，
 * 给你一个二叉树的根节点root，检查它是否轴对称。
 */
@SuppressWarnings({"Duplicates", "unused"})
@Slf4j
public class ComparingTrees {
    @Test
    public void test100() {
        Stream.of(
                Pair.of(of(new Integer[0]), of(new Integer[0])), // true
                Pair.of(of(new Integer[]{1, 2, 3}), of(new Integer[]{1, 2, 3})), // true
                Pair.of(of(new Integer[]{1, 2, 1}), of(new Integer[]{1, 1, 2})), // false
                Pair.of(of(new Integer[]{1}), of(new Integer[]{1, null, 2})), // false
                Pair.of(of(new Integer[]{1, 2}), of(new Integer[]{1, null, 2}))) // false
                .forEach(pair ->
                        log.info(methodLog(
                                "" + isSame(pair.getFirst(), pair.getSecond()))));
    }

    @Test
    public void test101() {
        Stream.of(
                of(new Integer[0]), // true
                of(new Integer[]{1, 2, 2, 3, 4, 4, 3}), // true
                of(new Integer[]{1, 2, 2, null, 3, 3}), // true
                of(new Integer[]{1, 2, 2, null, 3, null, 3})) // false
                .forEach(tree ->
                        log.info(methodLog(
                                "" + _isSymmetric(tree))));
    }

    /**
     * 相同树:
     * 1. 分层/前序/中序/后序遍历
     * (1) 【遍历序列】不能唯一确定一棵树
     * (2) 但是可以在【遍历】的过程中唯一确定一棵树(value/left/right相同)
     * > 方法2, {@link #_isSameTreeIter(TreeNode, TreeNode)}
     * (3) 每一层的【遍历序列】相同不能唯一确定一棵树, 从全局来看也是一种【遍历序列】
     * > 方法1(错误), {@link #_isSameTreeIterSequence(TreeNode, TreeNode)}
     * 2. 递归, 也是在【遍历】的过程中唯一确定一棵树(value/left/right相同)
     * > 方法3, {@link #_isSameRecur(TreeNode, TreeNode)}
     */
    private boolean isSame(TreeNode p, TreeNode q) {
        // 1. boundary
        if (p == null && q == null) return true;
        if (!equalValues(p, q)) return false;

        // 2. left/right
//        return _isSameTreeIterSequence(p, q); // 方法1(错误), 从全局来看也是一种【遍历序列】
//        return _isSameTreeIter(p, q); // 方法2, 在【遍历】的过程中唯一确定一棵树(value/left/right相同)
        return _isSameRecur(p, q); // 方法3, 递归遍历, 也是在【遍历】的过程中唯一确定一棵树(value/left/right相同)
    }

    /**
     * 方法1(错误), 每一层的【遍历序列】相同不能唯一确定一棵树, 从全局来看也是一种【遍历序列】
     * 每一层的【遍历序列】相同不能唯一确定一棵树: [1,2]/[1,null,2]
     * #            1
     * #         /
     * #       2
     * # -----------------
     * #             1
     * #              \
     * #               2
     */
    private boolean _isSameTreeIterSequence(TreeNode p, TreeNode q) {
        Deque<TreeNode> queue1 = new LinkedList<>();
        Deque<TreeNode> queue2 = new LinkedList<>();
        TreeNode node1, node2;
        queue1.offer(p);
        queue2.offer(q);
        while (!queue1.isEmpty() && !queue2.isEmpty()) {
            node1 = queue1.poll();
            node2 = queue2.poll();

            // value/ left value/ right value
            // 如果这里不对left value/ right value处理, 从全局来看就是一种【遍历序列】
            if (!equalValues(node1, node2)) return false;

            // put left/right into queue
            if (node1.left != null) queue1.offer(node1.left);
            if (node2.left != null) queue2.offer(node2.left);
            if (node1.right != null) queue1.offer(node1.right);
            if (node2.right != null) queue2.offer(node2.right);
        }
        return queue1.isEmpty() && queue2.isEmpty();
    }

    /**
     * 方法2, 在【遍历】的过程中唯一确定一棵树(value/left/right相同)
     */
    private boolean _isSameTreeIter(TreeNode p, TreeNode q) {
        Deque<TreeNode> queue1 = new LinkedList<>();
        Deque<TreeNode> queue2 = new LinkedList<>();
        TreeNode node1, node2;
        queue1.offer(p);
        queue2.offer(q);
        while (!queue1.isEmpty() && !queue2.isEmpty()) {
            node1 = queue1.poll();
            node2 = queue2.poll();

            // value/ left value/ right value
            // 如果这里不对left value/ right value处理, 那么就退化成了【遍历序列】
            //noinspection ConstantConditions
            if (node1.val != node2.val ||
                    !equalValues(node1.left, node2.left) ||
                    !equalValues(node1.right, node2.right)) return false;

            // put left/right into queue
            if (node1.left != null) {
                queue1.offer(node1.left);
                queue2.offer(node2.left);
            }
            if (node1.right != null) {
                queue1.offer(node1.right);
                queue2.offer(node2.right);
            }
        }
        return queue1.isEmpty() && queue2.isEmpty();
    }

    /**
     * 方法3, 递归遍历, 也是在【遍历】的过程中唯一确定一棵树(value/left/right相同)
     */
    private boolean _isSameRecur(TreeNode p, TreeNode q) {
        if (p == null && q == null) return true;
        if (!equalValues(p, q)) return false;
        return _isSameRecur(p.left, q.left) && _isSameRecur(p.right, q.right);
    }

    /**
     * 对称树:
     * 1. 分层/前序/中序/后序遍历
     * (1) 【遍历序列】不能唯一确定一棵树
     * (2) 但是可以在【遍历】的过程中唯一确定一棵树(value/left/right相同)
     * > 方法2, {@link #_isSymmetricIter(TreeNode, TreeNode)}
     * (3) 每一层的【遍历序列】相同不能唯一确定一棵树, 从全局来看也是一种【遍历序列】
     * > 方法1(错误), {@link #_isSymmetricIterSequence(TreeNode)}
     * 2. 递归, 也是在【遍历】的过程中唯一确定一棵树(value/left/right相同)
     * > 方法3, {@link #_isSymmetricRecur(TreeNode, TreeNode)}
     */
    private boolean _isSymmetric(TreeNode root) {
        // 1. boundary
        if (root == null) return true;
        if (root.left == null && root.right == null) return true;
        if (!equalValues(root.left, root.right)) return false;

        // 2. left/right
//        return _isSymmetricIterSequence(root); // 方法1(错误), 从全局来看也是一种【遍历序列】
//        return _isSymmetricIter(root.left, root.right); // 方法2, 在【遍历】的过程中唯一确定一棵树(value/left/right相同)
        return _isSymmetricRecur(root.left, root.right); // 方法3, 递归遍历, 也是在【遍历】的过程中唯一确定一棵树(value/left/right相同)
    }

    /**
     * 方法1(错误), 每一层的【遍历序列】相同不能唯一确定一棵树, 从全局来看也是一种【遍历序列】
     * (1) 不一定left/right都得存在: [1,2,2,null,3,3]
     * #             1
     * #         /     \
     * #       2       2
     * #        \    /
     * #       3   3
     *
     * (2) 【遍历序列】相同不能唯一确定一棵树: [1,2,2,null,3,null,3]
     * #             1
     * #         /     \
     * #        2       2
     * #        \       \
     * #         3       3
     */
    private boolean _isSymmetricIterSequence(TreeNode root) {
        // boundary
        if (root == null ||
                root.left == null && root.right == null) return true;
        if (root.left == null || root.right == null) return false;

        //
        Deque<TreeNode> queue = new LinkedList<>();
        queue.add(root.left);
        queue.add(root.right);
        int count = 0;
        int thisBoundary = 2;
        int nextBoundary = 0;
        List<Integer> levelNums = new ArrayList<>(thisBoundary);
        TreeNode node;

        while (!queue.isEmpty()) {
            levelNums.add((node = queue.poll()).val);

            // 1. add left/right
            if (node.left != null) {
                queue.add(node.left);
                ++nextBoundary;
            }
            if (node.right != null) {
                queue.add(node.right);
                ++nextBoundary;
            }

            // 2. level boundary
            if (++count == thisBoundary) {
                // (1) check nextBoundary
                if (nextBoundary % 2 != 0) return false;

                // (2) check this level, 每一层的【遍历序列】对称不能唯一确定一棵树, 从全局来看也是一种【遍历序列】
                if (!checkLevel(levelNums, thisBoundary)) return false;

                // (3) next level
                levelNums = new ArrayList<>(nextBoundary);
                thisBoundary = nextBoundary;
                count = 0;
                nextBoundary = 0;
            }
        }
        return true;
    }

    /**
     * 方法2, 在【遍历】的过程中唯一确定一棵树(value/left/right相同)
     */
    private boolean _isSymmetricIter(TreeNode p, TreeNode q) {
        Deque<TreeNode> queue1 = new LinkedList<>();
        Deque<TreeNode> queue2 = new LinkedList<>();
        TreeNode node1, node2;
        queue1.offer(p);
        queue2.offer(q);
        while (!queue1.isEmpty() && !queue2.isEmpty()) {
            node1 = queue1.poll();
            node2 = queue2.poll();

            // value/ left value/ right value
            // 如果这里不对left value/ right value处理, 那么就退化成了【遍历序列】
            // left <--> right, right <--> left
            //noinspection ConstantConditions
            if (node1.val != node2.val ||
                    !equalValues(node1.left, node2.right) ||
                    !equalValues(node1.right, node2.left)) return false;

            // put left/right into queue
            // queue1: left/right
            // queue2: right/left
            if (node1.left != null) {
                queue1.offer(node1.left);
                queue2.offer(node2.right);
            }
            if (node1.right != null) {
                queue1.offer(node1.right);
                queue2.offer(node2.left);
            }
        }
        return queue1.isEmpty() && queue2.isEmpty();
    }

    private boolean checkLevel(List<Integer> nums, int n) {
        for (int i = 0, j = n - 1; i < j; i++, j--)
            if (!nums.get(i).equals(nums.get(j))) return false;
        return true;
    }

    /**
     * 方法3, 递归遍历, 也是在【遍历】的过程中唯一确定一棵树(value/left/right)
     */
    private boolean _isSymmetricRecur(TreeNode left, TreeNode right) {
        if (left == null && right == null) return true;
        if (!equalValues(left, right)) return false;
        return _isSymmetricRecur(left.left, right.right) && _isSymmetricRecur(left.right, right.left);
    }

    /**
     * value相同
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean equalValues(TreeNode p, TreeNode q) {
        if (p == null && q == null) return true;
        if (p == null || q == null) return false;
        return p.val == q.val;
    }
}
