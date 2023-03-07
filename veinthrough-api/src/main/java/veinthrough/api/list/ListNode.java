package veinthrough.api.list;

public interface ListNode<V extends Comparable<? super V>> extends Comparable<ListNode<V>> {
    V getVal();

    void setVal(V value);

    ListNode<V> getNext();

    void setNext(ListNode<V> next);
    
    // 相对于toString, 可以处理null的问题
    static String listString(ListNode<?> head) {
        if (head == null) return "[]";
        if (head.getNext() == null) return "[" + head.getVal() + "]";

        StringBuilder str = new StringBuilder("[" + head.getVal());
        ListNode<?> p = head;
        while (p.getNext() != null) {
            p = p.getNext();
            str.append(",").append(p.getVal());
        }
        return str.append("]").toString();
    }
}
