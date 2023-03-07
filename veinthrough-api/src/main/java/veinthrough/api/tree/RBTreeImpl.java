package veinthrough.api.tree;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import veinthrough.api.tree.api.RBTree;

import static veinthrough.api.tree.api.RBTree.RBNode.keyValueColor;

public class RBTreeImpl<K extends Comparable<? super K>, V> extends AbstractBinaryTree<K, V>
        implements RBTree<K, V> {

    /**
     * New a node by key/value, which are different in different extended classes.
     */
    @Override
    RBNode<K, V> newNode(K key, V value) {
        return new RBNodeImpl<>(key, value);
    }

    /**
     * Put x under(as a child of) p.
     * Called only when x!=null && p!=null.
     */
    @Override
    void putAt(BinaryNode<K, V> x, BinaryNode<K, V> p) {
        // (1) link to each other
        link(x, p);
        // (2) handle RBNode's color beyond BinaryNode
        ((RBNode<K, V>) x).setColor(RED);
        if (((RBNode<K, V>) p).getColor() == RED)
            fixAfterPut((RBNode<K, V>) x);
    }

    /**
     * Fix RBTree after putting a node.
     *
     * @param x node put
     */
    private void fixAfterPut(RBNode<K, V> x) {
        // p: parent, u: uncle, pp: parent of parent
        RBNode<K, V> p, u, pp;
        while (colorOf(p = (RBNode<K, V>) x.getParent()) == RED
                && x != getRoot()) {
            // TODO 怎么解决这里需要强制转化的问题
            pp = (RBNode<K, V>) p.getParent();
            if (p == pp.getLeft()) {
                u = (RBNode<K, V>) pp.getRight();
                // 1. 3节点
                if (colorOf(u) == BLACK) {
                    if (x == p.getRight()) {
                        leftRotate(p);
                        p = (RBNode<K, V>) pp.getLeft();
                        x = (RBNode<K, V>) p.getLeft();
                    }
                    rightRotate(pp);
                    p.setColor(BLACK);
                    pp.setColor(RED);
                }
                // 2. 4节点
                // 4节点迭代往上也可能会进入3节点的情况
                else {
                    p.setColor(BLACK);
                    u.setColor(BLACK);
                    pp.setColor(RED);
                    x = pp;
                }
            } else {
                u = (RBNode<K, V>) pp.getLeft();
                // 1. 3节点
                if (colorOf(u) == BLACK) {
                    if (x == p.getLeft()) {
                        rightRotate(p);
                        p = (RBNode<K, V>) pp.getRight();
                        x = (RBNode<K, V>) p.getRight();
                    }
                    leftRotate(pp);
                    p.setColor(BLACK);
                    pp.setColor(RED);
                }
                // 2. 4节点
                // 4节点迭代往上也可能会进入3节点的情况
                else {
                    p.setColor(BLACK);
                    u.setColor(BLACK);
                    pp.setColor(RED);
                    x = pp;
                }
            }
        }
        // set color of root to black
        ((RBNode<K, V>) getRoot()).setColor(BLACK);
    }

    /**
     * Delete node x and return the truly deleted node.
     * Called only when x!=null.
     *
     * @param node the node to be deleted
     * @return the node truly deleted
     *
     *         #             4
     *         #         /      \
     *         #       1        7
     *         #     / \      /  \
     *         #   0   3    6    8
     *         #     /    /       \
     *         #    2    5        9
     *
     *         ==> delete (node 7)
     *
     *         #             4
     *         #         /      \
     *         #       1        8
     *         #     / \      /  \
     *         #   0   3    6    9
     *         #     /    /
     *         #    2    5
     *
     *         ==> return (node 8)
     */
    @Override
    RBNode<K, V> delete(BinaryNode<K, V> node) {
        // 1. replacement: predecessor or successor
        // x:
        // (1) 2 children, replace it's key/value with predecessor/successor
        // (2) 1 child
        // (3) leaf node
        RBNode<K, V> x = (RBNode<K, V>) node;
        RBNode<K, V> rep;
        if (x.getLeft() != null && x.getRight() != null) {
//            rep = (RBNode<K,V>)successor(x);
            rep = (RBNode<K, V>) predecessor(x);
            x.setKey(rep.getKey());
            x.setValue(rep.getValue());
            x = rep;
        }

        // 2. x(after replaced by predecessor/successor):
        // (1) 1 child, rep is the single child
        // (2) leaf node
        RBNode<K, V> p, l;
        p = (RBNode<K, V>) x.getParent();
        rep = (l = (RBNode<K, V>) x.getLeft()) != null ?
                l : (RBNode<K, V>) x.getRight();

        // 3. update:
        // 3.1 set p's child to rep
        // 3.2 set rep's parent to p
        // 3.3 fix color:
        // (1) 删除3节点中的黑色节点
        // (2) 删除2节点
        if (rep != null) {
            // 3.1 set p's child to rep
            if (p != null) {
                if (x == p.getLeft()) p.setLeft(rep);
                else p.setRight(rep);
            }
            // 3.2 set rep's parent to p
            rep.setParent(p);
            // 注意3.1/3.2在3.3.(1)前面, 否则rep还没和p连上
            // 3.3.(1) 此时必然是(x/rep)为组成的一个3节点
            // fix by rep: rep is red
            fixAfterDelete(rep);
        }
        // 3.3.(2) 此时x为一个叶子节点
        // x为黑色(2节点)的时候才需要调整
        // fix by x: x is black
        else if (colorOf(x) == BLACK) {
            // 注意3.1/3.2在3.3.(2)后面, 否则x已经和p断开
            fixAfterDelete(x);
            // 3.1 set p's child to rep
            if (p != null) {
                if (x == p.getLeft()) p.setLeft(null); // rep is null
                else p.setRight(null);
            }
            // 3.2 empty
        }
        // 3.4 update root: p is null  ==>  x is root
        if (p == null) setRoot(rep);

        return x;
    }

    /**
     * Fix RBTree after deleting a node.
     *
     * @param x from 3.3.(1) of {@link #delete delete}: fix by rep, rep is red, 相当于x为删除的节点(只有一个子节点)的子节点rep
     *          from 3.3.(2) of {@link #delete delete}: fix by x, x is black, 相当于x为删除的叶子节点
     */
    private void fixAfterDelete(RBNode<K, V> x) {
        // p:parent, s:sibling, sl: left of sibling, sr: right of sibling
        RBNode<K, V> p, s, sl, sr;
        // 1. x为2节点，来源于delete中的3.4.(2): fix by x, x is black
        while (colorOf(x) == BLACK && x != getRoot()) {
            p = (RBNode<K, V>) x.getParent();
            if (x == p.getLeft()) {
                s = (RBNode<K, V>) p.getRight();
                // 1.1 rotate to find truly sibling
                if (colorOf(s) == RED) {
                    leftRotate(p);
                    p.setColor(RED);
                    s.setColor(BLACK);
                    // reset s
                    s = (RBNode<K, V>) p.getRight();
                }
                sl = (RBNode<K, V>) s.getLeft();
                sr = (RBNode<K, V>) s.getRight();
                // 1.2.(1) 兄弟节点可借
                // 这里不能用空来判断，而应该用黑色判断，
                // 因为兄弟节点不可借是会迭代往上，此时x不再是叶子节点，对应的s也不是叶子节点(中的元素),
                // 兄弟节点节点不可借迭代往上，也可能进入兄弟节点可借的情况
//                if (sl != null && sr != null) {
                if (colorOf(sl) != BLACK || colorOf(sr) != BLACK) {
                    if (colorOf(sr) == BLACK) {
                        rightRotate(s);
                        sl.setColor(BLACK);
                        s.setColor(RED);
                        // reset s
                        s = (RBNode<K, V>) p.getRight();
                    }
                    leftRotate(p);
                    s.setColor(p.getColor());
                    p.setColor(BLACK);
                    sr.setColor(BLACK);
                    x = (RBNode<K, V>) getRoot();
                }
                // 1.2.(2) 兄弟节点不可借, 兄弟节点变红并且迭代往上
                // 相当于: colorOf(sl) == BLACK && colorOf(sr) == BLACK
                else {
                    s.setColor(RED);
                    x = p;
                }
            } else {
                s = (RBNode<K, V>) p.getLeft();
                // 1.1 rotate to find truly sibling
                if (colorOf(s) == RED) {
                    rightRotate(p);
                    p.setColor(RED);
                    s.setColor(BLACK);
                    // reset s
                    s = (RBNode<K, V>) p.getLeft();
                }
                sl = (RBNode<K, V>) s.getLeft();
                sr = (RBNode<K, V>) s.getRight();
                // 1.2.(1) 兄弟节点可借
                // 这里不能用空来判断，而应该用黑色判断，
                // 因为兄弟节点不可借是会迭代往上，此时x不再是叶子节点，对应的s也不是叶子节点(中的元素)
                // 兄弟节点节点不可借迭代往上，也可能进入兄弟节点可借的情况
//                if (sl != null && sr != null) {
                if (colorOf(sl) != BLACK || colorOf(sr) != BLACK) {
                    if (colorOf(sl) == BLACK) {
                        leftRotate(s);
                        sr.setColor(BLACK);
                        s.setColor(RED);
                        // reset s
                        s = (RBNode<K, V>) p.getLeft();
                    }
                    rightRotate(p);
                    s.setColor(p.getColor());
                    p.setColor(BLACK);
                    sl.setColor(BLACK);
                    x = (RBNode<K, V>) getRoot();
                }
                // 1.2.(2) 兄弟节点不可借, 兄弟节点变红并且迭代往上
                // 相当于: colorOf(sl) == BLACK && colorOf(sr) == BLACK
                else {
                    s.setColor(RED);
                    x = p;
                }
            }
        }
        // 2. 来源于delete中的3.4.(1): fix by rep, rep is red
        // colorOf(x) == RED || x == getRoot()
        x.setColor(BLACK);
    }


    // ------------------------------static-----------------------------------------------------------
    /**
     * Static implementation of getting color of a RBNode, so we can get BLACK by null.
     */
    private static <K extends Comparable<? super K>, V> boolean colorOf(RBNode<K, V> x) {
        return x == null ? BLACK : x.getColor();
    }

    @SuppressWarnings("unused")
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    static class RBNodeImpl<K extends Comparable<? super K>, V> extends BinaryNodeImpl<K, V>
            implements RBNode<K, V> {
        @Setter
        private boolean color; // default black(false)

        public RBNodeImpl(K key, V value, boolean color,
                          RBNode<K, V> left, RBNode<K, V> right, RBNode<K, V> parent) {
            super(key, value, left, right, parent);
            this.color = color;
        }

        public RBNodeImpl(K key, V value, boolean color) {
            super(key, value);
            this.color = color;
        }

        @SuppressWarnings("WeakerAccess")
        public RBNodeImpl(K key, V value) {
            super(key, value);
        }

        public RBNodeImpl(K key, boolean color) {
            super(key);
            this.color = color;
        }

        @Override
        public boolean getColor() {
            return color;
        }

        @Override
        public String toString() {
            return keyValueColor(this);
        }

        public String details() {
            return String.format("{%s--%s--[%s,%s]}",
                    keyValueColor((RBNode<K, V>) getParent()),
                    keyValueColor(this),
                    keyValueColor((RBNode<K, V>) getLeft()),
                    keyValueColor((RBNode<K, V>) getRight()));
        }
    }

}
