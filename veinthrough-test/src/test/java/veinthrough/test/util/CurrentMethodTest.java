package veinthrough.test.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.util.MethodLog;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * Get the current method name.
 * NOTE: 当前的函数在StackTrace[1], 如果要获取上一层的函数, 那应该用StackTrace[2], 依次类推
 * {@link MethodLog#getMethodString(int)}
 */

@Slf4j
public class CurrentMethodTest {
    @Test
    public void currentMethodTest() {
        log.info(methodLog(
                Thread.currentThread().getStackTrace()[1].getMethodName()+"()"));
    }
}
