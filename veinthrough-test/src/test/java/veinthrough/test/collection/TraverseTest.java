package veinthrough.test.collection;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * This program demonstrate different types of traversing list and their efficiency
 * NOTE: ArrayList/LinkedList都具有Iterator()/ListIterator(), 不要把LinkedList和ListIterator混淆
 *
 * APIs/Tests:
 * 1. Traverse by iterator: 对于list.iterator()/listIterator()效率一样
 * 2. Traverse by foreach: 使用精简的for(T item: list), 对于ArrayList/LinkedList效率一样
 * 3. Traverse by random access: 当使用ArrayList时效率最高; 当使用LinkedList极其糟糕, LinkedList不适合random access
 * 4. 以下方式只适用于LinkedList: poll/remove/pop
 * 分别当作AbstractSequentialList(顺序列表)/Deque(双向队列)/Stack来使用,
 * 列表为空返回null/NoSuchElementException/NoSuchElementException
 * (1) [LinkedList] Traverse by remove: 当作Deque(双向队列)来使用
 * 访问元素并移除: remove/removeFirst/removeLast, remove/removeFirst等价
 * 添加元素: add/addFirst/addLast, add/addLast等价
 * (2) [LinkedList] Traverse by poll: 当作Deque(双向队列)来使用
 * 访问元素并移除: poll/pollFirst/pollLast, poll/pollFirst等价
 * 添加元素: offer/offerFirst/offerLast, offer/offerLast等价
 * 访问元素: element/peek/peekFirst/peekLast, peek/peekFirst等价, element/peek不移除元素
 * (3) [LinkedList][Stack] Traverse by pop: 当作Stack来使用，
 * 这里的pop/push并不是真正意义上的stack, 实际上还是FIFO而不是LIFO, 因为等价于first接口, 使用last接口才能达到stack的效果
 * 访问元素并移除: pop/removeFirst等价
 * 添加元素: push/addFirst等价
 * 【注意】pop/push能够实现LIFO根本在于push为first函数, 而offer/add等为last函数, 而pop同poll/remove都为first函数
 * 5. 以下方式只适用于Vector
 * (1) [Vector]Traverse by Enumeration of Vector
 * Vector比List效率低, 因为需要同步, 每个函数都有synchronized
 * 6. Stack
 * (1) LinkedList/Stack/Vector:
 * 也可以将LinkedList当作栈来使用, LinkedList包含pop/push接口
 * (2) Stack<E> extends Vector<E>, 所以Stack也可以使用Enumeration
 * (3) 【注意】不能使用: while((element=stack.pop()) != null){}, 将导致ArrayIndexOutOfBoundsException
 */
@Slf4j
@SuppressWarnings({"unused", "ResultOfMethodCallIgnored", "ForLoopReplaceableByForEach", "StatementWithEmptyBody"})
public class TraverseTest {
    private static final int TEST_COUNT = 30;
    @Test
    public void arrayListTest() {
        // 使用ArrayList
        final List<Integer> list =
                IntStream.range(0, TEST_COUNT)
                        .boxed()
                        .collect(Collectors.toList());
        // Traverse by iterator: 15ms
        // Traverse by foreach: 21ms
        // Traverse by random access: 13, 效率最高
        ImmutableMap.<String, Function<List<Integer>, Long>>of(
                "Traverse by iterator", TraverseTest::traverseByIterator,
                "Traverse by foreach", TraverseTest::traverseByForeach,
                "Traverse by random access", TraverseTest::traverseByRandomAccess)
                .forEach((taskName, task) -> {
                    try {
                        log.info(methodLog(taskName, "" + task.apply(new ArrayList<>(list))));
                    } catch (Exception e) {
                        log.error(exceptionLog(e, taskName));
                    }
                });
    }

    @Test
    public void linkedListTest() {
        // 使用LinkedList
        final LinkedList<Integer> list =
                IntStream.range(0, TEST_COUNT)
                        .boxed()
                        .collect(Collectors.toCollection(LinkedList::new));

        // Traverse by iterator: 12ms
        // Traverse by foreach: 10ms
        // Traverse by random access: 6183, 极其糟糕
        // Traverse by poll: 5ms
        // Traverse by remove: 0ms
        // Traverse by pop: 0ms
        ImmutableMap.<String, Function<LinkedList<Integer>, Long>>builder()
                .put("Traverse by iterator", TraverseTest::traverseByIterator)
                .put("Traverse by foreach", TraverseTest::traverseByForeach)
                .put("Traverse by random access", TraverseTest::traverseByRandomAccess)
                .put("Traverse by poll", TraverseTest::traverseByPoll)
                .put("Traverse by remove", TraverseTest::traverseByRemove)
                .put("Traverse by pop", TraverseTest::traverseByPop)
                .build()
                .forEach((taskName, task) -> {
                    try {
                        log.info(methodLog(taskName, "" + task.apply(new LinkedList<>(list))));
                    } catch (Exception e) {
                        log.error(exceptionLog(e, taskName));
                    }
                });
    }

    @Test
    public void vectorTest() {
        // Vector extends AbstractList<E> implements List<E>
        Vector<Integer> vector = IntStream.range(0, TEST_COUNT)
                .boxed()
                .collect(Collectors.toCollection(Vector::new));
        // 可以看出来Vector比List效率低, 因为需要同步, 每个函数都有synchronized
        // Traverse by iterator: 40ms
        // Traverse by foreach: 39ms
        // [?] Traverse by random access: 62ms, 效率最低
        // Traverse by Enumeration: 44ms, 和iterator效率相当
        ImmutableMap.<String, Function<Vector<Integer>, Long>>of(
                "Traverse by iterator", TraverseTest::traverseByIterator,
                "Traverse by foreach", TraverseTest::traverseByForeach,
                "Traverse by random access", TraverseTest::traverseByRandomAccess,
                "Traverse by Enumeration", TraverseTest::traverseByEnumeration)
                .forEach((taskName, task) -> {
                    try {
                        log.info(methodLog(taskName, "" + task.apply(new Vector<>(vector))));
                    } catch (Exception e) {
                        log.error(exceptionLog(e, taskName));
                    }
                });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void stackTest() {
        // Stack<E> extends Vector<E>
        Stack<Integer> stack = new Stack<>();
        IntStream.range(0, TEST_COUNT)
                .boxed()
                .forEach(stack::push);
        // 可以看出来Vector比List效率低, 因为需要同步, 每个函数都有synchronized
        // Traverse by iterator: 40ms
        // Traverse by foreach: 39ms
        // [?] Traverse by random access: 62ms, 效率最低
        // Traverse by pop: 19ms, 效率最高
        // Traverse by Enumeration: 44, 和iterator效率相当
        ImmutableMap.<String, Function<Stack<Integer>, Long>>of(
                "Traverse by iterator", TraverseTest::traverseByIterator,
                "Traverse by foreach", TraverseTest::traverseByForeach,
                "Traverse by random access", TraverseTest::traverseByRandomAccess,
                "Traverse by pop", TraverseTest::traverseByPop2,
                "Traverse by Enumeration", TraverseTest::traverseByEnumeration)
                .forEach((key, value) -> {
                    try {
                        log.info(methodLog(key, "" + value.apply((Stack<Integer>) stack.clone())));
                    } catch (Exception e) {
                        log.error(exceptionLog(e, key));
                    }
                });
    }

    /**
     * 1. Traverse by iterator: 对于list.iterator()/listIterator()效率一样
     */
    static <T> long traverseByIterator(List<T> list) {
        long startTime = System.currentTimeMillis();
        // 对于list.iterator()/listIterator()效率一样
        for (Iterator<T> iterator = list.iterator(); iterator.hasNext(); ) {
//        for (Iterator<Integer> iterator = list.listIterator(); iterator.hasNext(); ) {
            iterator.next();
        }
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 5.(1) [Vector] Traverse by Enumeration of Vector
     */
    private static <T> long traverseByEnumeration(Vector<T> vector) {
        long startTime = System.currentTimeMillis();
        for (Enumeration enu = vector.elements(); enu.hasMoreElements(); ) {
            enu.nextElement();
        }
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 2. Traverse by foreach: 使用精简的for(T item: list), 对于ArrayList/LinkedList效率一样
     */
    private static <T> long traverseByForeach(List<T> list) {
        long startTime = System.currentTimeMillis();
        for (T item : list) {
        }
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 3. Traverse by random access: 当使用ArrayList时效率最高; 当使用LinkedList极其糟糕, LinkedList不适合random access
     */
    static <T> long traverseByRandomAccess(List<T> list) {
        long startTime = System.currentTimeMillis();
        // 对于ArrayList效率最高, 对于LinkedList效率极其糟糕
        for (int i = 0; i < list.size(); i++) {
            list.get(i);
        }
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 4.(1) [LinkedList] Traverse by remove: 当作AbstractSequentialList(双向列表)来使用
     */
    static <T> long traverseByRemove(LinkedList<T> list) {
        T element;
        List<T> traversing = new LinkedList<>();

        long startTime = System.currentTimeMillis();
        // remove/removeFirst/removeLast, remove/removeFirst等价
        // add/addFirst/addLast, add/addLast等价
//        while (list.removeLast() != null)
        try {
            while ((element = list.remove()) != null) // 最后会有一个NoSuchElementException
                traversing.add(element);
        } catch (NoSuchElementException e) {
            log.error(exceptionLog(e));
        }

        log.info(methodLog("traversed", traversing.toString()));
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 4.(2) [LinkedList] Traverse by poll: 当作Deque(队列)来使用
     */
    static <T> long traverseByPoll(LinkedList<T> list) {
        T element;
        List<T> traversing = new LinkedList<>();

        long startTime = System.currentTimeMillis();
        // poll/pollFirst/pollLast, poll/pollFirst等价
        // offer/offerFirst/offerLast, offer/offerLast等价
        // element/peek/peekFirst/peekLast, peek/peekFirst等价, element/peek不移除元素
//        while (list.pollLast() != null)
        while ((element = list.poll()) != null) // 最后会有一个NoSuchElementException
            traversing.add(element);

        log.info(methodLog("traversed", traversing.toString()));
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 4.(3) [LinkedList] Traverse by pop: 当作Stack来使用;
     * 【注意】pop/push能够实现LIFO根本在于push为first函数, 而offer/add等为last函数, 而pop同poll/remove都为first函数;
     * 所以这里的结果和traverseByPoll/traverseByRemove结果都一样
     */
    static <T> long traverseByPop(LinkedList<T> list) {
        T element;
        List<T> traversing = new LinkedList<>();

        long startTime = System.currentTimeMillis();
        // pop/removeFirst等价
        // push/addFirst等价
        try {
            while ((element = list.pop()) != null)
                traversing.add(element);
        } catch (NoSuchElementException e) {
            log.error(exceptionLog(e));
        }

        log.info(methodLog("traversed", traversing.toString()));
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 4.(3) [LinkedList] Traverse by pop: 当作Stack来使用
     * 【注意】不能使用: while((element=stack.pop()) != null){}, 将导致ArrayIndexOutOfBoundsException
     */
    private static <T> long traverseByPop2(Stack<T> stack) {
        Queue<T> traversing = new LinkedList<>();

        long startTime = System.currentTimeMillis();
        try {
            // 注意不能使用: while((element=stack.pop()) != null){}
            // 会导致ArrayIndexOutOfBoundsException
            while (!stack.empty())
                traversing.offer(stack.pop());
        } catch (NoSuchElementException e) {
            log.error(exceptionLog(e));
        }

        log.info(methodLog("traversed", traversing.toString()));
        return System.currentTimeMillis() - startTime;
    }
}
