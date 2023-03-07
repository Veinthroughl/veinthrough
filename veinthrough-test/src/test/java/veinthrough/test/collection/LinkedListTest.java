package veinthrough.test.collection;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * APIs:
 * 1. Iterator/ListIterator:
 * (1) hasNext()/next()/remove()
 * (2) set()/add()/hasPrevious()/previous()/nextIndex()/previousIndex()
 * NOTE: ArrayList/LinkedList都具有Iterator()/ListIterator(), 不要把LinkedList和ListIterator混淆
 * (3) listIterator方法【可以】有参数: {@link List#listIterator(int)},
 * {@link TraverseTest#traverseByIterator(List)}表示从某个index开始
 * 2. add()/set()/remove()
 * (1) add(): 只依赖于迭代器的【位置】,
 * Inserts the specified element into the list (optional operation)
 * (2) set()/remove(): 依赖于迭代器的【位置】和【状态】(previous/next)
 * Replaces the last element returned by {@link ListIterator#next} or {@link ListIterator#previous}
 * with the specified element (optional operation)
 * 3. LinkedList/ArrayList效率问题, LinkedList擅长于修改, ArrayList擅长于random access, 效率最高
 * {@link #mergeTest()}, 使用LinkedList做修改
 * {@link TraverseTest#traverseByRandomAccess(List)}, 使用ArrayList做random access
 * 4. LinkedList当作AbstractSequentialList(listIterator含有双向遍历功能所以一般实现为双向列表)/Deque(队列)/Stack来使用,
 * 【注意】pop/push能够实现LIFO根本在于push为first函数, 而offer/add等为last函数, 而pop同poll/remove都为first函数;
 * {@link TraverseTest#traverseByRemove(LinkedList)}
 * {@link TraverseTest#traverseByPoll(LinkedList)}
 * {@link TraverseTest#traverseByPop(LinkedList)}
 * 5. Merge methodReferenceTest: 将2个列表间断合并, 最好使用LinkedList;
 * LinkedList擅长于修改, ArrayList擅长于random access, 效率最高
 */
@SuppressWarnings("unused")
@Slf4j
public class LinkedListTest {
    /**
     * 将2个列表间断合并, 最好使用LinkedList
     * LinkedList擅长于修改, ArrayList擅长于random access, 效率最高
     */
    @Test
    public void mergeTest() {
        List<String> list1 =
                Lists.newLinkedList(
                        Lists.newArrayList("Amy", "Carl", "Erica"));
        List<String> list2 =
                Lists.newLinkedList(
                        Lists.newArrayList("Bob", "Doug", "Frances", "Gloria"));
        // 使用iterator就不会出现ConcurrentModificationException
        ListIterator<String> iter1 = list1.listIterator();
        for (String s : list2) {
            if (iter1.hasNext()) iter1.next();
            // listIterator才有add()
            iter1.add(s);
        }
        log.info(methodLog(list1.toString()));
    }
}
