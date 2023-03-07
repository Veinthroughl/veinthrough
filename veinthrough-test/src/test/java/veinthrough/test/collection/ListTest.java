package veinthrough.test.collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api._interface.UnCheckedFunction;
import veinthrough.test._class.Employee;
import veinthrough.test._class.Manager;
import veinthrough.test._enum.SIZE;
import veinthrough.test.array.ArrayTest;

import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.test._enum.SIZE.*;

@Slf4j
public class ListTest {
    private static List<Integer> listWith2Elements = new ArrayList<>(10);

    static {
        listWith2Elements.add(0);
        listWith2Elements.add(1);
    }
    private static final SIZE[] sizesArray = new SIZE[]{
            TOO_SMALL, SMALL,
            MEDIUM, LARGE,
            EXTRA_LARGE, TOO_LARGE};

    /**
     * 新建list:
     * 1. {@link Arrays#asList(Object[])}: only return a view of list from array,
     * 涉及到对数组长度的修改(add/remove) will cause UnsupportedOperationException.
     */
    @Test
    public void createTest() {
        log.info(methodLog(
                // only return a view of list from array
                "array view", "" + Arrays.asList(sizesArray),
                "new from array", "" + Lists.newArrayList(sizesArray),
                "copy of array", "" + ImmutableList.copyOf(sizesArray),
                "of elements", "" + ImmutableList.of(
                        TOO_SMALL, SMALL,
                        MEDIUM, LARGE,
                        EXTRA_LARGE, TOO_LARGE)));
    }

    @Test
    public void shuffleAndSortTest() {
        List<Integer> numbers = IntStream.range(1, 50)
                .boxed()
                .collect(Collectors.toList());
        // shuffle
        Collections.shuffle(numbers);
        List<Integer> winningCombination = numbers.subList(0, 6);
        // sort
        Collections.sort(winningCombination);
        log.info(methodLog("" + winningCombination));
    }

    /**
     * 1. 不可修改视图: {@link Collections#unmodifiableList(List)},
     * 但是Can use {@link ImmutableList} from Guava, which is not a view.
     * 2. 线程安全视图:
     * List	    ArrayList                   CopyOnWriteArrayList
     * Map	    HashMap                     ConcurrentHashMap
     * Set	    HashSet/TreeSet             CopyOnWriteArraySet
     * Queue	ArrayDeque/LinkedList       ArrayBlockingQueue/LinkedBlockingQueue/ConcurrentLinkedQueue
     * Deque	ArrayDeque/LinkedList       LinkedBlockingDeque
     * 3. 其他视图.
     */
    @Test
    @SuppressWarnings("unused")
    public void viewTest() {
        Employee[] employees = {new Employee("Alice Adams", 80000D),
                new Employee("Bob Brandson", 75000D),
                new Employee("Carl Cracker", 50000D),
                new Manager("Carl Cracker", 80000D, 80000D)};
        // list view of array
        // 改变数组大小的所有方法都会导致UnsupportedOperationException
        List<Employee> employeesList = Arrays.asList(employees);
        // nCopies view
        List<String> settings = Collections.nCopies(100, "DEFAULT");
        // singleton view
        Set<Employee> worker = Collections.singleton(new Employee("Alice Adams", 80000D));
        // sub range view
        List<Employee> workers = employeesList.subList(0, 3);
        // unmodifiable view
        List<Employee> unmodifiableWorkersList = Collections.unmodifiableList(workers);
        // synchronized view, 不如直接用线程安全集合
        List<Employee> synchronizedWorkersList = Collections.synchronizedList(workers);
    }

    /**
     * 1. {@link ArrayList<Integer>#remove(Integer)}/{@link ArrayList<Integer>#remove(index)}混淆的情况:
     * (1) List.remove(Object o)中使用了equals来判断o.equals(elementData[index])，而不是==
     * (2) HashMap不存在index/object(key)混淆的情况, 因为Hash中没有index的概念
     * 2. {@link List#removeIf(Predicate)}/{@link List#remove(Object)}:
     * (1) {@link List#remove(Object)}将会删除[第一个]equals的元素
     * (2) {@link List#removeIf(Predicate)}将会删除[所有]满足条件的元素
     */
    @Test
    public void arrayListRemoveTest() {
        List<Integer> list = IntStream.range(0, 9).boxed().collect(Collectors.toList());
        // ArrayList<Integer>#remove(index)
        list.remove(2);
        // ArrayList<Integer>#remove(Integer)
        list.remove(new Integer(3));
        // removeIf删除所有满足条件的元素
        list.removeIf(num -> num >= 5);
        log.info(methodLog("" + list));
    }

    /**
     * 1. {@link List#toArray()}, 将返回Object[]并且不能强制转换:
     * java.lang.ClassCastException: [Ljava.lang.Object; cannot be cast to [Ljava.lang.String
     * 2. {@link List#toArray(Object[])},
     * 如果参数的空间足够将直接调用{@link System#arraycopy(Object, int, Object, int, int)}来实现
     * 3. {@link List#toArray(Object[])}, 如果参数的空间不够将使用反射新建数组。
     * 一般的使用方式是: array = collection.toArray(array[0])
     * 4. {@link java.util.stream.Stream#toArray(IntFunction)},
     * 这种方式不推荐使用, 实际上绕了一圈但仍然和方式3的实现一样.
     * 5. 对于像List<Integer>可能需要转化成原始类型int[]: {@link ArrayTest#boxAndListTest()}
     */
    @Test
    public void toArrayTest() {
        List<String> list =
                Lists.newArrayList("aardvark", "elephant", "koala", "eagle", "kangaroo");

        // 1. java.lang.ClassCastException:
        // [Ljava.lang.Object; cannot be cast to [Ljava.lang.String
//        String[] array1 = (String[]) list.toArray();
        // 2. 实现: 空间足够直接使用System.arrayCopy
        String[] array2 = new String[list.size()];
        list.toArray(array2);
        // 3. 实现: 空间不够使用反射新建数组
        String[] array3 = list.toArray(new String[0]);
        // 4. 不推荐使用
        @SuppressWarnings("SimplifyStreamApiCallChains")
        String[] array4 = list.stream().toArray(String[]::new);
        log.info(methodLog("array2", Arrays.toString(array2),
                "array3", Arrays.toString(array3),
                "array4", Arrays.toString(array4)));
    }

    /**
     * {@link CollectionToMapTest}
     */
    @Test
    public void toMapTest() {}

    /**
     * {@link ArrayList}的index操作, 所有的index操作限定在已经添加过的数据范围内，也就是在size范围内(index<=size),
     * 否则会出现java.lang.IndexOutOfBoundsException;
     * {@link java.util.LinkedList}一样。
     * -------------------------
     * list中有2个元素, size为2
     *
     * index        add         set
     * 1            success     success
     * 2            success     exception
     * 3            exception   exception
     */
    @Test
    public void indexAPITest() {
        Stream.of(1, 2, 3)
                .forEach(index ->
                        Stream.<UnCheckedFunction<Integer, Integer>>of(this::indexAddTest, this::indexSetTest)
                                .map(UnCheckedFunction::lift)
                                .forEach(function ->
                                        log.info(methodLog(
                                                "Function", function.toString(),
                                                "index", index + "",
                                                "result", function.apply(index).toString()
                                        ))));
    }

    private Integer indexAddTest(Integer index) {
        new ArrayList<>(listWith2Elements).add(index, index);
        return index;
    }

    private Integer indexSetTest(Integer index) {
        return new ArrayList<>(listWith2Elements).set(index, index);
    }
}
