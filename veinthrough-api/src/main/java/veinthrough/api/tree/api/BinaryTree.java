package veinthrough.api.tree.api;

import java.util.List;

@SuppressWarnings("unused")
public interface BinaryTree<K extends Comparable<? super K>, V>
        extends Tree<K, V> {

    BinaryNode<K, V> find(K key);

    BinaryNode<K, V> max();

    BinaryNode<K, V> min();

    BinaryNode<K, V> predecessor(BinaryNode<K, V> x);

    BinaryNode<K, V> successor(BinaryNode<K, V> x);

    void leftRotate(BinaryNode<K, V> x);

    void rightRotate(BinaryNode<K, V> x);

    void put(K key, V value);

    boolean delete(K key);

    List<BinaryNode<K, V>> levelOrder();

    List<BinaryNode<K, V>> preOrder();

    List<BinaryNode<K, V>> inOrder();

    List<BinaryNode<K, V>> postOrder();

    interface BinaryNode<K extends Comparable<? super K>, V>
            extends TreeNode<K, V> {
        BinaryNode<K, V> getLeft();

        BinaryNode<K, V> getRight();

        BinaryNode<K, V> getParent();

        void setLeft(BinaryNode<K, V> x);

        void setRight(BinaryNode<K, V> x);

        void setParent(BinaryNode<K, V> x);

        K getKey();

        V getValue();

        void setKey(K key);

        void setValue(V value);

        /**
         * Static implementation of getting string of a node by key/value,
         * so we can get string by null.
         */
        static <K extends Comparable<? super K>, V> String keyValue(BinaryNode<K, V> x) {
            return x == null ? "N" :
                    x.getValue() == null ?
                            "(" + x.getKey() + ")" :
                            "(" + x.getKey() + "," + x.getValue() + ")";
        }
    }
}
