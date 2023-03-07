package veinthrough.test.tree;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.tree.RBTreeImpl;
import veinthrough.api.tree.api.BinaryTree;
import veinthrough.api.tree.api.BinaryTree.BinaryNode;
import veinthrough.api.tree.api.RBTree;

import static veinthrough.api.tree.BinaryTreeImpl.of;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author Veinthrough
 */
@Slf4j
public class TreeTest {
    @Test
    public void BinaryTreeTest() {
        /*
         #             4
         #         /      \
         #        1        7
         #       / \      /  \
         #      0   3    6    8
         #         /    /      \
         #        2    5        9
         ==>(4,1,7,0,3,6,8,null,null,2,null,5,null,null,9)
         height: 4,
         size: 10,
         level order: [(4), (1), (7), (0), (3), (6), (8), (2), (5), (9)],
         pre order: [(4), (1), (0), (3), (2), (7), (6), (5), (8), (9)],
         in order: [(0), (1), (2), (3), (4), (5), (6), (7), (8), (9)],
         post order: [(0), (2), (3), (1), (5), (6), (9), (8), (7), (4)]
         */
        BinaryTree<Integer, Integer> tree =
                of(new Integer[]{4, 1, 7, 0, 3, 6, 8, null, null, 2, null, 5, null, null, 9});
        int step = -1;

        log.info(methodLog(++step,
                "Initial state", tree.toString(),
                "height", String.valueOf(tree.height()),
                "size", String.valueOf(tree.size()),
                "level order", tree.levelOrder().toString(),
                "pre order", tree.preOrder().toString(),
                "in order", tree.inOrder().toString(),
                "post order", tree.postOrder().toString()));

        /*
         1. left rotate by root(4)
         #                   7
         #                /    \
         #               4      8
         #            /   \       \
         #           1    6        9
         #         /  \   /
         #        0   3  5
         #           /
         #          2
         ==>（7,4,8,1,6,null,9,0,3,5,null,null,null,null,null,2)
         */
        tree.leftRotate((BinaryNode<Integer, Integer>) tree.getRoot());
        log.info(methodLog(++step,
                "After left rotate", tree.toString(),
                "height", String.valueOf(tree.height()),
                "size", String.valueOf(tree.size())));
        
        /*
         2. right rotate by root(7)
         
         #            4
         #         /    \
         #       1       7
         #     /  \    /  \
         #    0   3   6    8
         #      /    /      \
         #     2    5        9
         ==>(4,1,7,0,3,6,8,null,null,2,null,5,null,null,9)
         */
        tree.rightRotate((BinaryNode<Integer, Integer>) tree.getRoot());
        log.info(methodLog(++step,
                "After right rotate", tree.toString(),
                "height", String.valueOf(tree.height()),
                "size", String.valueOf(tree.size())));

        /*
         3. delete 7
         
         rep: predecessor
         #            4
         #         /    \
         #       1       6
         #     /  \    /  \
         #    0   3   5    8
         #      /           \
         #     2             9
         ==>(4,1,6,0,3,5,8,null,null,2,null,null,null,null,9)
         */
        Integer toDelete = 7;
        tree.delete(toDelete);
        log.info(methodLog(++step,
                String.format("After delete node %s", toDelete), tree.toString(),
                "height", String.valueOf(tree.height()),
                "size", String.valueOf(tree.size())));

        /*
         4. put 7

         rep: predecessor
         #            4
         #         /    \
         #       1       6
         #     /  \    /  \
         #    0   3   5    8
         #      /         /  \
         #     2         7    9
         ==>(4,1,6,0,3,5,8,null,null,2,null,null,null,7,9)
         */
        Integer toPut = 7;
        tree.put(7, null);
        log.info(methodLog(++step,
                String.format("After put node %s", toPut), tree.toString(),
                "height", String.valueOf(tree.height()),
                "size", String.valueOf(tree.size())));
    }

    @Test
    public void RBTreeTest() {
        /*
         #            B3
         #         /      \
         #        B1       B5
         #       / \      /  \
         #     B0  B2   B4    R7
         #                  /   \
         #                B6    B8
         #                        \
         #                         R9
         ==>[B(3),B(1),B(5),B(0),B(2),B(4),R(7),(N),(N),(N),(N),(N),(N),B(6),B(8),(N),(N),(N),R(9)],
         height: 5,
         size: 10,
         level order: [B(3), B(1), B(5), B(0), B(2), B(4), R(7), B(6), B(8), R(9)],
         pre order: [B(3), B(1), B(0), B(2), B(5), B(4), R(7), B(6), B(8), R(9)],
         in order: [B(0), B(1), B(2), B(3), B(4), B(5), B(6), R(7), B(8), R(9)],
         post order: [B(0), B(2), B(1), B(4), B(6), R(9), B(8), R(7), B(5), B(3)]
         */
        RBTree<Integer, Integer> tree = new RBTreeImpl<>();
        int step = -1;

        for (int i = 0; i < 10; i++) {
            tree.put(i, null);
        }
        log.info(methodLog(++step,
                "Initial state", tree.toString(),
                "height", String.valueOf(tree.height()),
                "size", String.valueOf(tree.size()),
                "level order", tree.levelOrder().toString(),
                "pre order", tree.preOrder().toString(),
                "in order", tree.inOrder().toString(),
                "post order", tree.postOrder().toString()));

        /*
         #            B3
         #         /      \
         #        B1       B7
         #       / \      /  \
         #     B0  B2   B4    B8
         #               \     \
         #               R6     R9
         ==>[B(3),B(1),B(7),B(0),B(2),B(4),B(8),(N),(N),(N),(N),(N),R(6),(N),R(9)],
         height: 4,
         size: 9
         */
        Integer toDelete = 5;
        tree.delete(toDelete);
        log.info(methodLog(++step,
                String.format("After delete node %s", toDelete), tree.toString(),
                "height", String.valueOf(tree.height()),
                "size", String.valueOf(tree.size())));

        /*
         #            B2
         #         /      \
         #        B1       R7
         #       /        /  \
         #     R0       B4    B8
         #               \     \
         #               R6     R9
         ==>[B(2),B(1),R(7),R(0),(N),B(4),B(8),(N),(N),(N),R(6),(N),R(9)],
         height: 4,
         size: 8
         */
        toDelete = 3;
        tree.delete(toDelete);
        log.info(methodLog(++step,
                String.format("After delete node %s", toDelete), tree.toString(),
                "height", String.valueOf(tree.height()),
                "size", String.valueOf(tree.size())));
    }
}
