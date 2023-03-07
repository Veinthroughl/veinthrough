package veinthrough.test.guava;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import veinthrough.api.generic.Pair;
import veinthrough.api.generic.PairAlg;
import veinthrough.test._class.Employee;
import veinthrough.test._class.Manager;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author veinthrough
 *
 * 1. Ordering<T> implements Comparator<T>,
 * fluentIterable: 链式操作, 链式调用产生的排序器时, 应该从后往前读;
 * 是因为每次链式调用都是用后面的方法包装了前面的排序
 * 2. 生成ordering:
 * (1) [static]{@link Ordering#from(Comparator)}
 * (2) Implement the abstract Ordering<T> by override {@link  Ordering#compare(Object, Object)}
 * (3) [static] {@link Ordering#natural()}: 使用既有的compareTo() from Comparable<T>
 * (4) [static][Comparator]{@link Ordering#naturalOrder()}/{@link Comparator#naturalOrder()}:
 * 使用既有的compareTo() from Comparable<T>
 * (5) [static] {@link Ordering#usingToString()}:  先使用toString()，再根据String排序
 * (6) [static] {@link Ordering#onResultOf(Function)}, 类似于{@link Comparator#comparing(java.util.function.Function keyExtrator)}
 * (7) reverse
 * > [fluent] 使用{@link Ordering#reverse()}/{@link Ordering#reversed()},
 * > [static] {@link Ordering#reverseOrder()}
 * (8) [fluent] nullsFirst()/nullsLast()
 * 3. 使用ordering计算:
 * (1) {@link Ordering#min(Iterable)}/{@link Ordering#max(Iterable)}: 使用生成的ordering来计算min/max
 *
 * Tests:
 * {@link PairAlg#minMax(Comparable[])}
 * {@link PairAlg#minMax(Iterable)}
 */
@SuppressWarnings("unused")
public class OrderingTest {
    private static final Manager ceo =
            new Manager("Gus Greedy", 800000D, 80000D);
    private static final Manager cfo =
            new Manager("Sid Sneaky", 600000D, 60000D);
    private static final Employee worker = new Employee("src/main/java/veinthrough", 80000D);
    private static final Manager[] managers = {ceo, cfo};

    public void orderingTest() {
        List<Manager> managersList = Arrays.asList(managers);
        Pair<? super Manager> result = new Pair<>();

        Ordering<Manager> ordering =
                Ordering.natural().onResultOf(
                        Manager::getBonus);

        result.setFirst(ordering.min(managersList));
        result.setSecond(ordering.max(managersList));
    }
}
