package veinthrough.api.tree;

import lombok.NoArgsConstructor;
import veinthrough.api.tree.api.BinaryTree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


@NoArgsConstructor
public class BinaryTreeImpl<K extends Comparable<? super K>, V> extends AbstractBinaryTree<K, V>
        implements BinaryTree<K, V> {

    @SuppressWarnings("WeakerAccess")
    public BinaryTreeImpl(BinaryNode<K, V> root) {
        super(root);
    }

    /**
     * New a node by key/value, which are different in different extended classes.
     */
    @Override
    BinaryNode<K, V> newNode(K key, V value) {
        return new BinaryNodeImpl<>(key, value);
    }

    /**
     * Put x under(as a child of) p.
     * Called only when x!=null && p!=null.
     */
    @Override
    void putAt(BinaryNode<K, V> x, BinaryNode<K, V> p) {
        // (1) link to each other
        link(x, p);
    }

    /**
     * Delete node x and return the truly deleted node.
     * Called only when x!=null.
     *
     * @param x the node to be deleted
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
    BinaryNode<K, V> delete(BinaryNode<K, V> x) {
        // 1. replacement: predecessor or successor
        // x:
        // (1) 2 children, replace it's key/value with predecessor/successor
        // (2) 1 child
        // (3) leaf node
        BinaryNode<K, V> rep;
        if (x.getLeft() != null && x.getRight() != null) {
            rep = predecessor(x);
//            rep = successor(x);
            x.setKey(rep.getKey());
            x.setValue(rep.getValue());
            x = rep;
        }

        // 2. x(after replaced by predecessor/successor):
        // (1) 1 child, rep is the single child
        // (2) leaf node
        BinaryNode<K, V> p, l;
        rep = (l = x.getLeft()) != null ? x.getLeft() : x.getRight();

        // 3. update:
        // (1) set p's child to rep
        if ((p = x.getParent()) != null) {
            if (rep == l) p.setLeft(rep);
            else p.setRight(rep);
        }
        // (2) update root: p is null  ==>  x is root
        else setRoot(rep);

        // (3) set rep's parent to p
        if (rep != null) rep.setParent(p);

        // (4) recycle x
//        x.setLeft(null);
//        x.setRight(null);
//        x.setParent(null);

        return x;
    }

    //-------------------------------------static--------------------------------------------------
    /**
     * Static method of building a tree from list keys of level-order and values.
     *
     * @param keys      keys by level order.
     * @param valuesMap map of keys and values.
     * @return the built tree.
     */
    public static <K extends Comparable<? super K>, V> BinaryTree<K, V> of(K[] keys, Map<K, V> valuesMap) {
        if (keys == null || keys[0] == null) return null;

        // valuesMap may be null
        if (valuesMap == null) valuesMap = new HashMap<>(0);
        Queue<BinaryNode<K, V>> queue = new LinkedList<>();
        BinaryNode<K, V> root = new BinaryNodeImpl<>(keys[0], valuesMap.get(keys[0]));
        queue.add(root);
        int i = 0, size = 0;
        BinaryNode<K, V> x, l, r;
        while (!queue.isEmpty()) {
            size++;
            x = queue.poll();
            // left
            l = ++i < keys.length && keys[i] != null ?
                    new BinaryNodeImpl<>(keys[i], valuesMap.get(keys[i])) : null;
            if (l != null) {
                x.setLeft(l);
                queue.add(l);
            }
            // right
            r = ++i < keys.length && keys[i] != null ?
                    new BinaryNodeImpl<>(keys[i], valuesMap.get(keys[i])) : null;
            if (r != null) {
                x.setRight(r);
                queue.add(r);
            }
        }

        BinaryTreeImpl<K, V> tree = new BinaryTreeImpl<>(root);
        tree.setSize(size);

        return tree;
    }

    /**
     * Static method of building a tree from list keys of level-order.
     *
     * @param keys      keys by level order.
     * @return the built tree.
     */
    public static <K extends Comparable<? super K>, V> BinaryTree<K, V> of(K[] keys) {
        return of(keys, null);
    }
}
