package veinthrough.api.list;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@SuppressWarnings({"Duplicates", "unused", "WeakerAccess"})
@NoArgsConstructor
@AllArgsConstructor
public class ListNodeImpl<V extends Comparable<? super V>> implements ListNode<V> {
    @Getter
    @Setter
    private V val;
    @Getter
    @Setter
    private ListNode<V> next;

    public ListNodeImpl(V val) {
        this(val, null);
    }

    @Override
    public String toString() {
        return "[" + val + "]";
    }

    @Override
    public int compareTo(ListNode<V> o) {
        return val.compareTo(o.getVal());
    }

    /**
     * @param nums 这里使用List而不是可变参数(V ...nums)，泛型和可变参数最好不要一起用
     *             (1) 由于泛型擦除, 可能会引起Java堆污染(Heap Pollution)
     *             (2) (int ...nums)与(Integer ...nums)因为签名一样在调用时不能区分
     */
    public static <V extends Comparable<? super V>> ListNode<V> of(List<V> nums) {
        int len;
        if (nums == null || (len=nums.size())==0) return null;
        
        ListNode<V> head = new ListNodeImpl<>(nums.get(0));
        ListNode<V> p = head;
        for (int i = 1; i <len;i++) {
            p.setNext(new ListNodeImpl<>(nums.get(i)));
            p = p.getNext();
        }
        return head;
    }
}