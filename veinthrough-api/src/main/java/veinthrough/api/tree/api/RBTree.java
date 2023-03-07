package veinthrough.api.tree.api;

import static veinthrough.api.tree.api.BinaryTree.BinaryNode.keyValue;

public interface RBTree<K extends Comparable<? super K>, V>
        extends BinaryTree<K, V> {
    boolean BLACK = false;
    boolean RED = true;

    interface RBNode<K extends Comparable<? super K>, V> extends BinaryNode<K, V> {
        boolean getColor();

        void setColor(boolean color);

        /**
         * Static implementation of getting string of a node by key/value/color,
         * so we can get string by null.
         */
        static <K extends Comparable<? super K>, V> String keyValueColor(RBNode<K, V> x) {
            return x == null ? "N" :
                    (x.getColor() == BLACK ? "B" : "R") + keyValue(x);
        }
    }

}
