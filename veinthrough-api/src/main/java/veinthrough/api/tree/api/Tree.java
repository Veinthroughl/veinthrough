package veinthrough.api.tree.api;

public interface Tree<K extends Comparable<? super K>, V> {
    int height();

    int size();

    TreeNode<K,V> getRoot();

    interface TreeNode<K extends Comparable<? super K>, V> {
        K getKey();

        V getValue();
    }
}
