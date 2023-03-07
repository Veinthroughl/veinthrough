package veinthrough.leetcode.list;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.list.ListNode;
import veinthrough.leetcode.ExampleForErrorException;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.leetcode.list.api.ListNode.of;
import static veinthrough.api.list.ListNode.listString;

/**
 * 第24题：两两交换翻转链表
 * 给定一个链表，两两交换其中相邻的节点，并返回交换后的链表。
 * 你不能只是单纯的改变节点内部的值，而是需要实际的进行节点交换。
 *
 * 第25题：K个一组翻转链表
 * 给你一个链表，每k个节点一组进行翻转，请你返回翻转后的链表。
 * k是一个正整数，它的值小于或等于链表的长度。
 * 如果节点总数不是k的整数倍，那么请将最后剩余的节点保持原有顺序。
 * 进阶：
 * 你可以设计一个只使用常数额外空间的算法来解决此问题吗？
 * 你不能只是单纯的改变节点内部的值，而是需要实际进行节点交换。
 *
 * 第92题：翻转链表II
 * 给你单链表的头指针head和两个整数left和right，其中left<=right。
 * 请你反转从位置left到位置right的链表节点，返回反转后的链表。
 *
 *
 * K个一组翻转总结:
 * 1. 功能拆分: (1)找到K个元素的段; (2)段内翻转((p-->q)需要翻转成(q-->p)); (3)段间链接(维持(p-->q)不变);
 * (1)找到K个元素的段只能分开操作, 必须先找到k个元素的段再reverse该段, 不能上来就reverse, 因为有可能整个链表都没有k个元素;
 * 既然(1)找到K个元素的段分开操作, 那么(3)段间链接)应该和(1)找到K个元素的段放在一起操作(从设计模式的角度);
 * 所以最终形成了: 上层负责(1)找到K个元素的段+(3)段间链接, (2)段内reverse作为另外一个单独功能
 *
 * 2. K=2, 【见图示】作为特殊情况((1)找到K个元素的段不需要单独算作一个功能), K内翻转和K间链接同时进行, {@link #reverse2Group(ListNode)}
 *
 * 3. {@link #reverseKGroup(ListNode, int)}: 作为上层负责(1)找到K个元素的段+(3)段间链接
 *
 * 4. 段内reverse【见图示】:
 * (1) 方法1: p.next暂存q.next, p/q构成循环依赖, 结果【错误】, {@link #_reverseInterval_error1(ListNode, int)}
 * (2) 方法2: p.next暂存q.next, p/q构成循环依赖, 【试图绕过循环依赖】, 但结果【错误】, {@link #_reverseInterval_error2(ListNode, int)}
 * (3) 方法3: 新建next节点保存q.next
 * > {@link #_reverseInterval(ListNode head, int k)}: [head, head+k]
 * > {@link #_reverseInterval(ListNode head, ListNode end)}: [head, end)
 * (4) 方法4: 双指针对称翻转,但【性能不太好】, {@link #_reverseInterval_DP(ListNode, int)}
 *
 * 5. {@link #_reverseInterval(ListNode head, int start, int end)}: [head+start, head+end],
 * 因为同时涉及到(需翻转的段)和(不需翻转的段), 这里同时需要(1)找到k段(2)段内翻转(3)段间链接3个功能
 */
@SuppressWarnings({"Duplicates", "unused", "DeprecatedIsStillUsed"})
@Slf4j
public class ReverseKGroup {
    private ListNode<Integer> list = of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);

    @Test
    public void test24() {
        log.info(methodLog(
                "Reverse " + listString(list) + "by 2",
                listString(reverseKGroup(list, 2))));
    }

    @Test
    public void test25() {
        Stream.of(3, 4, 5, 6, 1)
                .forEach(k -> {
                    log.info(methodLog(
                            "Reverse " + listString(list) + "by " + k,
                            listString(reverseKGroup(list, k))));
                    list = of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11); // 需要重置list
                });
    }

    @Test
    public void test92() {
        Stream.of(1, 5)
                .forEach(left -> Stream.of(10, 11, 12)
                        .forEach(right -> {
                            log.info(methodLog(
                                    "Reverse " + listString(list) + "from " + left + " to " + right,
                                    listString(_reverseInterval(list, left, right))));
                            list = of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11); // 需要重置list
                        }));
    }

    /**
     * K=2,【见图示】
     * K内翻转和K间链接同时进行, p.next保存的正好是下一个k头(翻转前), K内翻转和K间链接可以无缝链接;
     * ------------->
     * |             |
     * ● <-- ● --> ● --> ●
     * p     q    p.next
     * 这里仍然需要记录pre tail(上一段尾), 因为上一段尾指向(p.next)最终要指向下一个k头(翻转后)，而不是下一个k头(翻转前)
     */
    private ListNode<Integer> reverse2Group(ListNode<Integer> head) {
        if (head == null || head.getNext() == null) return head;

        ListNode<Integer> prevTail, p, q;
        prevTail = null;
        p = head;
        if (((q = p.getNext()) == null)) return head;
        head = q; // head 为第2个元素

        while (true) {
            // 1. p.next保存q.next, 保存的是下一个p(正好是下一个k开头(翻转前))
            p.setNext(q.getNext());
            // 2. reverse
            q.setNext(p);

            // 3. 上一个k尾指向这一个k头
            if (prevTail != null) prevTail.setNext(q);

            // 4. next round. p.next保存的正好是下一个k开头
            prevTail = p;
            if ((p = p.getNext()) == null) return head;
            if ((q = p.getNext()) == null) return head;
        }
    }

    /**
     * 作为上层负责(1)找到段+(3)段间链接功能，
     * 调用(1)段内翻转:
     * 方法3: 新建next节点保存q.next
     * > {@link #_reverseInterval(ListNode head, int k)}: [head, head+k]
     * > {@link #_reverseInterval(ListNode head, ListNode end)}: [head, end)
     * 方法4: 双指针对称翻转,但【性能不太好】, {@link #_reverseInterval_DP(ListNode, int)}
     */
    private ListNode<Integer> reverseKGroup(ListNode<Integer> head, int k) {
        // boundary
        // k<=1, 只有1个节点
        if (k <= 1 || head.getNext() == null) return head;
        if (k == 2) return reverse2Group(head);

        // 需要nextP来保存每一段的头(翻转前),
        // 比如最后一段不足k个元素, q会走到nextP后面而nextP由于不满足(i==k)而得不到设置
        ListNode<Integer> p = head, tail = null, q = p, nextP = null;
        // 1. 找到并翻转每个k段
        for (int i = 2; q != null && (q = q.getNext()) != null; i++) {
            // 1.1 找到K个元素的段
            if (i == k) {
                // 1.2 保存下一个k段头(翻转前), 只有达到k个元素才会设置
                nextP = q.getNext();
                // 1.3 reverse, 只需要p
                // 方法3
                if (tail == null) head = _reverseInterval(p, k);
                else tail.setNext(_reverseInterval(p, k));
                // 方法3
//                if (tail == null) head = _reverseInterval(p, nextP);
//                else tail.setNext(_reverseInterval(p, nextP));
                // 方法4
//                if (tail == null) head = _reverseInterval_DP(p, k);
//                else tail.setNext(_reverseInterval_DP(p, k));

                // 1.4 relocate next K:
                // this K: [q,p]
                tail = p;
                q = p = nextP;
                i = 1;
            }
        }
        // 2. 链接上最后一段
        // 最后一段可能不足k个元素, 如果链表正好被k整除, 那么nextP为null
        if (tail != null) tail.setNext(nextP);
        return head;
    }


    /**
     * K>2, K内翻转,
     * 方法2【见图示】: p.next暂存q.next, p/q构成循环依赖,
     * 结果【错误】: q已经丢失原值, 导致初始化next round p/q不正确
     *
     * @throws ExampleForErrorException, 错误示例, 结果【错误】
     */
    @Deprecated
    private ListNode<Integer> _reverseInterval_error1(ListNode<Integer> head, int k) throws ExampleForErrorException {
        ListNode<Integer> p = head, q = p.getNext();
        for (int i = 2; q != null && i <= k; i++) {
            // 1. 使用p.next暂存q.next(next round)
            p.setNext(q.getNext());

            // 2. reverse
            q.setNext(p);

            // 3. next round p/q, 这里是【错误】的, p/q循环依赖
            q = p.getNext(); // 提取q.next(next round), 但q已经丢失原值
            p = q; // q已经丢失原值
        }
        throw new ExampleForErrorException();
//        return p;
    }

    /**
     * K>2, K内翻转,
     * 方法2, 【见图示】: p.next暂存q.next, p/q构成循环依赖, 【试图绕过循环依赖】,
     * 但结果【错误】:
     * (1) 初始化next round p/q正确,
     * (2) next round处理过程中: p为last round的q(即逆向指向last round的p),
     * 如果继续使用p.next暂存q.next, 将导致last round的逆向结果丢失
     *
     * @throws ExampleForErrorException, 错误示例, 结果【错误】
     */
    @Deprecated
    private ListNode<Integer> _reverseInterval_error2(ListNode<Integer> head, int k) throws ExampleForErrorException {
        ListNode<Integer> p = head, q = p.getNext();
        for (int i = 2; q != null && i <= k; i++) {
            // 1. 使用p.next暂存q.next(next round)
            p.setNext(q.getNext());

            // 2. reverse
            q.setNext(p);

            // 3. next round, p/q循环依赖, 【试图绕过循环依赖】, 但结果仍【错误】
            p = q; // 提取q.next(next round), 但q已经丢失原值
            q = p.getNext().getNext(); // 试图绕过循环依赖
        }
        throw new ExampleForErrorException();
//        return p;
    }

    /**
     * K>2, K内翻转,
     * 方法3, 【见图示】: 新建next节点保存q.next,
     * (1) 首节点head(逆向完之后为tail)会形成环,
     * (2) 但因为上层({@link #reverseKGroup(ListNode, int)}会将这个首节点(逆向完之后为tail)链接到下一个K,
     * 所以需要把head.next置空
     * (3) 但我们不能保证上层在所有情况下都会完成链接, 比如(整个链表的长度==k), 所以这里最好还是置空
     *
     * @param head inclusive
     * @param k    num of nodes to be reversed
     */
    private ListNode<Integer> _reverseInterval(ListNode<Integer> head, int k) {
        ListNode<Integer> p = head, q = p, next = q.getNext();
        for (int i = 2; (q = next) != null && i <= k; i++) {
            // 1. save next(round)
            next = q.getNext();

            // 2. reverse
            q.setNext(p);

            // 3. next round
            p = q;
        }

        // 4. 因为首节点(逆向完之后为tail)会形成环, 需要把head.next置空
        head.setNext(null);
        return p;
    }

    /**
     * K>2, K内翻转,
     * 方法3, 【见图示】: 新建next节点保存q.next,
     * (1) 首节点head(逆向完之后为tail)会形成环,
     * (2) 但因为上层({@link #reverseKGroup(ListNode, int)}会将这个首节点(逆向完之后为tail)链接到下一个K,
     * 所以需要把head.next置空
     * (3) 但我们不能保证上层在所有情况下都会完成链接, 比如(整个链表的长度==k), 所以这里最好还是置空
     *
     * @param head inclusive
     * @param end  exclusive
     */
    private ListNode<Integer> _reverseInterval(ListNode<Integer> head, ListNode<Integer> end) {
        ListNode<Integer> p = head, q = p, next = q.getNext();

        while ((q = next) != null && q != end) {
            // 1. save next(round)
            next = q.getNext();

            // 2. reverse
            q.setNext(p);

            // 3. next round
            p = q;
        }

        // 4. 因为首节点(逆向完之后为tail)会形成环, 需要把head.next置空
        head.setNext(null);
        return p;
    }

    /**
     * K>2, K内翻转,
     * 方法4, 【见图示】: 双指针对称翻转,但【性能不太好】：
     * 因为p是反方向移动的, 每次都要重新从head重新定位p, 性能差
     *
     * @param head inclusive
     * @param k    num of nodes to be reversed
     */
    @Deprecated
    private ListNode<Integer> _reverseInterval_DP(ListNode<Integer> head, int k) {
        // 1. middle: q
        ListNode<Integer> p, q, newHead = null, newTail = null;
        q = head;
        int m = (k + 1) / 2;
        // 2. locate q, 只需要定位一次
        for (int i = 0; i < m - 1; i++) {
            q = q.getNext();
        }
        if (k % 2 == 0) q = q.getNext();// left,right取right

        for (int i = m; i > 0; i--) {
            p = head;
            // 3. init p every loop, 每次都需要重新定位p, 【性能不太好】
            for (int j = 0; j < i - 1; j++) {
                p = p.getNext();
            }

            // 4. reverse
            // 4.1 set tail
            if (newTail != null) newTail.setNext(p);
            // 4.2 暂存q.getNext()
            p.setNext(q.getNext());
            // 4.3 翻转, reverse后3种情况
            // (1) p<-q, k为偶数, 首次翻转
            // (2) p->newTail<-...<-newHead->q, 非首次翻转
            // (3) q==p, k为奇数, 首次翻转, 不需要翻转
            if (newHead != null) q.setNext(newHead); // 4.3.(1)非首次翻转
            else if (q != p) q.setNext(p);  // 4.3.(2) k为偶数
            newTail = p;
            newHead = q;

            // 5. relocate q for next round
            q = p.getNext();
        }

        return newHead;
    }

    /**
     * 因为同时涉及到(需翻转的段)和(不需翻转的段), 这里同时需要(1)找到k段(2)段内翻转(3)段间链接3个功能
     *
     * @param left  inclusive
     * @param right inclusive
     */
    private ListNode<Integer> _reverseInterval(ListNode<Integer> head, int left, int right) {
        // boundary
        if (left == right) return head;

        ListNode<Integer> pre, p, q, next;

        // 1. move to left(p)
        pre = null;
        p = head;
        //noinspection StatementWithEmptyBody
        for (int i = 1; i < left && p != null;
             i++, pre = p, p = p.getNext())
            ;

        // no enough node for left, no need reverse
        if (p == null) return head;

        // 2. move right and reverse[left,right]
        // 一边(1)找到k段一边(2)段内翻转
        q = p;
        next = q.getNext();
        for (int i = 1; (q = next) != null && i <= right - left; i++) {
            // 2.1 save next
            next = q.getNext();

            // 2.2 reverse
            q.setNext(p);

            // 2.3 next round
            p = q;
        }

        // 3. 段间链接: 分别链接上[left,right]前面和后面的段
        // [left,right]:
        // head: p
        // tail: head(left为1)/pre.next(left不为1)
        if (pre != null) {
            if (pre.getNext() != null) pre.getNext().setNext(q); // link tail
            pre.setNext(p); // link head
        } else {
            head.setNext(q); // link tail
            head = p; // set head
        }

        return head;
    }
}