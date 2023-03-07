package veinthrough.leetcode.list;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.generic.Pair;
import veinthrough.leetcode.list.api.ListNode;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.leetcode.list.api.ListNode.of;
import static veinthrough.api.list.ListNode.listString;

/**
 * 第19题，链表的倒数第 N 个结点
 * 给你一个链表，删除链表的倒数第 n 个结点，并且返回链表的头结点。
 * 进阶：你能尝试使用一趟扫描实现吗？
 *
 * 第61题，旋转链表
 * 给你一个链表的头节点head，旋转链表，将链表每个节点向右移动 k 个位置。
 * 示例：0,1,2
 * Rotate 1: 2,0,1
 * Rotate 2: 1,2,0
 * Rotate 3: 0,1,2
 * Rotate 4: 2,0,1
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class KthFromEnd {
    private ListNode tail;

    @Test
    public void test19() {
        Stream.of(
                listString(removeNthFromEnd(of(1, 2, 3, 4, 5), 6)), // list长度<n
                listString(removeNthFromEnd(of(1, 2, 3, 4, 5), 2)), // list长度>n
                listString(removeNthFromEnd(of(1), 1)), // list长度==n
                listString(removeNthFromEnd(of(1, 2), 2))) // list长度==n
                .forEach(list -> log.info(methodLog(list)));
    }

    @Test
    public void test61() {
        Stream.of(5, 4, 8, 3).forEach(k -> log.info(methodLog(
                "" + k,
                listString(
                        rotateRight(ListNode.of(1, 2, 3, 4, 5), k)))));
    }

    /**
     * 获取<pair(倒数第n个节点的前置节点, 倒数第n个节点)>
     * &{@link #_kthFromEndRotated(ListNode, int)}:
     * (1) 循环的, 链表长度<k的时候也是有结果的(实际上是求k%n)，
     * 不需要区分结果不存在(3.(1))和prev==null(3.(2))
     * (2) 需要求得tail/n
     *
     * 1. prev,p,q: q最终要走到tail, 而p/q间隔为k-1， 也就是p为倒数第k个(相对于q)
     * 实际操作的时候【不需要p】
     * 2. 初始化: prev(null), q(head,实际上这个时候p为倒数第一个)
     * 3. 最后返回, 这里需要区分结果不存在(1)和prev==null(2)：
     * (1) 链表长度<k: null
     * (2) 链表长度=k: (null,head)
     * (3) 链表长度>k: (prev,p)
     */
    public static Pair<ListNode> kthFromEnd(ListNode head, int k) {
        int ki = k - 1;
        ListNode prev = null, q = head;
        while (true) {
            // 1. 链表没到底，倒数没数完
            // 只移动q
            if (ki > 0 && q.getNext() != null) {
                q = q.getNext();
                ki--;
            }
            // 2. 链表到底
            else if (q.getNext() == null) {
                // (1) 倒数刚好数完
                if (ki == 0) {
                    return prev == null ?
                            Pair.of(null, head) : // 链表长度=k
                            Pair.of(prev, prev.getNext()); // 链表长度>k
                }
                // (2) 倒数没数完(表示链表长度<k)
                else return null;
            }
            // 3. 链表没到底(q.getNext() != null)，倒数已经数完(ki == 0)
            // prev和q同时移动
            else {
                prev = prev == null ? head : prev.getNext();
                q = q.getNext();
            }
        }

    }

    /**
     * 第19题，
     * 方法1：双指针
     * 方法2: 栈[TODO]
     * 我们也可以在遍历链表的同时将所有节点依次入栈。根据栈「先进后出」的原则，
     * 我们弹出栈的第n个节点就是需要删除的节点，并且目前栈顶的节点就是待删除节点的前驱节点。
     * 这样一来，删除操作就变得十分方便了。
     */
    public ListNode removeNthFromEnd(ListNode head, int n) {
        Pair<ListNode> nth = kthFromEnd(head, n);
        // (1) 返回null表示不存在倒数n节点: list长度<n, 这个时候不需要删除
        if (nth != null) {
            ListNode prev = nth.getFirst();
            ListNode p = nth.getSecond();
            // (2) prev==null: list长度==n
            if (prev == null) head = head.getNext();
                // (3)
            else prev.setNext(p.getNext());
        }
        return head;
    }

    /**
     * 第61题，
     */
    private ListNode rotateRight(ListNode head, int k) {
        // boundary
        if (head == null || head.getNext() == null || k == 0) return head;

        //
        ListNode prev;
        if ((prev = _kthFromEndRotated(head, k)) != null) {
            tail.setNext(head);
            head = prev.getNext();
            prev.setNext(null);
        }
        return head;
    }

    /**
     * 与{@link #kthFromEnd(ListNode, int)}的区别:
     * (1) 这里是循环的, 链表长度<k的时候也是有结果的(实际上是求k%n)，
     * 不需要区分结果不存在和prev==null
     * (2) 需要求得tail/n
     *
     * 1. prev,p,q: q最终要走到tail, 而p/q间隔为k-1， 也就是p为倒数第n个(相对于q)
     * 实际操作的时候不需要p
     * 2. 初始化: prev(null), q(head,实际上这个时候p为倒数第一个)
     * 3. 最后返回, 这里需要区分结果不存在(1)和prev==null(2)，
     * (1) 链表长度<k: _kthFromEndRotated(k%n)
     * (2) 链表长度=k: prev(null)
     * (3) 链表长度>k: prev
     */
    private ListNode _kthFromEndRotated(ListNode head, int k) {
        int ki = k - 1;
        int n = 1;
        ListNode prev = null, q = head;
        while (true) {
            // 1. 链表没到底，倒数没数完
            // 只移动q
            if (ki > 0 && q.getNext() != null) {
                q = q.getNext();
                n++;
                ki--;
            }
            // 2. 链表到底
            else if (q.getNext() == null) {
                tail = q; // 获取到tail
                if (ki == 0) return prev;                       // (1) 倒数刚好数完
                else return _kthFromEndRotated(head, k % n);// (2) 倒数没数完，求模递归
            }
            // 3. 链表没到底(q.getNext() != null)，倒数已经数完(ki == 0)
            // prev和q同时移动
            else {
                prev = prev == null ? head : prev.getNext();
                q = q.getNext();
            }
        }

    }
}