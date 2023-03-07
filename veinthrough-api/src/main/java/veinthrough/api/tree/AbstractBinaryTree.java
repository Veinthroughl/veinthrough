package veinthrough.api.tree;

import lombok.*;
import veinthrough.api.generic.Pair;
import veinthrough.api.generic.Tuple;
import veinthrough.api.tree.api.BinaryTree;

import java.security.InvalidParameterException;
import java.util.*;

import static veinthrough.api.tree.api.BinaryTree.BinaryNode.keyValue;


@SuppressWarnings("Duplicates")
@NoArgsConstructor
public abstract class AbstractBinaryTree<K extends Comparable<? super K>, V>
        implements BinaryTree<K, V> {
    @Getter
    @Setter
    private int size = 0;

    @Getter
    @Setter
    private BinaryNode<K, V> root;

    AbstractBinaryTree(BinaryNode<K, V> root) {
        this.root = root;
    }

    //-------------------------------------abstract--------------------------------------------------

    /**
     * New a node by key/value, which are different in different extended classes.
     */
    abstract BinaryNode<K, V> newNode(K key, V value);

    /**
     * Put x under(as a child of) p.
     * Called only when x!=null && p!=null
     */
    abstract void putAt(BinaryNode<K, V> x, BinaryNode<K, V> p);

    /**
     * Delete node x and return the truly deleted node.
     * Called only when x!=null.
     */
    abstract BinaryNode<K, V> delete(BinaryNode<K, V> x);

    //-------------------------------------implementation--------------------------------------------------

    /**
     * Link x and p
     *
     * @param x child, should NOT be null.
     * @param p parent, can be null.
     */
    void link(@NonNull BinaryNode<K, V> x, BinaryNode<K, V> p) {
        if (p != null) {
            if (x.getKey().compareTo(p.getKey()) < 0) p.setLeft(x);
            else p.setRight(x);
        }
        x.setParent(p);
    }

    /**
     * Left rotate by node x.
     *
     * @param x by which rotate
     *
     *          #              p                            p
     *          #           /                             /
     *          #         x                            xr
     *          #      /   \                         /   \
     *          #    xl   xr          ==>         x     xrr
     *          #       /   \                  /   \
     *          #     xrl   xrr              xl    xrl
     */
    @Override
    public void leftRotate(BinaryNode<K, V> x) {
        if (x == null) return;
        BinaryNode<K, V> xr, xrl, p;
        p = x.getParent();
        if ((xr = x.getRight()) != null) {
            xrl = xr.getLeft();
            // (1) move xrl to x's right
            x.setRight(xrl);
            if (xrl != null) xrl.setParent(x);
            // (2) exchange x and xr
            xr.setLeft(x);
            x.setParent(xr);
        }
        // (3) link xr,p
        link(xr, p);
        // (4) should change root
        if (p == null) root = xr;
    }

    /**
     * Right rotate by node x.
     *
     * @param x by which rotate
     *
     *          #                  p                         p
     *          #                /                         /
     *          #              x                         xl
     *          #           /   \                     /    \
     *          #        xl     xr     ==>        xll      x
     *          #      /   \                             /   \
     *          #    xll   xlr                         xlr   xr
     */
    public void rightRotate(BinaryNode<K, V> x) {
        if (x == null) return;
        BinaryNode<K, V> xl, xlr, p;
        p = x.getParent();
        if ((xl = x.getLeft()) != null) {
            xlr = xl.getRight();
            // (1) move xlr to x's left
            x.setLeft(xlr);
            if (xlr != null) xlr.setParent(x);
            // (2) exchange x and xl
            xl.setRight(x);
            x.setParent(xl);
        }
        // (3) link xr,p
        link(xl, p);
        // (4) should change root
        if (p == null) root = xl;
    }

    @Override
    public int height() {
        return _levelOrder().getFirst();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public BinaryNode<K, V> find(K key) {
        return _find(key).getSecond();
    }

    /**
     * Find a node and it's parent by key.
     * Actually common codes of find/put.
     *
     * @return Pair of (parent node, found node)
     */
    private Pair<BinaryNode<K, V>> _find(K key) {
        if (key == null) return null;

        BinaryNode<K, V> p = null;
        BinaryNode<K, V> x = root;
        while (x != null && !key.equals(x.getKey())) {
            p = x;
            if (key.compareTo(x.getKey()) < 0) x = x.getLeft();
            else x = x.getRight();
        }
        return new Pair<>(p, x);
    }

    @Override
    public BinaryNode<K, V> max() {
        return max(root);
    }

    @Override
    public BinaryNode<K, V> min() {
        return min(root);
    }

    /**
     * Symmetry with successor.
     *
     * @return the predecessor of param x
     *
     *         #             4
     *         #         /      \
     *         #       1        7
     *         #     / \      /  \
     *         #   0   3    6    8
     *         #     /    /       \
     *         #    2    5        9
     *
     *         (1) predecessor(4), left is not null:
     *         4 --> 1 --> 3, so predecessor is 3
     *         (2) predecessor(0), left is null:
     *         0 --> 1 --> 4(x==root), so predecessor is null
     *         (3) predecessor(5), left is null:
     *         5 --> 6 --> 7(x!=root), so predecessor is 4
     */
    @Override
    public BinaryNode<K, V> predecessor(BinaryNode<K, V> x) {
        if (x == null) throw new InvalidParameterException("Argument of predecessor should not be null!");

        BinaryNode<K, V> l, p;
        // 1. left is not null
        if ((l = x.getLeft()) != null) {
            return max(l);
            // 2. left is null
        } else {
            while ((p = x.getParent()) != null && x == p.getLeft()) x = p;
            return x == root ? null : p;
        }
    }

    /**
     * Symmetry with predecessor
     *
     * @return the successor of x
     *
     *         #             4
     *         #         /      \
     *         #       1        7
     *         #     / \      /  \
     *         #   0   3    6    8
     *         #     /    /       \
     *         #    2    5        9
     *
     *         (1) successor(4), right is not null:
     *         4 --> 7 --> 6 --> 5, so successor is 5
     *         (2) successor(9), right is null:
     *         9 --> 8 --> 7 --> 4(x==root), so predecessor is null
     *         (3) successor(3), right is null:
     *         3--> 1(x!=root), so predecessor is 4
     */
    @Override
    public BinaryNode<K, V> successor(BinaryNode<K, V> x) {
        if (x == null) throw new InvalidParameterException("Argument of successor should not be null!");

        BinaryNode<K, V> r, p;
        // 1. right is not null
        if ((r = x.getRight()) != null) {
            return min(r);
            // 2. right is null
        } else {
            while ((p = x.getParent()) != null && x == p.getRight()) x = p;
            return x == root ? null : p;
        }
    }

    /**
     * Put a key/value into the tree.
     */
    @Override
    public void put(K key, V value) {
        Pair<BinaryNode<K, V>> pair = _find(key);
        BinaryNode<K, V> p = pair.getFirst();
        BinaryNode<K, V> x = pair.getSecond();

        // 1. not found x, put x at p
        if (x == null) {
            x = newNode(key, value);
            // 1.1 p is not null
            if (p != null) putAt(x, p);
                // 1.2 p is null, update root: p==null, x==null
            else root = x;
            // update size
            size++;
            // 2. (not) found x, set value
        } else x.setValue(value);
    }

    /**
     * Delete node with the key from the tree.
     *
     * @return true if node with the key found and the node with the key has been deleted,
     *         false if node with key not found.
     */
    @Override
    public boolean delete(K key) {
        BinaryNode<K, V> x = find(key);
        if (x != null) {
            x = delete(x);
            // update size
            size--;
            // recycle
            x.setLeft(null);
            x.setRight(null);
            x.setParent(null);
            return true;
        }
        return false;
    }

    /**
     * List nodes of a tree by level-order.
     */
    @Override
    public List<BinaryNode<K, V>> levelOrder() {
        return _levelOrder().getSecond();
    }

    /**
     * Traverse a tree by level-order,
     * during which calculating height and getting list nodes by level-order.
     *
     * @return Tuple of height and level-order.
     */
    private Tuple<Integer, List<BinaryNode<K, V>>> _levelOrder() {
        if (root == null) return new Tuple<>(0, null);

        BinaryNode<K, V> x = root;
        BinaryNode<K, V> l, r;
        Queue<BinaryNode<K, V>> queue = new LinkedList<>();
        List<BinaryNode<K, V>> order = new LinkedList<>();
        queue.offer(x);
        // currentIndex/nextIndex: last index of current/next layer
        int currentIndex = 0, nextIndex = 0, index = -1;
        int height = 0;
        while (!queue.isEmpty()) {
            x = queue.poll();
            order.add(x);
            index++;
            if ((l = x.getLeft()) != null) {
                queue.offer(l);
                nextIndex++;
            }
            if ((r = x.getRight()) != null) {
                queue.offer(r);
                nextIndex++;
            }
            // update index of current layer
            if (index == currentIndex) {
                height++;
                currentIndex = nextIndex;
            }
        }
        return new Tuple<>(height, new ArrayList<>(order));
    }


    /**
     * Non-recursive implementation of listing nodes of a tree by pre-order.
     */
    @Override
    public List<BinaryNode<K, V>> preOrder() {
        if (root == null) return null;

        BinaryNode<K, V> x = root;
        BinaryNode<K, V> l, r;
        Deque<BinaryNode<K, V>> stack = new LinkedList<>();
        List<BinaryNode<K, V>> order = new LinkedList<>();
        stack.push(x);
        while (!stack.isEmpty()) {
            order.add(x = stack.pop());
            // right comes before left
            if ((r = x.getRight()) != null) stack.push(r);
            if ((l = x.getLeft()) != null) stack.push(l);
        }
        return order;
    }


    /**
     * Non-recursive implementation of listing nodes of a tree by in-order.
     *
     * @return the in-order list
     *
     *         #             4
     *         #         /      \
     *         #       1        7
     *         #     / \      /  \
     *         #   0   3    6    8
     *         #     /    /       \
     *         #    2    5        9
     *
     *         4
     *         4/1		0
     *         4/3		0/1
     *         4/3		0/1/2
     *         4		0/1/2/3
     *         7		0/1/2/3/4
     *         7/6		0/1/2/3/4/5
     *         7		0/1/2/3/4/5/6
     *         8		0/1/2/3/4/5/6/7
     *         9		0/1/2/3/4/5/6/7/8
     *         -       0/1/2/3/4/5/6/7/8/9
     */
    @Override
    public List<BinaryNode<K, V>> inOrder() {
        LinkedList<BinaryNode<K, V>> stack = new LinkedList<>();
        // visited/sorted
        List<BinaryNode<K, V>> visited = new LinkedList<>();
        BinaryNode<K, V> node = root, left, right;
        stack.push(node);
        while (!stack.isEmpty()) {
            // (1) handle left
            // 不存在左子节点/左子节点已经访问
            //noinspection ConstantConditions
            while ((left = (node = stack.peek()).getLeft()) != null && !visited.contains(left))
                stack.push(left);
            // (2) visit: node.left==null or node.left has been visited
            stack.pop();
            visited.add(node);
            // (3) push right
            if ((right = node.getRight()) != null) stack.push(right);
        }
        return new ArrayList<>(visited);
    }

    /**
     * Non-recursive implementation of listing nodes of a tree by post-order.
     *
     * @return the post-order list
     *
     *         #             4
     *         #         /      \
     *         #       1        7
     *         #     / \      /  \
     *         #   0   3    6    8
     *         #     /    /       \
     *         #    2    5        9
     *
     *         4
     *         4/1          0
     *         4/1/3        0
     *         4/1/3		 0/2
     *         4/1		     0/2/3
     *         4		     0/2/3/1
     *         4/7		     0/2/3/1
     *         4/7/6		 0/2/3/1/5
     *         4/7		     0/2/3/1/5/6
     *         4/7/8		 0/2/3/1/5/6
     *         4/7/8/9      0/2/3/1/5/6
     *         4/7/8        0/2/3/1/5/6/9
     *         4/7          0/2/3/1/5/6/9/8
     *         4            0/2/3/1/5/6/9/8/7
     *         -            0/2/3/1/5/6/9/8/7/4
     */
    @Override
    public List<BinaryNode<K, V>> postOrder() {
        LinkedList<BinaryNode<K, V>> stack = new LinkedList<>();
        // visited/sorted
        List<BinaryNode<K, V>> visited = new LinkedList<>();
        BinaryNode<K, V> node = root;
        stack.push(node);
        while (!stack.isEmpty()) {
            // (1) handle left
            // 不存在左子节点/左子节点已经访问, 才会停止向左的迭代
            //noinspection ConstantConditions
            while ((node = stack.peek()).getLeft() != null && !visited.contains(node.getLeft()))
                stack.push(node.getLeft());
            // (2) handle right
            if (node.getRight() != null && !visited.contains(node.getRight()))
                stack.push(node.getRight());
            else {
                stack.pop();
                visited.add(node);
            }
        }
        return new ArrayList<>(visited);
    }

    @Override
    public String toString() {
        return treeString(root);
    }

    //-------------------------------------static--------------------------------------------------

    /**
     * Static implementation of getting string of a node, in case of null.
     */
    private static <K extends Comparable<? super K>, V> String stringOf(BinaryNode<K, V> x) {
        return x == null ? "(N)" : x.toString();
    }

    /**
     * static implementation of min
     */
    private static <K extends Comparable<? super K>, V> BinaryNode<K, V> min(BinaryNode<K, V> root) {
        if (root == null) return null;

        BinaryNode<K, V> x = root;
        BinaryNode<K, V> l;
        while ((l = x.getLeft()) != null) x = l;
        return x;
    }

    /**
     * static implementation of min
     */
    private static <K extends Comparable<? super K>, V> BinaryNode<K, V> max(BinaryNode<K, V> root) {
        if (root == null) return null;

        BinaryNode<K, V> x = root;
        BinaryNode<K, V> r;
        while ((r = x.getRight()) != null) x = r;
        return x;
    }

    /**
     * Build a serial node string of a tree by level-order.
     *
     * @param root root of a tree.
     * @return serial node string of the tree by level-order.
     *
     *         #             4
     *         #         /      \
     *         #       1        7
     *         #     / \      /  \
     *         #   0   3    6    8
     *         #     /    /       \
     *         #    2    5        9
     *         ==>(4,1,7,0,3,6,8,null,null,2,null,5,null,null,9)
     *
     *         #                   7
     *         #                /    \
     *         #              4      8
     *         #           /   \      \
     *         #         1     6      9
     *         #       /  \   /
     *         #      0   3  5
     *         #         /
     *         #        2
     *         ==>（7,4,8,1.6,null,9,0,3,5,null,null,null,null,null,2)
     */
    private static <K extends Comparable<? super K>, V> String treeString(BinaryNode<K, V> root) {
        // 1. 特殊情况
        if (root == null) return "[null]";
        if (root.getLeft() == null && root.getRight() == null) return "[" + root + "]";

        // 2.
        Queue<BinaryNode<K, V>> queue = new LinkedList<>();
        // (1) handle root
        StringBuilder str = new StringBuilder("[").append(root);
        queue.offer(root.getLeft());
        queue.offer(root.getRight());
        // (2) handle others by queue
        BinaryNode<K, V> x;
        while (!queue.isEmpty()) {
            x = queue.poll();
            str.append(",").append(stringOf(x));
            // 至少有一个子节点才会入队列，最后的叶子节点不添加2个null
            if (x != null) {
                queue.offer(x.getLeft());
                queue.offer(x.getRight());
            }
        }

        // (3) 去掉最后的连续null
        String nullStr = stringOf(null);
        int nullLen = nullStr.length();
        while (str.substring(str.length() - nullLen - 1).equals("," + nullStr))
            str.delete(str.length() - nullLen - 1, str.length());
        return str.append("]").toString();
    }

    @SuppressWarnings("WeakerAccess")
    @NoArgsConstructor
    @Data
    static class BinaryNodeImpl<K extends Comparable<? super K>, V> implements BinaryNode<K, V> {
        @NonNull
        private K key;
        private V value;
        private BinaryNode<K, V> left;
        private BinaryNode<K, V> right;
        private BinaryNode<K, V> parent;

        public BinaryNodeImpl(K key, V value,
                              BinaryNode<K, V> left, BinaryNode<K, V> right, BinaryNode<K, V> parent) {
            if (key == null) throw new InvalidParameterException("Key of BinaryNode can't be null");
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
            this.parent = parent;
            if (parent != null) {
                if (key.compareTo(parent.getKey()) < 0) {
                    parent.setLeft(this);
                } else {
                    parent.setRight(this);
                }
            }

        }

        public BinaryNodeImpl(K key, V value) {
            this(key, value, null, null, null);
        }

        public BinaryNodeImpl(K key) {
            this(key, null);
        }

        @Override
        public String toString() {
            return keyValue(this);
        }

        public String details() {
            return String.format("{%s--%s--[%s,%s]}",
                    keyValue(parent), keyValue(this), keyValue(left), keyValue(right));
        }
    }
}
