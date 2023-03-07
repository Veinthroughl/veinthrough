package veinthrough.test.collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * This program demonstrate fail-fast/ConcurrentModificationException in Java.
 * 1. 当某一个线程A通过iterator去遍历某集合的过程中，若该集合的内容被(自己/其他线程)改变了;
 * 那么线程A访问集合时，就会抛出ConcurrentModificationException异常，产生fail-fast事件。
 * 2. 具体实现: 调用iterator时会初始化expectedModCount = modCount
 * list的修改操作【只会更改modeCount不会更改expectedModCount】
 * iterator的修改操作会调用list的修改操作, 比如iterator.remove()会调用list.remove()
 * 但是iterator的修改操作会【同时修改expectedModCount和modeCount】
 * 3. CopyOnWriteArrayList
 * (1) copy-on-write: CopyOnWriteArrayList不会抛ConcurrentModificationException，是因为所有改变其内容的操作(add/remove/clear等),
 * 都会copy一份现有数据称为snapshot，在现有数据上修改好，在把原有数据的引用改成指向修改后的数据, 而不是在读的时候copy.
 * 这个快照并不会和外界有任何联系
 * (2) 某个线程在获取迭代器的时候就【不会】拷贝一份(只是使用当前最新的数据(实际上也可以称之为snapshot), 因为修改操作在修改完成前都是在copy上修改的)
 * (3) 内部使用了ReentrantLock, 是为了防止并发修改
 * CopyOnWriteArrayList为什么需要加锁呢？如何理解防止并发修改?
 * 让修改操作一个一个来，第二个修改操作能够看到第一个修改操作的结果，
 * 要不然比如有10个add操作，没有lock的话大家看到的都只是开始状态(空状态)，最终状态为只有1个元素而不是10个元素。
 *
 * Tests(traverse and modify a list):
 * 1. by foreach: 会出现ConcurrentModificationException, foreach实际上也是调用iterator,
 * list的修改操作只会更改modeCount不会更改expectedModCount
 * 2. by index: list中的元素个数会越来越少, 需要不断获取list.size()
 * NOTE: 如果是LinkedList而不是ArrayList, 通过index进行random access效率会很低
 * (1) 使用固定的size会导致IndexOutOfBoundsException
 * (2) 使用动态的list.size得到的结果不正确[Amy, Carl, Doug, Frances, Gloria], 因为奇数/偶数是不断变化的
 * 3. by Iterator: 不会出现ConcurrentModificationException
 * 使用list.iterator()/listIterator()效果一样， 都不会引起ConcurrentModificationException
 * iterator的修改操作会同时修改expectedModCount和modeCount
 * 4. by Stream: 出现ConcurrentModificationException, stream本质上也是调用foreach/iterator
 * 5. multi-thread:
 * (1) 使用ArrayList, 2个任务交叉执行会出现ConcurrentModificationException
 * (2) 使用CopyOnWriteArrayList, 2个任务交叉执行也不会出现ConcurrentModificationException
 * NOTE: CopyOnWriteArrayList.COWIterator中add/set/remove都未实现, 抛出UnsupportedOperationException
 * 6. merge methodReferenceTest: 将2个列表间断合并, 最好使用LinkedList, {@link LinkedListTest#mergeTest()},
 * LinkedList擅长于修改, ArrayList擅长于random access, 效率最高
 */
@SuppressWarnings("SuspiciousListRemoveInLoop")
@Slf4j
public class FailFastTest {
    /**
     * 1. iterateAndRemoveByForeachTest: 会出现ConcurrentModificationException, foreach实际上也是调用iterator,
     * list(AbstractList)的修改操作只会更改modeCount不会更改expectedModCount
     */
    @Test
    public void iterateAndRemoveByForeachTest() {
        List<String> list = Lists.newArrayList(
                "Amy", "Bob", "Carl", "Doug", "Erica", "Frances", "Gloria");
        // delete even-index item
        boolean shouldDelete = false;
        for (String str : list) {
            if (shouldDelete) {
                list.remove(str);
            }
            shouldDelete = !shouldDelete;
        }
        log.info(methodLog(list.toString()));
    }

    /**
     * 2. by index:
     * (1) list中的元素个数会越来越少, 需要不断获取list.size()
     * (2) 最终结果[Amy, Carl, Doug, Frances, Gloria], i=3的时候，删除的Erica(原来的4)而不是Doug
     * (3) 如果是LinkedList而不是ArrayList, 通过index进行random access效率会很低
     * (4) 使用固定的size会导致IndexOutOfBoundsException
     */
    @Test
    public void iterateAndRemoveByIndexTest1() {
        List<String> list = Lists.newArrayList(
                "Amy", "Bob", "Carl", "Doug", "Erica", "Frances", "Gloria");
        // delete even-index item
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (i % 2 != 0) {
                list.remove(i);
            }
        }
        log.info(methodLog(list.toString()));
    }

    /**
     * 2. by index: list中的元素个数会越来越少, 需要不断获取list.size()
     * (1) 如果是LinkedList而不是ArrayList, 通过index进行random access效率会很低
     * (2) 使用动态的list.size得到的结果不正确[Amy, Carl, Doug, Frances, Gloria], 因为奇数/偶数是不断变化的
     */
    @Test
    public void iterateAndRemoveByIndexTest2() {
        List<String> list = Lists.newArrayList(
                "Amy", "Bob", "Carl", "Doug", "Erica", "Frances", "Gloria");
        // delete even-index item
        for (int i = 0; i < list.size(); i++) {
            if (i % 2 != 0) {
                list.remove(i);
            }
        }
        log.info(methodLog(list.toString()));
    }

    /**
     * by Iterator: 不会出现ConcurrentModificationException
     * 使用list.iterator()/listIterator()效果一样， 都不会引起ConcurrentModificationException
     * iterator的修改操作会同时修改expectedModCount和modeCount
     */
    @Test
    public void iterateAndRemoveByIteratorTest() {
        List<String> list = Lists.newArrayList(
                "Amy", "Bob", "Carl", "Doug", "Erica", "Frances", "Gloria");
        Iterator iterator = list.iterator();
//        Iterator iterator = list.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
        log.info(methodLog(list.toString()));
    }

    /**
     * by stream:
     * (1) 出现ConcurrentModificationException, stream本质上也是调用foreach/iterator
     * (2) 但其实可用使用removeIf
     */
    @Test
    public void iterateAndRemoveByStreamTest() {
        List<Integer> list = IntStream.range(0, 1000)
                .boxed()
                .collect(Collectors.toList());
        // (1) 出现ConcurrentModificationException
//        list.forEach(number -> {
//            if (number % 2 != 0) list.remove(number);
//        });
        // (2) 使用removeIf
        list.removeIf(number -> number % 2 != 0);
        log.info(methodLog(list.toString()));
    }

    /**
     * multi-thread:
     * (1) 使用ArrayList, 2个任务交叉执行会出现ConcurrentModificationException
     */
    @Test
    public void mulThreadsTraverseAndExtendTest() throws InterruptedException {
        List<String> list = Lists.newArrayList(
                "Amy ", "Bob ", "Carl ", "Doug ", "Erica ", "Frances ", "Gloria ");
        ExecutorService pool = Executors.newCachedThreadPool();
        // 2个任务交叉执行会出现ConcurrentModificationException
        pool.invokeAll(ImmutableList.of(
                // 注意lambda会抑制ConcurrentModificationException
//                Executors.callable(() -> list.forEach(System.out::println)),
                Executors.callable(() -> printList(list)),
                Executors.callable(() -> extendList(list))));
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
        log.info(methodLog(list.toString()));
    }

    /**
     * multi-thread:
     * (2) 使用CopyOnWriteArrayList, 2个任务交叉执行也不会出现ConcurrentModificationException
     * 但是:
     * (1) extendList线程每次的修改都是建立一个副本再修改，最终才会将通过setArray(array)来设置新数组
     * 【printList中线程在创建Iterator时不会创建副本，非修改操作不会创建副本】
     * 所以printList能不能打印出extendList中添加的数据，完全依赖于创建Iterator时extendList线程是否已经更新完成；
     * 只要创建Iterator时，extendList没有完成修改/设置新数组，那么printList都将不能打印出extendList中添加的数据
     * (2) 使用CopyOnWriteArrayList的修改操作中是独占锁, 效率比较低
     */
    @Test
    public void mulThreadsTraverseAndExtendTest2() throws InterruptedException {
        // NOTE: CopyOnWriteArrayList.COWIterator中add/set/remove都未实现, 抛出UnsupportedOperationException
        List<String> list = Lists.newCopyOnWriteArrayList(Lists.newArrayList(
                "Amy ", "Bob ", "Carl ", "Doug ", "Erica ", "Frances ", "Gloria "));
        ExecutorService pool = Executors.newCachedThreadPool();
        // 2个任务交叉执行也不会出现ConcurrentModificationException
        pool.invokeAll(ImmutableList.of(
                // lambda会抑制ConcurrentModificationException
//                Executors.callable(() -> list.forEach(System.out::println)),
                Executors.callable(() -> printList(list)),
                Executors.callable(() -> extendList(list))));
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
        log.info(methodLog(list.toString()));
    }

    private static <T> void printList(List<T> list) {
        try {
            for (T str : list) {
                log.info(methodLog("" + str));
            }
            // 必须使用try...catch来捕获ConcurrentModificationException
            // 否则会被lambda抑制
        } catch (Exception e) {
            log.error(exceptionLog(e, "Exit..."));
            // System.exit()会终止整个进程, ConcurrentModificationException(UncheckedException)只会终止当前线程
            System.exit(1);
        }
    }

    private static void extendList(List<String> list) {
        Lists.newArrayList(
                "Hale", "Ian", "Jack", "Kalen", "Lakin")
                .forEach(item -> {
                    list.add(item);
                    log.info(methodLog(list.toString()));
                });
    }
}
