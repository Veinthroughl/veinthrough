package veinthrough.leetcode.list;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.generic.Tuple;
import veinthrough.leetcode.list.api.ListNode;

import java.util.stream.Stream;

import static veinthrough.api.list.ListNode.listString;
import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.leetcode.list.api.ListNode.of;

/**
 * 第86题：分隔链表，
 * 给你一个链表的头节点head和一个特定值x，请你对链表进行分隔，使得所有小于x的节点都出现在大于或等于x的节点之前。
 * 你应当 保留 两个分区中每个节点的初始相对位置。
 */
@Slf4j
public class PartitionList {
    @Test
    public void test86() {
        Stream.of(
                Tuple.of(of(1, 4, 3, 2, 5, 2), 3),
                Tuple.of(of(1, 1), 0),
                Tuple.of(of(1, 1), 2))
                .forEach(tuple -> log.info(methodLog(
                        "List", listString(tuple.getFirst()),
                        "Partition by " + tuple.getSecond(), listString(partition(tuple.getFirst(), tuple.getSecond())))));
    }

    private ListNode partition(ListNode head, int x) {
        // boundary
        if (head == null || head.getNext() == null) return head;

        //
        ListNode tail1 = null;
        ListNode head2 = null, tail2 = null;
        ListNode p = head;
        // 1.
        while (p != null) {
            if (p.getVal() < x) {
                if (tail1 == null) head = p;
                else tail1.setNext(p);
                tail1 = p;
            } else {
                if (head2 == null) head2 = p;
                else tail2.setNext(p);
                tail2 = p;
            }
            p = p.getNext();
        }
        // 2. 这里需要将tail2.next置为null, 否则存在环
        if (tail2 != null) tail2.setNext(null);
        // 3. tail1 --> head2
        if (tail1 != null) tail1.setNext(head2);
        // 4. return head
        return tail1 == null ? head2 : head;
    }
}
