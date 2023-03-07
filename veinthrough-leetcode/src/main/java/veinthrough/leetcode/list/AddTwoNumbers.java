package veinthrough.leetcode.list;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.leetcode.list.api.ListNode;

import static veinthrough.api.list.ListNode.listString;
import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.leetcode.list.api.ListNode.of;

/**
 * 第2题：
 * 给你两个非空的链表，表示两个非负的整数。它们每位数字都是按照【逆序】的方式存储的并且每个节点只能存储一位数字。
 * 请你将两个数相加，并以相同形式返回一个表示和的链表。
 * 你可以假设除了数字0之外，这两个数都不会以0开头。
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class AddTwoNumbers {
    @Test
    public void test2() {
        log.info(methodLog(
                "342+465",
                listString(addTwoNumbers(of(2, 4, 3), of(5, 6, 4))),
                "342+465",
                listString(addTwoNumbers2(of(2, 4, 3), of(5, 6, 4)))));
        log.info(methodLog(
                "9999999+9999",
                listString(addTwoNumbers(of(9, 9, 9, 9, 9, 9, 9), of(9, 9, 9, 9))),
                "9999999+9999",
                listString(addTwoNumbers2(of(9, 9, 9, 9, 9, 9, 9), of(9, 9, 9, 9)))));
    }

    private ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        // 提出第一步是为了不在每次循环里判断是否是第一个节点
        // 从而去新建节点
        int sum = l1.getVal() + l2.getVal();
        int carry = sum > 9 ? 1 : 0;
        ListNode result = new ListNode(sum - carry * 10);
        l1 = l1.getNext();
        l2 = l2.getNext();
        ListNode p = result;
        while (l1 != null || l2 != null) {
            int value1 = l1 == null ? 0 : l1.getVal();
            int value2 = l2 == null ? 0 : l2.getVal();
            sum = value1 + value2 + carry;
            carry = sum > 9 ? 1 : 0;
            p.setNext(new ListNode(sum - carry * 10));
            // continue only if not null
            if (l1 != null) l1 = l1.getNext();
            if (l2 != null) l2 = l2.getNext();
            p = p.getNext();
        }
        if (1 == carry) p.setNext(new ListNode(1));
        return result;
    }

    /**
     * 优化: 没有提出第一步
     */
    private ListNode addTwoNumbers2(ListNode l1, ListNode l2) {
        int carry = 0;
        int sum;
        ListNode result = new ListNode();
        ListNode p = result;
        while (true) {
            // calculate
            int value1 = l1 == null ? 0 : l1.getVal();
            int value2 = l2 == null ? 0 : l2.getVal();
            sum = value1 + value2 + carry;
            carry = sum > 9 ? 1 : 0;
            p.setVal(sum % 10);

            // continue only if not null
            if (l1 != null) l1 = l1.getNext();
            if (l2 != null) l2 = l2.getNext();
            if (l1 == null && l2 == null && carry == 0) break;

            // next
            p.setNext(new ListNode());
            p = p.getNext();
        }
        return result;
    }
}