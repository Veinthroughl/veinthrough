package veinthrough.leetcode.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@SuppressWarnings("Duplicates")
public class Tree {
    //

    /**
     * Build a tree from level order
     *
     * (4,1,7,0,3,6,9,null,null,2,null,5,null,8) ==>
     * #             4
     * #         /     \
     * #       1       7
     * #     /  \    /  \
     * #    0   3   6   9
     * #      /   /    /
     * #     2   5    8
     */
    @SuppressWarnings("Duplicates")
    public static TreeNode of(Integer[] nodes) {
        int len;
        if (nodes == null || (len=nodes.length)==0) return null;

        Queue<TreeNode> queue = new LinkedList<>();
        TreeNode root = new TreeNode(nodes[0]);
        queue.add(root);
        int i = 0;
        TreeNode x, left, right;
        while (!queue.isEmpty()) {
            x = queue.poll();
            left = ++i < len && nodes[i] != null ?
                    new TreeNode(nodes[i]) : null;
            right = ++i < len && nodes[i] != null ?
                    new TreeNode(nodes[i]) : null;
            // left
            if (left != null) {
                x.left = left;
                queue.add(left);
            }
            // right
            if (right != null) {
                x.right = right;
                queue.add(right);
            }
        }
        return root;
    }

    @SuppressWarnings("unused")
    // build a complete tree from complete level order
    public static TreeNode ofComplete(Integer[] array) {
        int size = array.length;
        int maxNonLeaf = (size - 1) / 2 - 1;
        TreeNode[] nodes = new TreeNode[size];
        for (int i = size - 1; i >= 0; i--) {
            if (i > maxNonLeaf) {
                nodes[i] = array[i] == null ? null :
                        new TreeNode(array[i]);
            } else {
                nodes[i] = new TreeNode(array[i]);
                nodes[i].left = nodes[2 * i + 1];
                // 节点数为奇数, 最后一个非叶节点才有右子叶节点
                if (i != maxNonLeaf || size % 2 == 1) nodes[i].right = nodes[2 * i + 2];
            }
        }
        return nodes[0];
    }

    /**
     * Static implementation of getting string of a node, in case of null.
     */
    private static String stringOf(TreeNode x) {
        return x == null ? "(N)" : x.toString();
    }

    /**
     * #             4
     * #         /     \
     * #       1       7
     * #     /  \    /  \
     * #    0   3   6   9
     * #      /   /    /
     * #     2   5    8
     * ==>(4,1,7,0,3,6,9,null,null,2,null,5,null,8)
     */
    static String treeString(TreeNode root) {
        // 1. 特殊情况
        if (root == null) return "[null]";
        if (root.left == null && root.right == null) return "[" + root.val + "]";

        // 2.
        Queue<TreeNode> queue = new LinkedList<>();
        // (1) handle root
        StringBuilder str = new StringBuilder("[").append(root.val);
        queue.offer(root.left);
        queue.offer(root.right);
        // (2) handle others by queue
        TreeNode x;
        while (!queue.isEmpty()) {
            x = queue.poll();
            str.append(",").append(stringOf(x));

            // 至少有一个子节点才会入队列，最后的叶子节点不添加2个null
            if (x != null) {
                queue.offer(x.left); // can be null
                queue.offer(x.right); // can be null
            }
        }

        // (3) 去掉最后的连续null
        String nullStr = stringOf(null);
        int nullLen = nullStr.length();
        while (str.substring(str.length() - nullLen - 1).equals("," + nullStr))
            str.delete(str.length() - nullLen - 1, str.length());
        return str.append("]").toString();
    }

    /**
     * in-order traverse: non-recursion
     *
     * #              6
     * #         /       \
     * #       2         8
     * #     /  \      /  \
     * #    1   4     7   9
     * #      /  \
     * #     3   5
     *
     * 6
     * 6/2		1
     * 6/4		1/2
     * 6/4		1/2/3
     * 6/5		1/2/3/4
     * 6		1/2/3/4/5
     * 8		1/2/3/4/5/6
     * 8		1/2/3/4/5/6/7
     * 9		1/2/3/4/5/6/7/8
     * -       1/2/3/4/5/6/7/8/9
     */
    static TreeNode[] inOrder(TreeNode root) {
        Deque<TreeNode> stack = new LinkedList<>();
        // visited/sorted
        List<TreeNode> visited = new LinkedList<>();
        TreeNode node = root, left, right;
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
            // (3) push right
            if ((right = node.right) != null) stack.push(right);
        }
        return visited.toArray(new TreeNode[0]);
    }

    static TreeNode[] morris(TreeNode root) {
        if (root == null) return new TreeNode[0];
        List<TreeNode> morrisSequence = new LinkedList<>();

        TreeNode mostRight;
        while (root != null) {
            morrisSequence.add(root);               // morris sequence
            if ((mostRight = root.left) != null) {
                // mostRight的right可能还没被改（第一次访问）也可能已经被改了（第二次访问）
                while (mostRight.right != null && mostRight.right!=root)
                    mostRight = mostRight.right;
                if (mostRight.right == null) {      // 第一次访问(有左孩子)
                    mostRight.right = root;
                    root = root.left;
                } else {                            // 第二次访问(有左孩子)
                    mostRight.right = null;
                    root = root.right;
                }
            } else {                                // 只能访问一次(没有左孩子)
                root = root.right;
            }
        }
        return morrisSequence.toArray(new TreeNode[0]);
    }

    static TreeNode[] preOrderMorris(TreeNode root) {
        if (root == null) return new TreeNode[0];
        List<TreeNode> preOrder = new LinkedList<>();

        TreeNode mostRight;
        while (root != null) {
//            morrisSequence.add(root);               // morris sequence
            if ((mostRight = root.left) != null) {
                // mostRight的right可能还没被改（第一次访问）也可能已经被改了（第二次访问）
                while (mostRight.right != null && mostRight.right!=root)
                    mostRight = mostRight.right;
                if (mostRight.right == null) {      // 第一次访问(有左孩子), 访问
                    preOrder.add(root);
                    mostRight.right = root;
                    root = root.left;
                } else {                            // 第二次访问(有左孩子), 不访问
                    mostRight.right = null;
                    root = root.right;
                }
            } else {                                // 只能访问一次(没有左孩子), 访问
                preOrder.add(root);
                root = root.right;
            }
        }
        return preOrder.toArray(new TreeNode[0]);
    }

    static TreeNode[] inOrderMorris(TreeNode root) {
        if (root == null) return new TreeNode[0];
        List<TreeNode> inOrder = new LinkedList<>();

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
                    inOrder.add(root);
                    mostRight.right = null;
                    root = root.right;
                }
            } else {                                // 只能访问一次(没有左孩子), 访问
                inOrder.add(root);
                root = root.right;
            }
        }
        return inOrder.toArray(new TreeNode[0]);
    }

    /**
     * 后续遍历需要【注意】节点
     * (1) 只有第二次访问时才添加right edge, 【不包括】没有left子节点的节点, 因为只有一次到达, 没有二次到达
     * (2) 添加的是【cur.left】的right edge, 而不是cur/mostRight的right edge
     * (3) 添加right edge应该在语句mostRight.right = null【之后】, 因为此时mostRight的right还指向cur
     * (4) {@link #getRightEdge(TreeNode)}本来需要【翻转链表】, 这里使用了【栈】, 空间复杂度就不再是O(1)了
     */
    static TreeNode[] postOrderMorris(TreeNode root) {
        if (root == null) return new TreeNode[0];
        List<TreeNode> postOrder = new LinkedList<>();

        TreeNode mostRight, cur=root;
        while (cur != null) {
//            morrisSequence.add(root);               // morris sequence
            if ((mostRight = cur.left) != null) {
                // mostRight的right可能还没被改（第一次访问）也可能已经被改了（第二次访问）
                while (mostRight.right != null && mostRight.right!=cur)
                    mostRight = mostRight.right;
                if (mostRight.right == null) {      // 第一次访问(有左孩子), 不访问
                    mostRight.right = cur;
                    cur = cur.left;
                } else {                            // 第二次访问(有左孩子), 生成right edge
                    mostRight.right = null;
                    postOrder.addAll(getRightEdge(cur.left));
                    // (1) 这里是cur.left的右边界 (2) 这句应该在将right置null之后, 因为此时mostRight的right还指向cur
                    cur = cur.right;
                }
            } else {                                // 只能访问一次(没有左孩子), 不访问
                cur = cur.right;
            }
        }
        postOrder.addAll(getRightEdge(root));
        return postOrder.toArray(new TreeNode[0]);
    }

    private static List<TreeNode> getRightEdge(TreeNode root) {
        LinkedList<TreeNode> stack = new LinkedList<>(); // 【注意】这里使用了栈, morris的空间复杂度就不再是O(1)了
        while(root!=null) {
            stack.push(root);
            root = root.right;
        }
        return stack;
    }

    @SuppressWarnings("unused")
    @Getter
    @Setter
    @AllArgsConstructor
    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }

        @Override
        public String toString() {
            return "" + val;
        }

        public String toStringWithLR() {
            String leftStr = left == null ? "null" : String.valueOf(left.val);
            String rightStr = right == null ? "null" : String.valueOf(right.val);
            return String.format("(%s,%s,%s)", leftStr, String.valueOf(val), rightStr);
        }
    }
}