package veinthrough.test.array;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.generic.Pair;
import veinthrough.test._class.Employee;
import veinthrough.test._class.Manager;
import veinthrough.test.collection.ListTest;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * 1. 使用Arrays.toString(str)打印数组
 * 2. 数组/多态: 数组的协变性, Employee[] staffs = managers; 所有数组都会记住创建它们的元素类型，
 * 如果给数组的某个index填入不对应的类型(比如将Employee填入Manager[]), 将产生{@link ArrayStoreException};
 * 这是为了防止访问不存在的域。
 * 3. 数组的强制转换
 * 4. 数组/泛型: 数组都会牢记创建它们的元素类型, 但是类型擦除会使泛型失效, 所以不能实例化参数化类型的数组;
 * 所以对于泛型最好用collection如ArrayList
 */
@Slf4j
public class ArrayTest {
    /**
     * 数组/多态: 数组的协变性, Employee[] staffs = managers; 所有数组都会记住创建它们的元素类型，
     * 如果给数组的某个index填入不对应的类型(比如将Employee填入Manager[]), 将产生{@link ArrayStoreException};
     * 这是为了防止访问不存在的域。
     */
    @Test
    @SuppressWarnings("UnnecessaryLocalVariable")
    public void covarianceTest() {
        Manager ceo =
                new Manager("Gus Greedy", 800000D, 80000D);
        Manager cfo =
                new Manager("Sid Sneaky", 600000D, 60000D);
        Employee worker = new Employee("veinthrough", 80000D);
        Manager[] managers = {ceo, cfo};
        // 数组的协变性, 所以数组都要牢记创建它们的元素类型(此处实际上指向managers, 所以为Manager[])
        Employee[] staffs = managers;
        log.info(methodLog("managers", Arrays.toString(managers),
                "staffs", Arrays.toString(staffs)));
        // 编译没问题，但是运行时java.lang.ArrayStoreException
        // 因为数组记住了创建它的元素类型为Manager[]，所以抛出异常
        // 否则如果调用managers[0].getBonus(), 将会调用不存在的域
        staffs[0] = worker;
        log.info(methodLog("managers", Arrays.toString(managers),
                "staffs", Arrays.toString(staffs)));
    }

    /**
     * 数组的强制转换
     */
    @Test
    public void castTest() {
        List<String> list =
                Lists.newArrayList("aardvark", "elephant", "koala", "eagle", "kangaroo");
        // java.lang.ClassCastException: [Ljava.lang.Object; cannot be cast to [Ljava.lang.String
        // 因为list.toArray()是使用反射生成的Object[]， Object[] --> String[]会发生错误
        String[] array = (String[]) list.toArray();
        log.info(methodLog(Arrays.toString(array)));
    }

    /**
     * 数组/泛型: 数组都会牢记创建它们的元素类型, 但是类型擦除会使泛型失效, 所以不能实例化参数化类型的数组;
     * 所以对于泛型最好用collection如ArrayList
     */
    @Test
    @SuppressWarnings({"UnnecessaryLocalVariable", "ConstantConditions"})
    public void arrayGenericTest() {
        // 1. 不能实例化参数化泛型的数组
//        Pair<String>[] table = new Pair<String>[10];
        // noinspection unchecked
        Pair<String>[] table = (Pair<String>[]) new Pair<?>[10]; // 通过强制转换才实例化了参数化泛型的数组
        Object[] objArray = table;
        // 编译没问题，数组都会牢记创建它们的元素类型(Pair<String>[]), 所以运行时java.lang.ArrayStoreException
        objArray[0] = "hello";
        // 2. 数组都会牢记创建它们的元素类型, 但是类型擦除会使泛型失效,
        // 所以不会有java.lang.ArrayStoreException,
        // 这就导致了不一致: Pair<String> -> Pair<Employee>
        // 所以不能实例化参数化类型的数组, 上面是通过强制转换才实例化了参数化泛型的数组
        objArray[1] = new Pair<Employee>();
    }

    /**
     * {@link ListTest#toArrayTest()}
     */
    @Test
    public void listToArrayTest(){}

    @SuppressWarnings({"UnusedAssignment", "unused"})
    @Test
    public void boxAndListTest() {
        int[] nums = {0, 1, 4, 3, 2}, nums2;
        Integer[] boxedNums = {5, 6, 7, 8, 9}, boxedNums2;
        List<Integer> list = IntStream.range(0, 10)
                .boxed()
                .collect(Collectors.toList()), list2;

        // int[] -> Integer[]
        // 不能使用Stream.of(T...)/Arrays.asList(T...), 都要求是Object[]
        // 会把int[]整个当作Object处理
        boxedNums2 = Arrays.stream(nums)
                .boxed()
                .toArray(Integer[]::new);

        // int[] -> String[]
        String[] numsStr = Arrays.stream(nums)
                .mapToObj(num -> "" + num)
                .sorted()
                .toArray(String[]::new);

        // Integer[] -> int[]
        nums2 = Arrays.stream(boxedNums)
                .mapToInt(Integer::intValue)
                .toArray();

        // int[] -> list<Integer>
        // 不能使用Stream.of(T...)/Arrays.asList(T...), 都要求是Object[]
        list2 = Arrays.stream(nums)
                .boxed()
                .collect(Collectors.toList());

        // Integer[] -> List<Integer>
        list2 = Arrays.stream(boxedNums)
                .collect(Collectors.toList());

        // List<Integer> -> int[]
        nums2 = list.stream()
                .mapToInt(Integer::intValue)
                .toArray();

        // List<Integer> -> Integer[]
        boxedNums2 = list.toArray(new Integer[0]);
    }

    /**
     * 1. {@link Arrays#sort(int[])}: 可以实现对java中的基本数据类型(byte/char/short/int/long/float/double/boolean)
     * 2. {@link Arrays#sort(Object[], Comparator)},
     * 但是要实现逆序排序(使用Comparator)就必须用包装类型
     */
    @Test
    public void arraySortTest() {
        int[] nums = {0, 1, 4, 3, 2};
        Arrays.sort(nums);
        nums = Arrays.stream(nums)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .mapToInt(Integer::intValue)
                .toArray();
        log.info(methodLog(Arrays.toString(nums)));
    }
}
