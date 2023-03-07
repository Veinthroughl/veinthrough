package veinthrough.test.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 * 
 * 1. Exceptions architecture.
 * Throwable-+---(2) Error
 *          |
 *         +---Exception-+---(1) 非RuntimeException(checked)
 *                      |
 *                     +---(3) RuntimeException
 * (1) [checked]非RuntimeException, 大部分为IOException;
 * 一个方法必须声明所有可能抛出的checked exception。
 * 而unchecked exception要么不可控制（Error），要么就应该避免发生（逻辑问题）。
 * (2) [unchecked]Error, 代表的是严重错误, 这种错误程序员无法进行处理, 例如操作系统崩溃/jvm出错/动态链接库失败等.
 * (3) [unchecked]RuntimeException: 包括除数为0, 数组下标超界等. 运行时异常的派生类有很多, 其产生频率较高.
 * 它的派生类可以由程序处理或者抛给(throw) 给jvm处理.
 * 2. 常见checked Exception
 * IOException
 * ClassNotFoundException	没有找到类
 * IllegalAccessException	访问类被拒绝
 * InstantiationException	试图创建抽象类或接口的对象
 * InterruptedException	    线程被另一个线程中断
 * NoSuchFieldException	    请求的域不存在
 * NoSuchMethodException	请求的方法不存在
 * ReflectiveOperationException	与反射有关的异常的超类
 * 3. 再次抛出异常与异常链：在catch子句中可以抛出一个异常.
 *
 * Tests:
 * 1. finally中也有可能抛出异常
 * (1) 使用2个try: 原来的异常被抑制, close()抛出的异常将会被抛出
 * (2) 使用try(resource): 原来的异常重新抛出, close()抛出的exception将会被抑制
 * 2. catch/finally含有return语句:
 * (1) catch含有return语句
 * (2) catch和finally中都包含return语句
 */
@Slf4j
public class ExceptionTest {
    // make it non-existed
    private static final String fileName = "exception_test.txt";

    /**
     * finally(调用close())中也有可能抛出异常
     * 1.(1) 使用2个try, 原来的异常被抑制, close()抛出的异常将会被抛出
     */
    @Test
    public void doubleTryTest() {
        InputStream in = null;
        try {
            try {
                log.info(methodLog(1, "New file stream from " + fileName));
                in = new FileInputStream(fileName);

                log.info(methodLog(2, "Read:" + in.read()));
            } catch (FileNotFoundException e) {
                log.info(methodLog(3, "Exception:" + e.getMessage()));
            } catch (IOException e) {
                log.info(methodLog(4, "Exception:" + e.getMessage()));
            } finally {
                log.info(methodLog(5, "In finally to close file stream"));
                if (in != null) {
                    in.close();
                }
            }
        } catch (IOException e) {
            log.info(methodLog(6, "Exception:" + e.getMessage()));
        }
    }

    /**
     * finally(调用close())中也有可能抛出异常
     * 1.(2) 使用try(resource), 原来的异常重新抛出, close()抛出的exception将会被抑制
     */
    @Test
    public void tryResourceTest() {
        log.info(methodLog(1, "New file stream from " + fileName));
        try (InputStream in = new FileInputStream(fileName)) {
            log.info(methodLog(2, "Read:" + in.read()));
        } catch (IOException e) {
            log.info(methodLog(3, "Exception:" + e.getMessage()));
        }
    }

    /**
     * 2. catch/finally含有return语句:
     * (1) catch含有return语句
     * (2) catch和finally中都包含return语句
     */
    @Test
    public void finalReturnTest() {
        log.info(methodLog(
                "finalReturn1()", "" + finalReturn1(),
                "finalReturn2()", "" + finalReturn2()));
    }

    @SuppressWarnings({"UnusedAssignment", "divzero"})
    private int finalReturn1() {
        int a = 10;
        try {
            log.info(methodLog(a / 0));
            a = 20;
        } catch (ArithmeticException e) {
            a = 30;
            return a;
            // return a 在程序执行到这一步的时候，不是return a 而是 return 30(已经将30放在栈顶准备return)
            // 但是呢，它发现后面还有finally，所以继续执行finally的内容，a=40
            // 再次回到以前的路径,继续走return 30，形成返回路径之后，这里的a就不是a变量了，而是常量30
        } finally { // finally在返回前执行
            a = 40;
        }
        return a;
    }

    @SuppressWarnings({"finally", "ReturnInsideFinallyBlock", "UnusedAssignment", "divzero"})
    private int finalReturn2() {
        int a = 10;
        try {
            log.info(methodLog(a / 0));
            a = 20;
        } catch (ArithmeticException e) {
            a = 30;
            return a;
            // return a 在程序执行到这一步的时候，不是return a 而是 return 30(已经将30放在栈顶准备return)
            // 但是呢，它发现后面还有finally，所以继续执行finally的内容，a=40
            // 再次回到以前的路径,继续走return 30，形成返回路径之后，这里的a就不是a变量了，而是常量30
        } finally {
            a = 40;
            return a; // 这里又重新形成了一条返回路径，(将40放在栈顶准备return)由于只能通过1个return返回，所以这里直接返回40
        }
//      return a;
    }
}
