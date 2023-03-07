package veinthrough.test.collection;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.test._class.Employee;
import veinthrough.test.array.ArrayTest;
import veinthrough.test.guava.OrderingTest;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

/**
 * {@link com.google.common.collect.Ordering}: Ordering<T> implements Comparator<T>,
 * {@link OrderingTest#orderingTest()}
 */
@Slf4j
public class ComparatorTest {
    /**
     * 1. {@link Comparator#comparing(Function)}/{@link Comparator#comparingDouble(ToDoubleFunction)}
     * 2. {@link Comparator#reversed()}/{@link Comparator#reverseOrder()}
     * (1) 为static函数, 正常比较({@link Comparable#compareTo(Object)})基础上的反向
     * (2) 为非static函数，可以在任何Comparator基础上的反向
     */
    @SuppressWarnings("unused")
    @Test
    public void comparatorTest() {
        // comparing: Employee::getSalary
        PriorityQueue<Employee> queue1 = new PriorityQueue<>(
                Comparator.comparing(Employee::getSalary));
        // comparing double: Employee::getSalary
        PriorityQueue<Employee> queue2 = new PriorityQueue<>(
                Comparator.comparingDouble(Employee::getSalary));
        // reversed: 在比较Employee::getSalary基础上的反向
        PriorityQueue<Employee> queue3 = new PriorityQueue<>(
                Comparator.comparingDouble(Employee::getSalary).reversed()); // 反向比较Employee::getSalary
        // reverse order: 正常比较(Employee::compareTo)基础上的反向
        PriorityQueue<Employee> queue4 = new PriorityQueue<>(  // 反向比较Employee(Employee::compareTo)
                Comparator.reverseOrder());
    }

    /**
     * {@link ArrayTest#arraySortTest()}
     */
    @Test
    public void arraySortTest() {
    }

}
