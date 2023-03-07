package veinthrough.leetcode.list.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * IntListNode的存在只是为了在leetcode中更方便，类似于C++中的struct。
 * 通用的ListNode参考{@link veinthrough.api.list.ListNode}。
 */
@SuppressWarnings("Duplicates")
@NoArgsConstructor
@AllArgsConstructor
public class ListNode implements veinthrough.api.list.ListNode<Integer> {
    /*
    不好使用@Getter/@Setter来override接口:
    (1) val: int/Integer
    (2) next: 类属性为接口中返回/传入类型的继承类型
     */
    private int val;
    @Getter
    private ListNode next;

    public ListNode(int val) {
        this(val, null);
    }

    @Override
    public Integer getVal() {
        return this.val;
    }

    @Override
    public void setVal(Integer value) {
        this.val = value;
    }

    @Override
    public void setNext(veinthrough.api.list.ListNode<Integer> next) {
//        checkArgument(next == null || next instanceof ListNode);
        this.next = next == null ? null : (ListNode) next;
    }

    @Override
    public int compareTo(veinthrough.api.list.ListNode<Integer> o) {
        checkArgument(o instanceof ListNode);
        return this.val - o.getVal();

    }

    @Override
    public String toString() {
        return "[" + val + "]";
    }

    public static ListNode of(int... nums) {
        int len;
        if (nums == null || (len = nums.length) == 0) return null;

        ListNode head = new ListNode(nums[0]);
        ListNode p = head;
        for (int i = 1; i < len; i++) {
            p.setNext(new ListNode(nums[i]));
            p = p.getNext();
        }
        return head;
    }
}