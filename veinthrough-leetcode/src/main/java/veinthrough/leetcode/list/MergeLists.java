package veinthrough.leetcode.list;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.leetcode.list.api.ListNode;

import java.util.*;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.leetcode.list.api.ListNode.of;
import static veinthrough.api.list.ListNode.listString;

/**
 * 第21题：
 * 将两个升序链表合并为一个新的 升序 链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。
 *
 * 第23题：
 * 给你一个链表数组，每个链表都已经按升序排列。
 * 请你将所有链表合并到一个升序链表中，返回合并后的链表。
 * 方法1：分治，见文档
 * 方法2：我们需要维护当前每个链表没有被合并的元素的最前面一个，k个链表就最多有 k个满足这样条件的元素，
 * 每次在这些元素里面选取 val 属性最小的元素合并到答案中。
 * (1) 在选取最小元素的时候，我们可以用优先队列（堆）来优化这个过程。
 * (2) 补充元素：每次将选取的最小元素的next作为补充
 */
@Slf4j
public class MergeLists {
    private static final int DEFAULT_HEAP_SIZE = 11;
    private static ListNode[] lists;
    private static ListNode[] interArray;
    private static PriorityQueue<ListNode> interHeap;
    private static int length, left;

    @Test
    public void test21() {
        Stream.of(
                new ListNode[]{null, of(1)},
                new ListNode[]{null, null},
                new ListNode[]{
                        of(1, 4, 5, 11, 12, 17, 19),
                        of(2, 3, 7, 13, 14, 20)})
                .forEach(lists ->
                        log.info(methodLog(listString(mergeLists(lists)))));
    }

    @Test
    public void test23() {
        Stream.of(
                new ListNode[]{
                        of(1, 4, 5, 11, 12, 17, 19),
                        of(2, 3, 7, 13, 14, 20),
                        of(6, 8, 9, 10, 15, 16, 18)},
                new ListNode[]{
                        of(1, 9),
                        of(2, 14),
                        of(3, 10),
                        of(11, 13),
                        of(12, 24),
                        of(5, 6),
                        of(15, 16),
                        of(7, 22),
                        of(8, 23),
                        of(4, 17),
                        of(18, 21),
                        of(19, 20)})
                .forEach(lists ->
                        log.info(methodLog(listString(mergeLists(lists)))));
    }

    /**
     * 第23题: 方法2
     * 我们需要维护当前每个链表没有被合并的元素的最前面一个，k个链表就最多有 k个满足这样条件的元素，
     * 每次在这些元素里面选取 val 属性最小的元素合并到答案中。
     * (1) 在选取最小元素的时候，我们可以用优先队列（堆）来优化这个过程。
     * (2) 补充元素：每次将选取的最小元素的next作为补充
     */
    public static ListNode mergeLists(ListNode[] lists) {
        // 1. empty/1 list
        if (lists.length == 0) return null;
        if (lists.length == 1) return lists[0];

        // 2. 2 lists
        if (lists.length == 2) return merge2Lists(lists[0], lists[1]);

        // 3. >2 lists
        MergeLists.lists = lists;
        left = length = lists.length;
        // (1) build each list
        if (!buildEachList()) return null;
        if (length == 1) return getFromArray();

        // (2) < DEFAULT_HEAP_SIZE lists
        ListNode p, head;
        if (length < DEFAULT_HEAP_SIZE) {
            // until last list
            for (head = getFromArray(), p = head; left > 1; ) {
                p.setNext(getFromArray());
                p = p.getNext();
            }
            // last list
            p.setNext(getFromArray());
            // (3) >= DEFAULT_HEAP_SIZE lists
        } else {
            // until last list
            for (head = getFromHeap(), p = head; left > 1; ) {
                p.setNext(getFromHeap());
                p = p.getNext();
            }
            // last list
            p.setNext(getFromHeap());
        }

        return head;
    }

    /**
     * 第21题
     */
    private static ListNode merge2Lists(ListNode l1, ListNode l2) {
        if (l1 == null) return l2;
        if (l2 == null) return l1;

        ListNode head, t, p, q;
        if (l1.getVal() < l2.getVal()) {
            head = l1;
            l1 = l1.getNext();
        } else {
            head = l2;
            l2 = l2.getNext();
        }
        for (p = l1, q = l2, t = head; ; ) {
            if (p == null) {
                t.setNext(q);
                return head;
            }
            if (q == null) {
                t.setNext(p);
                return head;
            }
            if (p.getVal() < q.getVal()) {
                t.setNext(p);
                t = t.getNext();
                p = p.getNext();
            } else {
                t.setNext(q);
                t = t.getNext();
                q = q.getNext();
            }
        }
    }

    private static boolean buildEachList() {
        List<ListNode> interNodes = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            if (lists[i] != null) {
                // add inter node
                interNodes.add(lists[i]);
            } else
                left--;
        }

        length = left;
        if (length == 0) return false;

        // inter array/heap
        if (length < DEFAULT_HEAP_SIZE) {
            interArray = interNodes.toArray(new ListNode[0]);
            Arrays.sort(interArray, Comparator.comparingInt(ListNode::getVal));
        } else {
            interHeap = new PriorityQueue<>(length, Comparator.comparingInt(ListNode::getVal));
            interHeap.addAll(interNodes);
        }
        return true;
    }

    private static ListNode getFromArray() {
        ListNode node, result;
        // (1) get node from 第一个没有遍历完的list
        node = result = interArray[length - left];
        // (2) if last node in the list
        if (node.getNext() == null)
            left--;
            // (3) put next node in interArray
        else {
            node = node.getNext();
            int k = length - left + 1;
            // 插入排序
            while (k < length && interArray[k].getVal() < node.getVal()) {
                interArray[k - 1] = interArray[k];
                k++;
            }
            interArray[k - 1] = node;
        }
        return result;
    }

    private static ListNode getFromHeap() {
        ListNode node, result;

        // (1) get node from interHeap
        // 可以保证poll()非空,
        node = result = interHeap.poll();
        // (2) last node in the list
        // noinspection ConstantConditions
        if (node.getNext() == null)
            left--;
            // (3) put next node in interHeap
        else {
            node = node.getNext();
            interHeap.offer(node);
        }
        return result;
    }
}