package veinthrough.test.reflect;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * This program shows how to invoke methods through reflection.
 *
 * 1. APIs:
 * (1) 通过class获取method: {@link Class#getMethod(String, Class[])}
 * (2) 调用method: {@link Method#invoke(Object, Object...)},
 * 调用static函数的第一个参数为null，否则第一个参数为调用该方法的对象
 */
@Slf4j
public class MethodInvokerTest {
    @Test
    public void methodInvokerTest() {
        try {
            // 1. get method pointers to the square and sqrt methods
            Method square = MethodInvokerTest.class.getMethod("square", double.class);
            Method sqrt = Math.class.getMethod("sqrt", double.class);

            // 2. print tables of x- and y-values
            printTable(1, 10, 1, square);
            printTable(2, 100, 10, sqrt);
        } catch (NoSuchMethodException | SecurityException e) {
            log.error(exceptionLog(e));
        }
    }

    // should be public, otherwise: NoSuchMethodException
    @SuppressWarnings("WeakerAccess")
    public static double square(double x) {
        return x * x;
    }

    /**
     * Prints a table with x- and y-values for a method
     *
     * @param from the lower bound for the x-values
     * @param to   the upper bound for the x-values
     * @param slice    the slice between rows in the table
     * @param f    a method with a double parameter and double return value
     */
    private static void printTable(double from, double to, int slice, Method f) {
        // 1. print out the method as table header
        log.info(methodLog(f.toGenericString()));

        for (double x = from; x <= to; x += slice) {
            try {
                // 2. invoke method
                // when invoking static method, the first argument is null
                double y = (Double) f.invoke(null, x);
                log.info(methodLog(String.format("%10.4f | %10.4f\n", x, y)));
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error(exceptionLog(e));
            }
        }
    }
}
