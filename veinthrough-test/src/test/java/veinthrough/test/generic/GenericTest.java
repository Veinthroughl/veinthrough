package veinthrough.test.generic;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.generic.Pair;
import veinthrough.api.generic.PairAlg;
import veinthrough.test._class.Employee;
import veinthrough.test._class.Manager;

import java.util.ArrayList;
import java.util.List;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * Tests:
 * 1. <? extends XX>
 * 2. <? super XX>
 * 3. <T extends Comparable<? super T>比<T extends Comparable<T>更好:
 * (1) 条件: Employee implements Comparable<Employee>, Manager extends Employee;
 * (2) Manager 是 Comparable<Employee>的子类，但Manager不是Comparable<Manager>的子类
 * Manager可以赋值给Comparable<? super Manager>；
 * Manger不能赋值给Comparable<Manager>。
 */
@Slf4j
public class GenericTest {
    private static final Manager ceo =
            new Manager("Gus Greedy", 800000D, 80000D);
    private static final Manager cfo =
            new Manager("Sid Sneaky", 600000D, 60000D);
    private static final Employee worker = new Employee("src/main/java/veinthrough", 80000D);
    private static final Manager[] managers = {ceo, cfo};

    /**
     * 泛型会被擦除
     */
    @Test
    public void erasureTest() {
        // 这里Lists.newArrayList将返回List<Employee>，赋值给List<? extends Employee>没问题,
        // 因为泛型会被擦除
        List<? extends Employee> extendsEmployeeList = Lists.newArrayList(worker);
        log.info(methodLog(extendsEmployeeList.toString()));

        // 同样, Pair.of将返回Pair<Employee>, 赋值给Pair<? extends Employee>没问题, 泛型会被擦除
        Pair<? extends Employee> ceo_cfo = Pair.of(ceo, worker);
        log.info(methodLog("" + ceo_cfo));
    }

    /**
     * <? extends XX>:
     * 1. XX = <? extends XX> is OK, <? extends XX> can be used to read
     * 2. <? extends XX> = XX is NOT OK, <? extends XX> can't be used to write
     * (1) {@link Employee} can't be wrote to List<? extends Employee>,
     * (2) {@link Manager} 同样不能写入List<? extends Employee>
     * 即使是{@link Manager}也不能写入: add (capture<? extends Employee>)cannot be applied to Manager),
     * 也就是说没有办法直接创建List<? extends Employee>, 因为不能填入元素,
     * (3) 但是可以将List<Employee>赋值给List<? extends Employee>.
     */
    @Test
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "unused"})
    public void wildcardExtendsTest() {
        List<? extends Employee> extendsEmployeeList;
        // 2. <? extends XX> = XX is NOT OK, <? extends XX> can't be used to write
        // (1) Employee can't be wrote to <? extends Employee>
        // (2) 即使是Manager也不能写入: add (capture<? extends Employee>)cannot be applied to Manager),
        // 也就是说没有办法直接创建List<? extends Employee>, 因为不能填入元素,
//        extendsEmployeeList.add(worker);
//        extendsEmployeeList.add(ceo);

        // 2.(3) 但是可以将List<Employee>赋值给List<? extends Employee>
        // 因为这里泛型会被擦除
        extendsEmployeeList = Lists.newArrayList(worker);

        // 1. XX = <? extends XX> is OK, <? extends XX> can be used to read
        Employee cxo = extendsEmployeeList.get(0);
        log.info(methodLog(cxo.toString()));
    }

    /**
     * <? super XX>:
     * 1. <? super XX> = XX is OK, <? super XX> can be used to write
     * 2. XX = <? super XX> is NOT OK, <? super XX> can't be used to read
     * (1) <? super Manager> can't be wrote to {@link Manager},
     * (2) <? super Manager>同样不能写入{@link Employee}
     * 即使是{@link Employee}同样不能通过<? super Manager>写入,
     * 也就是说没有办法直接将<? super Manager>赋值给一个变量.
     */
    @Test
    public void wildcardSuperTest() {
        List<? super Manager> superManagerList = new ArrayList<>();
        // 1. <? super XX> = XX is OK, <? super XX> can be used to write
        superManagerList.add(ceo);
        // 2. XX = <? super XX> is NOT OK, <? super XX> can't be used to read
        // (1) <? super Manager> can't be wrote to {@link Manager},
        // (2) <? super Manager>同样不能写入{@link Employee}
        // 即使是{@link Employee}同样不能通过<? super Manager>写入,
        // 也就是说没有办法直接将<? super Manager>赋值给一个变量.
//        Employee worker = superManagerList.get(0);
//        Manager ceo = superManagerList.get(0);
        log.info(methodLog("" + superManagerList.get(0)));
    }

    /**
     * 1. <T extends Comparable<? super T>比<T extends Comparable<T>更好:
     * (1) 条件: Employee implements Comparable<Employee>, Manager extends Employee;
     * (2) Manager 是 Comparable<Employee>的子类，但Manager不是Comparable<Manager>的子类
     * Manager可以赋值给Comparable<? super Manager>；
     * Manger不能赋值给Comparable<Manager>。
     * 2. {@link PairAlg#minMax(Comparable[])}: 用的是<T extends Comparable<? super T>>
     * 3. {@link GenericTest#minMax(Comparable[])}: 用的是<T extends Comparable<T>>
     */
    @SuppressWarnings("RedundantTypeArguments")
    @Test
    public void comparableTest() {
        // <T extends Comparable<? super T>>
        log.info(methodLog("" + PairAlg.minMax(managers))); // 这里推测出来的T参数为Manager
        log.info(methodLog("" + PairAlg.<Employee>minMax(managers)));
        log.info(methodLog("" + PairAlg.<Manager>minMax(managers)));

        // <T extends Comparable<T>
        log.info(methodLog("" + GenericTest.minMax(managers))); // 这里推测出来的T参数为Employee
        log.info(methodLog("" + GenericTest.<Employee>minMax(managers)));
        // 编译错误: Manager is not within its bound; should implement Comparable<Manager>
        // 因为Manager implements Comparable<Employee>, 而没有implementsComparable<Manager>
//        log.info(methodLog(""+GenericTest.<Manager>minMax(managers)));
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends Comparable<T>> Pair<T> minMax(T[] a) {
        return PairAlg.minMax(a);
    }
}
