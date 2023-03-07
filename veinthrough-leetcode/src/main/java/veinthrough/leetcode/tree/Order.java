package veinthrough.leetcode.tree;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.leetcode.tree.Tree.TreeNode;

import java.util.*;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.leetcode.tree.Tree.of;
import static veinthrough.leetcode.tree.Tree.treeString;

/**
 * 第94题: 二叉树的中序遍历
 *
 * 第1028题：从先序遍历还原二叉树
 * 我们从二叉树的根节点root开始进行深度优先搜索。
 * 在遍历中的每个节点处，我们输出D条短划线（其中D是该节点的深度），然后输出该节点的值。
 * （如果节点的深度为 D，则其直接子节点的深度为 D + 1。根节点的深度为 0）。
 * 如果节点只有一个子节点，那么保证该子节点为左子节点。
 * 给出遍历输出S，还原树并返回其根节点root。
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class Order {
    private TreeNode[] nodes;
    private int[] deeps;
    private int n;

    @Test
    public void test1028() {
        Stream.of(
                "1-2--3--4-5--6--7",
                "1-2--3---4-5--6---7",
                "10-7--8",
                "1-401--349---90--88")
                .forEach(str -> log.info(methodLog(
                        str, treeString(recoverFromPreorder(str)))));
    }

    /**
     * 注意这里测试用例不一定是二叉搜索树((1)符合排序 (2)没有重复元素)
     *  [-34, -100, 37, -100, -48, -71, -54, -22, 8, 48]
     *  [1, 3, 2]
     */
    @Test
    public void test94() {
        Stream.of(new Integer[]{1},
                new Integer[]{37, -34, -48, null, -100, -100, 48, null, null, null, null, -54, null, -71, -22, null, null, null, 8},
                new Integer[]{1, null, 2, 3})
                .forEach(nodes -> log.info(methodLog(Arrays.toString(inorderTraversal(of(nodes))))));
    }

    /**
     * 1. traverse nodes(nodes and deeps)
     * 2. 使用栈根据nodes and deeps来build tree
     * 构造先序遍历序列的时候用的是栈, 还原也应该使用栈
     */
    private TreeNode recoverFromPreorder(String S) {
        // 1 node
        if (S.length() == 1) return new TreeNode(Integer.valueOf(S));

        // 1. >1 nodes, traverse nodes
        // _traverseNodesByArray(S);
        _traverseNodesByList(S);

        // 2. build tree by stack
        return _buildTree();
    }

    /**
     * 2. 使用栈根据nodes and deeps来build tree
     * 构造先序遍历序列的时候用的是栈, 还原也应该使用栈
     */
    private TreeNode _buildTree() {
        int[] stack = new int[n];
        stack[0] = 0;
        int top = 0;
        for (int i = 1; i < n; i++) {
            // deep[i]>deep[stack[top]] is the same
            // 1. top为i的父节点: "-"数目递增, 说明一直是(父节点-->子节点)
            // 题目规定: 如果节点只有一个子节点，那么保证该子节点为左子节点
            if (deeps[i] == deeps[stack[top]] + 1) {
                // set left
                nodes[stack[top]].left = nodes[i];
            }
            // 2. 出现非递增:
            // (1) deep相同: 说明i和top为同级, 也就是说i的父节点为top的父节点
            // (2) deep递减: 说明i比top的层级更高, 但是是其父节点的右子节点
            else if (deeps[i] <= deeps[stack[top]]) {
                // pop nodes
                top -= deeps[stack[top]] - deeps[i] + 1;
                //
                nodes[stack[top]].right = nodes[i];
            }
            // push current node
            stack[++top] = i;
        }
        return nodes[0];
    }

    /**
     * 1. traverse nodes(nodes and deeps)
     * 将形如"1-2--3--4-5--6--7"的字符串转化成节点list/深度list
     * 之所以使用两个list而不是用map是因为HashMap不能保证节点的顺序,
     * 不过可以使用LinkedHashMap
     */
    private void _traverseNodesByList(String S) {
        List<Integer> dashesIndex = new ArrayList<>();
        List<Integer> numsIndex = new ArrayList<>();
        int nextDash = S.indexOf('-');
        numsIndex.add(0);
        dashesIndex.add(nextDash);
        // 1. 获得index:
        // (1) 每个数字在字符串中的index
        // (2) ‘-’在字符串中的的index
        for (int i = nextDash; i < S.length(); i = nextDash, dashesIndex.add(nextDash)) {
            // next number index
            while (S.charAt(i) == '-') i++;
            numsIndex.add(i);
            nextDash = S.indexOf('-', i);
            // next dash index, may be -1(end)
            if (nextDash == -1) nextDash = S.length();
        }

        //
        n = numsIndex.size();
        deeps = new int[n];
        Arrays.fill(deeps, 0);
        nodes = new TreeNode[n];
        // 2. 根据'-'/每个数字在字符串中的index来构造节点list/深度list
        deeps[0] = 0;
        nodes[0] = new TreeNode(Integer.valueOf(
                S.substring(0, dashesIndex.get(0))));
        for (int i = 1; i < n; i++) {
            // resolve deep
            deeps[i] = numsIndex.get(i) - dashesIndex.get(i - 1);
            // resolve value
            nodes[i] = new TreeNode(
                    Integer.valueOf(
                            S.substring(numsIndex.get(i), dashesIndex.get(i))));
        }

    }

    /**
     * [37,-34,-48,null,-100,-100,48,null,null,null,null,-54,null,-71,-22,null,null,null,8]
     * [-34,-100,37,-48,-71,-54,-22,8,48]
     * [-34,-100,37,-100,-48,-71,-54,-22,8,48]
     */
    private TreeNode[] inorderTraversal(TreeNode root) {
        return Tree.inOrder(root);
    }

}