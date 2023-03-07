package veinthrough.leetcode.tree;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.leetcode.tree.Tree.*;

/**
 * 第98题：验证二叉搜索树
 * 给你一个二叉树的根节点 root ，判断其是否是一个有效的二叉搜索树。
 * 有效 二叉搜索树定义如下：
 * 节点的左子树只包含 小于 当前节点的数。
 * 节点的右子树只包含 大于 当前节点的数。
 * 所有左子树和右子树自身必须也是二叉搜索树。
 *
 * 第99题：恢复二叉搜索树
 * 给你二叉搜索树的根节点root，该树中的恰好两个节点的值被错误地交换。
 * 请在不改变其结构的情况下，恢复这棵树。
 *
 */
@SuppressWarnings({"unused", "Duplicates"})
@Slf4j
public class ValidBST {
    @Test
    public void test98() {
        Stream.of(
                of(new Integer[]{2, 1, 3}),     // true
                of(new Integer[]{3, 1, 5, null, null, 4, 6}),   // true
                of(new Integer[]{4, 1, 5, null, null, 3, 6}),   // false
                of(new Integer[]{5, 1, 4, null, null, 3, 6}),   // false
                of(new Integer[]{4, 3, 6, null, null, 5, 7}),   // true
                of(new Integer[]{5, 4, 6, null, null, 3, 7}))   // false
                .forEach(tree -> log.info(methodLog(
                        treeString(tree), isValidBST(tree) + "")));
    }

    @Test
    public void test99() {
        Stream.of(
                of(new Integer[]{1,3,null,null,2}), // [3,1,null,null,2]
                of(new Integer[]{3,1,4,null,null,2})) // [2,1,4,null,null,3]
                .forEach(tree -> log.info(methodLog(
                        treeString(tree), treeString(recoverTree(tree)))));
    }

    @Test
    public void testMorris() {
        TreeNode tree = of(new Integer[]{1,2,3,4,5,6,7});
        ImmutableMap.<String,Function<TreeNode,TreeNode[]>>of(
                "Preorder by morris", Tree::preOrderMorris,
                "Inorder by morris", Tree::inOrderMorris,
                "Postorder by morris", Tree::postOrderMorris)
                .forEach((taskName, task) -> log.info(methodLog(
                        taskName, Arrays.toString(task.apply(tree)))));
    }


    /**
     * 第95题:
     * 1. 方法1(自己开始做法): 从下往上累积传递(nodes),
     * 从上往下传递(nodes)不行,
     * 因为要判断某个节点的左/右子树中的节点(累计,下)都满足该父节点(上, </>该父节点),
     * 而不是判断某个节点的所有祖先(累计, 上)满足该子节点(下, >/<该子节点)。
     * {@link #isValidBSTByChildren(TreeNode, List)}
     *
     * 2. 方法2(题解): 从上往下传递(区间),
     * 从下往上传递(区间)不行,
     * 因为要判断某个节点(下)满足所有祖先(累积, 上)构成的区间: 父节点 --> 左/右子节点区间
     * 而不是判断某个节点(上)满足所有子节点(累计, 下)构成的区间: 左/右子节点区间 -/> 父节点区间
     * {@link #isValidBSTByScopeOfAncesters(TreeNode, int, int)}
     *
     * 3. 方法3: Morris方法
     */
    private boolean isValidBST(TreeNode root) {
//        return isValidBSTByChildren(root, null);
//        return isValidBSTByScopeOfAncesters(root, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return isValidBSTByMorris(root);
    }

    /**
     * 第98题错误做法: 子树(6,3,7)局部满足, 但是全局不满足(3)不满足
     * [5,4,6,null,null,3,7] ==>
     * #             5
     * #         /     \
     * #       4       6
     * #             /  \
     * #            3   7
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isValidBST2(TreeNode root) {
        if (root.left != null &&
                (root.left.val >= root.val || !isValidBST2(root.left))) return false;
        //noinspection RedundantIfStatement
        if (root.right != null &&
                (root.right.val <= root.val || !isValidBST2(root.right))) return false;
        return true;
    }

    /**
     * 方法1(自己开始做法): 从下往上累积传递(nodes),
     * 从上往下传递(nodes)不行,
     * 因为要判断某个节点的左/右子树中的节点(累计,下)都满足该父节点(上, </>该父节点),
     * 而不是判断某个节点的所有祖先(累计, 上)满足该子节点(下, >/<该子节点)。
     *
     * @param values 代表root上层传递过来的,
     *               (1) root是上层的left: 传递过来的就是上层的leftNodes
     *               (2) root是上层的right: 传递过来的就是上层的rightNodes
     *               (3) 所以第一个root没有上层, 所以传递过来的是null
     */
    private boolean isValidBSTByChildren(TreeNode root, List<Integer> values) {
        if (root.left != null) {
            List<Integer> leftValues = new LinkedList<>();
            if (!isValidBSTByChildren(root.left, leftValues) ||           // 向左子树递归, 左子树中的节点都会放入leftNodes
                    !isAllSmallerThan(leftValues, root.val))
                return false;
            if (values != null) values.addAll(leftValues);      // 向上层添加左子树中的节点
        }
        if (root.right != null) {
            List<Integer> rightValues = new LinkedList<>();     // 向右子树递归, 右子树中的节点都会放入rightNodes
            if (!isValidBSTByChildren(root.right, rightValues) ||
                    !isAllLargerThan(rightValues, root.val))
                return false;
            if (values != null) values.addAll(rightValues);     // 向上层添加右子树中的节点
        }

        if (values != null) values.add(root.val);               // 向上层添加本节点
        return true;
    }

    private static boolean isValidBSTByMorris(TreeNode root) {
        int preValue = Integer.MIN_VALUE;

        TreeNode mostRight;
        while (root != null) {
//            morrisSequence.add(root);               // morris sequence
            if ((mostRight = root.left) != null) {
                // mostRight的right可能还没被改（第一次访问）也可能已经被改了（第二次访问）
                while (mostRight.right != null && mostRight.right!=root)
                    mostRight = mostRight.right;
                if (mostRight.right == null) {      // 第一次访问(有左孩子), 不访问
                    mostRight.right = root;
                    root = root.left;
                } else {                            // 第二次访问(有左孩子), 访问
                    if(root.val<=preValue) return false;
                    preValue = root.val;
                    mostRight.right = null;
                    root = root.right;
                }
            } else {                                // 只能访问一次(没有左孩子), 访问
                if(root.val<=preValue) return false;
                preValue = root.val;
                root = root.right;
            }
        }
        return true;
    }

    /**
     * 方法2(题解): 从上往下传递(区间),
     * 从下往上传递(区间)不行,
     * 因为要判断某个节点(下)满足所有祖先(累积, 上)构成的区间: 父节点 --> 左/右子节点区间
     * 而不是判断某个节点(上)满足所有子节点(累计, 下)构成的区间: 左/右子节点区间 -/> 父节点区间
     */
    private boolean isValidBSTByScopeOfAncesters(TreeNode root, int low, int up) {
        if (root.val <= low || root.val >= up) return false;
        return isValidBSTByScopeOfAncesters(root.left, low, root.val) &&
                isValidBSTByScopeOfAncesters(root.right, root.val, up);
    }

    private boolean isAllLargerThan(List<Integer> nodeValues, int value) {
        return nodeValues.stream().noneMatch(nodeValue -> nodeValue <= value);
    }

    private boolean isAllSmallerThan(List<Integer> nodes, int value) {
        return nodes.stream().noneMatch(nodeValue -> nodeValue >= value);
    }

    /**
     * 第99题：恢复二叉搜索树
     * 方法1: 使用stack做中序遍历, {@link #recoverTreeByStack(TreeNode)}
     * 方法2: 使用Morris做中序遍历, {@link #recoverTreeByMorris(TreeNode)}
     */
    private static TreeNode recoverTree(TreeNode root) {
//        return recoverTreeByStack(root);
        return recoverTreeByMorris(root);
    }

    private static TreeNode recoverTreeByStack(TreeNode root) {
        TreeNode first=null, second=null;
        TreeNode prev=null, node=root;
        TreeNode left, right;

        Deque<TreeNode> stack = new LinkedList<>();
        // visited/sorted
        List<TreeNode> visited = new LinkedList<>();
        stack.push(node);
        while (!stack.isEmpty()) {
            // (1) handle left
            //noinspection ConstantConditions
            while ((left = (node = stack.peek()).left) != null && !visited.contains(left)) {
                stack.push(left);
            }
            // (2) visit: node.left==null or node.left has been visited
            stack.pop();
            visited.add(node);
            if (prev!=null && node.val <= prev.val) {
                if (first == null) first = prev; // 有可能是两个相邻的需要交换
                second = node;
            }
            prev = node;
            // (3) push right
            if ((right = node.right) != null) stack.push(right);
        }

        // 交换first/second
        if (first != null) {
            int temp = first.val;
            first.val = second.val;
            second.val = temp;
        }
        return root;
    }

    private static TreeNode recoverTreeByMorris(TreeNode root) {
        TreeNode prev = null, cur=root;
        TreeNode first=null, second=null;

        TreeNode mostRight;
        while (cur != null) {
//            morrisSequence.add(root);               // morris sequence
            if ((mostRight = cur.left) != null) {
                // mostRight的right可能还没被改（第一次访问）也可能已经被改了（第二次访问）
                while (mostRight.right != null && mostRight.right!=cur)
                    mostRight = mostRight.right;
                if (mostRight.right == null) {      // 第一次访问(有左孩子), 不访问
                    mostRight.right = cur;
                    cur = cur.left;
                } else {                            // 第二次访问(有左孩子), 访问
                    if(prev!=null && cur.val<=prev.val) {
                        if(first==null) first=prev; // 有可能是两个相邻的需要交换
                        second = cur;
                    }
                    prev = cur;
                    mostRight.right = null;
                    cur = cur.right;
                }
            } else {                                // 只能访问一次(没有左孩子), 访问
                if(prev!=null && cur.val<=prev.val) {
                    if(first==null) first=prev; // 有可能是两个相邻的需要交换
                    second = cur;
                }
                prev = cur;
                cur = cur.right;
            }
        }
        // 交换first/second
        if (first != null) {
            int temp = first.val;
            first.val = second.val;
            second.val = temp;
        }
        return root;
    }
}
