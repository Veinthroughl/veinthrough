package veinthrough.leetcode.list;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.leetcode.list.api.ListNode;

import java.util.stream.Stream;

import static veinthrough.api.list.ListNode.listString;
import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.leetcode.list.api.ListNode.of;

/**
 * 第83题：删除排序链表中的重复元素，
 * 给定一个已排序的链表的头 head， 删除原始链表中所有重复数字的节点，只留下不同的数字。返回已排序的链表。
 *
 * 第82题：删除排序链表中的重复元素II，
 * 给定一个已排序的链表的头head， 删除所有重复的元素，使每个元素只出现一次 。返回已排序的链表。
 */
@Slf4j
public class RemoveElement {
    @Test
    public void test83() {
        Stream.of(
                of(1, 1, 2),
                of(1, 1, 2, 3, 3))
                .forEach(list -> log.info(methodLog(
                        "Before removing duplicates", listString(list),
                        "After removing duplicates", listString(removeDuplicates(list)))));
    }

    @Test
    public void test82() {
        Stream.of(
                of(1, 2, 2, 2, 3, 4, 4, 4, 5, 5),
                of(2, 2, 2, 2, 3, 4, 4, 4, 5, 5),
                of(2, 2, 2, 2, 3, 3, 4, 4, 5, 5),
                of(2, 2, 2, 2, 3, 3, 4, 4, 5, 6),
                of(1, 1, 1),
                of(1, 1, 2))
                .forEach(list -> log.info(methodLog(
                        "Before removing all duplicates", listString(list),
                        "After removing all duplicates", listString(removeDuplicatesAll(list)))));
    }

    /**
     * 使每个元素只出现一次:
     * (1) p/q相等更新p.getNext(), 相当于删除q
     * (2) p/q不相等更新p, 移动
     */
    private ListNode removeDuplicates(ListNode head) {
        // boundary
        if (head == null || head.getNext() == null) return head;

        //
        ListNode p = head;
        ListNode q;
        while ((q = p.getNext()) != null) {
            // p/q相等更新p.getNext(), 相当于删除q
            if (q.getVal().equals(p.getVal()))
                p.setNext(q.getNext());
                // p/q不相等更新p, 移动
            else
                p = q;
        }
        return head;
    }

    /**
     * 只要有重复就要删除不剩:
     * (1) 因为只要有重复就要删除不剩，这里需要prev
     * (2) [p,q)有重复，更新prev.getNext(), 相当于删除[p,q)
     * (3) [p,q)无重复，更新p, 移动prev
     * #------------------头部不同-----------------------------
     * #        0	1	2	3	4	5	6	7	8	9
     * #    pre	p/h	q
     * #		1	2	2	2	3	4	4	4	5	5
     * #	pre/h	p/q
     * #		1	2	2	2	3	4	4	4	5	5
     * #	pre/h				p/q
     * #		1	2	2	2	3	4	4	4	5	5
     * #		h				pre	p/q
     * #		1	2	2	2	3	4	4	4	5	5
     * #		h				pre				p/q
     * #		1	2	2	2	3	4	4	4	5	5
     * #		h				pre						p/q
     * #-----------------头部相同------------------------------
     * #	pre	p/h	q
     * #		2	2	2	2	3	4	4	4	5	5
     * #	pre	h				p/q
     * #		2	2	2	2	3	4	4	4	5	5
     * #-----------------全部相同------------------------------
     * #	pre	p/h	q
     * #		2	2	2	2	3	3	4	4	5	5
     * #		h				p/q
     * #		2	2	2	2	3	3	4	4	5	5
     * #	pre	h						p/q
     * #		2	2	2	2	3	3	4	4	5	5
     * #	pre	h								p/q
     * #		2	2	2	2	3	3	4	4	5	5
     * #	pre	h										p/q
     * # 最终pre==null, head=p=null
     * #-----------------只有最后不同------------------------------
     * #
     * #	pre	p/h	q
     * #		2	2	2	2	3	3	4	4	5	6
     * #		h				p/q
     * #		2	2	2	2	3	3	4	4	5	6
     * #    pre	h						p/q
     * #		2	2	2	2	3	3	4	4	5	6
     * #    pre	h								p/q
     * #       2	2	2	2	3	3	4	4	5	6
     * #        							  h/pre	p/q
     * #-----------------全部相同------------------------------
     * #    pre	p/h	q
     * #       1	1	1
     * #    pre			  p/q
     * # 最终pre==null, head=p=null
     * #-----------------只有最后不同------------------------------
     * #    pre	p/h	q
     * #       1	1	2
     * #    pre			p/q
     * # 最终pre==null, head=p=2
     */
    private ListNode removeDuplicatesAll(ListNode head) {
        // boundary
        if (head == null || head.getNext() == null) return head;

        //
        ListNode prev = null, p = head, q;
        int count;
        while (p != null && (q = p.getNext()) != null) {
            count = 0;
            // 1. 重复区间[p,q)为重复区间
            while (q != null && p.getVal().equals(q.getVal())) {
                count++;
                q = q.getNext();
            }
            // 2. 处理重复区间[p,q)
            // (1) [p,q)无重复，更新p, 移动prev
            if (count == 0) {
                // 更新head
                if (prev == null) head = p;
                // 更新prev
                prev = p;

            }
            // (2) [p,q)有重复，更新prev.getNext(), 相当于删除[p,q)
            else {
                if (prev != null) prev.setNext(q);
            }
            // 3. 移动p
            p = q;
        }

        return prev == null ? p : head;
    }
}
